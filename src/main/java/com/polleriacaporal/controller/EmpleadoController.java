package com.polleriacaporal.controller;

import com.polleriacaporal.model.DetallePedido;
import com.polleriacaporal.model.Pedido;
import com.polleriacaporal.model.Producto;
import com.polleriacaporal.model.Usuario;
import com.polleriacaporal.service.PedidoService;
import com.polleriacaporal.service.ProductoService;
import com.polleriacaporal.service.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Comparator;
import java.util.List;

/**
 * Controlador para el Dashboard del Empleado
 * Rutas protegidas - Solo empleados y administradores pueden acceder
 */
@Controller
@RequestMapping("/empleados")
@PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
public class EmpleadoController {

    private final ProductoService productoService;
    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;

    public EmpleadoController(ProductoService productoService, PedidoService pedidoService, UsuarioService usuarioService) {
        this.productoService = productoService;
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
    }

    /**
     * Dashboard principal del empleado
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        Usuario usuario = usuarioService.obtenerPorUsername(authentication.getName()).orElse(null);
        model.addAttribute("username", authentication.getName());
        model.addAttribute("activePage", "empleado-dashboard");
        if (usuario != null) {
            List<Pedido> todosPedidos = pedidoService.obtenerPorUsuario(usuario);
            List<Pedido> pedidosRecientes = todosPedidos.stream()
                    .sorted(Comparator.comparing(Pedido::getFechaPedido).reversed())
                    .limit(5)
                    .toList();
            double montoTotal = todosPedidos.stream().mapToDouble(p -> p.getTotal().doubleValue()).sum();
            model.addAttribute("pedidosRecientes", pedidosRecientes);
            model.addAttribute("totalPedidos", todosPedidos.size());
            model.addAttribute("montoTotal", montoTotal);
            model.addAttribute("ventaPromedio", todosPedidos.isEmpty() ? 0 : montoTotal / todosPedidos.size());
        } else {
            model.addAttribute("pedidosRecientes", java.util.Collections.emptyList());
            model.addAttribute("totalPedidos", 0);
            model.addAttribute("montoTotal", 0);
            model.addAttribute("ventaPromedio", 0);
        }
        return "empleado/dashboard";
    }

    /**
     * Interfaz para registrar nuevos pedidos
     */
    @GetMapping("/pedidos/nuevo")
    public String nuevoPedido(Model model) {
        model.addAttribute("activePage", "nuevo-pedido");
        model.addAttribute("productos", productoService.findAll());
        return "empleado/nuevo-pedido";
    }

    /**
     * Guardar nuevo pedido
     */
    @PostMapping("/pedidos/guardar")
    public String guardarPedido(@RequestParam(required = false) String clienteNombre,
                                @RequestParam(required = false) String clienteTelefono,
                                @RequestParam String tipoEntrega,
                                @RequestParam(required = false) Integer numeroMesa,
                                @RequestParam(required = false) String clienteDireccion,
                                @RequestParam(required = false) String nota,
                                @RequestParam String productosJson,
                                Authentication authentication) {
        try {
            Usuario usuario = usuarioService.obtenerPorUsername(authentication.getName()).orElseThrow();

            if (clienteNombre == null || clienteNombre.isBlank()) {
                clienteNombre = "Cliente sin nombre";
            }
            if (nota == null) {
                nota = "";
            }
            if (productosJson == null || productosJson.trim().isEmpty()) {
                return "redirect:/empleados/pedidos/nuevo?error=Debe agregar al menos un producto";
            }

            Pedido pedido = new Pedido();
            pedido.setUsuario(usuario);
            pedido.setClienteNombre(clienteNombre);
            pedido.setClienteTelefono(clienteTelefono);
            pedido.setNota(nota);

            // Procesar dirección
            if ("mesa".equals(tipoEntrega)) {
                pedido.setClienteDireccion("Mesa " + numeroMesa);
            } else {
                pedido.setClienteDireccion(clienteDireccion);
            }

            // Procesar productos (productosJson es un string JSON de productos)
            // Por simplicidad, asumir que productosJson es "id:cantidad,id:cantidad"
            String[] productosArray = productosJson.split(",");
            for (String prod : productosArray) {
                String[] parts = prod.split(":");
                Long productoId = Long.parseLong(parts[0]);
                int cantidad = Integer.parseInt(parts[1]);
                Producto producto = productoService.findById(productoId).orElseThrow();
                DetallePedido detalle = new DetallePedido();
                detalle.setProducto(producto);
                detalle.setCantidad(cantidad);
                detalle.setPrecioUnitario(producto.getPrecio());
                detalle.actualizarSubtotal();
                pedido.agregarDetalle(detalle);
            }

            pedido.calcularTotal();
            pedidoService.guardar(pedido);

            return "redirect:/empleados/dashboard?success=Pedido%20guardado";
        } catch (Exception e) {
            return "redirect:/empleados/pedidos/nuevo?error=Error%20al%20guardar%20el%20pedido";
        }
    }

    /**
     * Listar pedidos del empleado
     */
    @GetMapping("/pedidos")
    public String misPedidos(Authentication authentication, Model model) {
        Usuario usuario = usuarioService.obtenerPorUsername(authentication.getName()).orElse(null);
        model.addAttribute("username", authentication.getName());
        model.addAttribute("activePage", "mis-pedidos");
        if (usuario != null) {
            model.addAttribute("pedidos", pedidoService.obtenerPorUsuario(usuario));
        } else {
            model.addAttribute("pedidos", java.util.Collections.emptyList());
        }
        return "empleado/mis-pedidos";
    }

    /**
     * Ver histórico de ventas
     */
    @GetMapping("/ventas")
    public String misVentas(Model model) {
        model.addAttribute("activePage", "mis-ventas");
        return "empleado/mis-ventas";
    }
}

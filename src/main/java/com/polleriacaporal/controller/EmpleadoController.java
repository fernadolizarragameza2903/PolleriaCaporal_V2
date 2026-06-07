package com.polleriacaporal.controller;

import com.polleriacaporal.model.DetallePedido;
import com.polleriacaporal.model.EstadoVenta;
import com.polleriacaporal.model.Pedido;
import com.polleriacaporal.model.Producto;
import com.polleriacaporal.model.Usuario;
import com.polleriacaporal.service.PedidoPdfService;
import com.polleriacaporal.service.PedidoService;
import com.polleriacaporal.service.ProductoService;
import com.polleriacaporal.service.UsuarioService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
    private final PedidoPdfService pedidoPdfService;

    public EmpleadoController(ProductoService productoService, PedidoService pedidoService, UsuarioService usuarioService, PedidoPdfService pedidoPdfService) {
        this.productoService = productoService;
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
        this.pedidoPdfService = pedidoPdfService;
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
                // Guardar el precio unitario tal como está en Producto (precio final, IGV incluido)
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
        model.addAttribute("productos", productoService.findAll());
        model.addAttribute("estados", EstadoVenta.values());
        return "empleado/mis-pedidos";
    }

    /**
     * Ver detalle de un pedido (propio o ADMIN)
     */
    @GetMapping("/pedidos/{id}")
    public String verPedido(@PathVariable Long id, Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        var maybe = pedidoService.obtenerPorId(id);
        if (maybe.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Pedido no encontrado");
            return "redirect:/empleados/pedidos";
        }
        Pedido pedido = maybe.get();
        // Validar propietario o admin
        Usuario usuario = usuarioService.obtenerPorUsername(authentication.getName()).orElse(null);
        boolean esAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!esAdmin && (usuario == null || !pedido.getUsuario().getId().equals(usuario.getId()))) {
            redirectAttributes.addFlashAttribute("error", "No tiene permiso para ver este pedido");
            return "redirect:/empleados/pedidos";
        }

        inicializarPedido(pedido);
        model.addAttribute("pedido", pedido);
        model.addAttribute("activePage", "mis-pedidos");
        return "empleado/pedido-detalle";
    }

    /**
     * Editar pedido - formulario
     */
    @GetMapping("/pedidos/{id}/editar")
    public String editarPedidoForm(@PathVariable Long id, Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        var maybe = pedidoService.obtenerPorId(id);
        if (maybe.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Pedido no encontrado");
            return "redirect:/empleados/pedidos";
        }
        Pedido pedido = maybe.get();
        Usuario usuario = usuarioService.obtenerPorUsername(authentication.getName()).orElse(null);
        boolean esAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!esAdmin && (usuario == null || !pedido.getUsuario().getId().equals(usuario.getId()))) {
            redirectAttributes.addFlashAttribute("error", "No tiene permiso para editar este pedido");
            return "redirect:/empleados/pedidos";
        }

        inicializarPedido(pedido);
        model.addAttribute("pedido", pedido);
        model.addAttribute("productos", productoService.findAll());
        model.addAttribute("activePage", "mis-pedidos");
        return "empleado/pedido-edit";
    }

    /**
     * Editar pedido - guardar cambios (productosJson formato id:cantidad,id:cantidad)
     */
    @PostMapping("/pedidos/{id}/editar")
    public String editarPedidoGuardar(@PathVariable Long id,
                                      @RequestParam(required = false) String clienteNombre,
                                      @RequestParam(required = false) String clienteTelefono,
                                      @RequestParam(required = false) String clienteDireccion,
                                      @RequestParam(required = false) String nota,
                                      @RequestParam String productosJson,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes) {
        try {
            var maybe = pedidoService.obtenerPorId(id);
            if (maybe.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Pedido no encontrado");
                return "redirect:/empleados/pedidos";
            }
            Pedido pedido = maybe.get();
            Usuario usuario = usuarioService.obtenerPorUsername(authentication.getName()).orElseThrow();
            boolean esAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (!esAdmin && !pedido.getUsuario().getId().equals(usuario.getId())) {
                redirectAttributes.addFlashAttribute("error", "No tiene permiso para editar este pedido");
                return "redirect:/empleados/pedidos";
            }

            if (clienteNombre != null) pedido.setClienteNombre(clienteNombre);
            pedido.setClienteTelefono(clienteTelefono);
            pedido.setClienteDireccion(clienteDireccion);
            pedido.setNota(nota == null ? "" : nota);

            // Reemplazar detalles existentes
            pedido.getDetalles().clear();
                if (productosJson != null && !productosJson.trim().isEmpty()) {
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
            }

            pedido.calcularTotal();
            pedidoService.guardar(pedido);
            redirectAttributes.addFlashAttribute("success", "Pedido actualizado");
            return "redirect:/empleados/pedidos/" + pedido.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el pedido");
            return "redirect:/empleados/pedidos";
        }
    }

    /**
     * Imprimir boleta (vista optimizada para impresión)
     */
    @Transactional(readOnly = true)
    @GetMapping("/pedidos/{id}/imprimir")
    public String imprimirBoleta(@PathVariable Long id, Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        var maybe = pedidoService.obtenerPorId(id);
        if (maybe.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Pedido no encontrado");
            return "redirect:/empleados/pedidos";
        }
        Pedido pedido = maybe.get();
        Usuario usuario = usuarioService.obtenerPorUsername(authentication.getName()).orElse(null);
        boolean esAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!esAdmin && (usuario == null || !pedido.getUsuario().getId().equals(usuario.getId()))) {
            redirectAttributes.addFlashAttribute("error", "No tiene permiso para ver este pedido");
            return "redirect:/empleados/pedidos";
        }

        inicializarPedido(pedido);
        model.addAttribute("pedido", pedido);
        // Cálculos (ya están en la entidad) - asegurar formato
        model.addAttribute("activePage", "mis-pedidos");
        return "empleado/boleta";
    }

    @Transactional(readOnly = true)
    @GetMapping("/pedidos/{id}/pdf")
    public ResponseEntity<byte[]> descargarPedidoPdf(@PathVariable Long id, Authentication authentication) {
        Pedido pedido = pedidoService.obtenerPorId(id).orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        Usuario usuario = usuarioService.obtenerPorUsername(authentication.getName()).orElse(null);
        boolean esAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!esAdmin && (usuario == null || !pedido.getUsuario().getId().equals(usuario.getId()))) {
            return ResponseEntity.status(403).build();
        }

        byte[] pdf = pedidoPdfService.generarPedidoPdf(pedido);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=pedido-" + pedido.getId() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    /**
     * Ver histórico de ventas
     */
    @GetMapping("/ventas")
    public String misVentas(Model model) {
        model.addAttribute("activePage", "mis-ventas");
        return "empleado/mis-ventas";
    }

    private void inicializarPedido(Pedido pedido) {
        if (pedido.getUsuario() != null) {
            pedido.getUsuario().getUsername();
        }
        pedido.getDetalles().forEach(detalle -> {
            if (detalle.getProducto() != null) {
                detalle.getProducto().getNombre();
            }
        });
    }
}

package com.polleriacaporal.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controlador para el Dashboard del Administrador
 * Rutas protegidas - Solo administradores pueden acceder
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;

    public AdminController(PedidoService pedidoService, UsuarioService usuarioService, ProductoService productoService) {
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
        this.productoService = productoService;
    }

    /**
     * Dashboard principal del administrador
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("activePage", "admin-dashboard");
        // Agregar estadísticas
        model.addAttribute("totalUsuarios", usuarioService.obtenerTodos().size());
        model.addAttribute("totalProductos", productoService.findAll().size());
        model.addAttribute("totalVentas", pedidoService.obtenerTodos().size());

        // Agregar actividades recientes
        List<String> actividades = new ArrayList<>();
        // Últimos pedidos
        List<Pedido> ultimosPedidos = pedidoService.obtenerTodos().stream()
                .sorted(Comparator.comparing(Pedido::getFechaPedido).reversed())
                .limit(3)
                .toList();
        for (Pedido p : ultimosPedidos) {
            actividades.add("Nuevo pedido: " + p.getClienteNombre() + " - " + p.getTotal());
        }
        // Últimos usuarios
        List<Usuario> ultimosUsuarios = usuarioService.obtenerTodos().stream()
                .sorted(Comparator.comparing(Usuario::getFechaCreacion).reversed())
                .limit(2)
                .toList();
        for (Usuario u : ultimosUsuarios) {
            actividades.add("Nuevo usuario: " + u.getUsername());
        }
        // Últimos productos
        List<Producto> ultimosProductos = productoService.findAll().stream()
                .sorted(Comparator.comparing(Producto::getFechaCreacion).reversed())
                .limit(2)
                .toList();
        for (Producto pr : ultimosProductos) {
            actividades.add("Nuevo producto: " + pr.getNombre());
        }
        model.addAttribute("actividadesRecientes", actividades);

        return "admin/dashboard";
    }

    /**
     * Reportes de ventas
     */
    @GetMapping("/reportes")
    public String reportes(Model model) {
        model.addAttribute("activePage", "reportes");
        List<Pedido> pedidos = pedidoService.obtenerTodos();
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("totalVentas", pedidos.size());
        double montoTotal = pedidos.stream().mapToDouble(p -> p.getTotal().doubleValue()).sum();
        model.addAttribute("montoTotal", montoTotal);
        model.addAttribute("ventaPromedio", pedidos.isEmpty() ? 0 : montoTotal / pedidos.size());
        return "admin/reportes";
    }

    /**
     * Gestión de productos
     */
    @GetMapping("/productos")
    public String productos(Model model) {
        return "redirect:/productos";
    }

    /**
     * Ver todos los pedidos
     */
    @GetMapping("/pedidos")
    public String pedidos(Model model) {
        model.addAttribute("activePage", "pedidos-admin");
        model.addAttribute("pedidos", pedidoService.obtenerTodos());
        return "admin/pedidos";
    }
}

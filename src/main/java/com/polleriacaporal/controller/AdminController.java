package com.polleriacaporal.controller;

import com.polleriacaporal.model.Pedido;
import com.polleriacaporal.model.Producto;
import com.polleriacaporal.model.Usuario;
import com.polleriacaporal.model.EstadoVenta;
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

        // 1) Reportes globales de ventas
        var pedidos = pedidoService.obtenerTodos();
        model.addAttribute("totalVentas", pedidos.size());
        var montoTotal = pedidos.stream().map(p -> p.getTotal() == null ? java.math.BigDecimal.ZERO : p.getTotal())
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        model.addAttribute("montoTotal", montoTotal);
        long pedidosCompletados = pedidos.stream().filter(p -> p.getEstado() == com.polleriacaporal.model.EstadoVenta.COMPLETO).count();
        model.addAttribute("pedidosCompletados", pedidosCompletados);

        // 2) Gestión de usuarios
        model.addAttribute("totalUsuarios", usuarioService.obtenerTodos().size());

        // 3) Reportes de rendimiento de productos (más vendidos)
        var top = pedidoService.obtenerTodos().stream()
            .flatMap(p -> p.getDetalles().stream())
            .collect(java.util.stream.Collectors.groupingBy(d -> d.getProducto().getNombre(), java.util.stream.Collectors.summingInt(d -> d.getCantidad())))
            .entrySet().stream()
            .sorted(java.util.Map.Entry.<String,Integer>comparingByValue().reversed())
            .limit(10)
            .toList();
        model.addAttribute("topProductos", top);

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
        model.addAttribute("productos", productoService.findAll());
        model.addAttribute("estados", EstadoVenta.values());
        return "admin/pedidos";
    }
}

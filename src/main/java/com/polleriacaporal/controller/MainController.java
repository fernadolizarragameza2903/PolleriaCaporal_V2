package com.polleriacaporal.controller;

import com.polleriacaporal.model.Producto;
import com.polleriacaporal.service.ProductoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controlador principal de la aplicación
 * Gestiona las rutas públicas y el flujo de navegación
 */
@Controller
public class MainController {

    private final ProductoService productoService;

    public MainController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Redirección pública principal al login
     */
    @GetMapping({"/", "/index", "/inicio"})
    public String home() {
        return "redirect:/login";
    }

    /**
     * Página de login
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Dashboard después del login - redirige según el rol
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        String rol = esAdmin ? "ROLE_ADMIN" : "ROLE_EMPLOYEE";

        model.addAttribute("username", authentication.getName());
        model.addAttribute("rol", rol);

        if (esAdmin) {
            return "redirect:/admin/dashboard";
        }

        return "redirect:/empleados/dashboard";
    }

    /**
     * Rutas de ventas
     */
    @GetMapping("/ventas")
    public String ventas(Model model) {
        model.addAttribute("activePage", "ventas");
        return "ventas";
    }

    /**
     * Rutas de productos
     */
    @GetMapping("/productos")
    public String productos(Model model) {
        model.addAttribute("activePage", "productos");
        model.addAttribute("productos", productoService.findAll());
        model.addAttribute("producto", new Producto());
        model.addAttribute("editando", false);
        return "productos";
    }

    @GetMapping("/productos/editar/{id}")
    public String editarProducto(@PathVariable Long id, Model model) {
        model.addAttribute("activePage", "productos");
        model.addAttribute("productos", productoService.findAll());
        model.addAttribute("producto", productoService.findById(id).orElse(new Producto()));
        model.addAttribute("editando", true);
        return "productos";
    }

    @PostMapping("/productos/guardar")
    public String guardarProducto(Producto producto) {
        productoService.save(producto);
        return "redirect:/productos";
    }

    @GetMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        productoService.deleteById(id);
        return "redirect:/productos";
    }

    /** Listado CRUD en {@link UsuarioController} (GET {@code /usuarios}). */

    @GetMapping("/empleados")
    public String empleados(Model model) {
        model.addAttribute("activePage", "empleados");
        return "empleados";
    }
}

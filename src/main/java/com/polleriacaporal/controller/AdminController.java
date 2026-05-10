package com.polleriacaporal.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para el Dashboard del Administrador
 * Rutas protegidas - Solo administradores pueden acceder
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    /**
     * Dashboard principal del administrador
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("activePage", "admin-dashboard");
        // Se pueden agregar estadísticas aquí
        model.addAttribute("totalUsuarios", 0);
        model.addAttribute("totalProductos", 0);
        model.addAttribute("totalVentas", 0);
        return "admin/dashboard";
    }

    /**
     * Reportes de ventas
     */
    @GetMapping("/reportes")
    public String reportes(Model model) {
        model.addAttribute("activePage", "reportes");
        return "admin/reportes";
    }

    /**
     * Gestión de productos
     */
    @GetMapping("/productos")
    public String productos(Model model) {
        model.addAttribute("activePage", "productos-admin");
        return "admin/productos";
    }
}

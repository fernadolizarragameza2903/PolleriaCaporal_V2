package com.polleriacaporal.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para el Dashboard del Empleado
 * Rutas protegidas - Solo empleados y administradores pueden acceder
 */
@Controller
@RequestMapping("/empleados")
@PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
public class EmpleadoController {

    /**
     * Dashboard principal del empleado
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("activePage", "empleado-dashboard");
        return "empleado/dashboard";
    }

    /**
     * Interfaz para registrar nuevos pedidos
     */
    @GetMapping("/pedidos/nuevo")
    public String nuevoPedido(Model model) {
        model.addAttribute("activePage", "nuevo-pedido");
        return "empleado/nuevo-pedido";
    }

    /**
     * Listar pedidos del empleado
     */
    @GetMapping("/pedidos")
    public String misPedidos(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("activePage", "mis-pedidos");
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

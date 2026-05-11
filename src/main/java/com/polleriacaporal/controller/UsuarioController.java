package com.polleriacaporal.controller;

import com.polleriacaporal.model.RolUsuario;
import com.polleriacaporal.model.Usuario;
import com.polleriacaporal.service.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;

import java.util.Optional;

/**
 * Controlador para la gestión de Usuarios
 * Rutas protegidas - Solo administradores pueden acceder
 */
@Controller
@RequestMapping("/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Listar todos los usuarios
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.obtenerTodos());
        model.addAttribute("activePage", "usuarios");
        return "usuarios";
    }

    /**
     * Mostrar formulario para crear nuevo usuario
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", RolUsuario.values());
        model.addAttribute("editando", false);
        model.addAttribute("activePage", "usuarios");
        return "usuario-form";
    }

    /**
     * Guardar nuevo usuario
     */
    @PostMapping("/guardar")
    public String guardarUsuario(@Validated({Default.class, Usuario.OnCreate.class}) @ModelAttribute Usuario usuario,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", RolUsuario.values());
            model.addAttribute("editando", false);
            return "usuario-form";
        }

        try {
            usuarioService.crearUsuario(usuario);
            return "redirect:/usuarios?success=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", RolUsuario.values());
            return "usuario-form";
        }
    }

    /**
     * Mostrar formulario para editar usuario
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Optional<Usuario> usuario = usuarioService.obtenerPorId(id);

        if (usuario.isEmpty()) {
            return "redirect:/usuarios";
        }

        model.addAttribute("usuario", usuario.get());
        model.addAttribute("roles", RolUsuario.values());
        model.addAttribute("editando", true);
        model.addAttribute("activePage", "usuarios");
        return "usuario-form";
    }

    /**
     * Actualizar usuario existente
     */
    @PostMapping("/actualizar/{id}")
    public String actualizarUsuario(@PathVariable Long id,
                                   @Valid @ModelAttribute Usuario usuario,
                                   BindingResult result,
                                   Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", RolUsuario.values());
            model.addAttribute("editando", true);
            return "usuario-form";
        }

        try {
            usuario.setId(id);
            usuarioService.actualizarUsuario(usuario);
            return "redirect:/usuarios?updated=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", RolUsuario.values());
            return "usuario-form";
        }
    }

    /**
     * Cambiar estado del usuario (activo/inactivo)
     */
    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id,
                               @RequestParam Boolean activo) {
        usuarioService.cambiarEstado(id, activo);
        return "redirect:/usuarios";
    }

    /**
     * Eliminar usuario
     */
    @PostMapping("/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return "redirect:/usuarios";
    }

    /**
     * Ver detalle de un usuario
     */
    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        Optional<Usuario> usuario = usuarioService.obtenerPorId(id);

        if (usuario.isEmpty()) {
            return "redirect:/usuarios";
        }

        model.addAttribute("usuario", usuario.get());
        model.addAttribute("activePage", "usuarios");
        return "usuario-detalle";
    }
}

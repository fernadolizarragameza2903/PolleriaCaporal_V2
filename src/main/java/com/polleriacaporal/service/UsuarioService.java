package com.polleriacaporal.service;

import com.polleriacaporal.model.RolUsuario;
import com.polleriacaporal.model.Usuario;
import com.polleriacaporal.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de Usuarios
 * Contiene la lógica de negocio para crear, actualizar, eliminar y consultar usuarios
 */
@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Obtiene todos los usuarios del sistema
     */
    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Obtiene un usuario por su ID
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Obtiene un usuario por su nombre de usuario
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    /**
     * Obtiene todos los usuarios por rol
     */
    @Transactional(readOnly = true)
    public List<Usuario> obtenerPorRol(RolUsuario rol) {
        return usuarioRepository.findByRol(rol);
    }

    /**
     * Obtiene todos los empleados del sistema
     */
    @Transactional(readOnly = true)
    public List<Usuario> obtenerEmpleados() {
        return usuarioRepository.findByRol(RolUsuario.ROLE_EMPLOYEE);
    }

    /**
     * Crea un nuevo usuario en el sistema
     * La contraseña es encriptada antes de guardar
     */
    public Usuario crearUsuario(Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe: " + usuario.getUsername());
        }

        // Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setEstado(true);

        return usuarioRepository.save(usuario);
    }

    /**
     * Actualiza un usuario existente
     */
    public Usuario actualizarUsuario(Usuario usuario) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(usuario.getId());

        if (usuarioExistente.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + usuario.getId());
        }

        Usuario usuarioActualizar = usuarioExistente.get();
        usuarioActualizar.setNombreCompleto(usuario.getNombreCompleto());
        usuarioActualizar.setEmail(usuario.getEmail());
        usuarioActualizar.setTelefono(usuario.getTelefono());
        usuarioActualizar.setRol(usuario.getRol());
        usuarioActualizar.setEstado(usuario.getEstado());

        return usuarioRepository.save(usuarioActualizar);
    }

    /**
     * Actualiza la contraseña de un usuario
     * Valida que la nueva contraseña sea diferente
     */
    public void actualizarContrasena(Long usuarioId, String nuevaContrasena) {
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);

        if (usuario.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId);
        }

        Usuario usuarioActualizar = usuario.get();
        usuarioActualizar.setPassword(passwordEncoder.encode(nuevaContrasena));
        usuarioRepository.save(usuarioActualizar);
    }

    /**
     * Activa o desactiva un usuario
     */
    public Usuario cambiarEstado(Long usuarioId, Boolean activo) {
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);

        if (usuario.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId);
        }

        Usuario usuarioActualizar = usuario.get();
        usuarioActualizar.setEstado(activo);

        return usuarioRepository.save(usuarioActualizar);
    }

    /**
     * Elimina un usuario del sistema
     */
    public void eliminarUsuario(Long usuarioId) {
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);

        if (usuario.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId);
        }

        usuarioRepository.deleteById(usuarioId);
    }

    /**
     * Verifica si un usuario existe por nombre de usuario
     */
    @Transactional(readOnly = true)
    public boolean existeUsuario(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    /**
     * Obtiene todos los usuarios activos
     */
    @Transactional(readOnly = true)
    public List<Usuario> obtenerActivos() {
        return usuarioRepository.findByEstado(true);
    }
}

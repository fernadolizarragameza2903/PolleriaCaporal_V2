package com.polleriacaporal.repository;

import com.polleriacaporal.model.RolUsuario;
import com.polleriacaporal.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Usuario
 * Proporciona métodos CRUD y consultas personalizadas
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    List<Usuario> findByRol(RolUsuario rol);
    List<Usuario> findByEstado(Boolean estado);
    boolean existsByUsername(String username);
}

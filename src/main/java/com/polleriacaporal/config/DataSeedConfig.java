package com.polleriacaporal.config;

import com.polleriacaporal.model.RolUsuario;
import com.polleriacaporal.model.Usuario;
import com.polleriacaporal.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DataSeedConfig {

    @Bean
    @Transactional
    CommandLineRunner seedUsuariosDemo(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            upsertUsuarioDemo(
                    usuarioRepository,
                    passwordEncoder,
                    "admin",
                    "admin123",
                    "Administrador",
                    RolUsuario.ROLE_ADMIN
            );
            upsertUsuarioDemo(
                    usuarioRepository,
                    passwordEncoder,
                    "empleado",
                    "emp123",
                    "Mesero prototipo",
                    RolUsuario.ROLE_EMPLOYEE
            );
        };
    }

    private void upsertUsuarioDemo(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            String username,
            String rawPassword,
            String nombreCompleto,
            RolUsuario rol
    ) {
        Usuario usuario = usuarioRepository.findByUsername(username).orElseGet(() -> {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setUsername(username);
            return nuevoUsuario;
        });

        if (usuario.getPassword() == null || !passwordEncoder.matches(rawPassword, usuario.getPassword())) {
            usuario.setPassword(passwordEncoder.encode(rawPassword));
        }
        usuario.setNombreCompleto(nombreCompleto);
        usuario.setRol(rol);
        usuario.setEstado(true);

        usuarioRepository.save(usuario);
    }
}

package com.polleriacaporal.config;

import com.polleriacaporal.model.RolUsuario;
import com.polleriacaporal.model.Usuario;
import com.polleriacaporal.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeedConfig {

    @Bean
    CommandLineRunner seedUsuariosDemo(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.count() > 0) {
                return;
            }

            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNombreCompleto("Administrador");
            admin.setRol(RolUsuario.ROLE_ADMIN);
            admin.setEstado(true);

            Usuario employee = new Usuario();
            employee.setUsername("empleado");
            employee.setPassword(passwordEncoder.encode("emp123"));
            employee.setNombreCompleto("Mesero prototipo");
            employee.setRol(RolUsuario.ROLE_EMPLOYEE);
            employee.setEstado(true);

            usuarioRepository.save(admin);
            usuarioRepository.save(employee);
        };
    }
}

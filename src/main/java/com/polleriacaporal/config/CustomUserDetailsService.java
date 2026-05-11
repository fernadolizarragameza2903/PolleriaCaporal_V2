package com.polleriacaporal.config;

import com.polleriacaporal.model.Usuario;
import com.polleriacaporal.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Implementación de UserDetailsService para Spring Security
 * Carga los usuarios de la base de datos
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        if (!usuario.getEstado()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + username);
        }

        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(usuario.getRol().name())
        );

        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                Boolean.TRUE.equals(usuario.getEstado()),
                true,
                true,
                true,
                authorities
        );
    }
}

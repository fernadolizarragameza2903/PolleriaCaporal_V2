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

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println(">>> [LOGIN] Buscando usuario: " + username);

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println(">>> [LOGIN] Usuario NO encontrado en BD: " + username);
                    return new UsernameNotFoundException("Usuario no encontrado: " + username);
                });

        System.out.println(">>> [LOGIN] Usuario encontrado: " + usuario.getUsername());
        System.out.println(">>> [LOGIN] Rol: " + usuario.getRol());
        System.out.println(">>> [LOGIN] Estado: " + usuario.getEstado());
        System.out.println(">>> [LOGIN] Password hash: " + usuario.getPassword().substring(0, 10) + "...");

        if (!usuario.getEstado()) {
            System.out.println(">>> [LOGIN] Usuario INACTIVO, bloqueando acceso");
            throw new UsernameNotFoundException("Usuario inactivo: " + username);
        }

        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(usuario.getRol().name())
        );

        System.out.println(">>> [LOGIN] Authorities asignadas: " + authorities);

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
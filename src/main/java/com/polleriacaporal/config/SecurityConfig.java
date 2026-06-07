package com.polleriacaporal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Seguridad basada en Spring Security usando los roles ROLE_ADMIN y ROLE_EMPLOYEE
 * expuestos por {@link com.polleriacaporal.model.RolUsuario}. Los helpers {@code hasRole} resuelven los prefijos ROLE_.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Configurar autorización de rutas
                .authorizeHttpRequests(authorize -> authorize
                        // Rutas públicas
                        .requestMatchers("/", "/inicio", "/css/**", "/js/**", "/img/**", "/login").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        
                        // Rutas del Admin
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/usuarios/**").hasRole("ADMIN")
                        // Permitir gestión básica de productos también a empleados (actualizar stock, ver catálogo)
                        .requestMatchers("/productos/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers("/reportes/**").hasRole("ADMIN")
                        
                        // Rutas del Empleado
                        .requestMatchers("/empleados/**").hasAnyRole("EMPLOYEE", "ADMIN")
                        .requestMatchers("/ventas/**").hasAnyRole("EMPLOYEE", "ADMIN")
                        .requestMatchers("/pedidos/**").hasAnyRole("EMPLOYEE", "ADMIN")
                        
                        // Cualquier otra solicitud requiere autenticación
                        .anyRequest().authenticated()
                )
                // Configurar login
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                // Configurar logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/inicio")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                // Desactivar CSRF para desarrollo (en producción, usar CSRF token)
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}

package com.rollerspeed.rollerspeed.config;

import com.rollerspeed.rollerspeed.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(usuarioService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Agrega esta línea para ignorar la validación CSRF en la ruta de registro.
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(new AntPathRequestMatcher("/registro", "POST"))
                .ignoringRequestMatchers(new AntPathRequestMatcher("/inscripcion/**")) // Ignorar CSRF para la API de inscripción
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/home", "/registro", "/css/**", "/js/**",
                               "/images/**", "/webjars/**", "/sobre-nosotros", "/servicios",
                               "/galeria", "/noticias", "/contacto", "/testimonios").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/dashboard", "/perfil", "/inscripcion/**", "/historial-pagos", "/mi-asistencia")
                    .hasAnyRole("ALUMNO", "INSTRUCTOR", "ADMINISTRADOR")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/dashboard", true)
            )
            .logout(logout -> logout
                .permitAll()
                .logoutSuccessUrl("/")
            );

        return http.build();
    }
}
package com.rollerspeed.rollerspeed.config;

import com.rollerspeed.rollerspeed.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder; // Ya no importas BCryptPasswordEncoder aquí
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UsuarioService usuarioService;

    // Inyecta el bean PasswordEncoder que ahora se crea en BeanConfiguration
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(usuarioService);
        authProvider.setPasswordEncoder(passwordEncoder); // Usa la variable inyectada
        return authProvider;
    }

    // ELIMINA EL MÉTODO @Bean public PasswordEncoder() de esta clase
    // Ya no lo necesitas aquí, lo creaste en BeanConfiguration.java

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/home", "/registro", "/css/**", "/js/**", 
                               "/images/**", "/webjars/**").permitAll()
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
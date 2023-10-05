package com.example.ecommerce.config;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig
{
    @Autowired
    private HttpSession session;


    @Bean
    PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        String publicResources[] = new String[] // URLs y resources públicos para todos los usuarios
        {
            "/", // Home
            "/app.js", // Script
            "/style.css", // CSS
            "/images/**", // Imagenes de productos
            "/producto/**", // Vista de un producto especifico
            "/buscar", // Buscador de productos
            "/signup"
        };

        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth ->
            {
                auth.antMatchers(publicResources).permitAll(); // Rutas disponibles para cualquier usuario/visitante
                auth.antMatchers("/admin/**").hasAuthority("ADMIN"); // Rutas protegidas - solo pueden acceder quienes tengan el rol "ADMIN"
                auth.anyRequest().authenticated(); // Todas las demás rutas - disponible solo para usuarios logueados
            })
            .formLogin(auth ->
            {
                auth.loginPage("/login");
                auth.usernameParameter("email");
                auth.passwordParameter("password");
                auth.permitAll();
            })
            .logout(logout ->
            {
                logout.logoutSuccessUrl("/");
                logout.logoutSuccessHandler((request, response, authentication) ->
                {
                    session.invalidate();
                    response.sendRedirect("/");
                });
            })
            .build();
    }
}

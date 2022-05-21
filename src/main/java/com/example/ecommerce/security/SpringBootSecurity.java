package com.example.ecommerce.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
@EnableWebSecurity
public class SpringBootSecurity extends WebSecurityConfigurerAdapter 
{
    @Autowired
    private UserDetailsService userDetailService;

    @Bean
    public BCryptPasswordEncoder getEncoder()
    {
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        auth.userDetailsService(userDetailService).passwordEncoder(getEncoder());

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        String publicResources[] = new String[] // URLs y resources públicos para todos los usuarios
        {
            "/", // Home
            "/app.js", // Script
            "/style.css", // CSS
            "/images/**", // Imagenes de productos
            "/producto/**", // Vista de producto especifico
            "/buscar", // Buscador de productos
            "/signup"
        };

        http.csrf().disable()
            .authorizeRequests()
                .antMatchers(publicResources).permitAll() // Rutas disponibles para cualquier usuario/visitante
                .antMatchers("/admin/**", "/productos/**").hasRole("ADMIN") // Rutas protegidas - solo pueden acceder quienes tengan el rol "ADMIN"
                .anyRequest().authenticated() // Todas las demás rutas - disponible solo para usuarios logueados
                .and()
            .formLogin()
                .loginPage("/login")
                //.failureUrl("/login?error")
                .defaultSuccessUrl("/login_success", true)
                .usernameParameter("email") // El email remplaza el username
                .passwordParameter("password")
                .permitAll()
                .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
            ;
    }    
}

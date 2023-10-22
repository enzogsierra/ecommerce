package com.example.ecommerce.config;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import com.example.ecommerce.model.Carrito;
import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.repository.CarritoRepository;
import com.example.ecommerce.repository.UsuarioRepository;


@Configuration
@EnableWebSecurity
public class SecurityConfig
{
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CarritoRepository carritoRepository;

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
            "/icons/**",
            "/productos/**", // Vista de un producto especifico
            "/buscar", // Buscador de productos
            "/signup",
            "/mercadopago/webhook", // Endpoint donde MercadoPago envia Webhooks
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
            .oauth2Login(auth ->
            {
                auth.loginPage("/login");
                auth.permitAll();
                auth.successHandler((request, response, authentication) -> // Cuando un usuario se identifica correctamente
                {
                    OAuth2User user = (OAuth2User) authentication.getPrincipal();
                    
                    // Buscar usuario
                    String email = user.getAttribute("email"); // Obtener email

                    Usuario usuario = usuarioRepository.findByEmail(email).orElse(null); // Buscar usuario por el email
                    if(usuario == null) // No se encontró un usuario - crear cuenta
                    {
                        String fullName = user.getAttribute("name"); // Obtener el nombre completo

                        // Separar el nombre completo, para asi tener la propiedad "nombre" y "apellido"
                        String name = null;
                        String lastName = null;
                        if(fullName != null && fullName.contains(" ")) // Si el nombre contiene un espacio
                        {
                            int spaceIdx = fullName.indexOf(" "); // Obtener el index donde se encuentra el espacio en el string
                            name = fullName.substring(0, spaceIdx); // Obtener el nombre, antes del espacio
                            lastName = fullName.substring(spaceIdx + 1); // Obtener el apellido, después del espacio
                        }
                        else name = lastName = fullName; // Si no se pudo separar por espacio, entonces la propiedad "nombre" y "apellido" serán lo mismo

                        // Crear cuenta
                        usuario = new Usuario(); 
                        usuario.setNombre(name);
                        usuario.setApellido(lastName);
                        usuario.setEmail(email);
                        usuario.setTelefono("-");

                        usuarioRepository.save(usuario);
                    }

                    // Cambiar los datos de autenticacion para que "getName()" devuelva el email en vez de un ID
                    SecurityContext securityContext = SecurityContextHolder.getContext();
                    UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(email, usuario.getPassword(), securityContext.getAuthentication().getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(newAuth);

                    // Variable de sesion
                    List<Carrito> carrito = carritoRepository.findByUsuario(usuario);
                    session.setAttribute("usuarioNombre", usuario.getNombre());
                    session.setAttribute("carritoSize", carrito.size());

                    response.sendRedirect("/"); // Redirigir al home
                });
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

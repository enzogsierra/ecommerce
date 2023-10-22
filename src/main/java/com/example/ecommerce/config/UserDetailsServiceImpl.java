package com.example.ecommerce.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import com.example.ecommerce.model.Carrito;
import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.repository.CarritoRepository;
import com.example.ecommerce.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private HttpSession session;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
    {
        Optional<Usuario> opt = usuarioRepository.findByEmail(username); // Buscar usuario por el email dado
        
        if(opt.isPresent()) // Usuario encontrado
        {
            Usuario usuario = opt.get(); // Obtener usuario

            // Verificar que la contraseña no sea null
            // Si la contraseña es null, significa que se registró usando OAuth2, entonces no puede iniciar sesión usando el formulario
            if(usuario.getPassword() != null) 
            {
                // Variable de sesion
                List<Carrito> carrito = carritoRepository.findByUsuario(usuario);
                session.setAttribute("usuarioNombre", usuario.getNombre());
                session.setAttribute("carritoSize", carrito.size());
    
                // Añadir roles al usuario (en este sistema, cada usuario solo puede tener 1 rol)
                List<GrantedAuthority> roles = new ArrayList<>(); // Lista que almacena los roles del usuario
                GrantedAuthority role = new SimpleGrantedAuthority(usuario.getRole()); // Obtener rol del usuario
                roles.add(role); // Añadir 1 unico rol a la lista
    
                return new User(username, usuario.getPassword(), roles); // Crear usuario
            }
        }
        throw new UsernameNotFoundException("Usuario '" + username + "' no encontrado");
    }
}

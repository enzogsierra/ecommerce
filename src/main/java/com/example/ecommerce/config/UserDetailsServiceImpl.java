package com.example.ecommerce.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.ecommerce.model.Usuario;
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
    {
        Optional<Usuario> opt = usuarioRepository.findByEmail(username); // Buscar usuario por el email dado
        
        if(opt.isPresent()) // Usuario encontrado
        {
            Usuario usuario = opt.get(); // Obtener usuario

            // Añadir roles al usuario (en este sistema, cada usuario solo puede tener 1 rol)
            List<GrantedAuthority> roles = new ArrayList<>(); // Lista que almacena los roles del usuario
            GrantedAuthority role = new SimpleGrantedAuthority(usuario.getRole()); // Obtener rol del usuario
            roles.add(role); // Añadir 1 unico rol a la lista

            return new User(username, usuario.getPassword(), roles); // Crear usuario
        }
        throw new UsernameNotFoundException("Usuario '" + username + "' no encontrado");
    }
}
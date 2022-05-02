package com.example.ecommerce.security;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.service.IUsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    HttpSession session;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
    {
        Optional<Usuario> optUser = usuarioService.findByEmail(username); // Buscar usuario por el email dado
        if(optUser.isPresent())
        {
            Usuario usuario = optUser.get();
            session.setAttribute("login_email", username); // Nos ayudará a identificar el email que usó el usuario para iniciar sesión
            
            return User.builder()
                        .username(usuario.getEmail())
                        .password(usuario.getPassword())
                        .roles(usuario.getTipo())
                        .build();
        }
        else
        {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
    }
}

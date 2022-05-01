package com.example.ecommerce.service;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import com.example.ecommerce.model.Usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserDetailServiceImpl implements UserDetailsService
{
    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private BCryptPasswordEncoder bCrypt;

    @Autowired
    HttpSession session;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
    {
        Optional<Usuario> optUser = usuarioService.findByEmail(username);
        if(optUser.isPresent())
        {
            Usuario usuario = optUser.get();
            session.setAttribute("usuario.id", usuario.getId());
            
            return User.builder()
                        .username(usuario.getNombre())
                        .password(bCrypt.encode(usuario.getPassword()))
                        .roles(usuario.getTipo())
                        .build();
        }
        else
        {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
    }
}

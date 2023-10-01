package com.example.ecommerce.controller;

import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.repository.UsuarioRepository;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class AuthController 
{
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    

    // Inicio de sesion
    @GetMapping("/login")
    public String login() {
        return "public/login";
    }
    
    // Registro
    @GetMapping("/signup")
    public String signup(Model model)
    {
        model.addAttribute("usuario", new Usuario());
        return "public/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid Usuario usuario, BindingResult result, Model model)
    {
        // Verificar si el email ya está registrado
        Optional<Usuario> email = usuarioRepository.findByEmail(usuario.getEmail());
        if(email.isPresent()) // Email ya registrado
        {
            result.rejectValue("email", "usuario.email", "Este email ya está registrado, ¿deseas iniciar sesión?"); // Añadir mensaje de error al campo "email"
        }

        // Verificar errores de formulario
        if(result.hasErrors()) {
            return "public/signup";
        }

        // Registro correcto
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword())); // Hashear contraseña
        usuarioRepository.save(usuario);
        return "redirect:/login";
    }
}


package com.example.ecommerce.controller;

import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.service.IUsuarioService;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class AuthController 
{
    @Autowired
    private IUsuarioService usuarioService;
    
    
    @GetMapping("/signup")
    public String signup(Model model)
    {
        return "public/signup";
    }
    
    @PostMapping("/signup")
    public String signup_POST(Usuario usuario)
    {
        usuario.setTipo("USER");
        usuarioService.save(usuario);
        
        return "redirect:/";
    }
    
    @GetMapping("/login")
    public String login(Model model)
    {
        return "public/login";
    }
    
    @PostMapping("/login")
    public String login_POST(Model model, Usuario usuario, HttpSession session)
    {
        Optional<Usuario> auth = usuarioService.findByEmail(usuario.getEmail());
        
        if(!auth.isPresent()) // Verificar email
        {
            model.addAttribute("email", usuario.getEmail());
            model.addAttribute("error_email", 1);
            return "public/login";
        }
        if(!usuario.getPassword().equals(auth.get().getPassword())) // Verificar contraseña
        {
            model.addAttribute("email", usuario.getEmail());
            model.addAttribute("password", usuario.getPassword());
            model.addAttribute("error_password", 1);
            return "public/login";
        }
        
        //
        session.setAttribute("usuario.id", auth.get().getId());
        session.setAttribute("usuario.tipo", auth.get().getTipo());
        
        //
        return auth.get().getTipo().equals("ADMIN") ? ("redirect:/admin") : ("redirect:/");
    }
}

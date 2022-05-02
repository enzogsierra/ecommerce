package com.example.ecommerce.controller;

import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.service.IUsuarioService;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class AuthController 
{
    @Autowired
    private IUsuarioService usuarioService;

    BCryptPasswordEncoder passEncode = new BCryptPasswordEncoder();
    
    
    //
    @GetMapping("/signup")
    public String signup(Model model)
    {
        return "public/signup";
    }
    
    @PostMapping("/signup")
    public String signup_POST(Model model, Usuario usuario, HttpSession session)
    {
        Optional<Usuario> checkEmail = usuarioService.findByEmail(usuario.getEmail()); // Buscar si ya existe un email registrado
        if(checkEmail.isPresent()) // Email existente
        {
            model.addAttribute("nombre", usuario.getNombre()); // Autocompletar formulario
            model.addAttribute("apellido", usuario.getApellido());
            model.addAttribute("email", usuario.getEmail());
            model.addAttribute("direccion", usuario.getDireccion());
            model.addAttribute("error_email", 1); // Mostrar mensaje de error
            return "public/signup";
        }

        // Registro exitoso
        usuario.setTipo("USER"); // Dar rol de "USER" por default
        usuario.setPassword(passEncode.encode(usuario.getPassword())); // Encriptar la clave del usuario
        Usuario createdUser = usuarioService.save(usuario); // Guardar el usuario en la db

        //
        session.setAttribute("usuario.id", createdUser.getId());
        session.setAttribute("usuario.tipo", createdUser.getTipo());

        return "redirect:/";
    }

    
    @GetMapping("/login")
    public String login()
    {
        return "public/login";
    }

    @GetMapping("/login_success") // Ruta cuando el usuario inicia sesión exitosamente
    public String login_success(HttpSession session) 
    {
        String email = session.getAttribute("login_email").toString(); // Extraer el email que usó el usuario para iniciar sesión
        Usuario auth = usuarioService.findByEmail(email).get(); // Buscar los datos de este email

        //
        session.setAttribute("usuario.id", auth.getId());
        session.setAttribute("usuario.tipo", auth.getTipo());

        session.removeAttribute("login_email"); // Eliminar este atributo de la sesión (ya no se usa)

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session)
    {
        session.removeAttribute("usuario.id"); // Eliminar datos de sesión
        session.removeAttribute("usuario.tipo");
        return "redirect:/";
    }
    
    
    /*
        *
        * Overrided by Spring Securty
        *
    
    @PostMapping("/login")
    public String login_POST(Model model, Usuario usuario, HttpSession session)
    {
        Integer userId = Integer.parseInt(session.getAttribute("usuario.id").toString());
        Optional<Usuario> auth = usuarioService.findById(userId);
        
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

    @GetMapping("/logout")
    public String logout(HttpSession session)
    {
        session.removeAttribute("usuario.id");
        session.removeAttribute("usuario.tipo");
        return "redirect:/";
    }
    */
}


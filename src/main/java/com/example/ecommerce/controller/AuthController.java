package com.example.ecommerce.controller;

import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.service.IUsuarioService;

import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class AuthController 
{
    @Autowired
    private IUsuarioService usuarioService;

    BCryptPasswordEncoder passEncode = new BCryptPasswordEncoder();
    
    
    // Registro
    @GetMapping("/signup")
    public String signup(Model model)
    {
        model.addAttribute("usuario", new Usuario());
        return "public/signup";
    }

    @PostMapping("/signup")
    public String signup_POST(@Valid Usuario usuario, BindingResult result, RedirectAttributes redirect, Model model)
    {
        // Verificar si el email ya está registrado
        Optional<Usuario> checkEmail = usuarioService.findByEmail(usuario.getEmail());
        if(checkEmail.isPresent()) // Email ya registrado
        {
            model.addAttribute("error_emailExists", 1);
            return "public/signup";
        }

        // Verificar errores de formulario
        if(result.hasErrors())
        {
            return "public/signup";
        }

        // Registro correcto
        usuario.setTipo("USER"); // Rol "USER" por default
        usuario.setPassword(passEncode.encode(usuario.getPassword())); // Hashear contraseña
        //usuarioService.save(usuario);

        redirect.addFlashAttribute("signupRedirect_email", usuario.getEmail());
        return "redirect:/login";
    }
    

    // Sesión
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
}


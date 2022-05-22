package com.example.ecommerce.controller;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.ecommerce.service.ICompraService;
import com.example.ecommerce.service.IProductoService;
import com.example.ecommerce.service.IUsuarioService;


@Controller
@RequestMapping("/admin")
public class AdminController 
{
    @Autowired
    private IProductoService productoService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private ICompraService compraService;
    

    // Atributos globales
    @ModelAttribute("mainAttributes")
    public void mainAttributes(Model model, HttpSession session)
    {
        Object usuario_id = session.getAttribute("usuario.id");
        Object usuario_tipo = session.getAttribute("usuario.tipo");

        Boolean isLoggedIn = (usuario_id == null) ? (false) : (!usuario_id.toString().equals("0"));
        Boolean isAdmin = (usuario_tipo == null) ? (false) : (usuario_tipo.toString().equals("ADMIN"));

        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("isAdmin", isAdmin);
    }
    
    
    // Muestra todos los productos
    @GetMapping(value = {"", "/"})
    public String index(Model model) 
    {
        model.addAttribute("productos", productoService.all());
        return "admin/index";
    }

    // Muestra todos los usuarios
    @GetMapping("/usuarios")
    public String usuarios(Model model)
    {
        model.addAttribute("usuarios", usuarioService.all());
        return "admin/usuarios";
    }

    // Muestra todas las ordenes
    @GetMapping("/compras")
    public String ordenes(Model model)
    {
        model.addAttribute("compras", compraService.all());
        return "admin/compras";
    }
}

package com.example.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.ecommerce.repository.CompraRepository;
import com.example.ecommerce.repository.ProductoRepository;
import com.example.ecommerce.repository.UsuarioRepository;


@Controller
@RequestMapping("/admin")
public class AdminController 
{
    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CompraRepository compraRepository;
    
    
    // Muestra todos los productos
    @GetMapping(value = {"", "/"})
    public String index(Model model) 
    {
        model.addAttribute("productos", productoRepository.findAll());
        return "admin/index";
    }

    // Muestra todos los usuarios
    @GetMapping("/usuarios")
    public String usuarios(Model model)
    {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "admin/usuarios";
    }

    // Muestra todas las ordenes
    @GetMapping("/compras")
    public String ordenes(Model model)
    {
        model.addAttribute("compras", compraRepository.findAll());
        return "admin/compras";
    }
}

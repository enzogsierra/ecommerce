package com.example.ecommerce.controller;

import com.example.ecommerce.model.Producto;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.ecommerce.service.IProductoService;


@Controller
@RequestMapping("/admin")
public class AdminController 
{
    @Autowired
    private IProductoService productoService;
    
    
    @GetMapping("/")
    public String index(Model model)
    {
        List<Producto> productos = productoService.all();
        
        model.addAttribute("productos", productos);
        return "admin/index";
    }
}

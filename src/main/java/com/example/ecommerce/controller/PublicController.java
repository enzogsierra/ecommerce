package com.example.ecommerce.controller;

import com.example.ecommerce.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
public class PublicController 
{
    @Autowired
    private ProductoService productoService;
    
    
    @GetMapping("/")
    public String index(Model model)
    {
        model.addAttribute("productos", productoService.all());
        return "public/index";
    }
    
    @GetMapping("/producto/{id}")
    public String producto(@PathVariable Integer id, Model model)
    {
        return "public/producto";
    }
}

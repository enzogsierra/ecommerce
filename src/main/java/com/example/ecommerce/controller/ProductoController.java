package com.example.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/productos")
public class ProductoController 
{
    @GetMapping("/")
    public String index(Model model)
    {
        return "productos/index";
    }
}

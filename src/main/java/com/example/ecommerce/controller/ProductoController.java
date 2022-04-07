package com.example.ecommerce.controller;

import com.example.ecommerce.model.Producto;
import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.service.ProductoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/productos")
public class ProductoController 
{
    @Autowired
    private ProductoService productoService;
    
    //
    @GetMapping("")
    public String index(Model model)
    {
        model.addAttribute("productos", productoService.all());
        return "productos/index";
    }
    
    //
    @GetMapping("/crear")
    public String create(Model model)
    {
        return "productos/crear";
    }
    
    @PostMapping("/crear")
    public String create_POST(Producto producto) throws JsonProcessingException
    {
        /*ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(producto);
        System.out.println("Producto = " + json);*/
        
        Usuario usuario = new Usuario(1, "", "", "", "", "", "", "", "");
        producto.setUsuario(usuario);
        productoService.save(producto);
        
        return "redirect:/productos";
    }
    
    //
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) throws JsonProcessingException
    {
        Optional<Producto> opt = productoService.findById(id);
        Producto producto = opt.get();
        
        model.addAttribute("producto", producto);
        return "productos/editar";
    }
    
    @PostMapping("/editar")
    public String editar_POST(Producto producto)
    {  
        productoService.update(producto);
        return "redirect:/productos";
    }
    
    //
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id)
    {
        productoService.delete(id);
        return "redirect:/productos";
    }
}

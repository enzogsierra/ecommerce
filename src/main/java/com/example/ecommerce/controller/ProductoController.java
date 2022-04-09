package com.example.ecommerce.controller;

import com.example.ecommerce.model.Producto;
import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.service.ImageService;
import com.example.ecommerce.service.ProductoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/productos")
public class ProductoController 
{
    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private ImageService image;
    
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
    public String create_POST(Producto producto, @RequestParam("imagenFile") MultipartFile file) throws IOException
    {   
        Usuario usuario = new Usuario(1, "", "", "", "", "", "", "", "");
        producto.setUsuario(usuario);
        
        // Imagen
        String imageName = image.saveImage(file);
        producto.setImagen(imageName);
        
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
    public String editar_POST(Producto producto, @RequestParam("imagen") MultipartFile file) throws IOException
    {  
        Producto tmp = productoService.findById(producto.getId()).get();
        if(file.isEmpty()) // La imagen no cambia
        {
            producto.setImagen(tmp.getImagen());
        }
        else // La imagen cambia
        {
            if(!tmp.getImagen().equals("default.jpg")) // Eliminar la imagen anterior
            {
                image.deleteImage(tmp.getImagen());
            }
            
            String imageName = image.saveImage(file); // Crear la imagen nueva
            producto.setImagen(imageName);
        }
        
        producto.setUsuario(tmp.getUsuario());
        productoService.update(producto);
        return "redirect:/productos";
    }
    
    //
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id)
    {
        Producto tmp = productoService.findById(id).get();
        if(!tmp.getImagen().equals("default.jpg")) // Eliminar si no es una imagen default
        {
            image.deleteImage(tmp.getImagen());
        }
        
        productoService.delete(id);
        return "redirect:/productos";
    }
}

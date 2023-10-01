package com.example.ecommerce.controller;

import com.example.ecommerce.model.Producto;
import com.example.ecommerce.repository.ProductoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.example.ecommerce.service.ImageService;

import javax.validation.Valid;


@Controller
@RequestMapping("/productos")
public class ProductoController 
{
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private ImageService imageService;

    
    // Ruta principal - muestra todos los productos
    @GetMapping(value = {"", "/"}) 
    public String index(Model model)
    {
        model.addAttribute("productos", productoRepository.findAll());
        return "productos/index";
    }

    
    // Crear producto
    @GetMapping("/crear") 
    public String create(Model model)
    {
        model.addAttribute("producto", new Producto());
        return "productos/crear";
    }
    
    @PostMapping("/crear")
    public String create_POST(@Valid Producto producto, BindingResult result, @RequestParam("imagenFile") MultipartFile file, Model model) throws IOException
    {
        if(file.isEmpty()) { // No hay imagen del producto
            result.rejectValue("imagen", "producto.imagen", "Debes incluir una imagen del producto");
        }

        // Verificar errores
        if(result.hasErrors()) {
            return "productos/crear";
        }

        // Crear producto
        String imageName = imageService.saveImage(file);

        producto.setImagen(imageName);
        productoRepository.save(producto);
        return "redirect:/productos";
    }
    

    // Editar producto
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) throws JsonProcessingException
    {
        Optional<Producto> tmp = productoRepository.findById(id);
        if(!tmp.isPresent()) return "redirect:/productos/";

        //
        Producto producto = tmp.get();

        model.addAttribute("producto", producto);
        return "productos/editar";
    }
    
    @PostMapping("/editar")
    public String editar_POST(@Valid Producto producto, BindingResult result, @RequestParam("imagenFile") MultipartFile file) throws IOException
    {
        // Verificar errores
        if(result.hasErrors())
        {
            return "productos/editar";
        }

        // Editar producto
        Producto tmp = productoRepository.findById(producto.getId()).get();

        if(file.isEmpty()) // La imagen no cambia
        {
            producto.setImagen(tmp.getImagen());
        }
        else // La imagen cambia
        {
            if(!tmp.getImagen().equals("default.jpg")) // Eliminar la imagen anterior
            {
                imageService.deleteImage(tmp.getImagen());
            }

            String imageName = imageService.saveImage(file); // Crear la imagen nueva
            producto.setImagen(imageName);
        }

        productoRepository.save(producto);

        return "redirect:/productos";
    }
    

    // Eliminar un producto
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id)
    {
        Optional<Producto> opt = productoRepository.findById(id);
        if(!opt.isPresent()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        Producto producto = opt.get();

        if(!producto.getImagen().equals("default.jpg")) // Eliminar imagen del producto si no usa la imagen default
        {
            //imageService.deleteImage(producto.getImagen());
        }
        
        //productoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}

package com.example.ecommerce.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.ecommerce.model.Producto;
import com.example.ecommerce.repository.CompraRepository;
import com.example.ecommerce.repository.ProductoRepository;
import com.example.ecommerce.repository.UsuarioRepository;
import com.example.ecommerce.service.ImageService;


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

    @Autowired
    private ImageService imageService;
    
    
    // Muestra todos los productos
    @GetMapping("/productos")
    public String index(Model model) 
    {
        model.addAttribute("productos", productoRepository.findAll());
        return "admin/productos";
    }

    // Mostrar form para crear/editar producto
    @GetMapping("/productos/form")
    public String productoForm(@RequestParam(required = false) Integer id, Model model)
    {
        Producto producto = new Producto();

        if(id != null) // Está tratando de editar un producto
        {
            producto = productoRepository.findById(id).orElse(null); // Obtener producto
            if(producto == null) return "redirect:/admin/productos"; // Si el producto no existe, redirigir
        }

        model.addAttribute("producto", producto);
        return "admin/productoForm";
    }

    // POST para crear/editar producto
    @PostMapping("/productos/form")
    public String productoForm(@Valid Producto producto, BindingResult result, @RequestParam MultipartFile imagenFile) throws IOException
    {
        if(producto.getId() == null && imagenFile.isEmpty()) { // Si está creando el producto y no puso una imagen para el mismo, añadir error de validación
            result.rejectValue("imagen", "producto.imagen", "Debes incluir una imagen del producto");
        }

        // Verificar errores
        if(result.hasErrors()) {
            return "admin/productoForm";
        }

        if(producto.getId() == null) // Está creando el producto
        {
            String imageName = imageService.saveImage(imagenFile); // Guardar imagen
            producto.setImagen(imageName); // Establecer la url de la imagen
        }
        else if(producto.getId() != null && !imagenFile.isEmpty()) // Está editando el producto y cambiando la imagen
        {
            imageService.deleteImage(producto.getImagen()); // Eliminar imagen anterior

            String imageName = imageService.saveImage(imagenFile); // Crear una nueva imagen
            producto.setImagen(imageName);
        }

        productoRepository.save(producto);
        return "redirect:/admin/productos";
    }

    // Archivar producto
    @DeleteMapping("/productos/archivar/{id}")
    public ResponseEntity<?> archivarProducto(@PathVariable Integer id)
    {
        Producto producto = productoRepository.findById(id).orElse(null);
        if(producto == null) return ResponseEntity.badRequest().build();

        producto.setArchivado(true);
        productoRepository.save(producto);
        return ResponseEntity.ok().build();
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

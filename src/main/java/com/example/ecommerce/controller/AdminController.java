package com.example.ecommerce.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.ecommerce.model.Categoria;
import com.example.ecommerce.model.Producto;
import com.example.ecommerce.model.Subcategoria;
import com.example.ecommerce.repository.CategoriaRepository;
import com.example.ecommerce.repository.CompraRepository;
import com.example.ecommerce.repository.ProductoRepository;
import com.example.ecommerce.repository.SubcategoriaRepository;
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
    private CategoriaRepository categoriaRepository;

    @Autowired
    private SubcategoriaRepository subcategoriaRepository;

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
        model.addAttribute("categorias", categoriaRepository.findAll(Sort.by("nombre")));
        return "admin/productoForm";
    }

    // POST para crear/editar producto
    @PostMapping("/productos/form")
    public String productoForm(@Valid Producto producto, BindingResult result, @RequestParam MultipartFile imagenFile, Model model) throws IOException
    {
        if(producto.getId() == null && imagenFile.isEmpty()) { // Si está creando el producto y no puso una imagen para el mismo, añadir error de validación
            result.rejectValue("imagen", "producto.imagen", "Debes incluir una imagen del producto");
        }

        // Verificar errores
        if(result.hasErrors()) {
            model.addAttribute("categorias", categoriaRepository.findAll(Sort.by("nombre")));
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



    // Mostrar categorias
    @GetMapping("/categorias")
    public String categorias(Model model)
    {
        List<Categoria> categorias = categoriaRepository.findAll(Sort.by("nombre"));

        model.addAttribute("categorias", categorias);
        return "admin/categorias";
    }

    // POST para crear una categoria
    @PostMapping("/categorias/anadir")
    public @ResponseBody ResponseEntity<?> añadirCategoria(@RequestBody @Valid Categoria categoria, BindingResult result)
    {
        // Verificar errores
        if(result.hasErrors())
        {
            Map<String, String> errors = new HashMap<>(); // Crear un mapeo que almacenará todos los errores de validacion

            for(FieldError error: result.getFieldErrors()) { // Iterar sobre todos los campos que tienen errores de validacion
                errors.put(error.getField(), error.getDefaultMessage()); // Añadir campo y mensaje
            }
            
            return ResponseEntity.badRequest().body(errors); // Retornar un badRequest junto con todos los mensajes de error de validacion
        }

        categoriaRepository.save(categoria);
        return ResponseEntity.ok().build(); 
    }

    // Editar categoria y sus subcategorias
    @GetMapping("/categorias/form")
    public String categoriaForm(@RequestParam(required = false) Integer id, Model model)
    {
        Categoria categoria = new Categoria();

        if(id != null) // Está tratando de editar un producto
        {
            categoria = categoriaRepository.findById(id).orElse(null); // Obtener producto
            if(categoria == null) return "redirect:/admin/productos"; // Si el producto no existe, redirigir
        }

        model.addAttribute("categoria", categoria);
        return "admin/categoriaForm";
    }

    // POST para editar una categoria y sus subcategorias
    @PostMapping("/categorias/form")
    public String categoriaForm(@Valid Categoria categoria, BindingResult result)
    {
        if(result.hasErrors()) {
            return "admin/categoriaForm";
        }

        categoriaRepository.save(categoria);
        return "redirect:/admin/categorias";
    }

    // POST para eliminar una categoría
    @DeleteMapping("/categorias/eliminar/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Integer id)
    {
        Categoria categoria = categoriaRepository.findById(id).orElse(null); // Obtener categoria
        if(categoria == null) return ResponseEntity.badRequest().build(); // Si la categoria no existe, devolver un badrequest

        categoriaRepository.delete(categoria);
        return ResponseEntity.ok().build();
    }


    // POST para crear una subcategoria
    @PostMapping("/subcategorias/anadir")
    public @ResponseBody ResponseEntity<?> añadirSubategoria(@RequestBody @Valid Subcategoria subcategoria, BindingResult result)
    {
        Integer categoriaId = subcategoria.getCategoria().getId(); // Obtener el ID de la categoría
        Categoria categoria = categoriaRepository.findById(categoriaId).orElse(null); // Obtener categoria
        if(categoria == null) return ResponseEntity.notFound().build(); // Si la categoria no existe, retornar mensaje de error

        // Verificar errores
        if(result.hasErrors())
        {
            Map<String, String> errors = new HashMap<>(); // Crear un mapeo que almacenará todos los errores de validacion

            for(FieldError error: result.getFieldErrors()) { // Iterar sobre todos los campos que tienen errores de validacion
                errors.put(error.getField(), error.getDefaultMessage()); // Añadir campo y mensaje
            }
            
            return ResponseEntity.badRequest().body(errors); // Retornar un badRequest junto con todos los mensajes de error de validacion
        }

        subcategoriaRepository.save(subcategoria);
        return ResponseEntity.ok().build(); 
    }

    // Eliminar subcategoria
    @DeleteMapping("/subcategorias/eliminar/{id}")
    public ResponseEntity<?> eliminarSubcategoria(@PathVariable Integer id)
    {
        Subcategoria subcategoria = subcategoriaRepository.findById(id).orElse(null); // Obtener subcategoria
        if(subcategoria == null) return ResponseEntity.badRequest().build(); // Si la subcategoria no existe, devolver un badrequest

        subcategoriaRepository.delete(subcategoria);
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

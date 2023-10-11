package com.example.ecommerce.controller;

import com.example.ecommerce.model.Categoria;
import com.example.ecommerce.model.Domicilio;
import com.example.ecommerce.model.Producto;
import com.example.ecommerce.model.Subcategoria;
import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.repository.CategoriaRepository;
import com.example.ecommerce.repository.DomicilioRepository;
import com.example.ecommerce.repository.ProductoRepository;
import com.example.ecommerce.repository.SubcategoriaRepository;
import com.example.ecommerce.repository.UsuarioRepository;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class PublicController 
{
    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Autowired
    private SubcategoriaRepository subcategoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DomicilioRepository domicilioRepository;

    
    // Home page - muestra todos los productos
    @GetMapping("/")
    public String home(Model model)
    {
        List<Producto> productos = productoRepository.findAll();

        model.addAttribute("productos", productos); 
        return "public/home";
    }

    // Busca un producto por su título
    @GetMapping("/buscar")
    public String buscar(
                            Model model, 
                            @RequestParam(defaultValue = "") String q, 
                            @RequestParam(defaultValue = "ASC") String orden,
                            @RequestParam(required = false) Integer categoria, 
                            @RequestParam(required = false) Integer subcategoria)
    {
        List<Producto> productos = new ArrayList<>();
        Map<String, String> filtros = new HashMap<>(); // Almacena los filtros de busqueda
        Sort sort = Sort.by((orden.equals("DESC") ? (Sort.Direction.DESC) : (Sort.Direction.ASC)), "precioFinal"); // Establece la forma en la que se ordenarán los productos
        

        // Buscar productos segun los filtros
        if(!q.isEmpty()) // Hay un termino de busqueda
        {
            if(categoria != null) // Buscar por categoria y termino
            {
                Optional<Categoria> opt = categoriaRepository.findById(categoria);
                if(opt.isPresent()) 
                {
                    productos = productoRepository.findByCategoriaAndNombreContaining(opt.get(), q, sort);
                    filtros.put("categoria", opt.get().getNombre());
                }
            }
            else if(subcategoria != null) // Buscar por subcategoria y termino
            {
                Optional<Subcategoria> opt = subcategoriaRepository.findById(subcategoria);
                if(opt.isPresent()) 
                {
                    productos = productoRepository.findBySubcategoriaAndNombreContaining(opt.get(), q, sort);
                    filtros.put("subcategoria", opt.get().getNombre());
                }
            }
            else productos = productoRepository.findByNombreContaining(q, sort);
        }
        else // No hay un termino de busqueda
        {
            if(categoria != null) // Buscar por categoria
            {
                Optional<Categoria> opt = categoriaRepository.findById(categoria);
                if(opt.isPresent()) 
                {
                    productos = productoRepository.findByCategoria(opt.get(), sort);
                    filtros.put("categoria", opt.get().getNombre());
                }
            }
            else if(subcategoria != null) // Buscar por subcategoria
            {
                Optional<Subcategoria> opt = subcategoriaRepository.findById(subcategoria);
                if(opt.isPresent()) 
                {
                    productos = productoRepository.findBySubcategoria(opt.get(), sort);
                    filtros.put("subcategoria", opt.get().getNombre());
                }
            }
        }

        model.addAttribute("productos", productos);
        model.addAttribute("filtros", filtros);
        model.addAttribute("categorias", categoriaRepository.findAll(Sort.by("nombre")));
        return "public/buscar";
    }
    
    // Muestra los detalles de un producto específico
    @GetMapping("/productos/{id}")
    public String producto(@PathVariable Integer id, Model model, Principal principal)
    {
        Producto producto = productoRepository.findById(id).get();

        if(principal != null)
        {
            Usuario usuario = usuarioRepository.findByEmail(principal.getName()).get();
            Domicilio domicilio = domicilioRepository.findByUsuarioAndPrincipal(usuario, true).orElse(null); // Buscar el domicilio principal del usuario
            model.addAttribute("domicilio", domicilio);
        }

        model.addAttribute("producto", producto);
        return "public/producto";
    }
}

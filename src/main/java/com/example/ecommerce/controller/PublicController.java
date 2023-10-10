package com.example.ecommerce.controller;

import com.example.ecommerce.model.Domicilio;
import com.example.ecommerce.model.Producto;
import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.repository.CategoriaRepository;
import com.example.ecommerce.repository.DomicilioRepository;
import com.example.ecommerce.repository.ProductoRepository;
import com.example.ecommerce.repository.UsuarioRepository;

import java.security.Principal;
import java.util.List;

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
    public String buscar(@RequestParam(defaultValue = "") String q, Model model)
    {
        if(q.isEmpty()) return "redirect:/";

        List<Producto> productos = productoRepository.search(q);

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriaRepository.findAll(Sort.by("nombre")));
        return "public/buscar";
    }
    
    // Muestra los detalles de un producto específico
    @GetMapping("/producto/{id}")
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

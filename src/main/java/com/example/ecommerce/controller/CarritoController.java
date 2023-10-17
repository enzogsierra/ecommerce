package com.example.ecommerce.controller;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ecommerce.model.Carrito;
import com.example.ecommerce.model.Producto;
import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.repository.CarritoRepository;
import com.example.ecommerce.repository.ProductoRepository;
import com.example.ecommerce.repository.UsuarioRepository;


@Controller
@RequestMapping("/carrito")
public class CarritoController 
{
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private HttpSession session;


    // Mostrar carrito de compras del usuario
    @GetMapping
    public String carrito(Model model, Principal principal)
    {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName()).get(); // Obtener el usuario a través de la sesión
        List<Carrito> carrito = carritoRepository.findByUsuario(usuario); // Obtener el carrito de compras del usuario
        session.setAttribute("carritoSize", carrito.size());

        // Calcular precios, descuentos y unidades
        Double precioTotal = 0.0;
        Double precioFinal = 0.0;
        Integer totalUnidades = 0;

        for(Carrito item: carrito)
        {
            precioTotal += item.calcularPrecioTotal();
            precioFinal += item.calcularPrecioFinal();
            totalUnidades += item.getCantidad();
        }

        model.addAttribute("carrito", carrito);
        model.addAttribute("precioTotal", precioTotal);
        model.addAttribute("precioFinal", precioFinal);
        model.addAttribute("totalUnidades", totalUnidades);
        return "orden/carrito";
    }
    
    // Añade un nuevo producto al carrito
    @PostMapping("/añadir") 
    public String añadir(@RequestParam Integer productoId, @RequestParam int cantidad, Model model, Principal principal)
    {
        Producto producto = productoRepository.findById(productoId).orElse(null); // Buscar producto por su id
        if(producto != null) // El producto existe
        {
            Usuario usuario = usuarioRepository.findByEmail(principal.getName()).get();

            List<Carrito> carrito = carritoRepository.findByUsuario(usuario); // Obtener carrito del usuario
            session.setAttribute("carritoSize", carrito.size());

            final boolean inList = carrito.stream().anyMatch(item -> item.getProducto().getId().equals(productoId)); // Retorna true si el producto ya está añadido en el carrito del usuario
            if(!inList) // Si el producto no está añadido, crear un nuevo item de carrito
            {
                Carrito item = new Carrito();
                item.setUsuario(usuario);
                item.setProducto(producto);
                item.setCantidad(Math.max(1, Math.min(item.getProducto().getStock(), cantidad))); // Establecer la cantidad con un mínimo de 1 y un máximo según el stock del producto
                carritoRepository.save(item); // Agregar producto al carrito del usuario

                session.setAttribute("carritoSize", carrito.size() + 1);
            }
        }
        return "redirect:/carrito";
    }

    // Cambiar la cantidad de un producto
    @PostMapping("/cambiarCantidad/{operation}")
    public Object cambiarCantidad(@PathVariable char operation, @RequestParam Integer productoId, Principal principal)
    {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName()).get();
        List<Carrito> carrito = carritoRepository.findByUsuario(usuario); // Obtener carrito del usuario

        for(Carrito item: carrito) // Iterar sobre cada producto del carrito
        {
            if(item.getProducto().getId().equals(productoId)) // Si el producto.id del carrito coincide con el @RequestParam productoId
            {
                Integer cantidad = (operation == '+') ? (item.getCantidad() + 1) : (item.getCantidad() - 1); // Sumar o restar la cantidad de productos, según la operación
                item.setCantidad(Math.max(1, Math.min(item.getProducto().getStock(), cantidad))); // Establecer la cantidad con un mínimo de 1 y un máximo según el stock del producto
                carritoRepository.save(item);
                
                return "redirect:/carrito";
            }
        }
        return ResponseEntity.badRequest().build(); // Si no se encontró el productoId, retornar un bad request
    }
    
    // Eliminar un producto del carrito de compras del usuario
    @GetMapping("/quitar/{id}")
    public String quitar(@PathVariable Integer id, Principal principal)
    {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName()).get();

        List<Carrito> carrito = carritoRepository.findByUsuario(usuario); // Obtener carrito del usuario
        session.setAttribute("carritoSize", carrito.size());

        for(Carrito item: carrito) // Iterar sobre todos los productos del carrito
        {
            if(item.getId().equals(id)) // Si el id del item coincide con el @PathVarible id
            {
                carritoRepository.delete(item); // Eliminar item del carrito
                session.setAttribute("carritoSize", carrito.size() - 1);
            }
        }
        return "redirect:/carrito";
    }
}

package com.example.ecommerce.controller;

import com.example.ecommerce.model.DetalleOrden;
import com.example.ecommerce.model.Orden;
import com.example.ecommerce.model.Producto;
import com.example.ecommerce.service.ProductoService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class PublicController 
{
    @Autowired
    private ProductoService productoService;
    
    // Almacena los detalles de la orden
    List<DetalleOrden> detalles = new ArrayList<>();
    
    // Datos de la orden
    Orden orden = new Orden();
    
    
    //
    @GetMapping("/")
    public String index(Model model)
    {
        model.addAttribute("productos", productoService.all());
        return "public/index";
    }
    
    
    //
    @GetMapping("/producto/{id}")
    public String producto(@PathVariable Integer id, Model model)
    {
        Optional<Producto> opt = productoService.findById(id);
        Producto producto = opt.get();
        
        model.addAttribute("producto", producto);
        return "public/producto";
    }
    
    //
    @GetMapping("/carrito")
    public String carrito(Model model)
    {
        model.addAttribute("detalles", detalles);
        model.addAttribute("orden", orden);
        return "public/carrito";
    }
    
    @PostMapping("/carrito")
    public String carrito_POST(@RequestParam Integer id, @RequestParam Integer cantidad, Model model)
    {
        DetalleOrden detalleOrden = new DetalleOrden();
        Optional<Producto> opt = productoService.findById(id);
        Producto producto = opt.get();
        double total;
        
        //
        detalleOrden.setNombre(producto.getNombre());
        detalleOrden.setCantidad(cantidad);
        detalleOrden.setPrecio(producto.getPrecio() * cantidad);
        detalleOrden.setProducto(producto);
        detalles.add(detalleOrden);
        
        total = detalles.stream().mapToDouble(dt -> dt.getPrecio()).sum();
        orden.setTotal(total);
        
        return "redirect:/carrito";
    }
    
    
    //
    @GetMapping("/carrito-eliminar/{id}")
    public String removeProductFromCart(@PathVariable Integer id)
    {
        // Lista nueva
        List<DetalleOrden> newList = new ArrayList<>();
        
        for(DetalleOrden detalle: detalles)
        {
            if(detalle.getProducto().getId() != id)
            {
                newList.add(detalle);
            }
        }
        detalles = newList;
        
        double total =  detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();
        orden.setTotal(total);
        return "redirect:/carrito";
    }
}

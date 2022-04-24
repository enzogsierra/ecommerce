package com.example.ecommerce.controller;

import com.example.ecommerce.model.DetalleOrden;
import com.example.ecommerce.model.Orden;
import com.example.ecommerce.model.Producto;
import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.service.IDetalleOrdenService;
import com.example.ecommerce.service.IOrdenService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.ecommerce.service.IProductoService;
import com.example.ecommerce.service.IUsuarioService;
import java.util.Date;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;


@Controller
public class PublicController 
{
    @Autowired
    private IProductoService productoService;
    
    @Autowired
    private IUsuarioService usuarioService;
    
    @Autowired
    private IOrdenService ordenService;
    
    @Autowired
    private IDetalleOrdenService detalleOrdenService;
    
    // Almacena los detalles de la orden
    List<DetalleOrden> detalles = new ArrayList<>();
    
    // Datos de la orden
    Orden orden = new Orden();
   

    // Atributos globales
    @ModelAttribute("mainAttributes")
    public void mainAttributes(Model model, HttpSession session)
    {
        Object usuario_id = session.getAttribute("usuario.id");
        Object usuario_tipo = session.getAttribute("usuario.tipo");

        Boolean isLoggedIn = (usuario_id == null) ? (false) : (!usuario_id.toString().equals("0"));
        Boolean isAdmin = (usuario_tipo == null) ? (false) : (usuario_tipo.toString().equals("ADMIN"));

        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("isAdmin", isAdmin);
    } 

    
    //
    @GetMapping(value = {"", "/"})
    public String index(Model model, HttpSession session)
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
        
        // Verificar que el producto a añadir no esté en el carrito
        boolean isInList = detalles.stream().anyMatch(p -> p.getProducto().getId() == producto.getId());
        if(!isInList)
        {
            detalleOrden.setNombre(producto.getNombre());
            detalleOrden.setCantidad(cantidad);
            detalleOrden.setPrecio(producto.getPrecio() * cantidad);
            detalleOrden.setProducto(producto);
            detalles.add(detalleOrden);

            double total = detalles.stream().mapToDouble(dt -> dt.getPrecio()).sum();
            orden.setTotal(total);
        }
        
        return "redirect:/carrito";
    }
    
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
    
    
    @GetMapping("/orden")
    public String orden(Model model, HttpSession session)
    {
        int userId = Integer.parseInt(session.getAttribute("usuario.id").toString());
        Usuario usuario = usuarioService.findById(userId).get();
        
        model.addAttribute("detalles", detalles);
        model.addAttribute("orden", orden);
        model.addAttribute("usuario", usuario);
        return "public/orden";
    }
    
    @GetMapping("/guardar-orden")
    public String saveOrder(HttpSession session)
    {
        int userId = Integer.parseInt(session.getAttribute("usuario.id").toString());
        Usuario usuario = usuarioService.findById(userId).get();
        
        Date fechaCreacion = new Date();
        orden.setFechaCreacion(fechaCreacion);
        orden.setNumero(ordenService.generateOrderNumber());
        orden.setUsuario(usuario);
        ordenService.save(orden);
        
        // Guardar detalles
        detalles.forEach(d ->
        {
            d.setOrden(orden);
            detalleOrdenService.save(d);
        });
        
        // Limpiar lista y orden
        orden = new Orden();
        detalles.clear();
        return "redirect:/";
    }
    
    
    //
    @PostMapping("/buscar")
    public String buscar(@RequestParam String busqueda, Model model)
    {
        List<Producto> productos = productoService.all().stream().filter(p -> p.getNombre().contains(busqueda)).collect(Collectors.toList());
        
        model.addAttribute("productos", productos);
        model.addAttribute("busqueda", busqueda);
        return "public/index";
    }
}

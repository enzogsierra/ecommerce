package com.example.ecommerce.controller;

import com.example.ecommerce.model.Carrito;
import com.example.ecommerce.model.DetalleOrden;
import com.example.ecommerce.model.Orden;
import com.example.ecommerce.model.Producto;
import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.service.ICarritoService;
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

    @Autowired
    private ICarritoService carritoService;

    
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

    
    // Home page - muestra todos los productos
    @GetMapping(value = {"", "/"})
    public String index(Model model, HttpSession session)
    {
        model.addAttribute("productos", productoService.all()); 
        return "public/index";
    }


    // Busca un producto por su título
    @PostMapping("/buscar")
    public String buscar(@RequestParam String busqueda, Model model)
    {
        List<Producto> productos = productoService.all().stream().filter(p -> p.getNombre().contains(busqueda)).collect(Collectors.toList());
        
        model.addAttribute("productos", productos);
        model.addAttribute("busqueda", busqueda);
        return "public/index";
    }
    
    
    // Muestra los detalles de un producto específico
    @GetMapping("/producto/{id}")
    public String producto(@PathVariable Integer id, Model model)
    {
        Optional<Producto> opt = productoService.findById(id);
        Producto producto = opt.get();
        
        model.addAttribute("producto", producto);
        return "public/producto";
    }
    



    // Muestra el carrito de compras del usuario
    @GetMapping("/carrito")
    public String carrito(Model model, HttpSession session)
    {
        Integer userId = Integer.parseInt(session.getAttribute("usuario.id").toString()); 
        Usuario usuario = usuarioService.findById(userId).get(); // Obtener usuario
        List<Carrito> carrito = carritoService.findByUsuario(usuario); // Obtener el carrito de compras del usuario

        // Calcular total
        double total = 0.0d;
        for(Carrito item: carrito) 
        {
            total += item.getProducto().getPrecio() * item.getCantidad();
        }

        //
        model.addAttribute("carrito", carrito);
        model.addAttribute("total", total);
        return "public/carrito";
    }
    
    @PostMapping("/carrito") // Añade un nuevo producto al carrito
    public String carrito_POST(@RequestParam Integer id, @RequestParam int cantidad, Model model, HttpSession session)
    {
        Integer userId = Integer.parseInt(session.getAttribute("usuario.id").toString());
        Usuario usuario = usuarioService.findById(userId).get();
        Producto producto = productoService.findById(id).get();

        // Crear una nueva instancia de carrito
        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        carrito.setProducto(producto);
        carrito.setCantidad(cantidad);
        carritoService.save(carrito); // Agregar producto al carrito
        
        return "redirect:/carrito";
    }
    
    // Eliminar un producto del carrito de compras del usuario
    @GetMapping("/carrito-eliminar/{id}")
    public String removeProductFromCart(@PathVariable Integer id, HttpSession session)
    {
        Integer userId = Integer.parseInt(session.getAttribute("usuario.id").toString()); 
        Optional<Carrito> item = carritoService.isProductInCart(userId, id); // Buscar producto en el carrito del usuario

        if(item.isPresent()) // Verificar si existe
        {
            carritoService.delete(item.get()); // Eliminar producto del carrito del usuario
        }

        return "redirect:/carrito";
    }
    
    
    // Muestra la orden de los productos a comprar
    @GetMapping("/orden")
    public String orden(Model model, HttpSession session)
    {
        int userId = Integer.parseInt(session.getAttribute("usuario.id").toString());
        Usuario usuario = usuarioService.findById(userId).get();
        List<Carrito> carrito = carritoService.findByUsuario(usuario); // Obtener carrito de compras

        // Calcular total de la compra
        double total = 0.0d;
        for(Carrito item: carrito)
        {
            total += item.getProducto().getPrecio() * item.getCantidad();
        }

        
        model.addAttribute("usuario", usuario);
        model.addAttribute("carrito", carritoService.findByUsuario(usuario));
        model.addAttribute("cantidadProductos", carrito.size());
        model.addAttribute("total", total);
        return "public/orden";
    }
    


    @GetMapping("/guardar-orden") // Comprar productos
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


    // Muestra el historial de compras del usuario
    @GetMapping("/compras")
    public String compras(Model model, HttpSession session)
    {
        // Obtener usuario.id
        Object usuario_id = session.getAttribute("usuario.id");

        if(usuario_id == null) // Verificar que el usuario esté logueado
        {
            return "redirect:/login";
        }

        // Obtener las compras del usuario
        Integer userId = Integer.parseInt(usuario_id.toString());
        Usuario usuario = usuarioService.findById(userId).get();
        List<Orden> ordenes = ordenService.findByUsuario(usuario);

        model.addAttribute("ordenes", ordenes);
        return "public/compras";
    }


    // Muestra los detalles de una compra especifica
    @GetMapping("/detalles/{id}")
    public String detalles(Model model, @PathVariable Integer id)
    {
        Orden orden = ordenService.findById(id).get();

        model.addAttribute("detalles", orden.getDetalle());
        return "public/detalles";
    }
}

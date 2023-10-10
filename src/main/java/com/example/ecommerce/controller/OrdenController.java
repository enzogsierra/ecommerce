package com.example.ecommerce.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ecommerce.model.Carrito;
import com.example.ecommerce.model.Domicilio;
import com.example.ecommerce.model.Envio;
import com.example.ecommerce.model.Orden;
import com.example.ecommerce.model.OrdenItem;
import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.repository.CarritoRepository;
import com.example.ecommerce.repository.DomicilioRepository;
import com.example.ecommerce.repository.OrdenRepository;
import com.example.ecommerce.repository.UsuarioRepository;


@Controller
public class OrdenController
{
    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DomicilioRepository domicilioRepository;
    
    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private HttpSession session;


    // Muestra el historial de ordenes del usuario
    @GetMapping("/compras")
    public String ordenes(Model model, Principal principal)
    {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName()).get(); // Obtener el usuario a través de la sesión

        model.addAttribute("ordenes", ordenRepository.findByUsuario(usuario, Sort.by(Sort.Direction.DESC, "createdAt")));
        return "orden/ordenes";
    }

    // Muestra los detalles de una orden especifica
    @GetMapping("/compras/{id}")
    public String orden(@PathVariable Integer id, Model model, Principal principal)
    {
        Orden orden = ordenRepository.findById(id).orElse(null);
        if(orden == null) return "redirect:/compras"; // Si la orden no existe, redirigir

        Usuario usuario = usuarioRepository.findByEmail(principal.getName()).get();
        if(orden.getUsuario().getId() != usuario.getId() && !usuario.getRole().equals("ADMIN")) { // No es el mismo usuario y no es administrador
            return "redirect:/compras";
        }

        model.addAttribute("orden", orden);
        return "orden/orden";
    }


    // Seleccionar la direccion de envio para la orden
    @GetMapping("/orden/envio")
    public String envio(Model model, Principal principal)
    {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName()).get();
        List<Domicilio> domicilios = domicilioRepository.findByUsuarioOrderByPrincipalDesc(usuario);
        List<Carrito> carrito = carritoRepository.findByUsuario(usuario);

        model.addAttribute("usuario", usuario);
        model.addAttribute("carrito", carrito);
        model.addAttribute("domicilios", domicilios);
        return "orden/envio";
    }

    // Confirmar compra y generar orden
    @PostMapping("/orden/generar")
    public String generar(Model model, RedirectAttributes redirect, Principal principal, @RequestParam(required = false) Integer domicilioId)
    {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName()).get();

        // Verificar si seleccionó un domicilio
        if(domicilioId == null)
        {
            redirect.addFlashAttribute("error", "Debes seleccionar una dirección para el envío");
            return "redirect:/orden/envio";
        }

        // Verificar si el domicilio existe y si pertenece al usuario
        Domicilio domicilio = domicilioRepository.findById(domicilioId).orElse(null);
        if(!(domicilio != null && domicilio.getUsuario().getId().equals(usuario.getId())))
        {
            redirect.addFlashAttribute("error", "Debes seleccionar una dirección para el envío");
            return "redirect:/orden/envio";
        }

        // Verificar que el usuario tenga productos añadidos a su carrito
        List<Carrito> carritoList = carritoRepository.findByUsuario(usuario);
        if(carritoList.size() == 0)
        {
            redirect.addFlashAttribute("error", "No tienes productos añadidos a tu carrito. No puedes hacer una orden");
            return "redirect:/orden/envio";
        }


        // Generar orden
        Orden orden = new Orden();
        Envio envio = new Envio();
        List<OrdenItem> items = new ArrayList<>();

        // Crear objeto de envio
        envio.setCalle(domicilio.getCalle());
        envio.setCodigoPostal(domicilio.getCodigoPostal());
        envio.setEntrecalle1(domicilio.getEntrecalle1());
        envio.setEntrecalle2(domicilio.getEntrecalle2());
        envio.setIndicaciones(domicilio.getIndicaciones());
        envio.setLocalidad(domicilio.getLocalidad());
        envio.setNumeroCalle(domicilio.getNumeroCalle());
        envio.setPiso_dpto(domicilio.getPiso_dpto());
        envio.setOrden(orden);
        
        // Crear lista de items para la orden
        for(Carrito carrito: carritoList)
        {
            OrdenItem item = new OrdenItem();
            item.setProducto(carrito.getProducto());
            item.setCantidad(carrito.getCantidad());
            item.setTotal(item.calcularTotal());
            item.setOrden(orden);
            items.add(item);
        }

        // Crear orden
        orden.setUsuario(usuario);
        orden.setItems(items);
        orden.setTotal(orden.calcularTotal());
        orden.setEnvio(envio);
        ordenRepository.save(orden);

        // Limpiar carrito
        for(Carrito carrito: carritoList) {
            carritoRepository.delete(carrito);
        }
        session.setAttribute("carritoSize", 0);

        // Redirigir
        return "redirect:/";
    }
}

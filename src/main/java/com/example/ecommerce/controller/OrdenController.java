package com.example.ecommerce.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ecommerce.dto.PaymentCreateRequestDTO;
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
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;


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

    @Value("${mercadopago.public.key}")
    private String publicKey;


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
    public String envio(Model model, Principal principal) throws MPException, MPApiException
    {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName()).get();

        // Validar carrito
        List<Carrito> carrito = carritoRepository.findByUsuario(usuario);
        if(carrito.size() == 0) { // No tiene productos dentro del carrito, redirigir
            return "redirect:/carrito";
        }

        List<Domicilio> domicilios = domicilioRepository.findByUsuarioOrderByPrincipalDesc(usuario);
        session.removeAttribute("orden_domicilioId");

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
        model.addAttribute("domicilios", domicilios);
        model.addAttribute("precioTotal", precioTotal);
        model.addAttribute("precioFinal", precioFinal);
        model.addAttribute("totalUnidades", totalUnidades);
        return "orden/envio";
    }


    // Seleccionar los modos de pago
    @PostMapping("/orden/pago")
    public String pago(@RequestParam Integer domicilioId, Model model, Principal principal, RedirectAttributes redirect) throws MPException, MPApiException
    {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName()).get();

        // Verificar carrito
        List<Carrito> carrito = carritoRepository.findByUsuario(usuario);
        if(carrito.size() == 0) { // No tiene productos dentro del carrito, redirigir
            return "redirect:/carrito";
        }

        // Validar domicilio del usuario
        Domicilio domicilio = domicilioRepository.findById(domicilioId).orElse(null);
        if(domicilio == null || !domicilio.getUsuario().equals(usuario))
        {
            redirect.addFlashAttribute("error", "Selecciona una dirección de envío válida");
            return "redirect:/orden/envio";
        }

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
        
        session.setAttribute("orden_domicilioId", domicilio.getId());

        model.addAttribute("publicKey", publicKey);
        model.addAttribute("carrito", carrito);
        model.addAttribute("precioTotal", precioTotal);
        model.addAttribute("precioFinal", precioFinal);
        model.addAttribute("totalUnidades", totalUnidades);
        return "orden/pago";
    }

    @PostMapping("/orden/procesarPago")
    public ResponseEntity<?> procesarPago(@RequestBody PaymentCreateRequestDTO request, Principal principal) throws MPException, MPApiException
    {
        Map<String, Object> response = new HashMap<>();
        Usuario usuario = usuarioRepository.findByEmail(principal.getName()).get();
            
        // Verificar si el domicilio existe y si pertenece al usuario
        Integer domicilioId = (Integer) session.getAttribute("orden_domicilioId");
        Domicilio domicilio = domicilioRepository.findById(domicilioId).orElse(null);
        if(!(domicilio != null && domicilio.getUsuario().getId().equals(usuario.getId())))
        {
            response.put("status", "badRequest");
            response.put("detail", "Selecciona una dirección de envío válida");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Verificar que el usuario tenga productos añadidos a su carrito
        List<Carrito> carritoList = carritoRepository.findByUsuario(usuario);
        if(carritoList.size() == 0)
        {
            response.put("status", "badRequest");
            response.put("detail", "No tienes productos añadidos a tu carrito. No puedes hacer una orden");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        PaymentCreateRequest paymentCreateRequest =
            PaymentCreateRequest.builder()
                .transactionAmount(request.getTransaction_amount())
                .token(request.getToken())
                .description("Compra de productos a Spring eCommerce")
                .installments(request.getInstallments())
                .issuerId(request.getIssuer_id())
                .paymentMethodId(request.getPayment_method_id())
                .payer(PaymentPayerRequest.builder()
                    .email(request.getPayer().getEmail())
                    .identification(IdentificationRequest.builder()
                        .type(request.getPayer().getIdentification().getType())
                        .number(request.getPayer().getIdentification().getNumber())
                        .build())
                    .build())
                .binaryMode(true)
                .build();

        PaymentClient client = new PaymentClient();
        Payment payment = client.create(paymentCreateRequest);
        response.put("status", payment.getStatus());
        response.put("detail", payment.getStatusDetail());

        // Pago aprobado - crear orden 
        if(payment.getStatus().equals("approved"))
        {
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
                item.setPrecioFinal(carrito.calcularPrecioFinal());
                item.setDescuentoTotal(carrito.calcularPrecioTotal() - carrito.calcularPrecioFinal());
                item.setOrden(orden);
                items.add(item);
            }

            // Crear orden
            orden.setUsuario(usuario);
            orden.setItems(items);
            orden.setPrecioFinal(orden.calcularPrecioFinal());
            orden.setDescuentoTotal(orden.calcularDescuentoTotal());
            orden.setEnvio(envio);
            orden.setPaymentId(payment.getId());
            ordenRepository.save(orden);
            
            response.put("ordenId", orden.getId());

            // Limpiar carrito
            for(Carrito carrito: carritoList) {
                carritoRepository.delete(carrito);
            }

            session.setAttribute("carritoSize", 0);
            session.removeAttribute("orden_domicilioId");
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        else // Ocurrio algun error al crear el pago
        {
            response.put("status", payment.getStatus());
            response.put("detail", payment.getStatusDetail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    /*
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
            item.setPrecioFinal(carrito.calcularPrecioFinal());
            item.setDescuentoTotal(carrito.calcularPrecioTotal() - carrito.calcularPrecioFinal());
            item.setOrden(orden);
            items.add(item);
        }

        // Crear orden
        orden.setUsuario(usuario);
        orden.setItems(items);
        orden.setPrecioFinal(orden.calcularPrecioFinal());
        orden.setDescuentoTotal(orden.calcularDescuentoTotal());
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
    */
}

package com.example.ecommerce.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.ecommerce.dto.WebhookNotificationDTO;
import com.example.ecommerce.model.Carrito;
import com.example.ecommerce.model.Domicilio;
import com.example.ecommerce.model.Envio;
import com.example.ecommerce.model.Orden;
import com.example.ecommerce.model.OrdenItem;
import com.example.ecommerce.model.Producto;
import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.repository.CarritoRepository;
import com.example.ecommerce.repository.DomicilioRepository;
import com.example.ecommerce.repository.OrdenRepository;
import com.example.ecommerce.repository.ProductoRepository;
import com.example.ecommerce.repository.UsuarioRepository;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePaymentMethodRequest;
import com.mercadopago.client.preference.PreferencePaymentMethodsRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;


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
    private ProductoRepository productoRepository;

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


    // Mostrar resumen de la orden y generar boton de pago
    @GetMapping("/orden/checkout")
    public String checkout(@RequestParam Integer domicilioId, Model model, Principal principal, RedirectAttributes redirect) throws MPException, MPApiException
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

        // Calcular precios y unidades
        Double precioTotal = 0.0;
        Double precioFinal = 0.0;
        Integer totalUnidades = 0;

        for(Carrito item: carrito)
        {
            precioTotal += item.calcularPrecioTotal();
            precioFinal += item.calcularPrecioFinal();
            totalUnidades += item.getCantidad();
        }

        // Generar Preference (pago)
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();

        List<PreferenceItemRequest> preferenceItems = new ArrayList<>();
        for(Carrito item: carrito)
        {
            PreferenceItemRequest preferenceItem = 
                PreferenceItemRequest.builder()
                    .title(item.getProducto().getNombre())
                    .quantity(item.getCantidad())
                    .unitPrice(new BigDecimal(item.getProducto().getPrecioFinal()))
                    .currencyId("ARS")
                    .pictureUrl(baseUrl + "/images/" + item.getProducto().getImagen())
                    .build();

            preferenceItems.add(preferenceItem);
        }

        // Configurar los back urls
        PreferenceBackUrlsRequest backUrls =
            PreferenceBackUrlsRequest.builder()
                .success(baseUrl + "/compras")
                .pending(baseUrl + "/compras")
                .failure(baseUrl + "/carrito")
                .build();

        // Metadata
        Map<String, Object> metadata = new HashMap<>();
        List<Map<String, Object>> metadataCarrito = new ArrayList<>();

        for(Carrito item: carrito)
        {
            Map<String, Object> metadataItem = new HashMap<>();
            metadataItem.put("producto_id", item.getProducto().getId());
            metadataItem.put("cantidad", item.getCantidad());
            metadataCarrito.add(metadataItem);
        }

        metadata.put("carrito", metadataCarrito);
        metadata.put("domicilio_id", domicilio.getId());
        metadata.put("usuario_id", usuario.getId());

        // Lista de tarjetas de pago excluidas
        List<PreferencePaymentMethodRequest> excludedPaymentMethods = new ArrayList<>();
        String[] excludedCards = {"amex", "argencard", "cmr", "cencosud", "cordobesa", "diners", "tarshop"};
        for(String card: excludedCards)
        {
            excludedPaymentMethods.add(PreferencePaymentMethodRequest.builder().id(card).build());    
        }

        // Construir Preference
        PreferenceRequest preferenceRequest = 
            PreferenceRequest.builder()
                .paymentMethods(
                    PreferencePaymentMethodsRequest.builder()
                        .installments(1)
                        .excludedPaymentMethods(excludedPaymentMethods)
                        .build()
                )
                .items(preferenceItems)
                .metadata(metadata)
                .backUrls(backUrls)
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);


        model.addAttribute("carrito", carrito);
        model.addAttribute("domicilio", domicilio);
        model.addAttribute("precioTotal", precioTotal);
        model.addAttribute("precioFinal", precioFinal);
        model.addAttribute("totalUnidades", totalUnidades);
        model.addAttribute("publicKey", publicKey);
        model.addAttribute("preferenceId", preference.getId());
        return "orden/checkout";
    }
    

    // Endpoint donde MercadoPago enviara sus Webhooks (notificaciones de pagos)
    @CrossOrigin(origins = "https://api.mercadopago.com")
    @PostMapping("/mercadopago/webhook")
    public @ResponseBody ResponseEntity<?> webhookNotification(@RequestBody WebhookNotificationDTO request) throws MPException, MPApiException
    {
        if(request.getType().equals("payment"))
        {
            Long paymentId = Long.parseLong(request.getData().getId());
            PaymentClient client = new PaymentClient();
            Payment payment = client.get(paymentId);
            
            if(payment.getStatus().equals("approved")) // Pago aprobado, generar orden
            {
                Map<String, Object> metadata = payment.getMetadata();
                System.out.println(metadata);

                // Obtener valores del metadata
                Double d_usuarioId = Double.parseDouble(metadata.get("usuario_id").toString());
                Double d_domicilioId = Double.parseDouble(metadata.get("domicilio_id").toString());
                
                Integer usuarioId = d_usuarioId.intValue();
                Integer domicilioId = d_domicilioId.intValue();
                @SuppressWarnings("unchecked") List<Map<String, Object>> carritoMetadata = (List<Map<String, Object>>) metadata.get("carrito");
                
                // Obtener entidades
                Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
                Domicilio domicilio = domicilioRepository.findById(domicilioId).orElse(null);
                List<Carrito> carritoList = new ArrayList<>();

                for(Map<String, Object> metadataItem: carritoMetadata)
                {
                    Double d_productoId = Double.parseDouble(metadataItem.get("producto_id").toString());
                    Double d_cantidad = Double.parseDouble(metadataItem.get("cantidad").toString());

                    Integer productoId = d_productoId.intValue();
                    Integer cantidad = d_cantidad.intValue();
                    Producto producto = productoRepository.findById(productoId).orElse(null);

                    Carrito item = new Carrito();
                    item.setProducto(producto);
                    item.setCantidad(cantidad);
                    carritoList.add(item);
                }

                // Generar orden
                Orden orden = new Orden();
                Envio envio = new Envio();
                List<OrdenItem> items = new ArrayList<>();

                // Envio
                envio.setCalle(domicilio.getCalle());
                envio.setCodigoPostal(domicilio.getCodigoPostal());
                envio.setEntrecalle1(domicilio.getEntrecalle1());
                envio.setEntrecalle2(domicilio.getEntrecalle2());
                envio.setIndicaciones(domicilio.getIndicaciones());
                envio.setLocalidad(domicilio.getLocalidad());
                envio.setNumeroCalle(domicilio.getNumeroCalle());
                envio.setPiso_dpto(domicilio.getPiso_dpto());
                envio.setOrden(orden);
                
                // Lista de items de la orden
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

                // Limpiar carrito del usuario
                List<Carrito> carrito = carritoRepository.findByUsuario(usuario);
                for(Carrito item: carrito)
                {
                    carritoRepository.delete(item);
                }
            }
        }
        return ResponseEntity.ok().build();
    }
}

package com.example.ecommerce.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.ecommerce.dto.UsuarioDTO;
import com.example.ecommerce.model.Domicilio;
import com.example.ecommerce.model.Localidad;
import com.example.ecommerce.model.Provincia;
import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.repository.DomicilioRepository;
import com.example.ecommerce.repository.LocalidadRepository;
import com.example.ecommerce.repository.ProvinciaRepository;
import com.example.ecommerce.repository.UsuarioRepository;


@Controller
@RequestMapping("/perfil")
public class PerfilController 
{
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DomicilioRepository domicilioRepository;

    @Autowired
    private ProvinciaRepository provinciaRepository;

    @Autowired
    private LocalidadRepository localidadRepository;


    // Mostrar datos de la cuenta del usuario
    @GetMapping
    public String perfil(Model model, Principal principal)
    {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName()).get();
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNombre(usuario.getNombre());
        usuarioDTO.setApellido(usuario.getApellido());
        usuarioDTO.setEmail(usuario.getEmail());
        usuarioDTO.setTelefono(usuario.getTelefono());

        model.addAttribute("usuarioDTO", usuarioDTO);
        return "perfil/index";
    }

    // Editar datos del usuario
    @PostMapping("/form")
    public String perfil(@Valid UsuarioDTO usuarioDTO, BindingResult result, Principal principal)
    {
        if(result.hasErrors()) {
            return "perfil/index";
        }

        Usuario usuario = usuarioRepository.findByEmail(principal.getName()).get();
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellido(usuarioDTO.getApellido());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setTelefono(usuarioDTO.getTelefono());

        usuarioRepository.save(usuario);
        return "redirect:/perfil";
    }

    // Mostrar los domicilios del usuario
    @GetMapping("/domicilios")
    public String domicilios(Model model, Principal principal)
    {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName()).get();
        List<Domicilio> domicilios = domicilioRepository.findByUsuarioOrderByPrincipalDesc(usuario);

        model.addAttribute("domicilios", domicilios);
        return "perfil/domicilios";
    }

    // Mostrar form para crear/editar un domicilio
    @GetMapping("/domicilios/form")
    public String domicilioForm(@RequestParam(required = false) Integer id, Model model, Principal principal)
    {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName()).get();
        Domicilio domicilio = new Domicilio();

        if(id != null) // El usuario quiere editar un domicilio - verificaremos si ese domicilio pertenece al usuario
        {
            Optional<Domicilio> opt = domicilioRepository.findById(id); // Buscar domicilio por el id
            if(opt.isPresent()) // El domicilio existe
            { 
                if(opt.get().getUsuario().getId().equals(usuario.getId())) domicilio = opt.get(); // El domicilio pertenece al usuario
                else return "redirect:/perfil/domicilios"; // El domicilio no pertenece al usuario, redirigir
            }
            else return "redirect:/perfil/domicilios"; // Si el domicilio no existe, redirigir
        }

        model.addAttribute("domicilio", domicilio);
        model.addAttribute("provincias", provinciaRepository.findAll());
        return "perfil/domicilioForm";
    }

    // Guardar informacion de un domicilio
    @PostMapping("/domicilios/form")
    public String domicilioForm(@Valid Domicilio domicilio, BindingResult result, Model model, Principal principal)
    {
        // Verificar errores
        if(result.hasErrors())
        {
            model.addAttribute("provincias", provinciaRepository.findAll());
            return "perfil/domicilioForm";
        }

        Usuario usuario = usuarioRepository.findByEmail(principal.getName()).get();
        List<Domicilio> domicilios = domicilioRepository.findByUsuario(usuario); // Obtenemos una lista con los domicilios del usuario

        // Si el usuario elige este domicilio como el domicilio principal/predeterminado, entonces buscamos sus otros domicilios y le quitamos la propiedad "principal"
        if(domicilio.getPrincipal() == true) 
        {
            for(Domicilio item: domicilios) // Iteramos sobre cada domicilio
            {
                if(!item.getId().equals(domicilio.getId())) // Si la ID del domicilio iterado no es igual a la ID del domicilio que está siendo guardando
                {
                    item.setPrincipal(false); // Quitar como domicilio principal
                    domicilioRepository.save(item); // Guardar
                }
            }
        }
        if(domicilios.size() == 0) domicilio.setPrincipal(true); // Si es el primer domicilio que agrega el usuario, entonces hacerlo el domicilio principal por default

        domicilio.setUsuario(usuario);
        domicilioRepository.save(domicilio); 
        return "redirect:/perfil/domicilios";
    }

    // Eliminar un domicilio
    @DeleteMapping("/domicilios/eliminar/{id}")
    public @ResponseBody ResponseEntity<?> eliminar(@PathVariable Integer id, Principal principal)
    {
        Optional<Domicilio> opt = domicilioRepository.findById(id); // Obtener domicilio segun el id
        
        if(!opt.isPresent()) return ResponseEntity.badRequest().build(); // El domicilio no existe
        
        Usuario usuario = usuarioRepository.findByEmail(principal.getName()).get();
        Domicilio domicilio = opt.get();

        if(!domicilio.getUsuario().getId().equals(usuario.getId())) return ResponseEntity.badRequest().build(); // El domicilio no le pertenece al usuario
        if(domicilio.getPrincipal() == true) return ResponseEntity.badRequest().build(); // El domicilio es el domicilio principal (no se pueden eliminar)
        
        domicilioRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Obtener la lista de localidades según la provincia
    @PostMapping("/domicilios/getLocalidades")
    public @ResponseBody ResponseEntity<?> getLocalidades(@RequestParam @NotNull Integer provinciaId)
    {
        Optional<Provincia> provincia = provinciaRepository.findById(provinciaId); // Obtener la provincia segun su id
        if(!provincia.isPresent()) return ResponseEntity.badRequest().build(); // La provincia no existe

        List<Localidad> localidades = localidadRepository.findByProvincia(provincia.get()); // Obtener la lista de localidades
        return ResponseEntity.ok().body(localidades); // Devolver un OK con la lista de localidades
    }
}

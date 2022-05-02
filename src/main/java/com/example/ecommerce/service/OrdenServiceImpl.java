package com.example.ecommerce.service;

import com.example.ecommerce.model.Orden;
import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.repository.OrdenRepository;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OrdenServiceImpl implements IOrdenService
{
    @Autowired
    private OrdenRepository ordenRepository;
    
    
    @Override
    public List<Orden> all() {
        return ordenRepository.findAll();
    }
    
    @Override
    public void save(Orden orden) {
        ordenRepository.save(orden);
    }


    @Override
    public Optional<Orden> findById(int id)
    {
        return ordenRepository.findById(id);
    }
    
    @Override
    public List<Orden> findByUsuario(Usuario usuario)
    {
        return ordenRepository.findByUsuario(usuario);
    }
    
    
    @Override
    public String generateOrderNumber() // Función para añadir 0s delante de un numero hasta llegar a 10 digitos (ej: 0000000001, 0000060327)
    {
        int n;
        List<Orden> ordenes = all();
        
        if(ordenes.isEmpty()) n = 1; // Si aún no hay ordenes, dar el id 1
        else
        { 
            List<Integer> numbers = new ArrayList<>();
            
            ordenes.stream().forEach(orden -> numbers.add(Integer.parseInt(orden.getNumero())));
            n = numbers.stream().max(Integer::compare).get() + 1;
        }
        
        String str = new DecimalFormat("0000000000").format(n);
        return str;
    }
}

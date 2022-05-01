package com.example.ecommerce.service;

import com.example.ecommerce.model.Orden;
import com.example.ecommerce.model.Usuario;

import java.util.List;
import java.util.Optional;


public interface IOrdenService 
{
    List<Orden> all();
    void save(Orden orden);

    Optional<Orden> findById(int id);
    List<Orden> findByUsuario(Usuario usuario);
    
    String generateOrderNumber();
}

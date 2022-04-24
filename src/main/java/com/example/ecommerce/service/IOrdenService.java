package com.example.ecommerce.service;

import com.example.ecommerce.model.Orden;
import com.example.ecommerce.model.Usuario;

import java.util.List;


public interface IOrdenService 
{
    List<Orden> all();
    void save(Orden orden);

    List<Orden> findByUsuario(Usuario usuario);
    
    String generateOrderNumber();
}

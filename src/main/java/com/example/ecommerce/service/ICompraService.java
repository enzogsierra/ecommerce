package com.example.ecommerce.service;

import java.util.List;
import java.util.Optional;

import com.example.ecommerce.model.Compra;
import com.example.ecommerce.model.Usuario;

public interface ICompraService 
{
    List<Compra> all();
    Optional<Compra> findById(Integer id);
    List<Compra> findByUsuario(Usuario usuario);

    void save(Compra compra);
    void update(Compra compra);
    void delete(Compra compra);
}

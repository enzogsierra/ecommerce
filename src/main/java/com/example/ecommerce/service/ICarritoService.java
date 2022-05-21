package com.example.ecommerce.service;

import java.util.List;
import java.util.Optional;

import com.example.ecommerce.model.Carrito;
import com.example.ecommerce.model.Usuario;


public interface ICarritoService 
{
    List<Carrito> all();
    Optional<Carrito> findById(int id);
    List<Carrito> findByUsuario(Usuario usuario);
    Optional<Carrito> isProductInCart(Integer userId, Integer productId);

    void save(Carrito carrito);
    void update(Carrito carrito);
    void delete(Carrito carrito);
}

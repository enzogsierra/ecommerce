package com.example.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import com.example.ecommerce.model.Carrito;
import com.example.ecommerce.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ecommerce.model.Producto;


public interface CarritoRepository extends JpaRepository<Carrito, Integer> 
{
    List<Carrito> findByUsuario(Usuario usuario);

    Optional<Carrito> findByUsuarioAndProducto(Usuario usuario, Producto producto);
}

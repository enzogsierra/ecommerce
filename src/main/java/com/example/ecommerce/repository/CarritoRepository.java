package com.example.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import com.example.ecommerce.model.Carrito;
import com.example.ecommerce.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.ecommerce.model.Producto;




@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Integer> 
{
    List<Carrito> findByUsuario(Usuario usuario);

    Optional<Carrito> findByUsuarioAndProducto(Usuario usuario, Producto producto);
}

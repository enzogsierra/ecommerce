package com.example.ecommerce.repository;

import java.util.List;

import com.example.ecommerce.model.Orden;
import com.example.ecommerce.model.Usuario;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrdenRepository extends JpaRepository<Orden, Integer>
{
    List<Orden> findByUsuario(Usuario usuario);
    List<Orden> findByUsuario(Usuario usuario, Sort sort);
}

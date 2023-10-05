package com.example.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerce.model.Domicilio;
import com.example.ecommerce.model.Usuario;

import java.util.List;
import java.util.Optional;


public interface DomicilioRepository extends JpaRepository<Domicilio, Integer> 
{
    List<Domicilio> findByUsuario(Usuario usuario);
    List<Domicilio> findByUsuarioOrderByPrincipalDesc(Usuario usuario);

    Optional<Domicilio> findByUsuarioAndPrincipal(Usuario usuario, Boolean principal);
}

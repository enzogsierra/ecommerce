package com.example.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerce.model.Localidad;
import com.example.ecommerce.model.Provincia;


public interface LocalidadRepository extends JpaRepository<Localidad, Integer> 
{
    List<Localidad> findByProvincia(Provincia provincia);
}

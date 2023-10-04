package com.example.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerce.model.Provincia;


public interface ProvinciaRepository extends JpaRepository<Provincia, Integer> {
    
}

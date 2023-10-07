package com.example.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerce.model.Subcategoria;


public interface SubcategoriaRepository extends JpaRepository<Subcategoria, Integer> {
    
}

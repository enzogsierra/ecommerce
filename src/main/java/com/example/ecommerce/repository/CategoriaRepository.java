package com.example.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerce.model.Categoria;


public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    
}

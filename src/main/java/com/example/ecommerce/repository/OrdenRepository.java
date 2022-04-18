package com.example.ecommerce.repository;

import com.example.ecommerce.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OrdenRepository extends JpaRepository<Orden, Integer>
{
    
}

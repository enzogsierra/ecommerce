package com.example.ecommerce.repository;

import com.example.ecommerce.model.DetalleOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Integer>
{
    
}

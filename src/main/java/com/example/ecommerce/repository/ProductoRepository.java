package com.example.ecommerce.repository;

import java.util.List;

import com.example.ecommerce.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer>
{
    @Query(value = "SELECT * FROM productos WHERE nombre LIKE %:term%", nativeQuery = true)
    List<Producto> search(String term);
}

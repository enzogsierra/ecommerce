package com.example.ecommerce.repository;

import java.util.List;

import com.example.ecommerce.model.Categoria;
import com.example.ecommerce.model.Producto;
import com.example.ecommerce.model.Subcategoria;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductoRepository extends JpaRepository<Producto, Integer>
{
    List<Producto> findByCategoria(Categoria categoria, Sort sort);
    List<Producto> findBySubcategoria(Subcategoria subcategoria, Sort sort);

    List<Producto> findByNombreContaining(String nombre, Sort sort);
    List<Producto> findByCategoriaAndNombreContaining(Categoria categoria, String nombre, Sort sort);
    List<Producto> findBySubcategoriaAndNombreContaining(Subcategoria categoria, String nombre, Sort sort);
}

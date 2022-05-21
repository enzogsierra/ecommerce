package com.example.ecommerce.service;

import com.example.ecommerce.model.Producto;
import java.util.List;
import java.util.Optional;


public interface IProductoService 
{
    List<Producto> all();
    Optional<Producto> findById(int id);
    List<Producto> search(String term);
    
    void save(Producto producto);
    void update(Producto producto);
    void delete(int id);
}

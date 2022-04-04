package com.example.ecommerce.service;

import com.example.ecommerce.model.Producto;
import java.util.List;
import java.util.Optional;


public interface ProductoService 
{
    public List<Producto> all();
    public Optional<Producto> findById(int id);
    
    public void save(Producto producto);
    public void update(Producto producto);
    public void delete(int id);
}

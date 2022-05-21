package com.example.ecommerce.service;

import com.example.ecommerce.model.Producto;
import com.example.ecommerce.repository.ProductoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ProductoServiceImpl implements IProductoService
{
    @Autowired
    private ProductoRepository productoRepository;
    

    @Override
    public List<Producto> all() {
        return productoRepository.findAll();
    }

    @Override
    public Optional<Producto> findById(int id) {
        return productoRepository.findById(id);
    }

    @Override
    public List<Producto> search(String term) {
        return productoRepository.search(term);
    }

    @Override
    public void save(Producto producto) {
        productoRepository.save(producto);
    }

    @Override
    public void update(Producto producto) {
        productoRepository.save(producto);
    }

    @Override
    public void delete(int id) {
        productoRepository.deleteById(id);
    }
}

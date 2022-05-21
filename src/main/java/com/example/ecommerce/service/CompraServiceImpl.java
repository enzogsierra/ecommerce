package com.example.ecommerce.service;

import java.util.List;
import java.util.Optional;

import com.example.ecommerce.model.Compra;
import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.repository.CompraRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CompraServiceImpl implements ICompraService
{
    @Autowired
    private CompraRepository compraRepository;


    @Override
    public List<Compra> all() {
        return compraRepository.findAll();
    }

    @Override
    public Optional<Compra> findById(Integer id) {
        return compraRepository.findById(id);
    }

    @Override
    public List<Compra> findByUsuario(Usuario usuario) {
        return compraRepository.findByUsuario(usuario);
    }

    @Override
    public void save(Compra compra) {
        compraRepository.save(compra);
    }

    @Override
    public void update(Compra compra) {
        compraRepository.save(compra);
    }
    
    @Override
    public void delete(Compra compra) {
        compraRepository.delete(compra);
    }
}

package com.example.ecommerce.service;

import com.example.ecommerce.model.Orden;
import com.example.ecommerce.repository.OrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OrdenServiceImpl implements IOrdenService
{
    @Autowired
    private OrdenRepository ordenRepository;
    
    @Override
    public void save(Orden orden) {
        ordenRepository.save(orden);
    }
}

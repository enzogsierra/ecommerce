package com.example.ecommerce.service;

import com.example.ecommerce.model.DetalleOrden;
import com.example.ecommerce.repository.DetalleOrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DetalleOrdenServiceImpl implements IDetalleOrdenService
{
    @Autowired
    private DetalleOrdenRepository detalleOrdenRepository;
    
    @Override
    public void save(DetalleOrden detalleOrden) {
        detalleOrdenRepository.save(detalleOrden);
    }    
}

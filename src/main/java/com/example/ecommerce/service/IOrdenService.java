package com.example.ecommerce.service;

import com.example.ecommerce.model.Orden;
import java.util.List;


public interface IOrdenService 
{
    List<Orden> all();
    void save(Orden orden);
    
    String generateOrderNumber();
}

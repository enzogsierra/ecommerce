package com.example.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.ecommerce.model.Categoria;
import com.example.ecommerce.repository.CategoriaRepository;


@ControllerAdvice
public class GlobalController
{
    @Autowired
    private CategoriaRepository categoriaRepository;

    @ModelAttribute("categorias")
    public List<Categoria> categorias()
    {
        List<Categoria> categorias = categoriaRepository.findAll(Sort.by("nombre"));
        return categorias;
    }
}

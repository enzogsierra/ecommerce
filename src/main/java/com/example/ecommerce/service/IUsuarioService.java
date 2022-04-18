package com.example.ecommerce.service;

import com.example.ecommerce.model.Usuario;
import java.util.List;
import java.util.Optional;


public interface IUsuarioService 
{
    public List<Usuario> all();
    public Optional<Usuario> findById(int id);
    
    public void save(Usuario usuario);
    public void update(Usuario usuario);
    public void delete(int id);
}

package com.example.ecommerce;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.repository.UsuarioRepository;


@DataJpaTest
public class UsuarioTest 
{
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Crea un usuario correctamente, con todas las propiedades requeridas, pasando todas las validaciones")
    public void crearUsuarioConCamposRequeridos()
    {
        Usuario usuario = new Usuario();
        usuario.setNombre("Enzo");
        usuario.setApellido("Sierra");
        usuario.setEmail("test@mail.com");
        usuario.setTelefono("-");

        try 
        {
            usuarioRepository.save(usuario);
            assertTrue("El usuario pasó todas las validaciones y se creó correctamente", usuario.getId() != null);
        } 
        catch (ValidationException e) 
        {
            fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("Falla al crear un usuario ya que no completa todas las validaciones (faltan propiedades requeridas)")
    public void crearUsuarioSinCamposRequeridos()
    {
        Usuario usuario = new Usuario();
        // usuario.setNombre("Enzo");
        usuario.setApellido("Sierra");
        usuario.setEmail("test@mail.com");
        usuario.setTelefono("-");

        try
        {
            usuarioRepository.save(usuario);
            if(usuario.getId() != null) fail("El usuario no debería crearse ya que tiene propiedades requeridas que están vacías");
        }
        catch(ValidationException e)
        {
            assertTrue("La creación del usuario falló como se esperaba", usuario.getId() == null);
        }
    }

    @Test
    @DisplayName("Falla al crear un usuario ya que el email dado no es un email válido")
    public void crearUsuarioConEmailInvalido()
    {
        Usuario usuario = new Usuario();
        usuario.setNombre("Enzo");
        usuario.setApellido("Sierra");
        usuario.setEmail("email-no-valido");
        usuario.setTelefono("-");

        try
        {
            usuarioRepository.save(usuario);
            if(usuario.getId() != null) fail("El usuario no debería crearse ya que su email no es un email válido");
        }
        catch(ValidationException e)
        {
            assertTrue("La creación del usuario con email inválido falló como se esperaba", usuario.getId() == null);
        }
    }
}

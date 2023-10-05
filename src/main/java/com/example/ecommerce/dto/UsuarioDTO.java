package com.example.ecommerce.dto;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@MappedSuperclass
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class UsuarioDTO 
{
    @Column(length = 64)
    @NotBlank(message = "Debes ingresar tu nombre")
    @Size(min = 3, max = 64, message = "El nombre debe tener entre {min} y {max} caracteres")
    private String nombre;

    @Column(length = 64)
    @NotBlank(message = "Debes ingresar tu apellido")
    @Size(min = 3, max = 64, message = "El apellido debe tener entre {min} y {max} caracteres")
    private String apellido;

    @NotBlank(message = "Debes especificar una dirección de correo electrónico")
    @Email(message = "El correo electrónico ingresado no es válido")
    private String email;

    @Column(length = 15) // Máximo de caracteres de un número de celular
    @NotBlank(message = "Debes especificar un número de teléfono")
    @Size(max = 15, message = "El número de teléfono no puede superar los {max} caracteres")
    private String telefono;   
}

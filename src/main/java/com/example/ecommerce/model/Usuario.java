package com.example.ecommerce.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "usuarios")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Usuario 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 64)
    @Size(min = 3, max = 64, message = "El nombre debe tener entre {min} y {max} caracteres")
    private String nombre;

    @Column(length = 64)
    @Size(min = 3, max = 64, message = "El apellido debe tener entre {min} y {max} caracteres")
    private String apellido;

    @NotBlank(message = "Debes especificar tu dirección de correo electrónico")
    @Email(message = "El correo electrónico ingresado no es válido")
    private String email;

    @Column(length = 62) // 62 caracteres debido al hash
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 3, message = "La contraseña debe contener al menos {min} caracteres")
    private String password;

    @Column(length = 15) // Máximo de caracteres de un número de celular
    @Size(max = 15, message = "El número de teléfono no puede superar los {max} caracteres")
    private String telefono;

    @Column(length = 255)
    @Size(max = 255, message = "La dirección no puede superar los {max} caracteres")
    private String direccion;

    private String role = "USER";
}

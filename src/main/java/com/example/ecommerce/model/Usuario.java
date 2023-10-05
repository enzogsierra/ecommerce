package com.example.ecommerce.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.example.ecommerce.dto.UsuarioDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "usuarios")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter @ToString
public class Usuario extends UsuarioDTO
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 3, message = "La contraseña debe contener al menos {min} caracteres")
    private String password;

    private String role = "USER";
}

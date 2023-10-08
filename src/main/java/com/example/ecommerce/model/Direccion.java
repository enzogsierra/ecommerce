package com.example.ecommerce.model;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@MappedSuperclass
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public abstract class Direccion 
{
    @ManyToOne
    @NotNull(message = "Debes asignar una localidad a este domicilio")
    private Localidad localidad;

    @NotNull(message = "Debes indicar el código postal")
    @Min(value = 0, message = "El código postal no puede ser menor a {value}")
    private Integer codigoPostal;

    @NotBlank(message = "Debes ingresar el nombre de la calle")
    private String calle;

    @NotNull(message = "Debes ingresar un número de calle")
    @Min(value = 0, message = "El número de calle no puede ser menor a {value}")
    private Integer numeroCalle;

    private String entrecalle1;
    private String entrecalle2;

    @Column(length = 20)
    @Size(max = 20, message = "El piso/departamento no puede tener más de {max} caracteres")
    private String piso_dpto;

    private String indicaciones;
}

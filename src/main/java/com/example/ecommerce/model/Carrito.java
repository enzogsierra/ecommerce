package com.example.ecommerce.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "carritos")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Carrito 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @NotNull(message = "No hay un usuario asociado a este item")
    private Usuario usuario;

    @ManyToOne
    @NotNull(message = "No hay un producto asociado a este item")
    private Producto producto;
    
    @Min(value = 1, message = "Debes agregar al menos {value} producto")
    private Integer cantidad;
}

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
@Table(name = "orden_item")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class OrdenItem 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @NotNull(message = "Debes asociar una orden a este item")
    private Orden orden;

    @ManyToOne
    @NotNull(message = "Debes asociar un producto a este item")
    private Producto producto;

    @NotNull(message = "Debes indicar la cantidad de productos")
    @Min(value = 1, message = "La cantidad de productos no puede ser menor a {value}")
    private Integer cantidad;

    @NotNull(message = "Debes indicar el total de este item")
    @Min(value = 1, message = "El total de este item no puede ser menor a $ {value}")
    private Double total;


    // Funcion para calcular el total de este item
    public Double calcularTotal() {
        return this.producto.getPrecio() * this.cantidad;
    }
}

package com.example.ecommerce.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "ordenes")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Orden
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @NotNull(message = "No hay un usuario asociado a esta compra")
    private Usuario usuario;

    @OneToOne(mappedBy = "orden", cascade = CascadeType.ALL)
    private Envio envio;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL)
    @Size(min = 1, message = "Debes agregar al menos 1 producto para generar esta orden")
    private List<OrdenItem> items = new ArrayList<>();

    @NotNull(message = "Debes indicar el precio final de la orden")
    @Min(value = 1, message = "El precio final de la orden no puede ser menor a $ {value}")
    private Double precioFinal;

    @Min(value = 0, message = "El descuento total de la orden no puede ser menor a $ {value}")
    private Double descuentoTotal;

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;


    // Sumar el precio de cada item
    public Double calcularPrecioFinal()
    {
        Double total = 0.0;
        for(OrdenItem item: items) {
            total += item.getPrecioFinal();
        }
        return total;
    }

    // Sumar el precio de cada item
    public Double calcularDescuentoTotal()
    {
        Double total = 0.0;
        for(OrdenItem item: items) {
            total += item.getDescuentoTotal();
        }
        return total;
    }

    // Funcion para obtener la cantidad de productos (sumar la cantidad de todos los items)
    public int calcularUnidades()
    {
        int cantidad = 0;
        for(OrdenItem item: items) {
            cantidad += item.getCantidad();
        }
        return cantidad;
    }
}

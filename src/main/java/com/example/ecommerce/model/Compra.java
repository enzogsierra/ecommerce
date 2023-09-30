package com.example.ecommerce.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "compras")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Compra 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @NotNull(message = "No hay un usuario asociado a esta compra")
    private Usuario usuario;

    @ManyToOne
    @NotNull(message = "No hay un producto asociado a esta compra")
    private Producto producto;

    @Min(value = 1, message = "Debes agregar al menos {value} producto/s")
    private Integer cantidad;

    @NotNull(message = "El precio no puede estar vacío")
    @Min(value = 1, message = "El precio no puede ser menor a $ {min}")
    private Double precio;

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;
}

package com.example.ecommerce.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Where;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "productos")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@Where(clause = "archivado != 1")
public class Producto 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 255)
    @Size(min = 16, max = 255, message = "El nombre del producto debe tener entre {min} y {max} caracteres")
    private String nombre;

    @Column(length = 2048)
    @Size(min = 128, max = 2048, message = "La descripción del producto debe tener entre {min} y {max} caracteres")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "Debes asignar una categoría a este producto")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    private Subcategoria subcategoria;

    @Column(length = 41) // 36 caracteres por el randomUUID + extension (.jpg, .webp)
    private String imagen;

    @NumberFormat(pattern = "#,##0", style = Style.CURRENCY)
    @NotNull(message = "Debes indicar el precio de este producto")
    @Min(value = 1, message = "El precio del producto debe ser mayor a ${value}")
    private Double precio;

    @NotNull(message = "Debes indicar el stock de este producto")
    @Min(value = 0, message = "El stock no puede ser menor a {value}")
    private Integer stock;

    @Column(columnDefinition = "BIT DEFAULT 0")
    private boolean archivado = false;
}

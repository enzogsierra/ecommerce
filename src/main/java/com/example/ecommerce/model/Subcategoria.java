package com.example.ecommerce.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subcategorias")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Subcategoria 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre de la subcategoría no puede estar vacío")
    private String nombre;

    @ManyToOne
    @NotNull(message = "Debes asociar una categoría a esta subcategoría")
    private Categoria categoria;
}

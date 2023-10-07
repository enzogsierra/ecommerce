package com.example.ecommerce.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "categorias")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Categoria 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotBlank(message = "El nombre de la categoría no puede estar vacío")
    private String nombre;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL)
    @Valid
    @OrderBy("nombre ASC")
    private List<Subcategoria> subcategorias = new ArrayList<>();
}

package com.example.ecommerce.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "domicilios")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Domicilio extends Direccion
{
    @ManyToOne
    private Usuario usuario;

    private Boolean principal;
}

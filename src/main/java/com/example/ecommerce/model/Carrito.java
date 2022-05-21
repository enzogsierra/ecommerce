package com.example.ecommerce.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;


@Entity
@Table(name = "carritos")
public class Carrito 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Producto producto;
    
    @Min(value = 1, message = "Debes agregar al menos 1 producto")
    private int cantidad;



    public Carrito() {

    }

    public Carrito(Integer id, Usuario usuario, Producto producto, int cantidad)
    {
        this.id = id;
        this.usuario = usuario;
        this.producto = producto;
        this.cantidad = cantidad;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Producto getProducto() {
        return this.producto;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getCantidad() {
        return this.cantidad;
    }


    @Override
    public String toString() {
        return "Carrito{" + "id=" + id + ", usuario=" + usuario.getNombre() + ", producto=" + producto.getNombre() + ", cantidad=" + cantidad + '}';
    }
}

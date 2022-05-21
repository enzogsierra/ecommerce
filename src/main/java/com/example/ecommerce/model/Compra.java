package com.example.ecommerce.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "compras")
public class Compra 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //
    @ManyToOne
    private Usuario usuario;

    //
    @ManyToOne
    private Producto producto;

    //
    private int cantidad;

    //
    private double precio;

    //
    private Date fecha;

    
    public Compra() {
    }

    public Compra(Integer id, Producto producto, int cantidad, double precio, Date fecha) {
        this.id = id;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precio = precio;
        this.fecha = fecha;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }


    @Override
    public String toString() {
        return "Compra [cantidad=" + cantidad + ", fecha=" + fecha + ", id=" + id + ", precio=" + precio + ", producto="
                + producto.getNombre() + ", usuario=" + usuario.getId() + "]";
    }
}

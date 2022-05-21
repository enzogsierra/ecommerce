// package com.example.ecommerce.model;

// import java.util.Date;

// import javax.persistence.Entity;
// import javax.persistence.GeneratedValue;
// import javax.persistence.GenerationType;
// import javax.persistence.Id;
// import javax.persistence.ManyToOne;
// import javax.persistence.Table;


// @Entity
// @Table(name = "compras")
// public class Compra 
// {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private int id;

//     @ManyToOne
//     private Usuario usuario;

//     @ManyToOne
//     private Producto producto;

//     private double precio;

//     private int cantidad;

//     private Date fecha;


//     public Compra() {
//     }

//     public Compra(int id, Usuario usuario, Producto producto, double precio, int cantidad)
//     {
//         this.id = id;
//         this.usuario = usuario;
//         this.producto = producto;
//         this.precio = precio;
//         this.cantidad = cantidad;
//     }


//     public void setId(int id) {
//         this.id = id;
//     }

//     public int getId() {
//         return this.id;
//     }

//     public void setUsuario(Usuario usuario) {
//         this.usuario = usuario;
//     }

//     public Usuario getUsuario() {
//         return this.usuario;
//     }

//     public void setProducto(Producto producto) {
//         this.producto = producto;
//     }

//     public Producto getProducto() {
//         return this.producto;
//     }

//     public void setPrecio(double precio) {
//         this.precio = precio;
//     }

//     public double getPrecio() {
//         return this.precio;
//     }

//     public void setCantidad(int cantidad) {
//         this.cantidad = cantidad;
//     }

//     public int getCantidad() {
//         return this.cantidad;
//     }

//     public void setFecha(Date fecha) {
//         this.fecha = fecha;
//     }

//     public Date getFecha() {
//         return this.fecha;
//     }
// }



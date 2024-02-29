package com.example.dressapp.entidades;

public class Articulo {
    private String id;
    private String nombre;
    private String imagenUrl;
    private double precio;
    private String color;
    private String productoRef;
    private String link;

    public Articulo() {
        // Constructor vacío requerido para Firebase
    }

    public Articulo( String nombre, String imagenUrl, double precio, String color, String productoRef, String link) {

        this.nombre = nombre;
        this.imagenUrl = imagenUrl;
        this.precio = precio;
        this.color = color;
        this.productoRef = productoRef;
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getProductoRef() {
        return productoRef;
    }

    public void setProductoRef(String productoRef) {
        this.productoRef = productoRef;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}

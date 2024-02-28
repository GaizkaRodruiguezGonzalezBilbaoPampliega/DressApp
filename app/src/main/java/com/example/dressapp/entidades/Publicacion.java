package com.example.dressapp.entidades;

import java.util.List;

public class Publicacion {
    private String id;
    private Usuario usuario;
    private String imagenUrl;
    private String descripcion;
    private List<String> listaArticulos;

    public Publicacion() {
        // Constructor vacío requerido para Firebase
    }

    public Publicacion(String id, Usuario usuario, String imagenUrl, String descripcion, List<String> listaArticulos) {
        this.id = id;
        this.usuario = usuario;
        this.imagenUrl = imagenUrl;
        this.descripcion = descripcion;
        this.listaArticulos = listaArticulos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<String> getListaArticulos() {
        return listaArticulos;
    }

    public void setListaArticulos(List<String> listaArticulos) {
        this.listaArticulos = listaArticulos;
    }
}

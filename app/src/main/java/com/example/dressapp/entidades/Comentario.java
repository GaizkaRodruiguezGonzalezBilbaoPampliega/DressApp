package com.example.dressapp.entidades;

import java.util.Date;

public class Comentario {
    private String id;
    private Usuario autor;
    private String contenido;
    private String idPublicacion; // Referencia a la publicación a la que pertenece el comentario
    private Date fecha;

    public Comentario() {
        // Constructor vacío requerido por Firebase
    }

    public Comentario(String id, Usuario autor, String contenido, String idPublicacion, Date fecha) {
        this.id = id;
        this.autor = autor;
        this.contenido = contenido;
        this.idPublicacion = idPublicacion;
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getIdPublicacion() {
        return idPublicacion;
    }

    public void setIdPublicacion(String idPublicacion) {
        this.idPublicacion = idPublicacion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}

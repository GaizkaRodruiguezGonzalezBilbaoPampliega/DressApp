package com.example.dressapp.entidades;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Armario implements Serializable {
    private String idUsuario;
    private List<String> idArticulos;

    public Armario() {
        // Constructor vacío requerido para Firebase
    }

    public Armario(String idUsuario) {
        this.idUsuario = idUsuario;
        this.idArticulos = new ArrayList<>();
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public List<String> getIdArticulos() {
        return idArticulos;
    }

    public void agregarArticulo(String idArticulo) {
        idArticulos.add(idArticulo);
    }

    public void eliminarArticulo(String idArticulo) {
        idArticulos.remove(idArticulo);
    }
}


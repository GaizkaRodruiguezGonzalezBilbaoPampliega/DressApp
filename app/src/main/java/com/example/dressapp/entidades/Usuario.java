package com.example.dressapp.entidades;

import java.util.List;

public class Usuario {
    private String id;
    private String nick;
    private String nombre;
    private String email;
    private String imagenPerfil;
    private String descripcion;
    private List<String> seguidoresIds; // Lista de IDs de los seguidores
    private List<String> seguidosIds; // Lista de IDs de los seguidos
    private List<String> favoritosIds; // Lista de IDs de las publicaciones favoritas

    public Usuario() {
        // Constructor vacío requerido para Firebase
    }

    public Usuario( String nick, String nombre, String email, String descripcion, List<String> seguidoresIds, List<String> seguidosIds, List<String> favoritosIds) {

        this.nick = nick;
        this.nombre = nombre;
        this.email = email;
        this.imagenPerfil = "/ftosperfil/defaultProfile.png";
        this.descripcion = descripcion;
        this.seguidoresIds = seguidoresIds;
        this.seguidosIds = seguidosIds;
        this.favoritosIds = favoritosIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImagenPerfil() {
        return imagenPerfil;
    }

    public void setImagenPerfil(String imagenPerfil) {
        this.imagenPerfil = imagenPerfil;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<String> getSeguidoresIds() {
        return seguidoresIds;
    }

    public void setSeguidoresIds(List<String> seguidoresIds) {
        this.seguidoresIds = seguidoresIds;
    }

    public List<String> getSeguidosIds() {
        return seguidosIds;
    }

    public void setSeguidosIds(List<String> seguidosIds) {
        this.seguidosIds = seguidosIds;
    }

    public List<String> getFavoritosIds() {
        return favoritosIds;
    }

    public void setFavoritosIds(List<String> favoritosIds) {
        this.favoritosIds = favoritosIds;
    }
}

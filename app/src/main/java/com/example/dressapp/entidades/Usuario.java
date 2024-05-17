package com.example.dressapp.entidades;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.Date;

public class Usuario {
    private String id;
    private String nick;
    private String nombreCompleto;
    private String correo;
    private Date fecha;
    private String bio;
    private Bitmap imagenPerfil;

    // Constructor vacío requerido para Firebase
    public Usuario() {}

    public Usuario(String nick, String nombreCompleto, String correo, Date fecha, String bio) {
        this.nick = nick;
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.fecha = fecha;
        this.bio = bio;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNick() { return nick; }
    public void setNick(String nick) { this.nick = nick; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public Bitmap getImagenPerfil() { return imagenPerfil; }

    // Método para establecer la imagen de perfil del usuario descargada desde Firebase Storage
    public void setImagenPerfil(final String idUsuario) {
        // Obtén una referencia al archivo de imagen en Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + idUsuario + ".jpg");

        // Descarga el archivo de imagen y conviértelo en un bitmap
        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Convierte el array de bytes en un bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                // Asigna el bitmap a la propiedad de imagen de perfil del usuario
                setImagenPerfil(bitmap);
            }
        });
    }

    public void setImagenPerfil(Bitmap imagenPerfil) { this.imagenPerfil = imagenPerfil; }
}

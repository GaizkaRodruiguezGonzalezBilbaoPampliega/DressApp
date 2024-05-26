package com.example.dressapp.entidades;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.dressapp.manager.ImageDownloadCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Publicacion implements Serializable {
    private String id; // Se mantiene el identificador
    private Usuario autor;
    private Bitmap imagen;
    private String contenido;
    private Date fecha;
    private int nLikes;
    private int nComentarios;
    private List<String> listaArticulos;
    private ImageDownloadCallback callback; // Añadir el callback

    // Constructor vacío requerido para Firebase
    public Publicacion() {}

    public Publicacion(Usuario autor, String contenido, Date fecha, int nLikes, int nComentarios) {
        this.autor = autor;
        this.contenido = contenido;
        this.fecha = fecha;
        this.nLikes = nLikes;
        this.nComentarios = nComentarios;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario autor) { this.autor = autor; }

    public Bitmap getImagen() { return imagen;}

    // Método para establecer la imagen de la publicación descargada desde Firebase Storage
    public void setImagen(final String idPublicacion) {
        // Obtén una referencia al archivo de imagen en Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("publicaciones-images/" + idPublicacion + ".jpg");

        // Descarga el archivo de imagen y conviértelo en un bitmap
        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Convierte el array de bytes en un bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                // Asigna el bitmap a la propiedad de imagen de la publicación
                setImagenBitmap(bitmap);
                // Notifica al callback que la imagen se ha descargado
                if (callback != null) {
                    callback.onImageDownloaded();
                }
            }
        });
    }


    public void setImagenBitmap(Bitmap imagen) { this.imagen = imagen; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public int getnLikes() { return nLikes; }
    public void setnLikes(int nLikes) { this.nLikes = nLikes; }

    public int getnComentarios() { return nComentarios; }
    public void setnComentarios(int nComentarios) { this.nComentarios = nComentarios; }

    public List<String> getListaArticulos() { return listaArticulos; }
    public void setListaArticulos(List<String> listaArticulos) { this.listaArticulos = listaArticulos; }

    // Método para establecer el callback
    public void setImageDownloadCallback(ImageDownloadCallback callback) {
        this.callback = callback;
    }
}

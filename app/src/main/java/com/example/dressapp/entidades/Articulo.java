package com.example.dressapp.entidades;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.dressapp.manager.ImageDownloadCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;

public class Articulo implements Serializable {
    private String id;
    private String nombre;
    private Bitmap imagen;
    private double precio;
    private String color;
    private String productoRef;
    private String link;
    private ImageDownloadCallback callback; // Añadir el callback

    // Constructor vacío requerido para Firebase
    public Articulo() {}

    public Articulo(String nombre, double precio, String color, String productoRef, String link, Bitmap imagen) {
        this.nombre = nombre;
        this.precio = precio;
        this.color = color;
        this.productoRef = productoRef;
        this.link = link;
        this.imagen = imagen;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Bitmap getImagen() { return imagen; }

    // Método para establecer la imagen del artículo descargada desde Firebase Storage
    public void setImagen(final String idArticulo) {
        // Obtén una referencia al archivo de imagen en Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("articulos-images/" + idArticulo + ".jpg");

        // Descarga el archivo de imagen y conviértelo en un bitmap
        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Convierte el array de bytes en un bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                // Asigna el bitmap a la propiedad de imagen del artículo
                setImagenBitmap(bitmap);
                // Notifica al callback que la imagen se ha descargado
                if (callback != null) {
                    callback.onImageDownloaded();
                }
            }
        });
    }

    public void setImagenBitmap(Bitmap imagen) { this.imagen = imagen; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getProductoRef() { return productoRef; }
    public void setProductoRef(String productoRef) { this.productoRef = productoRef; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    // Método para establecer el callback
    public void setImageDownloadCallback(ImageDownloadCallback callback) {
        this.callback = callback;
    }
}

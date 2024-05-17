package com.example.dressapp.vista;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dressapp.R;
import com.example.dressapp.entidades.Articulo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class CrearArticuloActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 2;
    private ImageView imgArticulo;
    private EditText edtNombre, edtPrecio, edtColor, edtProductoRef, edtLink;
    private Button btnGuardar;

    private Bitmap imagenArticulo;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_articulo);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        imgArticulo = findViewById(R.id.imgArticulo);
        edtNombre = findViewById(R.id.edtNombre);
        edtPrecio = findViewById(R.id.edtPrecio);
        edtColor = findViewById(R.id.edtColor);
        edtProductoRef = findViewById(R.id.edtProductoRef);
        edtLink = findViewById(R.id.edtLink);
        btnGuardar = findViewById(R.id.btnCrearArticulo);

        imgArticulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarArticulo();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        // Verifica si se tienen los permisos de cámara
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Si no se tienen los permisos, solicítalos al usuario
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            // Si se tienen los permisos, inicia la actividad de captura de imágenes
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            // Verifica si el usuario concedió los permisos
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si el usuario concedió los permisos, inicia la captura de imágenes
                dispatchTakePictureIntent();
            } else {
                // Si el usuario negó los permisos, muestra un mensaje indicándolo
                Toast.makeText(this, "Se necesitan permisos de cámara para capturar imágenes", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null && extras.containsKey("data")) {
                imagenArticulo = (Bitmap) extras.get("data");
                imgArticulo.setImageBitmap(imagenArticulo);
            } else {
                Toast.makeText(this, "Error al capturar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void guardarArticulo() {
        // Obtener los datos del artículo
        String nombre = edtNombre.getText().toString();
        double precio = Double.parseDouble(edtPrecio.getText().toString());
        String color = edtColor.getText().toString();
        String productoRef = edtProductoRef.getText().toString();
        String link = edtLink.getText().toString();

        if (nombre.isEmpty() || color.isEmpty() || productoRef.isEmpty() || link.isEmpty() || imagenArticulo == null) {
            Toast.makeText(this, "Por favor, completa todos los campos y selecciona una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        // Creamos un nuevo documento en la colección "articulos"
        DocumentReference nuevoArticuloRef = db.collection("articulos").document();

        // Guardamos cada dato del artículo por separado en Firestore
        nuevoArticuloRef
                .set(new HashMap<String, Object>())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Guardamos cada dato del artículo por separado en Firestore
                        nuevoArticuloRef.update("nombre", nombre);
                        nuevoArticuloRef.update("precio", precio);
                        nuevoArticuloRef.update("color", color);
                        nuevoArticuloRef.update("productoRef", productoRef);
                        nuevoArticuloRef.update("link", link);

                        // Obtenemos el ID del documento recién creado
                        String idArticulo = nuevoArticuloRef.getId();

                        // Guardamos la imagen del artículo en Storage con el ID del artículo como nombre de archivo
                        guardarImagenArticulo(idArticulo, imagenArticulo);

                        // Agregamos el ID del artículo al array de artículos en la colección "armarios" del usuario
                        db.collection("armarios").document(userId)
                                .update("articulos", FieldValue.arrayUnion(idArticulo))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(CrearArticuloActivity.this, "Artículo y datos guardados con éxito", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("CrearArticuloActivity", "Error al agregar el ID del artículo al array de artículos en el armario", e);
                                        Toast.makeText(CrearArticuloActivity.this, "Error al guardar el artículo en el armario", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("CrearArticuloActivity", "Error al guardar los datos del artículo en Firestore", e);
                        Toast.makeText(CrearArticuloActivity.this, "Error al guardar los datos del artículo", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void guardarImagenArticulo(String idArticulo, Bitmap imagenArticulo) {
        // Guardamos la imagen del artículo en Storage con el ID del artículo como nombre de archivo
        StorageReference imageRef = storage.getReference().child("articulos-images").child(idArticulo + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagenArticulo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        imageRef.putBytes(imageData)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(CrearArticuloActivity.this, "Imagen del artículo guardada con éxito", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("CrearArticuloActivity", "Error al subir la imagen del artículo", e);
                        Toast.makeText(CrearArticuloActivity.this, "Error al subir la imagen del artículo", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

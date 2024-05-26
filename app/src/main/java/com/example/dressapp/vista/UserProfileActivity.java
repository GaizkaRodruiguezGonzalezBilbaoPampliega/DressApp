package com.example.dressapp.vista;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dressapp.R;
import com.example.dressapp.entidades.Publicacion;
import com.example.dressapp.vista.LoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView fotoPerfil;
    private TextView nombreUsuario;
    private TextView nombreCompleto;
    private TextView bio;
    private TextView nPublicaciones;
    private TextView nSeguidores;
    private TextView nSeguidos;
    private Button btnPublicaciones, btnSeguir;
    private GridLayout galeriaPublicaciones;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private String userID;
    private String clickedUserID; // ID del usuario clicado

    private List<Publicacion> publicacionesList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        fotoPerfil = findViewById(R.id.fotoPerfil);
        nombreUsuario = findViewById(R.id.nombreUsuario);
        nombreCompleto = findViewById(R.id.nombreCompleto);
        bio = findViewById(R.id.descripcion);
        nPublicaciones = findViewById(R.id.nPublicaciones);
        nSeguidores = findViewById(R.id.nSeguidores);
        nSeguidos = findViewById(R.id.nSeguidos);
        btnPublicaciones = findViewById(R.id.btnPublicaciones);
        galeriaPublicaciones = findViewById(R.id.galeriaPublicaciones);
        btnSeguir = findViewById(R.id.btnSeguirPerfil);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        publicacionesList = new ArrayList<>();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userID = currentUser.getUid();
            Log.d("UserProfileActivity", "UserID: " + userID);

            // Obtener el ID del usuario clicado desde el intent
            Intent intent = getIntent();
            clickedUserID = intent.getStringExtra("id");
            Log.d("UserProfileActivity", "Clicked UserID: " + clickedUserID);

            if (clickedUserID != null) {
                cargarPerfil(clickedUserID);
                mostrarPublicaciones();
            } else {
                Toast.makeText(UserProfileActivity.this, "No se encontró el ID del usuario", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            // Redirigir al login si no hay usuario autenticado
            startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
            finish();
        }

        btnPublicaciones.setOnClickListener(v -> mostrarPublicaciones());

        btnSeguir.setOnClickListener(v -> seguirUsuario());
    }

    private void cargarPerfil(String uid) {
        db.collection("usuarios").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    nombreUsuario.setText(documentSnapshot.getString("nick"));
                    nombreCompleto.setText(documentSnapshot.getString("nombreCompleto"));
                    bio.setText(documentSnapshot.getString("bio"));

                    // Cargar la foto de perfil
                    StorageReference fotoPerfilRef = storage.getReference().child("profile_images/" + uid);
                    fotoPerfilRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(fotoPerfil);
                        }
                    }).addOnFailureListener(exception ->
                            Toast.makeText(UserProfileActivity.this, "Error al cargar la foto de perfil", Toast.LENGTH_SHORT).show());

                    // Cargar las estadísticas
                    obtenerCantidadSeguidores(uid);
                    mostrarCantidadSeguidos(uid);
                } else {
                    Toast.makeText(UserProfileActivity.this, "No se encontraron datos de perfil", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(e ->
                Toast.makeText(UserProfileActivity.this, "Error al cargar el perfil", Toast.LENGTH_SHORT).show());
    }

    private void obtenerCantidadSeguidores(String uid) {
        db.collection("seguidores").whereEqualTo("idSeguido", uid).get().addOnSuccessListener(queryDocumentSnapshots -> {
            int cantidadSeguidores = queryDocumentSnapshots.size();
            nSeguidores.setText(String.valueOf(cantidadSeguidores) + " seguidores");
        }).addOnFailureListener(e ->
                Toast.makeText(UserProfileActivity.this, "Error al obtener la cantidad de seguidores", Toast.LENGTH_SHORT).show());
    }

    private void mostrarCantidadSeguidos(String uid) {
        db.collection("seguidores").whereEqualTo("idSeguidor", uid).get().addOnSuccessListener(queryDocumentSnapshots -> {
            int cantidadSeguidos = queryDocumentSnapshots.size();
            nSeguidos.setText(String.valueOf(cantidadSeguidos) + " seguidos");
        }).addOnFailureListener(e ->
                Toast.makeText(UserProfileActivity.this, "Error al obtener la cantidad de seguidos", Toast.LENGTH_SHORT).show());
    }

    private void mostrarPublicaciones() {
        Log.d("UserProfileActivity", "Mostrando publicaciones para userID: " + clickedUserID);
        db.collection("publicaciones").whereEqualTo("idAutor", clickedUserID).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("UserProfileActivity", "Publicaciones encontradas: " + queryDocumentSnapshots.size());
                    int cantidadPublicaciones = queryDocumentSnapshots.size();
                    nPublicaciones.setText(String.valueOf(cantidadPublicaciones) + " publicaciones");

                    galeriaPublicaciones.removeAllViews();
                    publicacionesList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Map<String, Object> publicacionData = document.getData();
                        Publicacion publicacion = new Publicacion();
                        publicacion.setId(document.getId());
                        publicacion.setImagen(document.getString("imagePath"));

                        // Establecer el callback para actualizar la vista una vez descargada la imagen
                        publicacion.setImageDownloadCallback(() -> agregarPublicacion(publicacion));

                        publicacionesList.add(publicacion);
                    }

                })
                .addOnFailureListener(e -> {
                    Log.e("UserProfileActivity", "Error al obtener las publicaciones", e);
                    Toast.makeText(UserProfileActivity.this, "Error al obtener las publicaciones", Toast.LENGTH_SHORT).show();
                });
    }

    private void agregarPublicacion(Publicacion publicacion) {
        ImageView imageView = new ImageView(UserProfileActivity.this);
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.rightMargin = 16;
        layoutParams.bottomMargin = 16;
        imageView.setLayoutParams(layoutParams);

        imageView.setImageBitmap(publicacion.getImagen());
        galeriaPublicaciones.addView(imageView);
    }

    private void seguirUsuario() {
        // Obtener el ID del usuario actual
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Crear una nueva entrada en la tabla de seguidores
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("seguidores")
                .document(clickedUserID)
                .set(new HashMap<String, Object>() {{
                    put("idSeguidor", currentUserId);
                    put("idSeguido", clickedUserID);
                }})
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UserProfileActivity.this, "Ahora estás siguiendo a este usuario", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserProfileActivity.this, "Error al seguir al usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }



}

package com.example.dressapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.dressapp.entidades.Publicacion;
import com.example.dressapp.manager.ImageDownloadCallback;
import com.example.dressapp.vista.EditProfileActivity;
import com.example.dressapp.vista.LoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView fotoPerfil;
    private TextView nombreUsuario;
    private TextView nombreCompleto;
    private TextView bio;
    private TextView nPublicaciones;
    private TextView nSeguidores;
    private TextView nSeguidos;
    private Button btnPublicaciones, btnFavoritos, btnEditarPerfil, btnSeguir;
    private GridLayout galeriaPublicaciones;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private String userID;
    private static final int EDIT_PROFILE_REQUEST_CODE = 100;

    private List<Publicacion> publicacionesList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        fotoPerfil = view.findViewById(R.id.fotoPerfil);
        nombreUsuario = view.findViewById(R.id.nombreUsuario);
        nombreCompleto = view.findViewById(R.id.nombreCompleto);
        bio = view.findViewById(R.id.descripcion);
        nPublicaciones = view.findViewById(R.id.nPublicaciones);
        nSeguidores = view.findViewById(R.id.nSeguidores);
        nSeguidos = view.findViewById(R.id.nSeguidos);
        btnPublicaciones = view.findViewById(R.id.btnPublicaciones);
        btnFavoritos = view.findViewById(R.id.btnFavoritos);
        galeriaPublicaciones = view.findViewById(R.id.galeriaPublicaciones);
        btnEditarPerfil = view.findViewById(R.id.btnEditarPerfil);
        btnSeguir = view.findViewById(R.id.btnSeguirPerfil);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        publicacionesList = new ArrayList<>();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userID = currentUser.getUid();
            Log.d("ProfileFragment", "UserID: " + userID);
            cargarPerfil(userID);
            mostrarPublicaciones();
        } else {
            // Redirigir al login si no hay usuario autenticado
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }

        btnPublicaciones.setOnClickListener(v -> mostrarPublicaciones());
        btnFavoritos.setOnClickListener(v -> mostrarFavoritos());

        btnEditarPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);
        });

        fotoPerfil.setOnClickListener(v -> cambiarFotoPerfil());

        return view;
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
                            Toast.makeText(getActivity(), "Error al cargar la foto de perfil", Toast.LENGTH_SHORT).show());

                    // Cargar las estadísticas
                    obtenerCantidadSeguidores(uid);
                    mostrarCantidadSeguidos(uid);
                } else {
                    Toast.makeText(getActivity(), "No se encontraron datos de perfil", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(e ->
                Toast.makeText(getActivity(), "Error al cargar el perfil", Toast.LENGTH_SHORT).show());
    }

    private void obtenerCantidadSeguidores(String uid) {
        db.collection("seguidores").whereEqualTo("idSeguido", uid).get().addOnSuccessListener(queryDocumentSnapshots -> {
            int cantidadSeguidores = queryDocumentSnapshots.size();
            nSeguidores.setText(String.valueOf(cantidadSeguidores) + " seguidores");
        }).addOnFailureListener(e ->
                Toast.makeText(getActivity(), "Error al obtener la cantidad de seguidores", Toast.LENGTH_SHORT).show());
    }

    private void mostrarCantidadSeguidos(String uid) {
        db.collection("seguidores").whereEqualTo("idSeguidor", uid).get().addOnSuccessListener(queryDocumentSnapshots -> {
            int cantidadSeguidos = queryDocumentSnapshots.size();
            nSeguidos.setText(String.valueOf(cantidadSeguidos) + " seguidos");
        }).addOnFailureListener(e ->
                Toast.makeText(getActivity(), "Error al obtener la cantidad de seguidos", Toast.LENGTH_SHORT).show());
    }
    private void mostrarFavoritos() {
        Log.d("ProfileFragment", "Mostrando publicaciones favoritas para userID: " + userID);
        db.collection("likes").whereEqualTo("userId", userID).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("ProfileFragment", "Favoritos encontrados: " + queryDocumentSnapshots.size());

                    galeriaPublicaciones.removeAllViews();
                    publicacionesList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String publicacionId = document.getString("publicacionId");

                        db.collection("publicaciones").document(publicacionId).get()
                                .addOnSuccessListener(publicacionSnapshot -> {
                                    if (publicacionSnapshot.exists()) {
                                        Publicacion publicacion = new Publicacion();
                                        publicacion.setId(publicacionSnapshot.getId());
                                        publicacion.setImagen(publicacionSnapshot.getString("imagePath"));

                                        // Establecer el callback para actualizar la vista una vez descargada la imagen
                                        publicacion.setImageDownloadCallback(() -> agregarPublicacion(publicacion));

                                        publicacionesList.add(publicacion);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("ProfileFragment", "Error al obtener la publicación favorita", e);
                                    Toast.makeText(getActivity(), "Error al obtener una de las publicaciones favoritas", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileFragment", "Error al obtener los favoritos", e);
                    Toast.makeText(getActivity(), "Error al obtener los favoritos", Toast.LENGTH_SHORT).show();
                });
    }

    private void mostrarPublicaciones() {
        Log.d("ProfileFragment", "Mostrando publicaciones para userID: " + userID);
        db.collection("publicaciones").whereEqualTo("idAutor", userID).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("ProfileFragment", "Publicaciones encontradas: " + queryDocumentSnapshots.size());
                    int cantidadPublicaciones = queryDocumentSnapshots.size();
                    nPublicaciones.setText(String.valueOf(cantidadPublicaciones) + " publicaciones");

                    galeriaPublicaciones.removeAllViews();
                    publicacionesList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Map<String, Object> publicacionData = document.getData();
                        Publicacion publicacion = new Publicacion();
                        publicacion.setId(document.getId());


                        // Establecer el callback para actualizar la vista una vez descargada la imagen
                        publicacion.setImageDownloadCallback(() -> agregarPublicacion(publicacion));
                        publicacion.setImagen(document.getId());
                        // Descargar la imagen


                        publicacionesList.add(publicacion);
                    }

                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileFragment", "Error al obtener las publicaciones", e);
                    Toast.makeText(getActivity(), "Error al obtener las publicaciones", Toast.LENGTH_SHORT).show();
                });
    }

    private void agregarPublicacion(Publicacion publicacion) {
        ImageView imageView = new ImageView(getActivity());
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.rightMargin = 16;
        layoutParams.bottomMargin = 16;
        imageView.setLayoutParams(layoutParams);

        imageView.setImageBitmap(publicacion.getImagen());
        galeriaPublicaciones.addView(imageView);
    }

    private void cambiarFotoPerfil() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                fotoPerfil.setImageBitmap(bitmap);

                if (filePath != null) {
                    StorageReference fotoPerfilRef = storage.getReference().child("profile_images/" + userID);
                    fotoPerfilRef.putFile(filePath).addOnSuccessListener(taskSnapshot ->
                            fotoPerfilRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                Picasso.get().load(uri).into(fotoPerfil);
                                Toast.makeText(getActivity(), "Foto de perfil actualizada", Toast.LENGTH_SHORT).show();
                            })
                    ).addOnFailureListener(e ->
                            Toast.makeText(getActivity(), "Error al actualizar la foto de perfil", Toast.LENGTH_SHORT).show());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Actualizar la visualización del perfil aquí
            cargarPerfil(userID);
        }
    }
}
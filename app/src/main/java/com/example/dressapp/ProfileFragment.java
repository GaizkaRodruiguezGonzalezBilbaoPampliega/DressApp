
package com.example.dressapp;

import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
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
    private Button btnPublicaciones;
    private GridLayout galeriaPublicaciones;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private String userID;

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
        galeriaPublicaciones = view.findViewById(R.id.galeriaPublicaciones);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

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

    private void mostrarPublicaciones() {
        Log.d("ProfileFragment", "Mostrando publicaciones para userID: " + userID);
        db.collection("publicaciones").whereEqualTo("idAutor", userID).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("ProfileFragment", "Publicaciones encontradas: " + queryDocumentSnapshots.size());
                    int cantidadPublicaciones = queryDocumentSnapshots.size();
                    nPublicaciones.setText(String.valueOf(cantidadPublicaciones) + " publicaciones");

                    galeriaPublicaciones.removeAllViews();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Map<String, Object> publicacion = document.getData();
                        Log.d("ProfileFragment", "Cargando publicación: " + publicacion.toString());
                        agregarPublicacion(document.getId(), publicacion);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileFragment", "Error al obtener las publicaciones", e);
                    Toast.makeText(getActivity(), "Error al obtener las publicaciones", Toast.LENGTH_SHORT).show();
                });
    }

    private void agregarPublicacion(String docId, Map<String, Object> publicacion) {
        if (publicacion.containsKey("urlImagen")) {
            String urlImagen = publicacion.get("urlImagen").toString();
            Log.d("ProfileFragment", "URL de la imagen: " + urlImagen);

            ImageView imageView = new ImageView(getActivity());
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.rightMargin = 16;
            layoutParams.bottomMargin = 16;
            imageView.setLayoutParams(layoutParams);

            Glide.with(getActivity()).load(urlImagen).into(imageView);
            galeriaPublicaciones.addView(imageView);
        } else {
            Log.w("ProfileFragment", "La publicación no contiene el campo 'urlImagen'");
        }
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
    }
}

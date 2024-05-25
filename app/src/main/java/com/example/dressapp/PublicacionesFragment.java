package com.example.dressapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dressapp.adapters.PostPagerAdapter;
import com.example.dressapp.entidades.Publicacion;
import com.example.dressapp.entidades.Usuario;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

public class PublicacionesFragment extends Fragment {

    private VerticalViewPager viewPager;
    private PostPagerAdapter adapter;
    private List<Publicacion> publicaciones = new ArrayList<>();
    private FirebaseFirestore db;

    public PublicacionesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_publicacion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.verticalViewPagerPublicaciones);
        adapter = new PostPagerAdapter(getContext(), publicaciones);
        viewPager.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // Cargar las publicaciones desde Firestore
        cargarPublicacionesDesdeFirestore();
    }

    // Método para cargar las publicaciones desde Firestore
    private void cargarPublicacionesDesdeFirestore() {
        db.collection("publicaciones").addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                // Manejar errores de Firestore
                Log.e("Firestore", "Error al obtener las publicaciones", e);
                return;
            }

            if (queryDocumentSnapshots != null) {
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        // Se ha agregado una nueva publicación
                        Publicacion publicacion = new Publicacion();
                        // Obtener los datos de la publicación
                        cargarDatosPublicacion(publicacion, doc.getDocument());
                    }
                }
            }
        });
    }

    // Método para cargar los datos de la publicación y el usuario correspondiente desde Firestore
    private void cargarDatosPublicacion(final Publicacion publicacion, DocumentSnapshot document) {
        // Obtener los datos de la publicación
        publicacion.setId(document.getId());
        publicacion.setContenido(document.getString("contenido"));
        publicacion.setFecha(document.getTimestamp("fecha").toDate());
        publicacion.setnLikes(document.getLong("nLikes").intValue());
        publicacion.setnComentarios(document.getLong("nComentarios").intValue());
        publicacion.setImagen(document.getId());
        List<String> listaArticulosIds = (List<String>) document.get("articulos");
        if (listaArticulosIds != null) {
            publicacion.setListaArticulos(listaArticulosIds);
        }
        String idAutor = document.getString("idAutor");

        // Obtener y establecer el usuario correspondiente a la publicación
        db.collection("usuarios").document(idAutor).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot userDocument = task.getResult();
                if (userDocument.exists()) {
                    Usuario autor = new Usuario();
                    autor.setId(userDocument.getId());
                    autor.setNick(userDocument.getString("nick"));
                    autor.setNombreCompleto(userDocument.getString("nombreCompleto"));
                    autor.setCorreo(userDocument.getString("correo"));
                    autor.setImagenPerfil(userDocument.getId());

                    // Establecer más datos del usuario si es necesario
                    publicacion.setAutor(autor);

                    // Agregar la publicación a la lista
                    publicaciones.add(publicacion);
                    // Notificar al adaptador después de cargar los datos del usuario
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("Firestore", "No se encontró el usuario correspondiente al ID: " + idAutor);
                    // Aquí puedes manejar el caso en el que no se encuentre el usuario
                }
            } else {
                Log.e("Firestore", "Error al obtener el usuario", task.getException());
                // Aquí puedes manejar el error al obtener el usuario
            }
        });
    }
}

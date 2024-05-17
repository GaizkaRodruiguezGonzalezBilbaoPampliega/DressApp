package com.example.dressapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.dressapp.R;
import com.example.dressapp.adapters.PostPagerAdapter;
import com.example.dressapp.entidades.Publicacion;
import com.example.dressapp.entidades.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

public class PublicacionesFragment extends Fragment {

    private VerticalViewPager viewPager;
    private PostPagerAdapter adapter;
    private List<Publicacion> publicaciones = new ArrayList<>();
    private FirebaseFirestore db;

    public PublicacionesFragment() {}

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
                // Limpiar la lista de publicaciones antes de agregar nuevas
                publicaciones.clear();

                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        // Se ha agregado una nueva publicación
                        Publicacion publicacion = new Publicacion();
                        // Obtener los datos de la publicación
                        String idAutor = doc.getDocument().getString("idAutor");
                        publicacion.setContenido(doc.getDocument().getString("contenido"));
                        publicacion.setFecha(doc.getDocument().getTimestamp("fecha").toDate());
                        publicacion.setnLikes(doc.getDocument().getLong("nLikes").intValue());
                        publicacion.setnComentarios(doc.getDocument().getLong("nComentarios").intValue());
                        String idPublicacion = doc.getDocument().getId();
                        publicacion.setImagen(idPublicacion);
                        // Obtener y establecer el usuario correspondiente a la publicación
                        obtenerUsuario(publicacion, idAutor);
                        // Agregar la publicación a la lista
                        publicaciones.add(publicacion);
                    }
                }

                // Notificar al adaptador que se han actualizado los datos
                adapter.notifyDataSetChanged();
            }
        });
    }

    // Método para obtener y establecer el usuario correspondiente a la publicación
    private void obtenerUsuario(final Publicacion publicacion, String autorId) {
        // Consultar la colección de usuarios utilizando el ID del autor
        db.collection("usuarios").document(autorId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Obtener los datos del usuario
                        Usuario autor = new Usuario();
                        autor.setId(document.getId());
                        autor.setNick(document.getString("nick"));
                        autor.setNombreCompleto(document.getString("nombreCompleto"));
                        autor.setCorreo(document.getString("correo"));
                        String fechaString = document.getString("fecha");
                        if (fechaString != null) {
                            try {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Date fechaDate = dateFormat.parse(fechaString);
                                autor.setFecha(fechaDate);
                            } catch (ParseException e) {
                                Log.e("Firestore", "Error al parsear la fecha del usuario", e);
                            }
                        }
                        autor.setBio(document.getString("bio"));

                        // Establecer el usuario en la publicación
                        publicacion.setAutor(autor);

                        // Cargar la imagen de perfil del usuario
                        publicacion.getAutor().setImagenPerfil(autorId);

                        // Verificar si todas las publicaciones tienen un usuario y notificar al adaptador
                        verificarNotificar();
                    } else {
                        Log.e("Firestore", "No se encontró el usuario correspondiente al ID: " + autorId);
                    }
                } else {
                    Log.e("Firestore", "Error al obtener el usuario", task.getException());
                }
            }
        });
    }

    // Método para verificar si todas las publicaciones tienen un usuario y notificar al adaptador
    private void verificarNotificar() {
        for (Publicacion publicacion : publicaciones) {
            if (publicacion.getAutor() == null || publicacion.getAutor().getImagenPerfil() == null) {
                // Aún hay publicaciones sin usuario o imagen de perfil cargada, espera hasta la próxima vez
                return;
            }
        }
        // Notificar al adaptador que se han actualizado todos los datos
        adapter.notifyDataSetChanged();
    }

}

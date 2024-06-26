package com.example.dressapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dressapp.adapters.ArticuloAdapter;
import com.example.dressapp.entidades.Articulo;
import com.example.dressapp.manager.ImageDownloadCallback;
import com.example.dressapp.vista.CrearArticuloActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

public class ArmarioFragment extends Fragment implements ArticuloAdapter.OnArticuloListener {

    private List<Articulo> listaArticulos;
    private ArticuloAdapter adaptador;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_armario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializa Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        // Inicializa Firestore
        db = FirebaseFirestore.getInstance();
        // Obtiene el usuario actual
        currentUser = mAuth.getCurrentUser();

        // Inicializa la lista de artículos y el adaptador
        listaArticulos = new ArrayList<>();
        adaptador = new ArticuloAdapter(listaArticulos, true, this);

        // Configura el RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewArticulos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adaptador);

        // Configura el botón de agregar artículo
        Button btnAgregarArticulo = view.findViewById(R.id.btnAgregarArticulo);
        btnAgregarArticulo.setOnClickListener(v -> {
            // Intent para abrir la actividad CrearArticuloActivity
            Intent intent = new Intent(getActivity(), CrearArticuloActivity.class);
            startActivity(intent);
        });

        // Obtiene el armario del usuario actual
        obtenerArmarioUsuario();
    }

    private void obtenerArmarioUsuario() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference armarioRef = db.collection("armarios").document(userId);
            armarioRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    List<String> idArticulos = (List<String>) documentSnapshot.get("articulos");
                    if (idArticulos != null && !idArticulos.isEmpty()) {
                        cargarDetallesArticulos(idArticulos);
                    } else {
                        // El armario está vacío o la lista de IDs de artículos es nula,
                        // puedes mostrar un mensaje al usuario o tomar otra acción
                    }
                }
            }).addOnFailureListener(e -> {
                // Maneja el caso de falla al obtener el armario del usuario
            });
        }
    }

    private void cargarDetallesArticulos(List<String> idArticulos) {
        final int totalArticulos = idArticulos.size(); // Total de artículos que se espera cargar

        for (String idArticulo : idArticulos) {
            DocumentReference articuloRef = db.collection("articulos").document(idArticulo);
            articuloRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Articulo articulo = documentSnapshot.toObject(Articulo.class);
                    if (articulo != null) {
                        // Establece el callback para notificar cuando la imagen se descargue
                        articulo.setImageDownloadCallback(() -> {
                            // Notifica al adaptador cuando la imagen se haya descargado
                            adaptador.notifyDataSetChanged();
                        });
                        // Establece la imagen del artículo desde Firebase Storage
                        articulo.setImagen(idArticulo);
                        listaArticulos.add(articulo);

                        // Verifica si se han cargado todos los artículos
                        if (listaArticulos.size() == totalArticulos) {
                            // Notifica al adaptador una vez que todos los artículos se han agregado
                            adaptador.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onEliminarClick(int position) {
        Articulo articuloAEliminar = listaArticulos.get(position);
        String idArticulo = articuloAEliminar.getId();

        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentReference articuloRef = db.collection("articulos").document(idArticulo);
            DocumentReference armarioRef = db.collection("armarios").document(currentUser.getUid());

            // Eliminar el artículo de la colección "articulos"
            transaction.delete(articuloRef);

            // Eliminar el ID del artículo del array de artículos en el armario del usuario
            transaction.update(armarioRef, "articulos", FieldValue.arrayRemove(idArticulo));

            return null;
        }).addOnSuccessListener(aVoid -> {
            listaArticulos.remove(position);
            adaptador.notifyItemRemoved(position);
            // Toast para confirmar la eliminación
            Toast.makeText(getContext(), "Artículo eliminado", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            // Toast para indicar error en la eliminación
            Toast.makeText(getContext(), "Error al eliminar el artículo", Toast.LENGTH_SHORT).show();
        });
    }
}

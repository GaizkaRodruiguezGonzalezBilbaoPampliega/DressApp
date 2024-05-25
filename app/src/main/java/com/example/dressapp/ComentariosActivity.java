package com.example.dressapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dressapp.adapters.ComentariosAdapter;
import com.example.dressapp.entidades.Comentario;
import com.example.dressapp.entidades.Usuario;
import com.example.dressapp.manager.ComentarioEliminarListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ComentariosActivity extends AppCompatActivity implements ComentarioEliminarListener {

    private RecyclerView recyclerView;
    private ComentariosAdapter adapter;
    private List<Comentario> comentariosList;
    private EditText editTextComentario;
    private Button buttonEnviar;
    private String postId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        recyclerView = findViewById(R.id.recycler_view_comentarios);
        editTextComentario = findViewById(R.id.edit_text_comentario);
        buttonEnviar = findViewById(R.id.button_enviar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        comentariosList = new ArrayList<>();
        adapter = new ComentariosAdapter(comentariosList);
        adapter.setEliminarListener(this); // Establecer el listener de eliminación
        recyclerView.setAdapter(adapter);

        if (getIntent().getExtras() != null) {
            postId = getIntent().getStringExtra("postId");
        }

        buttonEnviar.setOnClickListener(v -> enviarComentario());

        cargarComentarios();
    }

    private void cargarComentarios() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("comentarios")
                .whereEqualTo("idPublicacion", postId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        comentariosList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Comentario comentario = new Comentario();
                            comentario.setId(document.getId());
                            comentario.setContenido(document.getString("contenido"));

                            // Obtener y establecer el autor del comentario
                            String autorId = document.getString("idUsuario");
                            if (autorId != null) {
                                db.collection("usuarios").document(autorId).get().addOnCompleteListener(authorTask -> {
                                    if (authorTask.isSuccessful()) {
                                        DocumentSnapshot authorDoc = authorTask.getResult();
                                        if (authorDoc.exists()) {
                                            Usuario autor = new Usuario();
                                            autor.setId(autorId);
                                            autor.setNick(authorDoc.getString("nick"));
                                            autor.setNombreCompleto(authorDoc.getString("nombreCompleto"));
                                            autor.setCorreo(authorDoc.getString("correo"));
                                            // Establecer la imagen del autor si la tienes en tu entidad Usuario
                                            autor.setImagenPerfil(authorDoc.getString("imagenPerfil"));
                                            comentario.setAutor(autor);
                                            comentariosList.add(comentario); // Agregar comentario a la lista
                                            adapter.notifyDataSetChanged(); // Notificar al adaptador
                                        } else {
                                            Log.e("Firestore", "No se encontró el autor del comentario");
                                        }
                                    } else {
                                        Log.e("Firestore", "Error al obtener el autor del comentario", authorTask.getException());
                                    }
                                });
                            } else {
                                Log.e("Firestore", "El ID del autor del comentario es nulo");
                            }
                        }
                    } else {
                        Log.e("Firestore", "Error al cargar los comentarios", task.getException());
                    }
                });
    }


    private void enviarComentario() {
        String contenido = editTextComentario.getText().toString().trim();
        if (!contenido.isEmpty()) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                Map<String, Object> comentarioData = new HashMap<>();
                comentarioData.put("idUsuario", currentUser.getUid());
                comentarioData.put("idPublicacion", postId);
                comentarioData.put("contenido", contenido);
                comentarioData.put("fecha", FieldValue.serverTimestamp()); // Utilizar FieldValue.serverTimestamp()

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("comentarios").add(comentarioData)
                        .addOnSuccessListener(documentReference -> {
                            editTextComentario.setText("");
                            cargarComentarios();
                        })
                        .addOnFailureListener(e -> Log.e("Firestore", "Error al enviar el comentario", e));
            }
        }
    }

    @Override
    public void eliminarComentario(String comentarioId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("comentarios").document(comentarioId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Comentario eliminado correctamente.");
                    cargarComentarios();
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al eliminar el comentario", e));
    }
}

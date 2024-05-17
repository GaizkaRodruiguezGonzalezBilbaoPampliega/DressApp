package com.example.dressapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dressapp.adapters.ArticuloSeleccionAdapter;
import com.example.dressapp.entidades.Articulo;
import com.example.dressapp.entidades.Publicacion;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CrearPublicacionActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextContenido;
    private ImageView imageViewPublicacion;
    private Button btnCrearPublicacion;
    private SearchView searchViewArticulos;
    private RecyclerView recyclerViewArticulos;

    private Bitmap imagenPublicacion;
    private Uri imagenUri;

    private List<Articulo> listaArticulos;
    private List<Articulo> articulosSeleccionados;
    private ArticuloSeleccionAdapter adaptador;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_publicacion);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        editTextContenido = findViewById(R.id.editTextContenido);
        imageViewPublicacion = findViewById(R.id.imageViewPublicacion);
        btnCrearPublicacion = findViewById(R.id.btnCrearPublicacion);
        searchViewArticulos = findViewById(R.id.searchViewArticulos);
        recyclerViewArticulos = findViewById(R.id.recyclerViewArticulos);

        listaArticulos = new ArrayList<>();
        articulosSeleccionados = new ArrayList<>();
        adaptador = new ArticuloSeleccionAdapter(this, listaArticulos, articulosSeleccionados);

        recyclerViewArticulos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewArticulos.setAdapter(adaptador);

        imageViewPublicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarImagen();
            }
        });

        btnCrearPublicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearPublicacion();
            }
        });

        searchViewArticulos.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adaptador.filter(newText);
                return true;
            }
        });

        cargarDatos();
    }

    private void cargarDatos() {
        obtenerArmarioUsuario();
    }

    private void obtenerArmarioUsuario() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference armarioRef = db.collection("armarios").document(userId);
            armarioRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        List<String> idArticulos = (List<String>) documentSnapshot.get("articulos");
                        if (idArticulos != null && !idArticulos.isEmpty()) {
                            cargarArticulos(idArticulos);
                        } else {
                            // El armario está vacío o la lista de IDs de artículos es nula,
                            // puedes mostrar un mensaje al usuario o tomar otra acción
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Maneja el caso de falla al obtener el armario del usuario
                }
            });
        }
    }

    private void cargarArticulos(List<String> idArticulos) {
        db.collection("articulos")
                .whereIn(FieldPath.documentId(), idArticulos)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaArticulos.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            Articulo articulo = document.toObject(Articulo.class);
                            listaArticulos.add(articulo);
                        }
                        adaptador.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error al cargar artículos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void seleccionarImagen() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imagenUri = data.getData();
            try {
                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagenUri);
                imagenPublicacion = redimensionarBitmap(originalBitmap, 400, 600);
                imageViewPublicacion.setImageBitmap(imagenPublicacion);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap redimensionarBitmap(Bitmap bitmap, int ancho, int alto) {
        return Bitmap.createScaledBitmap(bitmap, ancho, alto, true);
    }

    private void crearPublicacion() {
        String contenido = editTextContenido.getText().toString().trim();
        if (contenido.isEmpty() || imagenUri == null || articulosSeleccionados.isEmpty()) {
            Toast.makeText(this, "Debes completar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Primero subimos la imagen
        StorageReference storageRef = storage.getReference().child("publicaciones-images/" + UUID.randomUUID().toString());
        storageRef.putFile(imagenUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Una vez que la imagen se haya subido exitosamente, obtenemos su URL
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imagenUrl = uri.toString();

                        // Creamos un nuevo objeto Publicacion
                        Publicacion publicacion = new Publicacion();
                        publicacion.setAutor(currentUser.getUid());
                        publicacion.setContenido(contenido);
                        publicacion.setFecha(new Date());
                        publicacion.setListaArticulos(articulosSeleccionados);

                        // Guardamos la publicación en Firestore
                        db.collection("publicaciones").add(publicacion)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(this, "Publicación creada", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Error al crear la publicación", Toast.LENGTH_SHORT).show());
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show());
    }

}

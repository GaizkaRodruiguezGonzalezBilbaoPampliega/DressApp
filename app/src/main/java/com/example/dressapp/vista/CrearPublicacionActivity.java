package com.example.dressapp.vista;

import static android.app.PendingIntent.getActivity;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dressapp.R;
import com.example.dressapp.adapters.ArticuloSeleccionAdapter;
import com.example.dressapp.entidades.Articulo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        initUI();
        initListeners();
        cargarDatos();
    }

    private void initUI() {
        editTextContenido = findViewById(R.id.editTextContenido);
        imageViewPublicacion = findViewById(R.id.imageViewPublicacion);
        btnCrearPublicacion = findViewById(R.id.btnCrearPublicacion);
        searchViewArticulos = findViewById(R.id.searchViewArticulos);
        recyclerViewArticulos = findViewById(R.id.recyclerViewArticulos);

        listaArticulos = new ArrayList<>();
        articulosSeleccionados = new ArrayList<>();

        adaptador = new ArticuloSeleccionAdapter(listaArticulos, articulosSeleccionados);


        recyclerViewArticulos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewArticulos.setAdapter(adaptador);
    }


    private void initListeners() {
        imageViewPublicacion.setOnClickListener(v -> seleccionarImagen());

        btnCrearPublicacion.setOnClickListener(v -> crearPublicacion());

        searchViewArticulos.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adaptador.filterList(filtrarArticulos(newText));
                return true;
            }
        });
    }

    private ArrayList<Articulo> filtrarArticulos(String query) {
        ArrayList<Articulo> listaFiltrada = new ArrayList<>();
        for (Articulo articulo : listaArticulos) {
            if (articulo.getNombre().toLowerCase().contains(query.toLowerCase())) {
                listaFiltrada.add(articulo);
            }
        }
        return listaFiltrada;
    }

    private void cargarDatos() {
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
                        cargarArticulos(idArticulos);
                    } else {
                        mostrarMensaje("Tu armario está vacío.");
                    }
                }
            }).addOnFailureListener(e -> mostrarMensaje("Error al obtener el armario del usuario"));
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
                            articulo.setId(document.getId());
                            articulo.setImagen(document.getId());
                            listaArticulos.add(articulo);
                        }
                        adaptador.notifyDataSetChanged();
                    } else {
                        mostrarMensaje("Error al cargar artículos");
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

        if (contenido.isEmpty()) {
            editTextContenido.setError("Debes ingresar un contenido para la publicación");
            return;
        }
        if (imagenUri == null) {
            Toast.makeText(this, "Debes seleccionar una imagen para la publicación", Toast.LENGTH_SHORT).show();
            return;
        }
        if (adaptador.getArticulosSeleccionados().isEmpty()) {
            Toast.makeText(this, "Debes seleccionar al menos un artículo para la publicación", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imagenUri != null) {
            try {
                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagenUri);
                imagenPublicacion = redimensionarBitmap(originalBitmap, 400, 600);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al redimensionar la imagen", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        List<String> idsArticulosSeleccionados = new ArrayList<>();
        for (Articulo articulo : adaptador.getArticulosSeleccionados()) {

            idsArticulosSeleccionados.add(articulo.getId());
        }

        // Crear un mapa de los campos de la publicación
        Map<String, Object> publicacionMap = new HashMap<>();
        publicacionMap.put("idAutor", currentUser.getUid());
        publicacionMap.put("contenido", contenido);
        publicacionMap.put("fecha", new Date());
        publicacionMap.put("nLikes", 0);
        publicacionMap.put("nComentarios", 0);
        Log.d("idsArticulosSeleccionados", idsArticulosSeleccionados.toString());
        publicacionMap.put("articulos", idsArticulosSeleccionados);

        // Guardar la publicación en Firestore para obtener su ID
        db.collection("publicaciones").add(publicacionMap)
                .addOnSuccessListener(documentReference -> {
                    String idPublicacion = documentReference.getId();
                    // Subir la imagen redimensionada con el ID de la publicación
                    if (imagenPublicacion != null) {
                        StorageReference storageRef = storage.getReference().child("publicaciones-images/" + idPublicacion + ".jpg");
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        imagenPublicacion.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();
                        UploadTask uploadTask = storageRef.putBytes(data);
                        uploadTask.addOnFailureListener(exception -> Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show())
                                .addOnSuccessListener(taskSnapshot -> {
                                    // Imagen subida exitosamente
                                    Toast.makeText(this, "Publicación creada exitosamente", Toast.LENGTH_SHORT).show();
                                    this.finish();
                                });
                    } else {
                        // No hay imagen para subir
                        Toast.makeText(this, "No se ha seleccionado ninguna imagen para la publicación", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al crear la publicación", Toast.LENGTH_SHORT).show());
    }


    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}


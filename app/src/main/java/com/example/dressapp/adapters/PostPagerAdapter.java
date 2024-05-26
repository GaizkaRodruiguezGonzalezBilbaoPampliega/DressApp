package com.example.dressapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dressapp.ComentariosActivity;
import com.example.dressapp.R;
import com.example.dressapp.entidades.Articulo;
import com.example.dressapp.entidades.Publicacion;
import com.example.dressapp.vista.UserProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostPagerAdapter extends PagerAdapter {

    private Context context;
    private List<Publicacion> publicaciones;

    public PostPagerAdapter(Context context, List<Publicacion> publicaciones) {
        this.context = context;
        this.publicaciones = publicaciones;
    }

    @Override
    public int getCount() {
        return publicaciones.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_post, container, false);

        TextView username = view.findViewById(R.id.username);
        TextView postContent = view.findViewById(R.id.post_content);
        ImageView postImage = view.findViewById(R.id.post_image);
        ImageView profileImage = view.findViewById(R.id.profile_image);
        ImageView likeButton = view.findViewById(R.id.like_icon);
        ImageView commentButton = view.findViewById(R.id.comment_icon);
        ImageView articlesButton = view.findViewById(R.id.articles_icon);
        LinearLayout articulosContainer = view.findViewById(R.id.articulos_container);
        RecyclerView recyclerViewArticulos = view.findViewById(R.id.recycler_view_articulos);

        Publicacion publicacion = publicaciones.get(position);

        username.setText(publicacion.getAutor().getNick());
        postContent.setText(publicacion.getContenido());
        Glide.with(context).load(publicacion.getImagen()).into(postImage);
        Glide.with(context).load(publicacion.getAutor().getImagenPerfil()).into(profileImage);

        verificarLike(publicacion.getId(), likeButton);
        likeButton.setOnClickListener(v -> handleLikeButtonClick(publicacion.getId(), likeButton));
        commentButton.setOnClickListener(v -> handleCommentButtonClick(publicacion.getId()));
        articlesButton.setOnClickListener(v -> handleArticlesButtonClick(articulosContainer, recyclerViewArticulos, publicacion.getListaArticulos()));
        username.setOnClickListener(v -> handleUsernameClick(publicacion.getAutor().getId()));
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    private void handleUsernameClick(String id) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    private void handleLikeButtonClick(String postId, ImageView likeButton) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("likes")
                    .whereEqualTo("IDUsuario", currentUserId)
                    .whereEqualTo("IDPublicacion", postId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                likeButton.setImageResource(R.drawable.ic_like_red);
                                addLike(postId, db, currentUserId);
                            } else {
                                likeButton.setImageResource(R.drawable.ic_like);
                                removeLike(postId, db, currentUserId);
                            }
                        } else {
                            Toast.makeText(context, "Error al verificar Me gusta", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(context, "Inicia sesión para dar Me gusta", Toast.LENGTH_SHORT).show();
        }
    }

    private void verificarLike(String postId, ImageView likeButton) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("likes")
                    .whereEqualTo("IDUsuario", currentUserId)
                    .whereEqualTo("IDPublicacion", postId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                likeButton.setImageResource(R.drawable.ic_like_red);
                            }
                        } else {
                            Log.e("Firestore", "Error al verificar Me gusta", task.getException());
                        }
                    });
        }
    }

    private void addLike(String postId, FirebaseFirestore db, String currentUserId) {
        Map<String, Object> likeData = new HashMap<>();
        likeData.put("IDUsuario", currentUserId);
        likeData.put("IDPublicacion", postId);

        db.collection("likes").add(likeData)
                .addOnFailureListener(e -> Toast.makeText(context, "Error al agregar Me gusta", Toast.LENGTH_SHORT).show());
    }

    private void removeLike(String postId, FirebaseFirestore db, String currentUserId) {
        db.collection("likes")
                .whereEqualTo("IDUsuario", currentUserId)
                .whereEqualTo("IDPublicacion", postId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        db.collection("likes").document(document.getId()).delete()
                                .addOnFailureListener(e -> Toast.makeText(context, "Error al eliminar Me gusta", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error al buscar Me gusta", Toast.LENGTH_SHORT).show());
    }

    private void handleCommentButtonClick(String postId) {
        Intent intent = new Intent(context, ComentariosActivity.class);
        intent.putExtra("postId", postId);
        context.startActivity(intent);
    }

    private void handleArticlesButtonClick(LinearLayout articulosContainer, RecyclerView recyclerViewArticulos, List<String> listaArticulosIds) {
        if (listaArticulosIds != null) {
            if (articulosContainer.getVisibility() == View.GONE) {
                articulosContainer.setVisibility(View.VISIBLE);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                List<Articulo> articulos = new ArrayList<>();
                ArticuloAdapter articuloAdapter = new ArticuloAdapter(articulos, false, null);
                recyclerViewArticulos.setLayoutManager(new LinearLayoutManager(context));
                recyclerViewArticulos.setAdapter(articuloAdapter);

                for (String articuloId : listaArticulosIds) {
                    db.collection("articulos").document(articuloId).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                Articulo articulo = documentSnapshot.toObject(Articulo.class);
                                if (articulo != null) {
                                    // Establece la imagen del artículo desde Firebase Storage
                                    articulo.setImageDownloadCallback(() -> {
                                        // Notifica al adaptador cuando la imagen se haya descargado
                                        articuloAdapter.notifyDataSetChanged();
                                    });
                                    articulo.setImagen(articuloId);

                                    articulos.add(articulo);
                                    articuloAdapter.notifyDataSetChanged();
                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(context, "Error al cargar artículos", Toast.LENGTH_SHORT).show());
                }
            } else {
                articulosContainer.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(context, "La lista de IDs de artículos es nula", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.example.dressapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dressapp.R;
import com.example.dressapp.entidades.Publicacion;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AdapterPublicacionesPerfil extends RecyclerView.Adapter<AdapterPublicacionesPerfil.ViewHolder> {

    private Context context;
    private List<Publicacion> publicacionesList;

    public AdapterPublicacionesPerfil(Context context, List<Publicacion> publicacionesList) {
        this.context = context;
        this.publicacionesList = publicacionesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_img_publicacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Publicacion publicacion = publicacionesList.get(position);
        holder.bind(publicacion);
    }

    @Override
    public int getItemCount() {
        return publicacionesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewPublicacion);
        }

        public void bind(Publicacion publicacion) {
            // Cargar la imagen usando Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("publicaciones-images/" + publicacion.getImagen() + ".jpg");
            storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Manejar errores aquí, puedes establecer un placeholder en caso de fallo
                    imageView.setImageResource(R.drawable.article_picture_placeholder);
                }
            });
        }
    }
}

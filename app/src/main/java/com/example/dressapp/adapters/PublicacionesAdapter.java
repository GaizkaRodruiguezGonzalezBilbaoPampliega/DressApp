package com.example.dressapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dressapp.R;
import com.example.dressapp.entidades.Publicacion;
import com.example.dressapp.entidades.Usuario;

import java.util.List;

public class PublicacionesAdapter extends RecyclerView.Adapter<PublicacionesAdapter.PublicacionesViewHolder> {

    List<Publicacion> listaPublicaciones;

    public PublicacionesAdapter(List<Publicacion> listaPublicaciones) {
        this.listaPublicaciones = listaPublicaciones;
    }

    @NonNull
    @Override
    public PublicacionesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post, parent, false);
        return new PublicacionesViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull PublicacionesViewHolder holder, int position) {
        holder.setPublicacionData(listaPublicaciones.get(position));
    }

    @Override
    public int getItemCount() {
        return listaPublicaciones.size()  ;
    }

    public class PublicacionesViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView, userProfile;
        TextView userNick, descripcion;


        public PublicacionesViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            userProfile = itemView.findViewById(R.id.imageProfile);
            userNick = itemView.findViewById(R.id.textUser);
            descripcion = itemView.findViewById(R.id.textDescription);

        }

        public void setPublicacionData(Publicacion publicacion){
            imageView.setImageResource(Integer.valueOf(publicacion.getImagenUrl()));
            userProfile.setImageResource(Integer.valueOf(publicacion.getUsuario().getImagenPerfil()));
            userNick.setText(publicacion.getUsuario().getNick());
            descripcion.setText(publicacion.getDescripcion());

            imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    float imageRatio = (float) imageView.getDrawable().getIntrinsicWidth() / (float) imageView.getDrawable().getIntrinsicHeight();
                    float viewRatio = (float) imageView.getWidth() / (float) imageView.getHeight();
                    float scale = imageRatio / viewRatio;

                    if (scale >= 1f) {
                        imageView.setScaleX(scale);
                    } else {
                        imageView.setScaleY(1f / scale);
                    }

                    // Remove the listener to avoid redundant calls
                    imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });

        }

    }

}

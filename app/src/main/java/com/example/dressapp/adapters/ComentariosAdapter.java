package com.example.dressapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dressapp.R;
import com.example.dressapp.entidades.Comentario;
import com.example.dressapp.manager.ComentarioEliminarListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ComentariosAdapter extends RecyclerView.Adapter<ComentariosAdapter.ComentarioViewHolder> {

    private List<Comentario> comentariosList;
    private ComentarioEliminarListener eliminarListener;

    public ComentariosAdapter(List<Comentario> comentariosList) {
        this.comentariosList = comentariosList;
    }

    public void setEliminarListener(ComentarioEliminarListener eliminarListener) {
        this.eliminarListener = eliminarListener;
    }

    @NonNull
    @Override
    public ComentarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comentario, parent, false);
        return new ComentarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComentarioViewHolder holder, int position) {
        Comentario comentario = comentariosList.get(position);
        holder.bind(comentario);
    }

    @Override
    public int getItemCount() {
        return comentariosList.size();
    }

    public class ComentarioViewHolder extends RecyclerView.ViewHolder {

        private TextView textoComentario;
        private ImageView imagenPerfil;
        private TextView username;
        private ImageButton botonEliminar;

        public ComentarioViewHolder(@NonNull View itemView) {
            super(itemView);
            textoComentario = itemView.findViewById(R.id.texto_comentario);
            imagenPerfil = itemView.findViewById(R.id.imagen_perfil_comentario);
            username = itemView.findViewById(R.id.username_comentario);
            botonEliminar = itemView.findViewById(R.id.boton_eliminar_comentario);
        }

        public void bind(Comentario comentario) {
            textoComentario.setText(comentario.getContenido());
            username.setText(comentario.getAutor().getNick());


            Glide.with(imagenPerfil.getContext()).load(comentario.getAutor().getImagenPerfil()).into(imagenPerfil);

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null && currentUser.getUid().equals(comentario.getAutor().getId())) {
                botonEliminar.setVisibility(View.VISIBLE);
                botonEliminar.setOnClickListener(v -> eliminarListener.eliminarComentario(comentario.getId()));
            } else {
                botonEliminar.setVisibility(View.GONE);
            }
        }
    }
}

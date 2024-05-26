package com.example.dressapp.adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dressapp.R;
import com.example.dressapp.entidades.Articulo;

import java.util.List;

public class ArticuloAdapter extends RecyclerView.Adapter<ArticuloAdapter.ArticuloViewHolder> {

    private List<Articulo> listaArticulos;
    private boolean mostrarBotonEliminar;
    private OnArticuloListener onArticuloListener;

    public ArticuloAdapter(List<Articulo> listaArticulos, boolean mostrarBotonEliminar, OnArticuloListener onArticuloListener) {
        this.listaArticulos = listaArticulos;
        this.mostrarBotonEliminar = mostrarBotonEliminar;
        this.onArticuloListener = onArticuloListener;
    }

    @NonNull
    @Override
    public ArticuloViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_articulo, parent, false);
        return new ArticuloViewHolder(itemView, mostrarBotonEliminar, onArticuloListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticuloViewHolder holder, int position) {
        Articulo articulo = listaArticulos.get(position);
        holder.bind(articulo);
    }

    @Override
    public int getItemCount() {
        return listaArticulos.size();
    }

    public static class ArticuloViewHolder extends RecyclerView.ViewHolder {

        private ImageView imagenArticulo;
        private TextView txtNombreArticulo;
        private TextView txtPrecioArticulo;
        private TextView txtColorArticulo;
        private TextView txtReferenciaProducto;
        private TextView txtLinkArticulo;
        private Button btnEliminar;
        private boolean mostrarBotonEliminar;
        private OnArticuloListener onArticuloListener;

        public ArticuloViewHolder(@NonNull View itemView, boolean mostrarBotonEliminar, OnArticuloListener onArticuloListener) {
            super(itemView);
            imagenArticulo = itemView.findViewById(R.id.imagenArticulo);
            txtNombreArticulo = itemView.findViewById(R.id.txtNombreArticulo);
            txtPrecioArticulo = itemView.findViewById(R.id.txtPrecioArticulo);
            txtColorArticulo = itemView.findViewById(R.id.txtColorArticulo);
            txtReferenciaProducto = itemView.findViewById(R.id.txtReferenciaProducto);
            txtLinkArticulo = itemView.findViewById(R.id.txtLinkArticulo);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            this.mostrarBotonEliminar = mostrarBotonEliminar;
            this.onArticuloListener = onArticuloListener;
        }

        public void bind(Articulo articulo) {
            // Cargar la imagen del artículo utilizando Glide
            Glide.with(itemView.getContext()).load(articulo.getImagen()).into(imagenArticulo);
            txtNombreArticulo.setText(articulo.getNombre());
            txtPrecioArticulo.setText(String.valueOf(articulo.getPrecio()));
            txtColorArticulo.setText(articulo.getColor());
            txtReferenciaProducto.setText(articulo.getProductoRef());
            txtLinkArticulo.setText(articulo.getLink());

            // Mostrar u ocultar el botón de eliminar según corresponda
            btnEliminar.setVisibility(mostrarBotonEliminar ? View.VISIBLE : View.GONE);

            // Configurar el clic en el card para abrir el enlace del artículo
            itemView.setOnClickListener(v -> {
                // Abrir el enlace del artículo en un navegador web
                String url = articulo.getLink();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                itemView.getContext().startActivity(intent);
            });

            // Configurar el clic en el botón de eliminar
            btnEliminar.setOnClickListener(v -> {
                if (onArticuloListener != null) {
                    onArticuloListener.onEliminarClick(getAdapterPosition());
                }
            });
        }
    }

    public interface OnArticuloListener {
        void onEliminarClick(int position);
    }
}

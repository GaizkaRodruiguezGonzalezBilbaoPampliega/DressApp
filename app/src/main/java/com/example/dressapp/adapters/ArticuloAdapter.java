package com.example.dressapp.adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dressapp.R;
import com.example.dressapp.entidades.Articulo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArticuloAdapter extends RecyclerView.Adapter<ArticuloAdapter.ArticuloViewHolder> {

    private List<Articulo> listaArticulos;

    public ArticuloAdapter(List<Articulo> listaArticulos) {
        this.listaArticulos = listaArticulos;
    }

    @NonNull
    @Override
    public ArticuloViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_articulo, parent, false);
        return new ArticuloViewHolder(itemView);
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

        public ArticuloViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenArticulo = itemView.findViewById(R.id.imagenArticulo);
            txtNombreArticulo = itemView.findViewById(R.id.txtNombreArticulo);
            txtPrecioArticulo = itemView.findViewById(R.id.txtPrecioArticulo);
            txtColorArticulo = itemView.findViewById(R.id.txtColorArticulo);
            txtReferenciaProducto = itemView.findViewById(R.id.txtReferenciaProducto);
            txtLinkArticulo = itemView.findViewById(R.id.txtLinkArticulo);
        }

        public void bind(Articulo articulo) {
            // Cargar la imagen del artículo usando Picasso
            Picasso.get().load(articulo.getLink()).into(imagenArticulo);
            txtNombreArticulo.setText(articulo.getNombre());
            txtPrecioArticulo.setText(String.valueOf(articulo.getPrecio()));
            txtColorArticulo.setText(articulo.getColor());
            txtReferenciaProducto.setText(articulo.getProductoRef());
            txtLinkArticulo.setText(articulo.getLink());

            // Configurar el clic en el card para abrir el enlace del artículo
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Abrir el enlace del artículo en un navegador web
                    String url = articulo.getLink();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    itemView.getContext().startActivity(intent);
                }
            });
        }

    }
}

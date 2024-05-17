package com.example.dressapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dressapp.R;
import com.example.dressapp.entidades.Articulo;
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
        private TextView txtReferenciaProducto;

        public ArticuloViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenArticulo = itemView.findViewById(R.id.imagenArticulo);
            txtNombreArticulo = itemView.findViewById(R.id.txtNombreArticulo);
            txtReferenciaProducto = itemView.findViewById(R.id.txtReferenciaProducto);
        }

        public void bind(Articulo articulo) {

            imagenArticulo.setImageBitmap(articulo.getImagen());
            txtNombreArticulo.setText(articulo.getNombre());
            txtReferenciaProducto.setText(articulo.getProductoRef());
        }
    }
}

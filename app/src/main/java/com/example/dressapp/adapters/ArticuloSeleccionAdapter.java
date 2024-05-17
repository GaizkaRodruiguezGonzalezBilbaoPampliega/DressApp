package com.example.dressapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dressapp.R;
import com.example.dressapp.entidades.Articulo;

import java.util.ArrayList;
import java.util.List;

public class ArticuloSeleccionAdapter extends RecyclerView.Adapter<ArticuloSeleccionAdapter.ArticuloViewHolder> {

    private List<Articulo> listaArticulos;
    private List<Articulo> articulosSeleccionados;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ArticuloViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textViewNombre;
        public CheckBox checkBoxSeleccionado;

        public ArticuloViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imagenArticulo);
            textViewNombre = itemView.findViewById(R.id.txtNombreArticulo);
            checkBoxSeleccionado = itemView.findViewById(R.id.checkBoxSeleccionado);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public ArticuloSeleccionAdapter(List<Articulo> listaArticulos, List<Articulo> articulosSeleccionados) {
        this.listaArticulos = listaArticulos;
        this.articulosSeleccionados = articulosSeleccionados;
    }

    @NonNull
    @Override
    public ArticuloViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_articulo_seleccion, parent, false);
        ArticuloViewHolder evh = new ArticuloViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ArticuloViewHolder holder, int position) {
        Articulo currentItem = listaArticulos.get(position);

        holder.imageView.setImageBitmap(currentItem.getImagen());
        holder.textViewNombre.setText(currentItem.getNombre());

        if (articulosSeleccionados.contains(currentItem)) {
            holder.checkBoxSeleccionado.setChecked(true);
        } else {
            holder.checkBoxSeleccionado.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return listaArticulos.size();
    }

    public void filterList(ArrayList<Articulo> filteredList) {
        listaArticulos = filteredList;
        notifyDataSetChanged();
    }
}

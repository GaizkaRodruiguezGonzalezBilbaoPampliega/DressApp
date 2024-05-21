package com.example.dressapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dressapp.R;
import com.example.dressapp.entidades.Articulo;

import java.util.ArrayList;
import java.util.List;

public class ArticuloSeleccionAdapter extends RecyclerView.Adapter<ArticuloSeleccionAdapter.ArticuloViewHolder> {

    private List<Articulo> listaArticulos;
    private List<Articulo> articulosSeleccionados;

    public ArticuloSeleccionAdapter(List<Articulo> listaArticulos, List<Articulo> articulosSeleccionados) {
        this.listaArticulos = listaArticulos;
        this.articulosSeleccionados = articulosSeleccionados;
    }

    @NonNull
    @Override
    public ArticuloViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_articulo_seleccion, parent, false);
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

    public class ArticuloViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBoxArticulo;

        public ArticuloViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxArticulo = itemView.findViewById(R.id.checkBoxSeleccionado);
        }

        public void bind(Articulo articulo) {
            checkBoxArticulo.setText(articulo.getNombre());
            checkBoxArticulo.setOnCheckedChangeListener(null); // Clear previous listener to prevent unwanted callbacks

            // Check if the current article is selected
            checkBoxArticulo.setChecked(articulosSeleccionados.contains(articulo));

            // Handle checkbox state change
            checkBoxArticulo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // Add the selected article to the list
                        articulosSeleccionados.add(articulo);
                    } else {
                        // Remove the article from the list
                        articulosSeleccionados.remove(articulo);
                    }
                }
            });
        }
    }

    // Method to update the filter list
    public void filterList(ArrayList<Articulo> filteredList) {
        listaArticulos = filteredList;
        notifyDataSetChanged();
    }

    // Method to get the selected articles
    public List<Articulo> getArticulosSeleccionados() {
        return articulosSeleccionados;
    }
}

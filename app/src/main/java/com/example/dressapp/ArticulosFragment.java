package com.example.dressapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dressapp.adapters.ArticuloAdapter;
import com.example.dressapp.entidades.Articulo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ArticulosFragment extends Fragment {

    private static final String ARG_POST_ID = "postId";
    private String postId;
    private RecyclerView recyclerView;
    private ArticuloAdapter articuloAdapter;
    private List<Articulo> articuloList = new ArrayList<>();

    public static ArticulosFragment newInstance(String postId) {
        ArticulosFragment fragment = new ArticulosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getString(ARG_POST_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_articulos, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_articulos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        articuloAdapter = new ArticuloAdapter( articuloList);
        recyclerView.setAdapter(articuloAdapter);

        cargarArticulos(postId);

        return view;
    }

    private void cargarArticulos(String postId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("articulos")
                .whereEqualTo("idPublicacion", postId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        articuloList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Articulo articulo = document.toObject(Articulo.class);
                            articulo.setId(document.getId());
                            articuloList.add(articulo);
                        }
                        articuloAdapter.notifyDataSetChanged();
                    }
                });
    }
}

package com.example.dressapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dressapp.adapters.PublicacionesAdapter;
import com.example.dressapp.entidades.Publicacion;

import java.util.ArrayList;
import java.util.List;


public class PostsFragment extends Fragment {

    private ViewPager2 viewPager2;
    private List<Publicacion> publicaciones;
    private PublicacionesAdapter publicacionesAdapter;

    public PostsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View rootview = inflater.inflate(R.layout.fragment_posts, container, false);

        publicaciones = new ArrayList<>();
        viewPager2 = rootview.findViewById(R.id.viewPagerPosts);

        //Codigo para insertar posts//

        publicacionesAdapter = new PublicacionesAdapter(publicaciones);
        viewPager2.setAdapter(publicacionesAdapter);

        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }
}
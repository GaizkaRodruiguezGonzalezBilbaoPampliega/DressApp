package com.example.dressapp.vista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;


import com.example.dressapp.CreateFragment;
import com.example.dressapp.PostsFragment;
import com.example.dressapp.ProfileFragment;
import com.example.dressapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String userId = getIntent().getStringExtra("USER_ID");
        mAuth = FirebaseAuth.getInstance();


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // No hay usuario conectado, redirigir al LoginActivity
            abrirLoginActivity();
        } else {
            // Hay un usuario conectado, realizar acciones adicionales si es necesario
            //this line hide statusbar
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            navigationView = findViewById(R.id.bottom_navigation);
            getSupportFragmentManager().beginTransaction().replace(R.id.body_container, new PostsFragment()).commit();
            navigationView.setSelectedItemId(R.id.nav_posts);

            navigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment = null;
                    int itemid = item.getItemId();
                    if(itemid ==  R.id.nav_posts){
                        fragment = new PostsFragment();
                    }else if(itemid ==  R.id.nav_profile){
                        fragment = new ProfileFragment();
                    }else if(itemid ==  R.id.nav_create) {
                        fragment = new CreateFragment();
                    }


                    getSupportFragmentManager().beginTransaction().replace(R.id.body_container, fragment).commit();
                    return true;
                }
            });
        }
    }

    private void abrirLoginActivity() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}
// MainActivity.java
package com.example.dressapp.vista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;


import com.example.dressapp.R;
import com.example.dressapp.adapters.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private BottomNavigationView navigationView;
    private ViewPager2 viewPager;
    private ViewPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String userId = getIntent().getStringExtra("USER_ID");
        mAuth = FirebaseAuth.getInstance();
        viewPager = findViewById(R.id.body_container);
        pagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
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
            navigationView.setSelectedItemId(R.id.nav_posts);

            navigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment = null;
                    int itemid = item.getItemId();
                    if (itemid == R.id.nav_posts) {
                        viewPager.setCurrentItem(0);
                        return true;
                    } else if (itemid == R.id.nav_profile) {
                        viewPager.setCurrentItem(1);
                        return true;
                    } else if (itemid == R.id.nav_create) {
                        viewPager.setCurrentItem(2);
                        return true;
                    } else if (itemid == R.id.nav_wardrobe) {
                    viewPager.setCurrentItem(3);
                    return true;
                    }

                    return false;
                }
            });
        }
    }

    private void abrirLoginActivity() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}

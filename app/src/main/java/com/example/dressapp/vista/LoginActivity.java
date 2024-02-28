package com.example.dressapp.vista;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dressapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText editTextEmail, editTextContraseña;
    private Button btnIniciarSesion;
    private TextView textViewRegistrarse;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextContraseña = findViewById(R.id.editTextContraseña);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        textViewRegistrarse = findViewById(R.id.textViewRegistrarse);

        btnIniciarSesion.setOnClickListener(v -> iniciarSesion());
        textViewRegistrarse.setOnClickListener(v -> abrirActividadRegistro());
    }

    private void iniciarSesion() {
        String email = editTextEmail.getText().toString();
        String contraseña = editTextContraseña.getText().toString();

        mAuth.signInWithEmailAndPassword(email, contraseña)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Inicio de sesión exitoso, redirige a la actividad principal
                            abrirActividadPrincipal(user.getUid());
                        }
                    } else {
                        // Error en el inicio de sesión
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Error al iniciar sesión.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void abrirActividadRegistro() {
        Intent intent = new Intent(this, RegistroActivity.class);
        startActivity(intent);
    }

    private void abrirActividadPrincipal(String userId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("USER_ID", userId); // Pasar el ID del usuario al MainActivity
        startActivity(intent);
    }
}

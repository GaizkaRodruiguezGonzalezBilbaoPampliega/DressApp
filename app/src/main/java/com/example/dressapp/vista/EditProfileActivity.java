package com.example.dressapp.vista;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dressapp.R;
import com.example.dressapp.manager.ProfileUpdateListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editNombreCompleto;
    private EditText editNick;
    private EditText editBio;
    private Button btnGuardar;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String userID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editNombreCompleto = findViewById(R.id.editNombreCompleto);
        editNick = findViewById(R.id.editNick);
        editBio = findViewById(R.id.editBio);
        btnGuardar = findViewById(R.id.btnGuardar);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userID = currentUser.getUid();
            cargarDatosPerfil(userID);
        } else {
            Toast.makeText(this, "No se encontró el usuario autenticado", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnGuardar.setOnClickListener(v -> guardarCambiosPerfil());
    }

    private void cargarDatosPerfil(String uid) {
        db.collection("usuarios").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    editNombreCompleto.setText(documentSnapshot.getString("nombreCompleto"));
                    editNick.setText(documentSnapshot.getString("nick"));
                    editBio.setText(documentSnapshot.getString("bio"));
                } else {
                    Toast.makeText(EditProfileActivity.this, "No se encontraron datos de perfil", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(EditProfileActivity.this, "Error al cargar el perfil", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void guardarCambiosPerfil() {
        String nuevoNombreCompleto = editNombreCompleto.getText().toString();
        String nuevoNick = editNick.getText().toString();
        String nuevaBio = editBio.getText().toString();


        db.collection("usuarios").document(userID)
                .update("nombreCompleto", nuevoNombreCompleto, "nick", nuevoNick, "bio", nuevaBio)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProfileActivity.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show();
                    Log.e("EditProfileActivity", "Error al actualizar el perfil", e);
                });
        setResult(Activity.RESULT_OK);
    }

}

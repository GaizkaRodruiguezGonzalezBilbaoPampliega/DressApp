package com.example.dressapp.vista;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dressapp.R;
import com.example.dressapp.entidades.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistroActivity extends AppCompatActivity {

    private static final String TAG = "RegistroActivity";

    private EditText editTextNick, editTextNombre, editTextEmail, editTextContraseña;
    private Button btnRegistrar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        editTextNick = findViewById(R.id.editTextNick);
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextContraseña = findViewById(R.id.editTextContraseña);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        String email = editTextEmail.getText().toString();
        String contraseña = editTextContraseña.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, contraseña)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Registro exitoso, guarda los datos adicionales del usuario en la base de datos
                            guardarDatosUsuario(user.getUid());
                        }
                    } else {
                        // Error en el registro
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegistroActivity.this, "Error al registrar usuario.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void guardarDatosUsuario(String userId) {
        String nick = editTextNick.getText().toString();
        String nombre = editTextNombre.getText().toString();
        String email = editTextEmail.getText().toString();

        Usuario usuario = new Usuario();
        usuario.setId(userId);
        usuario.setNick(nick);
        usuario.setNombre(nombre);
        usuario.setEmail(email);

        mDatabase.child("usuarios").child(userId).setValue(usuario)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegistroActivity.this, "Usuario registrado exitosamente.",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.e(TAG, "Error al guardar datos de usuario en la base de datos.", task.getException());
                        Toast.makeText(RegistroActivity.this, "Error al guardar datos de usuario.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

package com.example.dressapp.vista;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dressapp.R;
import com.example.dressapp.entidades.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistroActivity extends AppCompatActivity {

    private static final String TAG = "RegistroActivity";

    private EditText editTextNick, editTextNombre, editTextEmail, editTextContraseña;
    private Button btnRegistrar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Registro exitoso, guarda los datos adicionales del usuario en Firestore
                                guardarDatosUsuario(user.getUid());
                            }
                        } else {
                            // Error en el registro
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistroActivity.this, "Error al registrar usuario.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void guardarDatosUsuario(String userId) {
        String nick = editTextNick.getText().toString();
        String nombre = editTextNombre.getText().toString();
        String email = editTextEmail.getText().toString();

        Usuario usuario = new Usuario();
        usuario.setNick(nick);
        usuario.setNombre(nombre);
        usuario.setEmail(email);

        // Guardar los datos del usuario en Firestore
        db.collection("usuarios").document(userId)
                .set(usuario)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistroActivity.this, "Usuario registrado exitosamente.",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Log.e(TAG, "Error al guardar datos de usuario en Firestore.", task.getException());
                            Toast.makeText(RegistroActivity.this, "Error al guardar datos de usuario.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

package com.upiiz.examen_dja_03;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.upiiz.examen_dja_03.model.Usuario;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etUser, etClave, etName;
    Button btnBack, btnAdd;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        etUser = findViewById(R.id.etUser);
        etClave = findViewById(R.id.etClave);
        etName = findViewById(R.id.etName);
        btnAdd = findViewById(R.id.btnAdd);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==btnBack.getId()){
            startActivity(new Intent(this, LoginActivity.class));
        }
        if(v.getId()==btnAdd.getId()){
            String usuario = etUser.getText().toString();
            String password = etClave.getText().toString();
            String name = etName.getText().toString();
            AddUser(usuario,password,name);
        }
    }
    private void AddUser(String usuario, String password, String nombre) {

        String email = usuario + "@app.com";

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (!task.isSuccessful()) {
                        Toast.makeText(this,
                                "Error al registrar: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    String uid = mAuth.getCurrentUser().getUid();

                    // Obtener TOKEN FCM
                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(tokenTask -> {

                                if (!tokenTask.isSuccessful()) {
                                    Toast.makeText(this,
                                            "No se pudo obtener el token",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String token = tokenTask.getResult();

                                Usuario nuevoUsuario = new Usuario(
                                        uid,
                                        nombre,
                                        usuario,
                                        token
                                );

                                // Guardarlo en Realtime Database
                                DatabaseReference ref = FirebaseDatabase.getInstance()
                                        .getReference("usuarios")
                                        .child(uid);

                                ref.setValue(nuevoUsuario);

                                startActivity(new Intent(this, ChatsActivity.class));
                                finish();
                            });
                });
    }

}
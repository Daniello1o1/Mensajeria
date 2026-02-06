package com.upiiz.examen_dja_03;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ConfigurationActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    LinearLayout llChats, llLogout, llExit;
    Button btnSaveClave;
    EditText etClaveActual, etClaveNueva, etClaveConf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_configuration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        llChats = findViewById(R.id.llChats);
        llExit = findViewById(R.id.llExit);
        llLogout = findViewById(R.id.llLogout);
        btnSaveClave = findViewById(R.id.btnSaveClave);

        etClaveActual = findViewById(R.id.etClaveActual);
        etClaveNueva = findViewById(R.id.etClaveNueva);
        etClaveConf = findViewById(R.id.etClaveConf);

        llChats.setOnClickListener(this);
        llLogout.setOnClickListener(this);
        llExit.setOnClickListener(this);
        btnSaveClave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==llChats.getId()){
            finish();
        }
        if(v.getId()==llExit.getId()){
            finishAffinity();
        }
        if(v.getId()==llLogout.getId()){
            cerrarSesion();
        }
        if(v.getId()==btnSaveClave.getId()){
            String claveActual = etClaveActual.getText().toString();
            String clave1 = etClaveNueva.getText().toString();
            String clave2 = etClaveConf.getText().toString();
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            cambiarPassword(email, claveActual, clave1, clave2);
        }
    }

    private void cambiarPassword(String email, String claveActual, String claveNueva, String claveConfirmacion) {

        // 1. VALIDACIONES
        if (claveActual.isEmpty() || claveNueva.isEmpty() || claveConfirmacion.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!claveNueva.equals(claveConfirmacion)) {
            Toast.makeText(this, "La nueva clave no coincide con la confirmación", Toast.LENGTH_SHORT).show();
            return;
        }

        if (claveNueva.length() < 6) {
            Toast.makeText(this, "La nueva clave debe tener mínimo 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Error: no hay usuario autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. REAUTENTICACIÓN (Firebase exige esto)
        AuthCredential credential = EmailAuthProvider.getCredential(email, claveActual);

        user.reauthenticate(credential).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                // 3. ACTUALIZAR CONTRASEÑA
                user.updatePassword(claveNueva).addOnCompleteListener(updateTask -> {

                    if (updateTask.isSuccessful()) {
                        Toast.makeText(this, "Contraseña cambiada correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error al actualizar contraseña: "
                                + updateTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }

                });

            } else {
                Toast.makeText(this, "La contraseña actual es incorrecta", Toast.LENGTH_SHORT).show();
            }

        });
    }
    private void cerrarSesion() {
        mAuth.signOut();

        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        preferences.edit().clear().apply();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
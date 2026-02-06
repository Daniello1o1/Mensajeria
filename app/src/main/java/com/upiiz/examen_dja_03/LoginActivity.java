package com.upiiz.examen_dja_03;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etUser, etClave;
    CheckBox cbRemember;
    Button btnRegister, btnLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        etUser = findViewById(R.id.etUser);
        etClave = findViewById(R.id.etClave);
        cbRemember = findViewById(R.id.cbRemember);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==btnRegister.getId()){
            startActivity(new Intent(this, RegisterActivity.class));
        }
        if(v.getId()==btnLogin.getId()){
            String usuario, password;
            boolean recordar = cbRemember.isChecked();
            usuario = etUser.getText().toString();
            password = etClave.getText().toString();

            if(usuario.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Ingrese un usuario y contraseña", Toast.LENGTH_SHORT).show();
            }
            else{
                Login(usuario,password,recordar);
            }
        }
    }
    private void Login(String usuario, String password, boolean recordar) {
        mAuth.signInWithEmailAndPassword(usuario+"@app.com",password).addOnCompleteListener(this,task -> {
            if(task.isSuccessful()){
                FirebaseUser user = mAuth.getCurrentUser();
                Log.d("LOGIN", "Login correcto: " + user.getUid());
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(tokenTask -> {
                            if (tokenTask.isSuccessful()) {
                                String token = tokenTask.getResult();
                                Log.d("FCM", "Token actualizado: " + token);

                                FirebaseDatabase.getInstance().getReference("usuarios")
                                        .child(user.getUid())
                                        .child("token")
                                        .setValue(token);
                            }
                        });

                SharedPreferences.Editor editor =
                        getSharedPreferences("loginPrefs", MODE_PRIVATE).edit();

                editor.putBoolean("recordar", recordar);
                editor.apply();

                String uid = mAuth.getCurrentUser().getUid();

                FirebaseDatabase.getInstance()
                        .getReference("usuarios")
                        .child(uid)
                        .child("estado")
                        .setValue("online");

                FirebaseDatabase.getInstance()
                        .getReference("usuarios")
                        .child(uid)
                        .child("estado")
                        .onDisconnect()
                        .setValue("offline");

                Intent intent = new Intent(LoginActivity.this,ChatsActivity.class);
                startActivity(intent);
            }
            else{
                Log.e("LOGIN","Error",task.getException());
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
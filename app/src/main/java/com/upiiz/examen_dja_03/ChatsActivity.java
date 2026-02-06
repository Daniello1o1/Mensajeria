package com.upiiz.examen_dja_03;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upiiz.examen_dja_03.adapter.UsuarioAdapter;
import com.upiiz.examen_dja_03.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    LinearLayout llConf, llLogout, llExit;
    RecyclerView rvUsers;
    UsuarioAdapter adapter;
    List<Usuario> listUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chats);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        mAuth = FirebaseAuth.getInstance();
        llConf = findViewById(R.id.llConf);
        llExit = findViewById(R.id.llExit);
        llLogout = findViewById(R.id.llLogout);
        rvUsers = findViewById(R.id.rvUsers);
        listUsuarios = new ArrayList<>();

        llConf.setOnClickListener(this);
        llExit.setOnClickListener(this);
        llLogout.setOnClickListener(this);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UsuarioAdapter(this,listUsuarios,uid -> {
            Intent intent = new Intent(this, ConversationActivity.class);
            intent.putExtra("UserUID",uid);
            startActivity(intent);
        });
        rvUsers.setAdapter(adapter);

        cargarUsuarios();

    }



    @Override
    public void onClick(View v) {
        if(v.getId()==llConf.getId()){
            startActivity(new Intent(this, ConfigurationActivity.class));
        }
        if(v.getId()==llExit.getId()){
            String uid = mAuth.getCurrentUser().getUid();
            FirebaseDatabase.getInstance()
                    .getReference("usuarios")
                    .child(uid)
                    .child("estado")
                    .setValue("offline");
            finishAffinity();
        }
        if(v.getId()==llLogout.getId()){
            cerrarSesion();
        }
    }

    private void cerrarSesion() {
        String uid = mAuth.getCurrentUser().getUid();
        FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(uid)
                .child("estado")
                .setValue("offline");

        mAuth.signOut();

        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        preferences.edit().clear().apply();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    private void cargarUsuarios() {
        String uidActual = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                listUsuarios.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Usuario u = snap.getValue(Usuario.class);
                    String uid = snap.getKey();

                    if (!uid.equals(uidActual)) {
                        listUsuarios.add(u);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("USUARIOS", "Error: ", error.toException());
            }
        });
    }

}
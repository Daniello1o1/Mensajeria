    package com.upiiz.examen_dja_03;

    import static com.upiiz.examen_dja_03.FcmAuth.getAccessToken;

    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.TextView;

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
    import com.upiiz.examen_dja_03.adapter.MensajeAdapter;
    import com.upiiz.examen_dja_03.model.Mensaje;

    import org.json.JSONObject;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.concurrent.Executors;

    import okhttp3.MediaType;
    import okhttp3.OkHttpClient;
    import okhttp3.Request;
    import okhttp3.RequestBody;
    import okhttp3.Response;
    import okhttp3.MediaType.Companion;


    public class ConversationActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvName;
    RecyclerView rvMensajes;
    String uidReceptor;
    FirebaseAuth mAuth;
    EditText etMensaje;
    ImageView btnBack;
    ImageView btnSend;
    List<Mensaje> listmensajes;
    MensajeAdapter adapter;
    String myUID;
    String nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        myUID =mAuth.getCurrentUser().getUid();
        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        uidReceptor = getIntent().getStringExtra("UserUID");

        if (uidReceptor == null) {
            Log.e("CHAT", "Error: no se recibió el UID del receptor");
            finish();
            return;
        }


        tvName = findViewById(R.id.tvName);
        btnBack = findViewById(R.id.btnBack);
        btnSend = findViewById(R.id.btnSend);
        etMensaje = findViewById(R.id.etMensaje);
        mAuth = FirebaseAuth.getInstance();
        btnBack.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        listmensajes = new ArrayList<>();

        rvMensajes = findViewById(R.id.rvMensajes);
        rvMensajes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MensajeAdapter(this,listmensajes,myUID);
        rvMensajes.setAdapter(adapter);

        cargarMensajes();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==btnBack.getId()){
            finish();
        }
        if(v.getId()==btnSend.getId()){
            String texto = etMensaje.getText().toString().trim();
            if (!texto.isEmpty()) {
                AddMsg(texto, uidReceptor);
            }
        }
    }
        private void AddMsg(String mensaje, String uidReceptor) {

            String uidEmisor = mAuth.getCurrentUser().getUid();
            String chatId = getChatId(uidEmisor, uidReceptor);

            Mensaje msg = new Mensaje(uidEmisor, mensaje, System.currentTimeMillis());

            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("chats")
                    .child(chatId);

            // 1. Guardar mensaje
            ref.push().setValue(msg)
                    .addOnSuccessListener(a -> {

                        etMensaje.setText("");

                        // 2. Obtener nombre del emisor (para mostrarlo en la notificación)
                        FirebaseDatabase.getInstance()
                                .getReference("usuarios")
                                .child(uidEmisor)
                                .child("nombre")
                                .get()
                                .addOnSuccessListener(snEmisor -> {

                                    String nombreEmisor = snEmisor.getValue(String.class);

                                    // 3. Obtener token del receptor
                                    FirebaseDatabase.getInstance()
                                            .getReference("usuarios")
                                            .child(uidReceptor)
                                            .child("token")
                                            .get()
                                            .addOnSuccessListener(sn -> {

                                                String tokenReceptor = sn.getValue(String.class);

                                                if (tokenReceptor != null) {
                                                    // 4. Enviar notificación
                                                    enviarNotificacion(tokenReceptor, nombreEmisor, mensaje);
                                                }
                                            });
                                });

                    })
                    .addOnFailureListener(e -> {
                        Log.e("FB", "Error guardando mensaje: " + e.getMessage());
                    });
        }

        private String getChatId(String uid1, String uid2) {
        if (uid1.compareTo(uid2) < 0) {
            return uid1 + "_" + uid2;
        } else {
            return uid2 + "_" + uid1;
        }
    }
    private void cargarMensajes(){
        String otherUID = getIntent().getStringExtra("UserUID");

        FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(otherUID)
                .child("nombre")
                .get()
                .addOnSuccessListener(snEmisor -> {

                    String nombreEmisor = snEmisor.getValue(String.class);
                    tvName.setText(nombreEmisor);
                });

        String chatID = (myUID.compareTo(otherUID) < 0)
                ? myUID + "_" + otherUID
                : otherUID + "_" + myUID;

        DatabaseReference chatRef = FirebaseDatabase.getInstance()
                .getReference("chats")
                .child(chatID);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listmensajes.clear();

                for (DataSnapshot msgSnap : snapshot.getChildren()) {
                    Mensaje msg = msgSnap.getValue(Mensaje.class);
                    listmensajes.add(msg);
                    Log.d("msn",msg.getTexto());
                }

                adapter.notifyDataSetChanged();
                rvMensajes.scrollToPosition(listmensajes.size() - 1);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FB", "Error cargando mensajes: " + error.getMessage());
            }
        });

    }

        private void enviarNotificacion(String token, String title, String body) {

            new Thread(() -> {

                try {
                    OkHttpClient client = new OkHttpClient();

                    // JSON de FCM v1
                    JSONObject json = new JSONObject();
                    JSONObject message = new JSONObject();
                    JSONObject data = new JSONObject();
                    data.put("title", title);
                    data.put("body", body);

                    message.put("token", token);
                    message.put("data", data);

                    json.put("message", message);

                    // MediaType correcto para Java
                    MediaType JSON = MediaType.get("application/json; charset=utf-8");

                    RequestBody bodyReq = RequestBody.create(
                            json.toString(),
                            JSON
                    );

                    Request request = new Request.Builder()
                            .url("https://fcm.googleapis.com/v1/projects/155734572412/messages:send")
                            .addHeader("Authorization", "Bearer " + FcmAuth.getAccessToken(ConversationActivity.this))
                            .addHeader("Content-Type", "application/json; charset=utf-8")
                            .post(bodyReq)
                            .build();

                    Response response = client.newCall(request).execute();

                    Log.d("FCM_V1", "Respuesta: " + response.body().string());

                } catch (Exception e) {
                    Log.e("FCM_V1", "Error: " + e.getMessage());
                }

            }).start();  // IMPORTANTÍSIMO: evitar NetworkOnMainThreadException
        }

    }
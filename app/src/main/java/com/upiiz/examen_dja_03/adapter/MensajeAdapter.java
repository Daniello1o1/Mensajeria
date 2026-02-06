package com.upiiz.examen_dja_03.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.upiiz.examen_dja_03.R;
import com.upiiz.examen_dja_03.model.Mensaje;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.ViewHolder> {

    private static final int VIEW_TYPE_ENVIADO = 1;
    private static final int VIEW_TYPE_RECIBIDO = 2;

    private String myUID;

    private List<Mensaje> mensajes;
    private LayoutInflater layoutInflater;

    public MensajeAdapter(Context context, List<Mensaje> mensajes, String myUID) {
        this.mensajes = mensajes;
        this.myUID = myUID;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        Mensaje msg = mensajes.get(position);

        if (msg.getEmisor().equals(myUID)) {
            return VIEW_TYPE_ENVIADO;
        } else {
            return VIEW_TYPE_RECIBIDO;
        }
    }

    @NonNull
    @Override
    public MensajeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_ENVIADO) {
            view = layoutInflater.inflate(R.layout.alt_message, parent, false);
        } else {
            view = layoutInflater.inflate(R.layout.message, parent, false);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeAdapter.ViewHolder holder, int position) {
        Mensaje msg = mensajes.get(position);
        holder.tvMensaje.setText(msg.getTexto());
        holder.tvHour.setText(formatearFecha(msg.getTimestamp()));

    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMensaje, tvHour;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMensaje = itemView.findViewById(R.id.tvMessage);
            tvHour = itemView.findViewById(R.id.tvHour);
        }
    }
    private String formatearFecha(long timestamp) {
        Calendar ahora = Calendar.getInstance();
        Calendar fecha = Calendar.getInstance();
        fecha.setTimeInMillis(timestamp);

        SimpleDateFormat horaFormato = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        SimpleDateFormat fechaFormato = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        // Comparar si es hoy
        boolean mismoDia =
                ahora.get(Calendar.YEAR) == fecha.get(Calendar.YEAR) &&
                        ahora.get(Calendar.DAY_OF_YEAR) == fecha.get(Calendar.DAY_OF_YEAR);

        if (mismoDia) {
            return "hoy -> " + horaFormato.format(fecha.getTime());
        }

        // Comparar si es ayer
        ahora.add(Calendar.DAY_OF_YEAR, -1);
        boolean ayer =
                ahora.get(Calendar.YEAR) == fecha.get(Calendar.YEAR) &&
                        ahora.get(Calendar.DAY_OF_YEAR) == fecha.get(Calendar.DAY_OF_YEAR);

        if (ayer) {
            return "ayer -> " + horaFormato.format(fecha.getTime());
        }

        // Si es más antiguo
        return fechaFormato.format(fecha.getTime()) + " -> " + horaFormato.format(fecha.getTime());
    }


}

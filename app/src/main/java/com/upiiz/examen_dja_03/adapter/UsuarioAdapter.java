package com.upiiz.examen_dja_03.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.upiiz.examen_dja_03.R;
import com.upiiz.examen_dja_03.model.Usuario;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder> {

    public interface OnUsuarioClickListener{
        void onUsuarioClick(String uid);
    }

    private List<Usuario> usuarios;
    private OnUsuarioClickListener listener;
    private LayoutInflater layoutInflater;

    public UsuarioAdapter(Context context, List<Usuario> usuarios, OnUsuarioClickListener listener) {
        this.usuarios = usuarios;
        this.listener = listener;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public UsuarioAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = layoutInflater.inflate(R.layout.usuario_item,parent,false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioAdapter.ViewHolder holder, int position) {
        Usuario u = usuarios.get(position);

        holder.tvName.setText(u.getNombre());
        holder.tvEstado.setText(u.getEstado());

        holder.itemView.setOnClickListener(v -> {
            if(listener!=null){
                listener.onUsuarioClick(u.getUid());
            }
        });

    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEstado;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
        notifyDataSetChanged();
    }

}

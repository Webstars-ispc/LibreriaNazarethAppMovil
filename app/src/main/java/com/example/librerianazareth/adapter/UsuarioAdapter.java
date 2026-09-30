package com.example.librerianazareth.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.librerianazareth.R;
import com.example.librerianazareth.data.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    public interface OnUsuarioClickListener {
        void onEditar(Usuario usuario);
        void onEliminar(Usuario usuario);
    }

    private List<Usuario> usuarios = new ArrayList<>();
    private final OnUsuarioClickListener listener;

    public UsuarioAdapter(OnUsuarioClickListener listener) {
        this.listener = listener;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario u = usuarios.get(position);

        holder.tvNombre.setText(u.getUsername());
        holder.tvCorreo.setText(u.getEmail());
        holder.tvRol.setText("Rol: " + u.getRole());

        holder.btnEditar.setOnClickListener(v -> listener.onEditar(u));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminar(u));
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCorreo, tvRol;
        Button btnEditar, btnEliminar;

        UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreUsuario);
            tvCorreo = itemView.findViewById(R.id.tvCorreoUsuario);
            tvRol = itemView.findViewById(R.id.tvRolUsuario);
            btnEditar = itemView.findViewById(R.id.btnEditarUsuario);
            btnEliminar = itemView.findViewById(R.id.btnEliminarUsuario);
        }
    }
}

package com.example.librerianazareth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.librerianazareth.data.ApiService;
import com.example.librerianazareth.data.RetrofitClient;
import com.example.librerianazareth.data.model.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuariosActivity extends AppCompatActivity {

    private LinearLayout contenedorUsuarios;
    private EditText buscador;

    private List<Usuario> listaUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);

        // Contenedor donde se van a mostrar los usuarios
        contenedorUsuarios = findViewById(R.id.contenedorUsuarios);

        // Buscador
        buscador = findViewById(R.id.etBuscarUsuario);

        // Botón agregar usuario
        findViewById(R.id.fabAgregarUsuario).setOnClickListener(v -> {

            Intent intent = new Intent(
                    UsuariosActivity.this,
                    FormularioUsuarioActivity.class
            );

            startActivity(intent);
        });

        // Buscar usuarios en tiempo real
        buscador.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count) {

                filtrarUsuarios(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Cargar usuarios desde la API
        cargarUsuarios();
    }

    private void cargarUsuarios() {

        ApiService apiService = RetrofitClient.getApi(this);

        apiService.getUsuarios().enqueue(new Callback<List<Usuario>>() {

            @Override
            public void onResponse(
                    Call<List<Usuario>> call,
                    Response<List<Usuario>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    listaUsuarios = response.body();

                    mostrarUsuarios(listaUsuarios);

                    Toast.makeText(
                            UsuariosActivity.this,
                            "Usuarios cargados: " + listaUsuarios.size(),
                            Toast.LENGTH_SHORT
                    ).show();

                } else {

                    Toast.makeText(
                            UsuariosActivity.this,
                            "Error al obtener usuarios: " + response.code(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    Call<List<Usuario>> call,
                    Throwable t) {

                Toast.makeText(
                        UsuariosActivity.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void mostrarUsuarios(List<Usuario> usuarios) {

        contenedorUsuarios.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (Usuario usuario : usuarios) {

            View vistaUsuario = inflater.inflate(
                    R.layout.item_usuario,
                    contenedorUsuarios,
                    false
            );

            TextView tvNombre = vistaUsuario.findViewById(
                    R.id.tvNombreUsuario
            );

            TextView tvCorreo = vistaUsuario.findViewById(
                    R.id.tvCorreoUsuario
            );

            TextView tvRol = vistaUsuario.findViewById(
                    R.id.tvRolUsuario
            );

            tvNombre.setText(usuario.getUsername());
            tvCorreo.setText(usuario.getEmail());

            String rol = usuario.getRole();

            if (rol == null || rol.isEmpty()) {
                tvRol.setText("Rol: Cliente");
            } else {
                tvRol.setText("Rol: " + rol);
            }

            contenedorUsuarios.addView(vistaUsuario);
        }
    }

    private void filtrarUsuarios(String texto) {

        if (listaUsuarios == null) {
            return;
        }

        texto = texto.toLowerCase().trim();

        for (int i = 0; i < contenedorUsuarios.getChildCount(); i++) {

            View vistaUsuario = contenedorUsuarios.getChildAt(i);

            TextView tvNombre = vistaUsuario.findViewById(
                    R.id.tvNombreUsuario
            );

            String nombre = tvNombre.getText()
                    .toString()
                    .toLowerCase();

            if (nombre.contains(texto)) {
                vistaUsuario.setVisibility(View.VISIBLE);
            } else {
                vistaUsuario.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Volver a consultar la API al regresar
        // desde el formulario de usuario.
        cargarUsuarios();
    }
}
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

import com.example.librerianazareth.data.ApiService;
import com.example.librerianazareth.data.RetrofitClient;
import com.example.librerianazareth.data.model.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuariosActivity extends BaseActivity {

    private LinearLayout contenedorUsuarios;
    private EditText buscador;
    private List<Usuario> listaUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);

        setupBottomNavigation(0);

        contenedorUsuarios = findViewById(R.id.contenedorUsuarios);
        buscador = findViewById(R.id.etBuscarUsuario);

        // Botón agregar usuario
        findViewById(R.id.fabAgregarUsuario).setOnClickListener(v -> {
            Intent intent = new Intent(
                    UsuariosActivity.this,
                    FormularioUsuarioActivity.class
            );
            startActivity(intent);
        });

        // Buscador
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

                mostrarUsuariosFiltrados(
                        s.toString().trim()
                );
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        cargarUsuarios();
    }

    private void cargarUsuarios() {

        ApiService apiService = RetrofitClient.getApi(this);

        apiService.getUsuarios().enqueue(
                new Callback<List<Usuario>>() {

                    @Override
                    public void onResponse(
                            Call<List<Usuario>> call,
                            Response<List<Usuario>> response) {

                        if (response.isSuccessful() &&
                                response.body() != null) {

                            listaUsuarios = response.body();

                            mostrarUsuariosFiltrados(
                                    buscador.getText().toString().trim()
                            );

                        } else {

                            Toast.makeText(
                                    UsuariosActivity.this,
                                    "Error al obtener usuarios: "
                                            + response.code(),
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
                                "Error de conexión: "
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void mostrarUsuariosFiltrados(String texto) {

        if (listaUsuarios == null) {
            return;
        }

        contenedorUsuarios.removeAllViews();

        LayoutInflater inflater =
                LayoutInflater.from(this);

        texto = texto.toLowerCase();

        for (Usuario usuario : listaUsuarios) {

            String nombre = usuario.getUsername();

            if (nombre == null) {
                nombre = "";
            }

            if (nombre.toLowerCase().contains(texto)) {

                View vistaUsuario = inflater.inflate(
                        R.layout.item_usuario,
                        contenedorUsuarios,
                        false
                );

                TextView tvNombreUsuario =
                        vistaUsuario.findViewById(
                                R.id.tvNombreUsuario
                        );

                TextView tvCorreoUsuario =
                        vistaUsuario.findViewById(
                                R.id.tvCorreoUsuario
                        );

                TextView tvRolUsuario =
                        vistaUsuario.findViewById(
                                R.id.tvRolUsuario
                        );

                tvNombreUsuario.setText(
                        usuario.getUsername()
                );

                tvCorreoUsuario.setText(
                        usuario.getEmail()
                );

                String rol = usuario.getRole();

                if (rol == null || rol.isEmpty()) {
                    rol = "Sin rol";
                }

                tvRolUsuario.setText(
                        "Rol: " + rol
                );

                contenedorUsuarios.addView(
                        vistaUsuario
                );
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        cargarUsuarios();
    }
}
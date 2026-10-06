package com.example.librerianazareth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.librerianazareth.adapter.UsuarioAdapter;
import com.example.librerianazareth.data.RetrofitClient;
import com.example.librerianazareth.data.model.Usuario;
import com.example.librerianazareth.data.model.UsuarioResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuariosActivity extends BaseActivity
        implements UsuarioAdapter.OnUsuarioClickListener {

    private EditText etBuscarUsuario;
    private RecyclerView rvUsuarios;
    private UsuarioAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEstado;

    // Lista completa recibida desde la API
    private final List<Usuario> todosLosUsuarios = new ArrayList<>();

    // Para esperar 500 ms antes de realizar la búsqueda
    private final Handler searchHandler =
            new Handler(Looper.getMainLooper());

    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_usuarios);

        // Navegación inferior
        setupBottomNavigation(0);

        // Referencias de la pantalla
        etBuscarUsuario = findViewById(R.id.etBuscarUsuario);
        rvUsuarios = findViewById(R.id.rvUsuarios);
        progressBar = findViewById(R.id.progressBarUsuarios);
        tvEstado = findViewById(R.id.tvEstadoUsuarios);

        // Configurar RecyclerView
        adapter = new UsuarioAdapter(this);

        rvUsuarios.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvUsuarios.setAdapter(adapter);

        // ==========================================
        // BÚSQUEDA POR NOMBRE
        // ==========================================

        etBuscarUsuario.addTextChangedListener(
                new TextWatcher() {

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

                        if (searchRunnable != null) {
                            searchHandler.removeCallbacks(
                                    searchRunnable
                            );
                        }

                        searchRunnable = () -> {

                            String texto =
                                    s.toString()
                                            .trim()
                                            .toLowerCase();

                            filtrarUsuarios(texto);
                        };

                        searchHandler.postDelayed(
                                searchRunnable,
                                300
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );

        // ==========================================
        // BOTÓN AGREGAR USUARIO
        // ==========================================

        findViewById(R.id.fabAgregarUsuario)
                .setOnClickListener(v -> {

                    Intent intent = new Intent(
                            UsuariosActivity.this,
                            FormularioUsuarioActivity.class
                    );

                    startActivity(intent);
                });

        // Cargar usuarios al entrar
        cargarUsuarios();
    }

    // ==============================================
    // RECARGAR AL VOLVER DEL FORMULARIO
    // ==============================================

    @Override
    protected void onResume() {
        super.onResume();

        cargarUsuarios();
    }

    // ==============================================
    // OBTENER USUARIOS DESDE LA API
    // ==============================================

    private void cargarUsuarios() {

        progressBar.setVisibility(View.VISIBLE);
        tvEstado.setVisibility(View.GONE);

        Call<UsuarioResponse> call =
                RetrofitClient
                        .getApi(this)
                        .getUsuarios();

        call.enqueue(
                new Callback<UsuarioResponse>() {

                    @Override
                    public void onResponse(
                            Call<UsuarioResponse> call,
                            Response<UsuarioResponse> response) {

                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful()
                                && response.body() != null) {

                            // Limpiar lista anterior
                            todosLosUsuarios.clear();

                            // Guardar usuarios recibidos
                            todosLosUsuarios.addAll(
                                    response.body().getResults()
                            );

                            // Mostrar todos
                            adapter.setUsuarios(
                                    new ArrayList<>(
                                            todosLosUsuarios
                                    )
                            );

                            // Si no hay usuarios
                            if (todosLosUsuarios.isEmpty()) {

                                tvEstado.setText(
                                        "No se encontraron usuarios"
                                );

                                tvEstado.setVisibility(
                                        View.VISIBLE
                                );
                            }

                        } else if (response.code() == 401) {

                            Toast.makeText(
                                    UsuariosActivity.this,
                                    "Sesión expirada",
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else if (response.code() == 403) {

                            tvEstado.setText(
                                    "No tenés permisos para gestionar usuarios"
                            );

                            tvEstado.setVisibility(
                                    View.VISIBLE
                            );

                        } else {

                            tvEstado.setText(
                                    "Error al cargar usuarios: "
                                            + response.code()
                            );

                            tvEstado.setVisibility(
                                    View.VISIBLE
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<UsuarioResponse> call,
                            Throwable t) {

                        progressBar.setVisibility(View.GONE);

                        tvEstado.setText(
                                "Error de conexión: "
                                        + t.getMessage()
                        );

                        tvEstado.setVisibility(
                                View.VISIBLE
                        );
                    }
                }
        );
    }

    // ==============================================
    // FILTRAR USUARIOS POR NOMBRE
    // ==============================================

    private void filtrarUsuarios(String texto) {

        // Si no escribió nada, mostrar todos
        if (texto.isEmpty()) {

            adapter.setUsuarios(
                    new ArrayList<>(
                            todosLosUsuarios
                    )
            );

            if (todosLosUsuarios.isEmpty()) {

                tvEstado.setText(
                        "No se encontraron usuarios"
                );

                tvEstado.setVisibility(
                        View.VISIBLE
                );

            } else {

                tvEstado.setVisibility(
                        View.GONE
                );
            }

            return;
        }

        // Lista filtrada
        List<Usuario> usuariosFiltrados =
                new ArrayList<>();

        for (Usuario usuario : todosLosUsuarios) {

            String nombre =
                    usuario.getUsername();

            if (nombre != null
                    && nombre
                    .toLowerCase()
                    .contains(texto)) {

                usuariosFiltrados.add(usuario);
            }
        }

        // Mostrar resultado
        adapter.setUsuarios(
                usuariosFiltrados
        );

        if (usuariosFiltrados.isEmpty()) {

            tvEstado.setText(
                    "No se encontraron usuarios"
            );

            tvEstado.setVisibility(
                    View.VISIBLE
            );

        } else {

            tvEstado.setVisibility(
                    View.GONE
            );
        }
    }

    // ==============================================
    // EDITAR USUARIO
    // ==============================================

    @Override
    public void onEditar(Usuario usuario) {

        Intent intent = new Intent(
                this,
                FormularioUsuarioActivity.class
        );

        intent.putExtra(
                "modo_edicion",
                true
        );

        intent.putExtra(
                "usuario_id",
                usuario.getId()
        );

        intent.putExtra(
                "nombre_usuario",
                usuario.getUsername()
        );

        intent.putExtra(
                "correo_usuario",
                usuario.getEmail()
        );

        intent.putExtra(
                "rol_usuario",
                usuario.getRole()
        );

        startActivity(intent);
    }

    // ==============================================
    // ELIMINAR USUARIO
    // ==============================================

    @Override
    public void onEliminar(Usuario usuario) {

        new AlertDialog.Builder(this)

                .setTitle("Eliminar usuario")

                .setMessage(
                        "¿Estás seguro de eliminar \""
                                + usuario.getUsername()
                                + "\"?"
                )

                .setPositiveButton(
                        "Eliminar",
                        (dialog, which) -> {

                            RetrofitClient
                                    .getApi(this)
                                    .eliminarUsuario(
                                            usuario.getId()
                                    )
                                    .enqueue(
                                            new Callback<Void>() {

                                                @Override
                                                public void onResponse(
                                                        Call<Void> call,
                                                        Response<Void> response) {

                                                    if (response.isSuccessful()) {

                                                        Toast.makeText(
                                                                UsuariosActivity.this,
                                                                "Usuario eliminado correctamente",
                                                                Toast.LENGTH_SHORT
                                                        ).show();

                                                        cargarUsuarios();

                                                    } else {

                                                        Toast.makeText(
                                                                UsuariosActivity.this,
                                                                "Error al eliminar usuario: "
                                                                        + response.code(),
                                                                Toast.LENGTH_SHORT
                                                        ).show();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(
                                                        Call<Void> call,
                                                        Throwable t) {

                                                    Toast.makeText(
                                                            UsuariosActivity.this,
                                                            "Error de conexión",
                                                            Toast.LENGTH_SHORT
                                                    ).show();
                                                }
                                            }
                                    );
                        }
                )

                .setNegativeButton(
                        "Cancelar",
                        null
                )

                .show();
    }
}
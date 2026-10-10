package com.example.librerianazareth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.librerianazareth.data.RetrofitClient;
import com.example.librerianazareth.data.local.TokenManager;
import com.example.librerianazareth.data.repository.AuthRepository;
import com.example.librerianazareth.ui.login.LoginValidator;
import com.example.librerianazareth.ui.login.LoginViewModel;
import com.example.librerianazareth.ui.login.LoginViewModelFactory;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsuario, etPassword;
    private Button btnLogin;
    private TextView tvModoInvitado;
    private TextView tvIrAlRegistro;
    private TokenManager tokenManager;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsuario      = findViewById(R.id.etUsuario);
        etPassword     = findViewById(R.id.etPassword);
        btnLogin       = findViewById(R.id.btnLogin);
        tvModoInvitado = findViewById(R.id.tvModoInvitado);
        tvIrAlRegistro = findViewById(R.id.tvIrAlRegistro);
        tokenManager   = new TokenManager(this);

        // Inyección de dependencias: repositorio (api) -> ViewModel
        AuthRepository repository = new AuthRepository(RetrofitClient.getApi(this));
        viewModel = new ViewModelProvider(this, new LoginViewModelFactory(repository))
                .get(LoginViewModel.class);

        String emailPrellenado = getIntent().getStringExtra("EXTRA_EMAIL");
        if (emailPrellenado != null && !emailPrellenado.isEmpty()) {
            etUsuario.setText(emailPrellenado);
        }

        btnLogin.setOnClickListener(v -> intentarLogin());

        // Ir a la pantalla de registro
        tvIrAlRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        // Modo invitado
        tvModoInvitado.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("EXTRA_USUARIO", "Invitado");
            intent.putExtra("ES_INVITADO", true);
            startActivity(intent);
            finish();
        });

        observarEstado();
    }

    private void observarEstado() {
        viewModel.getState().observe(this, state -> {
            switch (state.status) {
                case LOADING:
                    btnLogin.setEnabled(false);
                    btnLogin.setText("Ingresando...");
                    break;

                case SUCCESS:
                    tokenManager.saveTokens(state.accessToken, state.refreshToken);
                    tokenManager.saveUsername(state.username);
                    tokenManager.saveRole(state.role);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("EXTRA_USUARIO", state.username);
                    intent.putExtra("ES_INVITADO", false);
                    startActivity(intent);
                    finish();
                    break;

                case ERROR:
                    restaurarBoton();
                    Toast.makeText(LoginActivity.this, state.message, Toast.LENGTH_SHORT).show();
                    break;

                case IDLE:
                default:
                    break;
            }
        });
    }

    private void intentarLogin() {
        String email    = etUsuario.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validaciones locales (muestran el error debajo de cada campo)
        if (email.isEmpty()) {
            etUsuario.setError("Ingresá tu correo");
            etUsuario.requestFocus();
            return;
        }
        if (!LoginValidator.isValidEmail(email)) {
            etUsuario.setError("Ingresá un correo válido");
            etUsuario.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Ingresá tu contraseña");
            etPassword.requestFocus();
            return;
        }

        viewModel.login(email, password);
    }

    private void restaurarBoton() {
        btnLogin.setEnabled(true);
        btnLogin.setText("INGRESAR");
    }
}

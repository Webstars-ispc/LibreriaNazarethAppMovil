package com.example.librerianazareth;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ContactoActivity extends BaseActivity {

    private EditText etNombre;
    private EditText etEmail;
    private EditText etAsunto;
    private EditText etMensaje;
    private TextView btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        TextView btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> finish());

        etNombre = findViewById(R.id.etContactoNombre);
        etEmail = findViewById(R.id.etContactoEmail);
        etAsunto = findViewById(R.id.etContactoAsunto);
        etMensaje = findViewById(R.id.etContactoMensaje);

        btnEnviar = findViewById(R.id.btnEnviarMensaje);
        btnEnviar.setOnClickListener(v -> enviarMensaje());

        setupBottomNavigation(R.id.nav_contacto);
    }

    private void enviarMensaje() {
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String asunto = etAsunto.getText().toString().trim();
        String mensaje = etMensaje.getText().toString().trim();

        if (nombre.isEmpty() || email.isEmpty() || asunto.isEmpty() || mensaje.isEmpty()) {
            Toast.makeText(this, R.string.msj_campos_obligatorios, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, R.string.msj_email_invalido, Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: cuando el backend exponga el endpoint de contacto, enviar el mensaje por POST.
        // Por ahora se simula el envio: se muestra "ENVIANDO..." y luego la confirmacion.
        btnEnviar.setEnabled(false);
        btnEnviar.setText(R.string.enviando_mensaje);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            btnEnviar.setEnabled(true);
            btnEnviar.setText(R.string.enviar_mensaje);
            etNombre.setText("");
            etEmail.setText("");
            etAsunto.setText("");
            etMensaje.setText("");
            Toast.makeText(this, R.string.msj_mensaje_enviado, Toast.LENGTH_SHORT).show();
        }, 1500);
    }
}
package com.darklove.appcalendario;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        EditText etRut, etPassword;
        Button btnIngresar;

        etRut = findViewById(R.id.etRut);
        etPassword = findViewById(R.id.etPassword);
        btnIngresar = findViewById(R.id.btnIngresar);

        // Obtenido de https://es.stackoverflow.com/a/535116
        etRut.addTextChangedListener(new TextWatcher() {
            private boolean isEditing = false;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String rut = String.valueOf(etRut.getText());
                if (!isEditing && !rut.isEmpty()) {
                    isEditing = true;
                    etRut.getText().clear();
                    etRut.append(formatearRUT(rut));
                    isEditing = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnIngresar.setOnClickListener(view -> {
            String rut = etRut.getText().toString();
            String password = etPassword.getText().toString();

            if (!validateRut(rut)) {
                Toast.makeText(getApplicationContext(), "El RUT ingresado no es válido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Ingresa una clave", Toast.LENGTH_SHORT).show();
                return;
            }

            login(rut, password);
        });

    }

    private void login(String rut, String password) {
        LoginRequest loginRequest = new LoginRequest(rut, password);
        String data = loginRequest.getData();

        int id = loginRequest.getUserId(data);
        String token = loginRequest.getToken(data);

        SharedPreferences sharedPreferences = getSharedPreferences("userdata", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("user_id", id);
        editor.putString("token", token);
        editor.apply();
    }

    // Obtenido de https://es.stackoverflow.com/a/156485
    private boolean validateRut(String rut) {
        boolean validation = false;

        try {
            rut = rut.toUpperCase();
            rut = rut.replace(".", "");
            rut = rut.replace("-", "");
            int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));

            char dv = rut.charAt(rut.length() - 1);

            int m = 0, s = 1;
            for (; rutAux != 0; rutAux /= 10) {
                s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
            }
            if (dv == (char) (s != 0 ? s + 47 : 75)) {
                validation = true;
            }
        } catch(Exception e) {}

        return validation;
    }

    // Obtenido de https://es.stackoverflow.com/a/535116
    public String formatearRUT(String rut) {
        String format;
        int cont = 0;
        rut = rut.replace(".", "");
        rut = rut.replace("-", "");
        if ((rut.length() - 1) != 0) {
            format = "-" + rut.substring(rut.length() - 1);
            for (int i = rut.length() - 2; i >= 0; i--) {
                format = rut.charAt(i) + format;
                cont++;
                if (cont == 3 && i != 0) {
                    format = "." + format;
                    cont = 0;
                }
            }
        } else {
            return rut;
        }
        return format;
    }

}
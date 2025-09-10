package com.example.order_manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditText, phoneEditText;
    Button loginButton, goRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        loginButton = findViewById(R.id.loginButton);
        goRegisterButton = findViewById(R.id.goRegisterButton);

        loginButton.setOnClickListener(v -> loginUser());
        goRegisterButton.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        if (email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/order_manager/login.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.equals("admin")) {
                        startActivity(new Intent(this, EmployeeMainActivity.class));
                        finish();
                    } else if (response.equals("client")) {
                        startActivity(new Intent(this, ClientMainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Ошибка сети", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("phone", phone);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}

package com.example.order_manager;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText emailInput, passwordInput;
    Button loginBtn, backToRegisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginBtn = findViewById(R.id.loginBtn);
        backToRegisterBtn = findViewById(R.id.backToRegisterBtn);

        loginBtn.setOnClickListener(v -> loginUser());

        // Обработчик кнопки "Назад к регистрации"
        backToRegisterBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish(); // чтобы не возвращаться обратно на логин
        });
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.1.4/order_manager/login.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("LOGIN_RESPONSE", "Сервер ответил: " + response);

                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            String role = json.getString("role");
                            int userId = json.getInt("user_id"); // <- получаем ID

                            // Сохраняем user_id в SharedPreferences
                            SharedPreferences prefs = getSharedPreferences("myAppPrefs", MODE_PRIVATE);
                            prefs.edit().putInt("user_id", userId).apply();
                            Log.d("LOGIN_DEBUG", "Сохранили user_id = " + userId);

                            if (role.equals("admin")) {
                                startActivity(new Intent(this, EmployeeMainActivity.class));
                            } else if (role.equals("client")) {
                                startActivity(new Intent(this, ClientMainActivity.class));
                            } else {
                                Toast.makeText(this, "Неизвестная роль: " + role, Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        } else {
                            Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("LOGIN_ERROR", "Ошибка парсинга JSON", e);
                        Toast.makeText(this, "Ошибка обработки ответа. Смотри Logcat", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("LOGIN_ERROR", "Volley ошибка", error);
                    Toast.makeText(this, "Ошибка сети. Смотри Logcat", Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}

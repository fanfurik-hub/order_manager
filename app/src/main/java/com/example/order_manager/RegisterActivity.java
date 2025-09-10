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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import android.util.Log;


public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton, exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Инициализация всех полей
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        exitButton = findViewById(R.id.exitButton);

        registerButton.setOnClickListener(v -> registerUser());
        exitButton.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        // Получение текста из всех полей
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        Log.d("REGISTER", "name=" + name + ", email=" + email + ", password=" + password + ", confirm=" + confirmPassword);



        // Проверка заполненности всех полей
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверка совпадения паролей
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return;
        }

        // Определение роли
        String role = email.toLowerCase().contains("admin") ? "admin" : "client";
        String url = "http://10.0.2.2/order_manager/register.php";


        // Отправка POST-запроса на сервер
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            Toast.makeText(this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                            // Переход в нужную активность в зависимости от роли
                            if (role.equals("admin")) {
                                startActivity(new Intent(this, EmployeeMainActivity.class));
                            } else {
                                startActivity(new Intent(this, ClientMainActivity.class));
                            }
                            finish();
                        } else {
                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Ошибка обработки ответа", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Ошибка сети: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                // Отправка всех параметров на сервер
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("role", role);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}

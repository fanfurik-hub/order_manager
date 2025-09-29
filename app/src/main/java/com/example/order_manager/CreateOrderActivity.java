package com.example.order_manager;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateOrderActivity extends AppCompatActivity {

    private Button createOrderBtn, myOrdersBtn, backBtn;
    private EditText lengthEditText, widthEditText, dateEditText;
    private TextView priceTextView;
    private double pricePerSqM = 1000; // цена за 1 кв.м
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        // Получаем user_id из SharedPreferences
        SharedPreferences prefs = getSharedPreferences("myAppPrefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Ошибка: пользователь не найден. Авторизуйтесь снова", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Log.d("ORDER_DEBUG", "Загрузили user_id из SharedPreferences: " + userId);

        lengthEditText = findViewById(R.id.lengthEditText);
        widthEditText = findViewById(R.id.widthEditText);
        dateEditText = findViewById(R.id.dateEditText);
        priceTextView = findViewById(R.id.priceTextView);
        createOrderBtn = findViewById(R.id.createOrderBtn);
        myOrdersBtn = findViewById(R.id.myOrdersBtn);
        backBtn = findViewById(R.id.backBtn);

        // Выбор даты
        dateEditText.setOnClickListener(v -> showDatePicker());

        // Автоматический пересчёт цены при вводе
        lengthEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) calculatePrice();
        });
        widthEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) calculatePrice();
        });

        createOrderBtn.setOnClickListener(v -> createOrder());

        myOrdersBtn.setOnClickListener(v -> {
            startActivity(new Intent(CreateOrderActivity.this, MyOrdersActivity.class));
        });

        backBtn.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, y, m, d) -> {
            String formattedDate = String.format("%04d-%02d-%02d", y, (m + 1), d);
            dateEditText.setText(formattedDate);
        }, year, month, day).show();
    }

    private void calculatePrice() {
        String lengthStr = lengthEditText.getText().toString().trim();
        String widthStr = widthEditText.getText().toString().trim();

        if (!lengthStr.isEmpty() && !widthStr.isEmpty()) {
            try {
                double length = Double.parseDouble(lengthStr);
                double width = Double.parseDouble(widthStr);
                double price = length * width * pricePerSqM;
                priceTextView.setText("Цена: " + price + " тг");
            } catch (NumberFormatException e) {
                priceTextView.setText("Цена: 0");
            }
        } else {
            priceTextView.setText("Цена: 0");
        }
    }

    private void createOrder() {
        String lengthStr = lengthEditText.getText().toString().trim();
        String widthStr = widthEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();

        if (lengthStr.isEmpty() || widthStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        double length, width;
        try {
            length = Double.parseDouble(lengthStr);
            width = Double.parseDouble(widthStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Неверный формат длины/ширины", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = length * width * pricePerSqM;

        String url = "http://10.0.2.2/order_manager/create_order.php";

        Log.d("ORDER_DEBUG", "Отправляем заказ -> user_id=" + userId +
                ", length=" + length + ", width=" + width + ", price=" + price + ", date=" + date);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            int savedUserId = obj.optInt("saved_user_id", -1);
                            Toast.makeText(this,
                                    "Заказ создан! Цена: " + price + " тг\nuser_id = " + savedUserId,
                                    Toast.LENGTH_SHORT).show();

                            Log.d("ORDER_DEBUG", "Сервер сохранил заказ с user_id=" + savedUserId);
                        } else {
                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            Log.e("ORDER_DEBUG", "Ошибка: " + obj.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Ошибка обработки ответа", Toast.LENGTH_SHORT).show();
                        Log.e("ORDER_DEBUG", "Response parse error", e);
                    }
                },
                error -> {
                    Toast.makeText(this, "Ошибка сети: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ORDER_DEBUG", "Volley error", error);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId)); // настоящий user_id
                params.put("length", lengthStr);
                params.put("width", widthStr);
                params.put("date", date);
                params.put("price", String.valueOf(price));

                Log.d("ORDER_DEBUG", "POST params: " + params.toString());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}

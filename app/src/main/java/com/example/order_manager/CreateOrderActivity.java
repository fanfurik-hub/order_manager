package com.example.order_manager;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;

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
            Toast.makeText(this, "Ошибка: пользователь не найден", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        lengthEditText = findViewById(R.id.lengthEditText);
        widthEditText = findViewById(R.id.widthEditText);
        dateEditText = findViewById(R.id.dateEditText);
        priceTextView = findViewById(R.id.priceTextView);
        createOrderBtn = findViewById(R.id.createOrderBtn);
        myOrdersBtn = findViewById(R.id.myOrdersBtn);
        backBtn = findViewById(R.id.backBtn);

        dateEditText.setOnClickListener(v -> showDatePicker());

        lengthEditText.setOnFocusChangeListener((v, hasFocus) -> { if(!hasFocus) calculatePrice(); });
        widthEditText.setOnFocusChangeListener((v, hasFocus) -> { if(!hasFocus) calculatePrice(); });

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
            dateEditText.setText(y + "-" + (m+1) + "-" + d);
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
        }
    }

    private void createOrder() {
        String lengthStr = lengthEditText.getText().toString().trim();
        String widthStr = widthEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();

        if(lengthStr.isEmpty() || widthStr.isEmpty() || date.isEmpty()){
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        double length = Double.parseDouble(lengthStr);
        double width = Double.parseDouble(widthStr);
        double price = length * width * pricePerSqM;

        String url = "http://10.0.2.2/order_manager/create_order.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if(obj.getBoolean("success")){
                            Toast.makeText(this, "Заказ создан! Цена: " + price + " тг", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this, "Ошибка обработки ответа", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Ошибка сети: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId)); // <- используем реальный ID
                params.put("length", String.valueOf(length));
                params.put("width", String.valueOf(width));
                params.put("date", date);
                params.put("price", String.valueOf(price));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}

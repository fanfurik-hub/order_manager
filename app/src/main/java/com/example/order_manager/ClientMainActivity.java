package com.example.order_manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ClientMainActivity extends AppCompatActivity {

    Button btnCreateOrder, btnMyOrders, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_main);

        btnCreateOrder = findViewById(R.id.btnCreateOrder);
        btnMyOrders = findViewById(R.id.btnMyOrders);
        btnBack = findViewById(R.id.btnBack);

        // Кнопка "Создать заказ"
        btnCreateOrder.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateOrderActivity.class));
        });

        // Кнопка "Мои заказы"
        btnMyOrders.setOnClickListener(v -> {
            startActivity(new Intent(this, MyOrdersActivity.class));
        });

        // Кнопка "Назад"
        btnBack.setOnClickListener(v -> {
            finish(); // вернуться на LoginActivity
        });
    }
}

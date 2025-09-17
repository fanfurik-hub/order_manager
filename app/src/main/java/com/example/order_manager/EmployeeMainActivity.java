package com.example.order_manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EmployeeMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_main);

        Button btnViewOrders = findViewById(R.id.btnViewOrders);
        Button btnEditOrders = findViewById(R.id.btnEditOrders);
        Button btnDeleteUser = findViewById(R.id.btnDeleteUser);
        Button btnBack = findViewById(R.id.btnBack);

        // Переход на экран всех заказов
        btnViewOrders.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeMainActivity.this, AllOrdersActivity.class);
            startActivity(intent);
        });

        // Переход на экран редактирования заказа
        btnEditOrders.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeMainActivity.this, EditOrderActivity.class);
            startActivity(intent);
        });

        btnDeleteUser.setOnClickListener(v ->
                Toast.makeText(this, "Удаление пользователя", Toast.LENGTH_SHORT).show());

        btnBack.setOnClickListener(v -> finish());
    }
}

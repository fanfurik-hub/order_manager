package com.example.order_manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class EmployeeMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_main);

        Button btnViewOrders = findViewById(R.id.btnViewOrders);
        Button btnDeleteUser = findViewById(R.id.btnDeleteUser);
        Button btnBack = findViewById(R.id.btnBack);

        // Переход на экран всех заказов
        btnViewOrders.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeMainActivity.this, AllOrdersActivity.class);
            startActivity(intent);
        });

        // Переход на экран удаления пользователей
        btnDeleteUser.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeMainActivity.this, DeleteUserActivity.class);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }
}

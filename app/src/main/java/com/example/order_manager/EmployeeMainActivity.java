package com.example.order_manager;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class EmployeeMainActivity extends AppCompatActivity {

    TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_main);

        welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Добро пожаловать, админ!");
    }
}

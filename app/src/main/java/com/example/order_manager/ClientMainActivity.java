package com.example.order_manager;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ClientMainActivity extends AppCompatActivity {

    TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_main);

        welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Добро пожаловать, клиент!");
    }
}

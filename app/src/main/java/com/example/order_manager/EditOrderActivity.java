package com.example.order_manager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditOrderActivity extends AppCompatActivity {

    EditText editName, editEmail, editPassword, editDate, editStatus;
    Button btnSaveOrder, btnBackOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order);

        // Находим все элементы по ID
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editDate = findViewById(R.id.editDate);
        editStatus = findViewById(R.id.editStatus);

        btnSaveOrder = findViewById(R.id.btnSaveOrder);
        btnBackOrder = findViewById(R.id.btnBackOrder);

        // Кнопка Назад
        btnBackOrder.setOnClickListener(v -> finish());

        // Кнопка Сохранить (пока только Toast)
        btnSaveOrder.setOnClickListener(v ->
                Toast.makeText(EditOrderActivity.this, "Сохранение изменений...", Toast.LENGTH_SHORT).show()
        );
    }
}

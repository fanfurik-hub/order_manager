package com.example.order_manager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog; // 🔹 добавь

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class EditOrderActivity extends AppCompatActivity {

    EditText editName, editEmail, editPassword, editDate, editStatus;
    Button btnSaveOrder, btnBackOrder;

    String orderId; // сохраняем ID заказа

    // 🔹 список статусов
    private static final String[] STATUS_OPTIONS = {
            "Новый", "Принят", "Забран", "В чистке", "Готов", "Доставлен", "Отменён"
    };

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

        // 🔹 делаем поле статуса кликабельным (без клавиатуры) и вешаем диалог
        editStatus.setFocusable(false);
        editStatus.setClickable(true);
        editStatus.setKeyListener(null);
        editStatus.setOnClickListener(v -> showStatusPicker());

        btnSaveOrder = findViewById(R.id.btnSaveOrder);
        btnBackOrder = findViewById(R.id.btnBackOrder);

        // Получаем данные из Intent (если есть) и подставляем в поля
        Intent intent = getIntent();
        if (intent != null) {
            orderId = intent.getStringExtra("id");

            String name = intent.getStringExtra("name");
            String email = intent.getStringExtra("email");
            // Пароль не подставляем (чтобы не показывать хэш)
            String date = intent.getStringExtra("date");
            String status = intent.getStringExtra("status");

            if (name == null || name.isEmpty()) {
                name = intent.getStringExtra("user_id");
            }

            editName.setText(name != null ? name : "");
            editEmail.setText(email != null ? email : "");
            editPassword.setText(""); // поле оставляем пустым
            editDate.setText(date != null ? date : "");
            editStatus.setText(status != null ? status : "");
        }

        // Кнопка Назад
        btnBackOrder.setOnClickListener(v -> finish());

        // Кнопка Сохранить
        btnSaveOrder.setOnClickListener(v -> {
            if (orderId == null || orderId.isEmpty()) {
                Toast.makeText(this, "Ошибка: нет ID заказа", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = editName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();
            String date = editDate.getText().toString().trim();
            String status = editStatus.getText().toString().trim();

            new UpdateOrderTask().execute(orderId, name, email, password, date, status);
        });
    }

    // 🔹 диалог выбора статуса
    private void showStatusPicker() {
        int preselect = -1;
        String cur = editStatus.getText() != null ? editStatus.getText().toString() : "";
        for (int i = 0; i < STATUS_OPTIONS.length; i++) {
            if (STATUS_OPTIONS[i].equalsIgnoreCase(cur)) { preselect = i; break; }
        }

        new AlertDialog.Builder(this)
                .setTitle("Выберите статус")
                .setSingleChoiceItems(STATUS_OPTIONS, preselect, (d, which) -> {
                    editStatus.setText(STATUS_OPTIONS[which]);
                    d.dismiss();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    // AsyncTask для отправки данных (без изменений)
    private class UpdateOrderTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String id = params[0];
                String name = params[1];
                String email = params[2];
                String password = params[3];
                String date = params[4];
                String status = params[5];

                // берём employee_id
                int employeeId = getSharedPreferences("myAppPrefs", MODE_PRIVATE)
                        .getInt("user_id", -1);

                URL url = new URL("http://192.168.1.4/order_manager/update_order.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String postData =
                        "id=" + URLEncoder.encode(id, "UTF-8") +
                                "&name=" + URLEncoder.encode(name, "UTF-8") +
                                "&email=" + URLEncoder.encode(email, "UTF-8") +
                                "&password=" + URLEncoder.encode(password, "UTF-8") +
                                "&date=" + URLEncoder.encode(date, "UTF-8") +
                                "&status=" + URLEncoder.encode(status, "UTF-8") +
                                "&order_id=" + URLEncoder.encode(id, "UTF-8") +
                                "&new_status=" + URLEncoder.encode(status, "UTF-8") +
                                "&employee_id=" + URLEncoder.encode(String.valueOf(employeeId), "UTF-8");

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.getBytes());
                    os.flush();
                }

                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line; while ((line = reader.readLine()) != null) sb.append(line);
                }
                return sb.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return "{\"success\":false, \"error\":\"Exception: " + e.getMessage() + "\"}";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.contains("\"success\":true")) {
                Toast.makeText(EditOrderActivity.this, "Изменения сохранены", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(EditOrderActivity.this, "Ошибка: " + result, Toast.LENGTH_LONG).show();
            }
        }
    }
}

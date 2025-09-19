package com.example.order_manager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DeleteUserActivity extends AppCompatActivity {

    ListView usersListView;
    ArrayList<String> users = new ArrayList<>();
    ArrayList<Integer> userIds = new ArrayList<>();
    ArrayAdapter<String> adapter;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);

        usersListView = findViewById(R.id.usersListView);
        btnBack = findViewById(R.id.btnBack);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, users);
        usersListView.setAdapter(adapter);

        // Загружаем пользователей из БД
        loadUsers();

        // Клик по пользователю → показать диалог удаления
        usersListView.setOnItemClickListener((parent, view, position, id) -> {
            int userId = userIds.get(position);
            String userName = users.get(position);
            showDeleteDialog(userId, userName);
        });

        // Кнопка "Назад"
        btnBack.setOnClickListener(v -> finish());
    }

    // Метод загрузки пользователей
    private void loadUsers() {
        String url = "http://10.0.2.2/order_manager/get_users.php";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        users.clear();
                        userIds.clear();

                        JSONObject root = new JSONObject(response);
                        if (root.getBoolean("success")) {
                            JSONArray array = root.getJSONArray("users");

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                int id = obj.getInt("id");
                                String email = obj.getString("email");

                                users.add(email);
                                userIds.add(id);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, root.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("LOAD_USERS", "Ошибка JSON", e);
                        Toast.makeText(this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("LOAD_USERS", "Volley ошибка", error);
                    Toast.makeText(this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    // Метод диалога подтверждения
    private void showDeleteDialog(int userId, String userName) {
        new AlertDialog.Builder(this)
                .setTitle("Удаление пользователя")
                .setMessage("Удалить пользователя " + userName + "?")
                .setPositiveButton("Да", (dialog, which) -> deleteUser(userId))
                .setNegativeButton("Нет", null)
                .show();
    }

    // Метод удаления
    private void deleteUser(int userId) {
        String url = "http://10.0.2.2/order_manager/delete_user.php?id=" + userId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    Toast.makeText(this, "Пользователь удалён", Toast.LENGTH_SHORT).show();
                    loadUsers(); // обновляем список
                },
                error -> {
                    Log.e("DELETE_USER", "Volley ошибка", error);
                    Toast.makeText(this, "Ошибка при удалении", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }
}

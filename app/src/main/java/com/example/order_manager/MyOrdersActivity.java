package com.example.order_manager;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MyOrdersActivity extends AppCompatActivity {

    private Button backBtn;
    private ListView ordersListView;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        // Получаем user_id из SharedPreferences
        SharedPreferences prefs = getSharedPreferences("myAppPrefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Ошибка: пользователь не найден", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        backBtn = findViewById(R.id.backBtn);
        ordersListView = findViewById(R.id.ordersListView);

        backBtn.setOnClickListener(v -> finish());

        fetchOrders();
    }

    private void fetchOrders() {
        String url = "http://10.0.2.2/order_manager/get_orders.php?user_id=" + userId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONArray orders = json.getJSONArray("orders");
                            ArrayList<HashMap<String, String>> list = new ArrayList<>();

                            for (int i = 0; i < orders.length(); i++) {
                                JSONObject order = orders.getJSONObject(i);
                                HashMap<String, String> map = new HashMap<>();
                                map.put("info", "Размер: " + order.getString("length") + "x" + order.getString("width") +
                                        " м, Цена: " + order.getString("price") + " тг, Дата: " + order.getString("date"));
                                list.add(map);
                            }

                            SimpleAdapter adapter = new SimpleAdapter(
                                    this,
                                    list,
                                    android.R.layout.simple_list_item_1,
                                    new String[]{"info"},
                                    new int[]{android.R.id.text1}
                            );

                            ordersListView.setAdapter(adapter);

                        } else {
                            Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Ошибка обработки ответа", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Ошибка сети: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }
}

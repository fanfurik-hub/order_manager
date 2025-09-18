package com.example.order_manager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AllOrdersActivity extends AppCompatActivity {

    ListView listOrders;
    Button btnBackOrders;
    // Сохраняем массив заказов, чтобы по позиции взять объект
    private JSONArray ordersArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_orders);

        listOrders = findViewById(R.id.listOrders);
        btnBackOrders = findViewById(R.id.btnBackOrders);

        btnBackOrders.setOnClickListener(v -> finish());

        // Сразу загружаем реальные данные
        new LoadOrdersTask().execute();
    }

    private class LoadOrdersTask extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            ArrayList<String> ordersList = new ArrayList<>();
            try {
                // Меняем URL на твой PHP скрипт (оставил 10.0.2.2 для эмулятора)
                URL url = new URL("http://10.0.2.2/order_manager/get_all_orders.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                // Заполняем ordersArray чтобы использовать позже при клике
                ordersArray = new JSONArray(sb.toString());

                for (int i = 0; i < ordersArray.length(); i++) {
                    JSONObject order = ordersArray.getJSONObject(i);
                    String orderText = "ID: " + order.optInt("id") +
                            ", User ID: " + order.optString("user_id") +
                            ", Length: " + order.optString("length") +
                            ", Width: " + order.optString("width") +
                            ", Price: " + order.optString("price") +
                            ", Date: " + order.optString("date") +
                            ", Status: " + order.optString("status");
                    ordersList.add(orderText);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return ordersList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> orders) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    AllOrdersActivity.this,
                    android.R.layout.simple_list_item_1,
                    orders
            );
            listOrders.setAdapter(adapter);

            // Добавляем обработчик клика: открываем EditOrderActivity и передаём все поля из JSON (если есть)
            listOrders.setOnItemClickListener((parent, view, position, id) -> {
                try {
                    if (ordersArray == null) return;
                    JSONObject order = ordersArray.getJSONObject(position);

                    Intent intent = new Intent(AllOrdersActivity.this, EditOrderActivity.class);

                    // Кладём в интент все возможные поля (если их нет — положится пустая строка)
                    intent.putExtra("id", order.optString("id", ""));
                    intent.putExtra("user_id", order.optString("user_id", ""));
                    intent.putExtra("length", order.optString("length", ""));
                    intent.putExtra("width", order.optString("width", ""));
                    intent.putExtra("price", order.optString("price", ""));
                    intent.putExtra("date", order.optString("date", ""));
                    intent.putExtra("status", order.optString("status", ""));

                    // Если в JSON есть дополнительные поля (name, email, password), тоже передаём их
                    intent.putExtra("name", order.optString("name", order.optString("user_name", "")));
                    intent.putExtra("email", order.optString("email", ""));
                    intent.putExtra("password", order.optString("password", ""));

                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}

package com.example.order_manager;

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
                // Меняем URL на твой PHP скрипт
                URL url = new URL("http://10.0.2.2/order_manager/get_all_orders.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                JSONArray ordersArray = new JSONArray(sb.toString());
                for (int i = 0; i < ordersArray.length(); i++) {
                    JSONObject order = ordersArray.getJSONObject(i);
                    String orderText = "ID: " + order.getInt("id") +
                            ", User ID: " + order.getInt("user_id") +
                            ", Length: " + order.getDouble("length") +
                            ", Width: " + order.getDouble("width") +
                            ", Price: " + order.getDouble("price") +
                            ", Date: " + order.getString("date") +
                            ", Status: " + order.getString("status");
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
        }
    }
}

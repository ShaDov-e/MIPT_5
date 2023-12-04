package com.example.mipt_5_ce;

import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static MainActivity instance;
    private ListView listView;
    private EditText filterEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;
        listView = findViewById(R.id.listView);
        filterEditText = findViewById(R.id.filterEditText);

        // Set up the adapter and set it to the ListView
        List<String> exchangeRates = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, exchangeRates);
        listView.setAdapter(adapter);
    }

    public static MainActivity getInstance() {
        return instance;
    }

    // Called when the user clicks the "Load Data" button
    public void loadData(View view) {
        // Get the selected currency code from the EditText or Dropdown item
        String selectedCurrency = filterEditText.getText().toString();

        if (!selectedCurrency.isEmpty()) {
            // Execute DataLoader AsyncTask to fetch data
            new DataLoader().execute(selectedCurrency);
        } else {
            Toast.makeText(this, "Enter a currency code", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to update the UI with the fetched data
    public void updateUI(List<String> data) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
        adapter.clear();
        adapter.addAll(data);
    }
}


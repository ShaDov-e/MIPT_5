package com.example.mipt_5_ce;

import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

        private static MainActivity instance;
        private EditText amountEditText;
        private Spinner sourceCurrencySpinner;
        private Spinner targetCurrencySpinner;
        private TextView convertedTextView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);

                instance = this;
                amountEditText = findViewById(R.id.amountEditText);
                sourceCurrencySpinner = findViewById(R.id.sourceCurrencySpinner);
                targetCurrencySpinner = findViewById(R.id.targetCurrencySpinner);
                convertedTextView = findViewById(R.id.convertedTextView);

                // Populate the source and target currency Spinners with default values
                List<String> defaultCurrencies = new ArrayList<>();
                defaultCurrencies.add("USD");
                defaultCurrencies.add("EUR");

                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, defaultCurrencies);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                sourceCurrencySpinner.setAdapter(spinnerAdapter);
                targetCurrencySpinner.setAdapter(spinnerAdapter);

                // Execute DataLoader AsyncTask to fetch currency options
                new DataLoader().execute();
        }

        public static MainActivity getInstance() {
                return instance;
        }

        // Called when the user clicks the "Convert" button
        public void convertCurrency(View view) {
                // Get the selected source and target currencies
                String sourceCurrency = (String) sourceCurrencySpinner.getSelectedItem();
                String targetCurrency = (String) targetCurrencySpinner.getSelectedItem();

                if (sourceCurrency != null && targetCurrency != null) {
                        // Execute DataLoader AsyncTask to fetch exchange rates and perform conversion
                        String amountString = amountEditText.getText().toString();
                        if (!amountString.isEmpty()) {
                                double amount = Double.parseDouble(amountString);
                                new DataLoader().execute(sourceCurrency, targetCurrency, String.valueOf(amount));
                        } else {
                                Toast.makeText(this, "Enter an amount", Toast.LENGTH_SHORT).show();
                        }
                } else {
                        Toast.makeText(this, "Select source and target currencies", Toast.LENGTH_SHORT).show();
                }
        }

        // Method to update the UI with the fetched data
        public void updateCurrencyOptions(List<String> currencyOptions) {
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencyOptions);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                sourceCurrencySpinner.setAdapter(spinnerAdapter);
                targetCurrencySpinner.setAdapter(spinnerAdapter);
        }

        // Method to update the UI with the converted currency
        public void updateConvertedCurrency(String convertedCurrency) {
                convertedTextView.setText("Converted Currency: " + convertedCurrency);
        }
}





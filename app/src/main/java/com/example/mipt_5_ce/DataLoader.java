package com.example.mipt_5_ce;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DataLoader extends AsyncTask<String, Void, Void> {

    private List<String> currencyOptions;
    private String convertedCurrency;

    @Override
    protected Void doInBackground(String... params) {
        if (params.length > 0) {
            // Fetch initial currency options
            fetchCurrencyOptions();

            // Fetch exchange rates and perform conversion
            if (params.length > 2) {
                String sourceCurrency = params[0];
                String targetCurrency = params[1];
                double amount = Double.parseDouble(params[2]);

                fetchExchangeRates(sourceCurrency, targetCurrency, amount);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        // Update UI with fetched data
        if (currencyOptions != null) {
            MainActivity.getInstance().updateCurrencyOptions(currencyOptions);
        }

        if (convertedCurrency != null) {
            MainActivity.getInstance().updateConvertedCurrency(convertedCurrency);
        }
    }

    private void fetchCurrencyOptions() {
        try {
            URL url = new URL("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            InputStream inputStream = connection.getInputStream();
            Parser parser = new Parser();
            currencyOptions = parser.parseCurrencyOptions(inputStream);

            inputStream.close();
            connection.disconnect();

        } catch (Exception e) {
            Log.e("DataLoader", "Error fetching currency options: " + e.getMessage());
        }
    }

    private void fetchExchangeRates(String sourceCurrency, String targetCurrency, double amount) {
        try {
            URL url = new URL("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            InputStream inputStream = connection.getInputStream();
            Parser parser = new Parser();

            // Fetch exchange rates for both source and target currencies
            double sourceRate = parser.parseExchangeRate(inputStream, sourceCurrency, targetCurrency);
            double targetRate = parser.parseExchangeRate(inputStream, sourceCurrency, targetCurrency);

            // Calculate the converted amount using both rates
            double convertedAmount = (amount / sourceRate) * targetRate;
            convertedCurrency = String.format("%.2f %s", convertedAmount, targetCurrency);

            inputStream.close();
            connection.disconnect();

        } catch (Exception e) {
            Log.e("DataLoader", "Error fetching exchange rates: " + e.getMessage());
        }
    }

}




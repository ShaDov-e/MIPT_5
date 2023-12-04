package com.example.mipt_5_ce;

import android.os.AsyncTask;
import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);

            Element rootElement = doc.getDocumentElement();
            NodeList cubeList = rootElement.getElementsByTagName("Cube");

            currencyOptions = new ArrayList<>();

            for (int i = 0; i < cubeList.getLength(); i++) {
                Element cube = (Element) cubeList.item(i);
                if (cube.hasAttribute("currency")) {
                    currencyOptions.add(cube.getAttribute("currency"));
                }
            }

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
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);

            Element rootElement = doc.getDocumentElement();
            NodeList cubeList = rootElement.getElementsByTagName("Cube");

            double sourceRate = 1.0;
            double targetRate = 1.0;

            for (int i = 0; i < cubeList.getLength(); i++) {
                Element cube = (Element) cubeList.item(i);
                if (cube.hasAttribute("currency") && cube.hasAttribute("rate")) {
                    String currency = cube.getAttribute("currency");
                    double rate = Double.parseDouble(cube.getAttribute("rate"));

                    if (currency.equals(sourceCurrency)) {
                        sourceRate = rate;
                    } else if (currency.equals(targetCurrency)) {
                        targetRate = rate;
                    }
                }
            }

            double convertedAmount = (amount / sourceRate) * targetRate;
            convertedCurrency = String.format("%.2f %s", convertedAmount, targetCurrency);

            inputStream.close();
            connection.disconnect();

        } catch (Exception e) {
            Log.e("DataLoader", "Error fetching exchange rates: " + e.getMessage());
        }
    }
}


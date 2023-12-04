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

public class DataLoader extends AsyncTask<String, Void, List<String>> {

    @Override
    protected List<String> doInBackground(String... params) {
        String selectedCurrency = params[0];
        List<String> data = new ArrayList<>();

        try {
            // Construct the URL
            URL url = new URL("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");

            // Open connection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            InputStream in = urlConnection.getInputStream();

            // Parse XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(in);
            doc.getDocumentElement().normalize();

            // Find the Cube containing the rates
            NodeList cubeList = doc.getElementsByTagName("Cube");
            for (int i = 0; i < cubeList.getLength(); i++) {
                Element cubeElement = (Element) cubeList.item(i);

                if (cubeElement.hasAttribute("time")) {
                    // This is the Cube containing rates
                    NodeList ratesList = cubeElement.getElementsByTagName("Cube");
                    for (int j = 0; j < ratesList.getLength(); j++) {
                        Element rateElement = (Element) ratesList.item(j);

                        String currencyCode = rateElement.getAttribute("currency");
                        String rate = rateElement.getAttribute("rate");

                        // Filter by selected currency
                        if (selectedCurrency.isEmpty() || currencyCode.equals(selectedCurrency)) {
                            data.add(currencyCode + " - " + rate);
                        }
                    }
                }
            }

            // Close connection
            in.close();
            urlConnection.disconnect();
        } catch (Exception e) {
            Log.e("DataLoader", "Error fetching data: " + e.getMessage());
        }

        return data;
    }

    @Override
    protected void onPostExecute(List<String> result) {
        MainActivity mainActivity = MainActivity.getInstance();
        if (mainActivity != null) {
            mainActivity.updateUI(result);
        }
    }
}

package com.hackpsuedu.psh.hackpsuspring2016;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ExpandedMenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    TextView results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        results = (TextView) findViewById(R.id.textView);

        CurrentWeather currentWeater = new CurrentWeather();
        currentWeater.execute("17050");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class CurrentWeather extends AsyncTask<String, Void, HashMap<String, String>> {

        @Override
        protected HashMap<String, String> doInBackground(String... params) {
            String api = "c3e7df4f2d6a40698cc75fac1b6a2c83";
            String zipCode = "17050";
            String units = "Imperial"; // Imperial Metric

            String request =
                    String.format("http://apidev.accuweather.com/locations/v1/postalcodes/search.json?q=%s&apikey=%s", zipCode, api);
            try {
                URL url = new URL(request);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null)
                    builder.append(line);

                JSONArray arr = new JSONArray(builder.toString());
                String Key = arr.getJSONObject(0).getString("Key");

                request =
                        String.format("http://apidev.accuweather.com/currentconditions/v1/%s.json?apikey=%s", Key, api);

                URL weatherUrl = new URL(request);
                HttpURLConnection weatherConnection = (HttpURLConnection) weatherUrl.openConnection();
                BufferedReader weatherReader = new BufferedReader(new InputStreamReader(weatherConnection.getInputStream()));
                StringBuilder weatherBuilder = new StringBuilder();
                String weatherLine;

                while ((weatherLine = weatherReader.readLine()) != null)
                    weatherBuilder.append(weatherLine);


                arr = new JSONArray(weatherBuilder.toString());
                JSONObject weatherJsonObject = arr.getJSONObject(0);
                String weatherDescription = weatherJsonObject.getString("WeatherText");
                JSONObject temp = weatherJsonObject.getJSONObject("Temperature");
                temp = temp.getJSONObject(units);
                int currentTemp = temp.getInt("Value");
                String weatherUnits = temp.getString("Unit");


                HashMap<String, String> retVal = new HashMap<>();
                retVal.put("currentTemp", String.valueOf(currentTemp));
                retVal.put("weatherUnits", weatherUnits);
                retVal.put("weatherDescription", weatherDescription);

                return retVal;
            } catch (Exception e) {
                Log.e("CurrentWeatherAPI", e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> setText) {
            super.onPostExecute(setText);
            results.setText(setText.toString());
        }
    }

}

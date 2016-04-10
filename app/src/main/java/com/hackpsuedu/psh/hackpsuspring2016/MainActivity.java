package com.hackpsuedu.psh.hackpsuspring2016;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ExpandedMenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    TextView temputure;
    TextView Desciption;
    ImageView WeatherIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        temputure = (TextView) findViewById(R.id.mainCurrentTemputure);
        Desciption = (TextView) findViewById(R.id.mainWeatherDesctiption);
        WeatherIcon = (ImageView) findViewById(R.id.mainWeatherIcon);

    }

    @Override
    protected void onResume() {
        super.onResume();

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

            startActivity(new Intent(this, settings.class));
            return false;
        }

        return super.onOptionsItemSelected(item);
    }


    private class CurrentWeather extends AsyncTask<String, Void, HashMap<String, String>> {

        @Override
        protected HashMap<String, String> doInBackground(String... params) {
            String api = "c3e7df4f2d6a40698cc75fac1b6a2c83";

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String zipCode = prefs.getString("zipCodeKey", "17050");


            String units = prefs.getString("Units", "Imperial");
            //String units = "Imperial"; // Imperial Metric

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
                int weatherIcon = weatherJsonObject.getInt("WeatherIcon");
                String weatherDescription = weatherJsonObject.getString("WeatherText");
                JSONObject temp = weatherJsonObject.getJSONObject("Temperature");
                temp = temp.getJSONObject(units);
                int currentTemp = temp.getInt("Value");
                String weatherUnits = temp.getString("Unit");

                String weatherIconString = String.valueOf(weatherIcon);
                if (weatherIconString.length() == 1)
                    weatherIconString += "0" + weatherIconString;

                String iconURL = String.format("https://apidev.accuweather.com/developers/Media/Default/WeatherIcons/%s-s.png", weatherIconString);

                HashMap<String, String> retVal = new HashMap<>();
                retVal.put("currentTemp", String.valueOf(currentTemp));
                retVal.put("weatherUnits", weatherUnits);
                retVal.put("weatherDescription", weatherDescription);
                retVal.put("weatherIcon", weatherIconString);
                retVal.put("weatherIconPic", iconURL);

                return retVal;
            } catch (Exception e) {
                Log.e("CurrentWeatherAPI", e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> setText) {
            super.onPostExecute(setText);

            if (setText != null) {
                temputure.setText(MessageFormat.format("{0} {1}", setText.get("currentTemp"), setText.get("weatherUnits")));
                Desciption.setText(setText.get("weatherDescription"));
                Picasso.with(getApplicationContext()).load(setText.get("weatherIconPic")).into(WeatherIcon);

            } else {
                temputure.setText(R.string.invalidZipCode);
                Desciption.setText(R.string.invalidZipCode);

            }

        }
    }

    public void onSettingsClick(View view) {
        Intent intent = new Intent(this, settings.class);
        startActivity(intent);
    }

}

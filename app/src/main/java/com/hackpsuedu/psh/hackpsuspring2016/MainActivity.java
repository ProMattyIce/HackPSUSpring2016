package com.hackpsuedu.psh.hackpsuspring2016;


import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, android.view.View.OnClickListener {

    TextView temputure;
    TextView Desciption;
    ImageView WeatherIcon;
    ListView listView;

    private SimpleCursorAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        temputure = (TextView) findViewById(R.id.mainCurrentTemputure);
        Desciption = (TextView) findViewById(R.id.mainWeatherDesctiption);
        WeatherIcon = (ImageView) findViewById(R.id.mainWeatherIcon);

        listView = (ListView) findViewById(R.id.Scores);

        ArrayList<String> array = new ArrayList<String>();
        array.add("Run");
        array.add("Bike");
        array.add("Swim");
        array.add("Work-out");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

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

    public void onClick(View view) {
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AlertDialog.Builder counter = new AlertDialog.Builder(MainActivity.this);
        counter.setMessage(R.string.where)
                .setPositiveButton(R.string.in, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "Inside",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .setNeutralButton(R.string.out, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "Outside",
                                Toast.LENGTH_LONG).show();
                    }
                });
        counter.show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent intent = new Intent(MainActivity.this, AddActivity.class);
//        intent.putExtra("rowid", id);
//        startActivity(intent);
//        return true;
        return false;
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
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
                    weatherIconString = "0" + weatherIconString;

                String iconURL =
                        String.format("https://apidev.accuweather.com/developers/Media/Default/WeatherIcons/%s-s.png", weatherIconString);

                Log.v("WEATHERAPI", iconURL);

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

package com.hackpsuedu.psh.hackpsuspring2016;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Set Defaults
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.settings);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            //IT NEVER GETS IN HERE!
            if (key.equals("zipCodeKey")) {

                // Set summary to be the user-description for the selected value
                Preference zipCode = findPreference(key);
                zipCode.setSummary("Current: " + sharedPreferences.getString(key, ""));
            }
            if (key.equals("Units")) {
                Preference units = findPreference(key);
                units.setSummary("Units: " + sharedPreferences.getString(key, ""));

            }

        }

        @Override
        public void onResume() {
            super.onResume();

            SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
            onSharedPreferenceChanged(prefs, "zipCodeKey");
            onSharedPreferenceChanged(prefs, "Units");

            prefs.registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }
    }

}

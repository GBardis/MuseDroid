package com.example.musedroid.musedroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Locale;


public class UserSettingActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference mDatabase = database.getReference();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String s) {
        switch (s) {
            case "prefUsername":
                // Set username from firebase if username does not exist it will set the username to null
                if (!sharedPrefs.getString("prefUsername", "NULL").equals("NULL")) {
                    try {
                        //get username from preferences , email and uid  from firebase
                        String userEmail = auth.getCurrentUser().getEmail();
                        String uId = auth.getCurrentUser().getUid();

                        String username = sharedPrefs.getString("prefUsername", "NULL");
                        //save new user settings in firebase
                        User user = new User(username, userEmail);
                        mDatabase.child("users").child(uId).setValue(user);

                    } catch (NullPointerException ex) {
                        sharedPrefs.getString("prefUsername", "NULL");
                        Log.e("NullPointerException", ex.getMessage());
                        Log.e("NullPointerException", Arrays.toString(ex.getStackTrace()));
                    }
                }
                break;
            case "prefAppLanguage":
                //change language app if a language is selected in dropdown select else
                if (!sharedPrefs.getString("prefAppLanguage", "NULL").equals("NULL")) {
                    //set the language of the as user wants
                    sharedPrefs.getString("prefAppLanguage", sharedPrefs.getString("prefAppLanguage", "NULL"));
                    updateViews(sharedPrefs.getString("prefAppLanguage", "NULL"));
                    this.recreate();
                } else {
                    //set the language from system locale
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putString("prefAppLanguage", Locale.getDefault().getLanguage());
                    editor.apply();
                    updateViews(sharedPrefs.getString("prefAppLanguage", "NULL"));
                }
                break;

            case "distanceInterval":
                    int interval = sharedPrefs.getInt("distanceInterval", 0);

            default:
                break;
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void updateViews(String language) {
        LocaleHelper.setLocale(this, language);
    }
}

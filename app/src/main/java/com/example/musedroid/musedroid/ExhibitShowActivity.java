package com.example.musedroid.musedroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ExhibitShowActivity extends AppCompatActivity {
    private static final String DESCRIPTION = "exhibit_description";
    private static final String TITLE = "exhibit_title";
    private static final String USER_LANG = "user language";
    public static Exhibit exhibit;
    public static String exhibitId, language;
    public ExhibitFields exhibitFields;
    public String userLanguage;
    TextView exhibitName, exhibitDescription;
    Intent intent;
    Context context;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit_show);
        context = this;
        intent = getIntent();
        ExhibitShowActivity.exhibitId = intent.getStringExtra("exhibitId");

        if (savedInstanceState == null) {
            getExhibitFields();
            userLanguage = userSettingsLang();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // Save custom values into the bundle
        if (savedInstanceState != null) {
            savedInstanceState.putString(USER_LANG, userLanguage);
            savedInstanceState.putString(DESCRIPTION, exhibit.description);
            savedInstanceState.putString(TITLE, exhibit.name);
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    //This function restores  Exhibit settings after orientation
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            userLanguage = savedInstanceState.getString(USER_LANG);
            savedInstanceState.getString(DESCRIPTION, exhibit.description);
            savedInstanceState.getString(TITLE, exhibit.name);
            try {
                exhibitName = findViewById(R.id.exhibitName);
                exhibitDescription = findViewById(R.id.exhibitDescription);
                exhibitDescription.setText(exhibit.description);
                exhibitName.setText(exhibit.name);
            } catch (Exception ex) {

                intent = getIntent();
                Exhibit exhibit = intent.getParcelableExtra("Exhibit");
            }
        }
    }

    public void getExhibitFields() {
        mDatabase.child("exhibitFields").orderByChild("exhibit").equalTo(ExhibitShowActivity.exhibitId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    exhibitFields = dataSnapshot.getValue(ExhibitFields.class);
                    //exhibitFieldsList.add(exhibitFields);
                    assert exhibitFields != null;
                    if (exhibitFields.exhibit.equals(ExhibitShowActivity.exhibitId) && exhibitFields.language.equals(userLanguage.toLowerCase())) {
                        exhibitDescription = findViewById(R.id.exhibitDescription);
                        exhibitDescription.setText(exhibitFields.description);
                        exhibitName = findViewById(R.id.exhibitName);
                        exhibitName.setText(exhibitFields.name);
                    }
                } catch (Exception ex) {

                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String userSettingsLang() {
        //get user settings language
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        return sharedPrefs.getString("prefAppLanguage", "NULL");
    }
}

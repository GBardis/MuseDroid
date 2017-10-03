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

import java.util.List;

public class ExhibitShowActivity extends AppCompatActivity {
    private static final String DESCRIPTION = "exhibit_description";
    private static final String TITLE = "exhibit_title";
    public static Exhibit exhibit;
    TextView exhibitName, exhibitDescription;
    Intent intent;
    Context context;
    public static String exhibitId,language;
    public ExhibitFields exhibitFields;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();
    static List<ExhibitFields> exhibitFieldsList;
    public String userLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit_show);
        context = this;
        intent = getIntent();
        ExhibitShowActivity.exhibitId = intent.getStringExtra("exhibitId");
        ExhibitShowActivity.language = intent.getStringExtra("language");
        //exhibitFieldsList = new ArrayList<ExhibitFields>();
        if (savedInstanceState == null) {
            getExhibitFields();
            userLanguage = showUserSettings();
        }


    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // Save custom values into the bundle
        if (savedInstanceState != null) {
            savedInstanceState.putString(DESCRIPTION, exhibit.description);
            savedInstanceState.putString(TITLE, exhibit.name);
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    //This function restores restore ArrayList after orientation and set it into listview
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            savedInstanceState.getString(DESCRIPTION, exhibit.description);
            savedInstanceState.getString(TITLE, exhibit.name);
            try {
                exhibitName = (TextView) findViewById(R.id.exhibitName);
                exhibitDescription = (TextView) findViewById(R.id.exhibitDescription);
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
                    if (exhibitFields.exhibit.equals(ExhibitShowActivity.exhibitId )&& exhibitFields.language.equals(userLanguage.toLowerCase())){
                        exhibitDescription = (TextView) findViewById(R.id.exhibitDescription);
                        exhibitDescription.setText(exhibitFields.description);
                        exhibitName = (TextView) findViewById(R.id.exhibitName);
                        exhibitName.setText(exhibitFields.name);
                    }
                }catch (Exception ex){

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

    private String showUserSettings() {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        sharedPrefs.getString("prefUsername", "NULL");
        sharedPrefs.getBoolean("prefSendReport", false);
        sharedPrefs.getString("prefSyncFrequency", "NULL");
        return sharedPrefs.getString("prefAppLanguage", "NULL");
    }

}


/*

-Kr1FksV0GyAinNNyAMH
-Kr1Gw1XjksufLliUNtG
-Kr1GzDWx43NEE-HVQjT
-KuQMWjbTHacLTuebHgi
 */
package com.example.musedroid.musedroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ExhibitShowActivity extends AppCompatActivity {
    private static final String DESCRIPTION = "exhibit_description";
    private static final String TITLE = "exhibit_title";
    public static Exhibit exhibit;
    TextView exhibitName, exhibitDescription;
    Intent intent;
    Context context;
    String exhibitId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit_show);
        context = this;
        intent = getIntent();
        exhibitId = intent.getStringExtra("exhibitId");
        if (savedInstanceState == null) {
            getExhibitById(exhibitId);
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

    public void getExhibitById(final String id) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = database.getReference();
        mDatabase.child("exhibits").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                exhibit = dataSnapshot.getValue(Exhibit.class);
                try {
                    exhibitDescription = (TextView) findViewById(R.id.exhibitDescription);
                    exhibitDescription.setText(exhibit.description);
                    exhibitName = (TextView) findViewById(R.id.exhibitName);
                    exhibitName.setText(exhibit.name);
                } catch (Exception ex) {

                    intent = getIntent();
                    Exhibit exhibit = intent.getParcelableExtra("Exhibit");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}



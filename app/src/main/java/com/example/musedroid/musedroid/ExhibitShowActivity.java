package com.example.musedroid.musedroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ExhibitShowActivity extends AppCompatActivity {
    TextView exhibitName,exhibitDescription;
    Intent intent;
    Context context;
    String exhibitId;


    public static Exhibit exhibit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit_show);
        context = this;
        intent = getIntent();
        exhibitId = intent.getStringExtra("exhibitId");
        getExhibitById(exhibitId);
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
                }
                catch(Exception ex){

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}



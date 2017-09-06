package com.example.musedroid.musedroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ExhibitShowActivity extends AppCompatActivity {
    TextView exhibitDescription;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit_show);
        intent = getIntent();
        Exhibit exhibit = intent.getParcelableExtra("Exhibit");


        exhibitDescription = (TextView) findViewById(R.id.exhibitDescription);
        exhibitDescription.setText(exhibit.description);
    }
}

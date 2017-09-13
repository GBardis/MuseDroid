package com.example.musedroid.musedroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    Button btnNearbyMuseum;
    Button goToListView;
    Intent intent;

    Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        setContentView(R.layout.activity_main);


        //set Buttons from View!
        btnNearbyMuseum = (Button) findViewById(R.id.btnNearbyMuseum);

        btnNearbyMuseum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, NearbyListViewActivity.class);
                startActivity(intent);
            }

        });

        goToListView = (Button) findViewById(R.id.goToListView);

        goToListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, ListViewActivity.class);
                startActivity(intent);
            }
        });
    }
}

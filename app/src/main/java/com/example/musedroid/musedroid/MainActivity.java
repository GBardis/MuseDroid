package com.example.musedroid.musedroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {
    Button goToListView;
    Intent intent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        goToListView = (Button) findViewById(R.id.goToListView);
        getItemsOnSpinner();

        goToListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, ListViewActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getItemsOnSpinner() {
        final ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item);
        final Spinner museumsSpinner = (Spinner) findViewById(R.id.museumsSpinner);
        FirebaseHandler firebaseHandler = new FirebaseHandler();

        //Get items for spinner
        firebaseHandler.getMuseums(adapterSpinner);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Init Adapter
        museumsSpinner.setAdapter(adapterSpinner);
        //Go to museum activities
        changeActivity(museumsSpinner);
    }

    private void changeActivity(final Spinner spinner) {

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int check = 0;

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (++check > 1) {
                    Intent intent = new Intent(view.getContext(), ShowActivity.class);
                    intent.putExtra("", spinner.getItemAtPosition(position).toString());
                    startActivity(intent);
                }
            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}



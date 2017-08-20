package com.example.musedroid.musedroid;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
                switch (v.getId()) {
                    case R.id.btnNearbyMuseum:
                        new AsyncIntent().execute(intent = new Intent(MainActivity.this, NearbyListViewActivity.class));
                        break;
                }
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

    private class AsyncIntent extends AsyncTask<Intent, Void, String> {

        @Override
        protected String doInBackground(Intent... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.interrupted();
            }
            return "Success";
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == "Success") {
                startActivity(intent);
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}



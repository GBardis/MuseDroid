package com.example.musedroid.musedroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class ShowActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            setTitle(bundle.getString(""));
        }
    }
}

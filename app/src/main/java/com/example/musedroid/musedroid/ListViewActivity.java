package com.example.musedroid.musedroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ListViewActivity extends AppCompatActivity {
    static final String SOME_VALUE = "museum_list";
    public ListView listView;
    public ArrayAdapter<Museum> adapter;
    public GetFirebase getFirebase;
    Intent intent;
    ArrayAdapter<Museum> museumAdapter;
    ArrayList<Museum> budleArrayList= new ArrayList<>();;
    ArrayList<Museum> listAfterDestroy = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);


        setContentView(R.layout.activity_list_view);
        listView = (ListView) findViewById(R.id.LIstView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        getFirebase = new GetFirebase();
        museumAdapter = getFirebase.listViewFromFirebase(adapter, new ArrayList<Museum>());
        listView.setAdapter(museumAdapter);
        changeActivity(listView);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // Save custom values into the bundle
        for (int i = 0; i < museumAdapter.getCount(); i++) {
            budleArrayList.add(museumAdapter.getItem(i));
        }
        savedInstanceState.putSerializable(SOME_VALUE, budleArrayList);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        // Restore state members from saved instance
        budleArrayList = (ArrayList<Museum>) savedInstanceState.getSerializable(SOME_VALUE);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1,budleArrayList);
        listView.setAdapter(museumAdapter);
        changeActivity(listView);
    }

    private void changeActivity(final ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            // argument position gives the index of item which is clicked
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                intent = new Intent(view.getContext(), ShowActivity.class);
                intent.putExtra("museum", (Museum) listView.getItemAtPosition(position));
                startActivity(intent);
            }
        });
    }
}


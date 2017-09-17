package com.example.musedroid.musedroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class ListViewActivity extends AppCompatActivity {

    public ListView listView;
    public ArrayAdapter<Museum> adapter;
    public Toolbar toolbar;
    public GetFirebase getFirebase;

    ArrayList<Museum> museumArrayList;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        listView = (ListView) findViewById(R.id.LIstView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        getFirebase = new GetFirebase();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Available Museums");


        listView.setAdapter(getFirebase.listViewFromFirebase(adapter, new ArrayList<Museum>()));
//        museumArrayList = getFirebase.getMuseumList();
        changeActivity(listView);

//
//        details.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Bundle passDataBundle = new Bundle();
//                passDataBundle.putParcelableArrayList(getString(R.string.bundle_museum_list), museumArrayList);
//                Intent startActivityIntent = new Intent(ListViewActivity.this, PagerActivity.class);
//                startActivityIntent.putExtras(passDataBundle);
//                startActivity(startActivityIntent);
//            }
//
//        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
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


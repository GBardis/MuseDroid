package com.example.musedroid.musedroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ListViewActivity extends AppCompatActivity {
    public ListView listView;
    public ArrayAdapter<Museum> adapter;
    public GetFirebase getFirebase;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        listView = (ListView) findViewById(R.id.LIstView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        getFirebase = new GetFirebase();


        new ListHandler().execute(adapter);
    }


    class ListHandler extends AsyncTask<ArrayAdapter<Museum>, Void, ArrayAdapter<Museum>> {
        @Override
        protected ArrayAdapter<Museum> doInBackground(ArrayAdapter<Museum>... params) {

            ArrayAdapter<Museum> MuseumAdapterList = getFirebase.listViewFromFirebase(adapter, new ArrayList<Museum>());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.interrupted();
            }
            return MuseumAdapterList;
        }

        @Override
        protected void onPostExecute(ArrayAdapter<Museum> adapter) {
            listView.setAdapter(adapter);
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
}


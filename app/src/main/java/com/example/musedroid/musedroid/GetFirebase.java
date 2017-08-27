package com.example.musedroid.musedroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * Created by frcake on 18/8/2017.
 */

public class GetFirebase extends AppCompatActivity {
    Intent intent;
    private FirebaseHandler firebaseHandler = new FirebaseHandler();


    public ArrayAdapter<Museum> listViewFromFirebase(ArrayAdapter<Museum> adapter, List<Museum> museumList) {

        firebaseHandler.getMuseums(adapter, museumList);
        return adapter;
    }

    public void changeActivity(final ListView listView) {
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

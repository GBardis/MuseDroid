package com.example.musedroid.musedroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListViewActivity extends AppCompatActivity {
    FirebaseHandler firebaseHandler = new FirebaseHandler();
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        listViewFromFirebase();

        //firebaseHandler.createMuseum("1", new Museum("museum akropolis", "The Acropolis Museum (Greek: Μουσείο Ακρόπολης, Mouseio Akropolis) is an archaeological museum focused on the findings of the archaeological site of the Acropolis of Athens.", "37.968450", "23.728523"));
        // firebaseHandler.createMuseum("2", new Museum("museum goulandri", "The Goulandris Museum of Natural History is a museum in Kifisia, a northeastern suburb of Athens, Greece. It was founded by Angelos Goulandris and Niki Goulandris in 1965 in order to promote interest in the natural sciences, to raise the awareness of the public, in general, and in particular to call its attention to the need to protect Greece's natural wildlife habitats and species in the danger of extinction.", "38.074472", "23.814854"));
    }

    private void listViewFromFirebase() {
        final ListView listView = (ListView) findViewById(R.id.LIstView);
        final ArrayAdapter<Museum> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        listView.setAdapter(adapter);
        firebaseHandler.getMuseums(adapter);
        changeActivity(listView);
    }

    private void changeActivity(final ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            // argument position gives the index of item which is clicked
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                intent = new Intent(view.getContext(), ShowActivity.class);
                intent.putExtra("museum", (Museum)listView.getItemAtPosition(position));
                startActivity(intent);
            }
        });


    }
}


package com.example.musedroid.musedroid;
import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    //GPS Vars
    public static final int REQUEST_LOCATION=001;
    GoogleApiClient googleApiClient;
    private final int permissionCode = 100;
    LocationRequest locationRequest;
    LocationManager locationManager;
    LocationSettingsRequest.Builder locationSettingsRequest;
    PendingResult<LocationSettingsResult> pendingResult;
    //
    Button btnNearbyMuseum;
    Button goToListView;
    Intent intent;
    FirebaseHandler firebaseHandler = new FirebaseHandler();

    Context context;
    String[] perm = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    int result[] = new int[]{};


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




    private void changeActivity(final Spinner spinner) {

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int check = 0;

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (++check > 1) {
                    intent = new Intent(view.getContext(), ShowActivity.class);
                    intent.putExtra("", spinner.getItemAtPosition(position).toString());
                    startActivity(intent);
                }
            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}


//    private void getItemsOnSpinner() {
//        final ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item);
//        final Spinner museumsSpinner = (Spinner) findViewById(R.id.museumsSpinner);
//
//        //Get items for spinner
//      //  firebaseHandler.getMuseums(adapterSpinner);
//        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        //Init Adapter
//        museumsSpinner.setAdapter(adapterSpinner);
//        //Go to museum activities
//        changeActivity(museumsSpinner);
//    }



//
//    public void onLocationChanged(Location location) {
//        double currentLat = location.getLatitude();
//        double currentLong = location.getLongitude();
//        // textView.setText("Current Loc:" + String.valueOf(currentLat) + "," + String.valueOf(currentLong));
//        float[] distance1 = new float[3];
//        float[] distance2 = new float[3];
//        float[] distance3 = new float[3];
//        float[] distance4 = new float[3];
//        Location.distanceBetween(currentLat, currentLong, athensLat, athensLong, distance1);
//        Location.distanceBetween(currentLat, currentLong, peirLat, peirLong, distance2);
//        Location.distanceBetween(currentLat, currentLong, thesLat, thesLong, distance3);
//        Location.distanceBetween(currentLat, currentLong, kritiLat, kritiLong, distance4);
//        float[][] distances = {distance1, distance2, distance3, distance4};
//        String closest = "defaultLocation";
//        switch (result(distances)) {
//            case "Athens":
//                if (!athFlag) {
//                    athFlag = true;
//                    startActivity(new Intent(MainActivity.this, AthensActivity.class));
//                }
//                break;
//            case "Piraeus":
//                if(!peirFlag) {
//                    peirFlag = true;
//                    startActivity(new Intent(MainActivity.this, PiraeusActivity.class));
//                }
//                break;
//            case "Thessaloniki":
//                if (!thessFlag) {
//                    thessFlag = true;
//                    startActivity(new Intent(MainActivity.this, ThessalonikiActivity.class));
//                }
//                break;
//            case "Kriti":
//                if(!kritFlag) {
//                    kritFlag = true;
//                    startActivity(new Intent(MainActivity.this, KritiActivity.class));
//                }
//                break;
//            default:
//                startActivity(new Intent(this, MainActivity.class));
//                break;
//        }
//    }

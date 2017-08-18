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


public class MainActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
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
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        context = this;
        setContentView(R.layout.activity_main);

        //FireBase GET
        final ArrayAdapter<Museum> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);

        firebaseHandler.getMuseums(adapter);


        //set Buttons from View!
        btnNearbyMuseum = (Button) findViewById(R.id.btnNearbyMuseum);

        btnNearbyMuseum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    askForPermission();
                    onRequestPermissionsResult(permissionCode, perm, result);
                } else if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getUpdates();
                }
            }
        });


        //ask for permissions GPS!



        goToListView = (Button) findViewById(R.id.goToListView);

        goToListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, ListViewActivity.class);
                startActivity(intent);
            }
        });


        //Google location service opener
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Gps is Enabled", Toast.LENGTH_SHORT).show();

        } else {
            mEnableGps();
        }
    }

    //GPS Permissions callback
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
//        button = (Button) findViewById(R.id.button);
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    button.setText("Stop Guide");
//                    clickFlag = true;
                    getUpdates();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
//                    button.setText("Start Guide");
//                    clickFlag = false;
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public void askForPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,perm, permissionCode);
    }


    public void getUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this);

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

    public void mEnableGps() {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
        mLocationSetting();
    }

    public void mLocationSetting() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1 * 1000);
        locationRequest.setFastestInterval(1 * 1000);

        locationSettingsRequest = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        mResult();

    }

    public void mResult() {
        pendingResult = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, locationSettingsRequest.build());
        pendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();


                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {

                            status.startResolutionForResult(MainActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.


                        break;
                }
            }

        });
    }


//    public void checkProximity(Location location){
//       double a =  location.getLatitude();
//    }


    @Override
    public void onLocationChanged(Location location) {
        double currentLat = location.getLatitude();
        double currentLong = location.getLongitude();
        //Location.distanceBetween(currentLat, currentLong, athensLat, athensLong, distance1);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

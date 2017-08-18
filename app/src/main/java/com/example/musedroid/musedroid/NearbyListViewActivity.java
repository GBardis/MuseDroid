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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class NearbyListViewActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    public static final int REQUEST_LOCATION=001;
    GoogleApiClient googleApiClient;
    private final int permissionCode = 100;
    LocationRequest locationRequest;
    LocationManager locationManager;
    LocationSettingsRequest.Builder locationSettingsRequest;
    PendingResult<LocationSettingsResult> pendingResult;
    String[] perm = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    int result[] = new int[]{};
    Context context;

    public ListView nearbyListView;
    public ArrayAdapter<Museum> adapter,listFeed;
    public GetFirebase getFirebase;
    private static List<Museum> museumList;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_list_view);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        nearbyListView = (ListView) findViewById(R.id.NearbyListViewList);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        getFirebase = new GetFirebase();
        museumList = new ArrayList<Museum>();

        listFeed = getFirebase.listViewFromFirebase(adapter,museumList);
        nearbyListView.setAdapter(listFeed);
        changeActivity(nearbyListView);

        for (int i = 0 ; i < listFeed.getCount() ; i++){
            museumList.add(listFeed.getItem(i));
        }

        if (ActivityCompat.checkSelfPermission(NearbyListViewActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askForPermission();
            onRequestPermissionsResult(permissionCode, perm, result);
        } else if (ActivityCompat.checkSelfPermission(NearbyListViewActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getUpdates();
        }


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Gps is Enabled", Toast.LENGTH_SHORT).show();

        } else {
            mEnableGps();
        }


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
        ActivityCompat.requestPermissions(NearbyListViewActivity.this,perm, permissionCode);
    }


    public void getUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, NearbyListViewActivity.this);

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

                            status.startResolutionForResult(NearbyListViewActivity.this, REQUEST_LOCATION);
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


    @Override
    public void onLocationChanged(Location location) {
        Location dest = new Location("provider");

        int i =0;
        adapter.clear();
            for (Museum museum : museumList) {
                i++;
                dest.setLatitude(Double.parseDouble(museum.lat));
                dest.setLongitude(Double.parseDouble(museum.lon));
                museum.distance = String.valueOf(location.distanceTo(dest)/1000);
                if (Double.parseDouble(museum.distance) < 5){
                    adapter.add(museum);
                }
            }
            nearbyListView.clearChoices();
            nearbyListView.setAdapter(adapter);
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

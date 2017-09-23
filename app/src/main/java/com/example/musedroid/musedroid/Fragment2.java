package com.example.musedroid.musedroid;

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
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class Fragment2 extends Fragment implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final int REQUEST_LOCATION = 001;
    private static final String NEARBY_MUSEUM = "NearByMuseum";
    private final int permissionCode = 100;
    //    public ListView nearbyListView;
//    public ArrayAdapter<Museum> adapter, listFeed;
//    public GetFirebase getFirebase;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    LocationManager locationManager;
    LocationSettingsRequest.Builder locationSettingsRequest;
    PendingResult<LocationSettingsResult> pendingResult;
    String[] perm = new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION};
    int result[] = new int[]{};
    Context context;
    Intent intent;
    RecyclerView mRecyclerView;
    MuseumAdapter museumAdapter, nearbyMuseumAdapter;
    ArrayList<Museum> nearbyMuseumList, museumArrayList;
    GetFirebase getFirebase;
    ArrayList<Museum> bundledMuseumsList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //setContentView(R.layout.activity_nearby_list_view);
        context = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.fragment_listview, container, false);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        //onCreatedView code

        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askForPermission();
            onRequestPermissionsResult(permissionCode, perm, result);
        } else if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getUpdates();
        }


        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getActivity().getApplicationContext(), "Gps is Enabled", Toast.LENGTH_SHORT).show();

        } else {
            try {
                mEnableGps();
            } catch (Exception ex) {
                int s = 1;
            }
        }
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nearbyMuseumList = new ArrayList<>();

        mRecyclerView = view.findViewById(R.id.museumNearbyRecycleView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        //use getActivity instead of this in LinearLayoutManager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //initialize Museum adapter and give as import an array list
        //call firebase function after the initialize of the adapter
//        if (savedInstanceState == null) {
            getFirebase = new GetFirebase();
            museumArrayList = new ArrayList<>();
            museumAdapter = new MuseumAdapter(museumArrayList);

            nearbyMuseumAdapter = getFirebase.listViewFromFirebase(museumAdapter);
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MuseumAdapter) nearbyMuseumAdapter).setOnItemClickListener(new MuseumAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                intent = new Intent(view.getContext(), ShowActivity.class);
                intent.putExtra("museum", nearbyMuseumAdapter.getItem(position));
                startActivity(intent);
            }
        });
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        if (outState != null) {
//            bundledMuseumsList = new ArrayList<>();
//            for (int i = 0; i < museumAdapter.getItemCount(); i++) {
//                bundledMuseumsList.add(museumAdapter.getItem(i));
//            }
//            outState.putSerializable(NEARBY_MUSEUM, bundledMuseumsList);
//            // Always call the superclass so it can save the view hierarchy state
//        }
//        super.onSaveInstanceState(outState);
//    }

    public void askForPermission() {
        ActivityCompat.requestPermissions(getActivity(), perm, permissionCode);
    }


    public void getUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, Fragment2.this);

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
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);

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

                            status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
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
    public void onLocationChanged(final Location location) {
        Location dest = new Location("provider");

        nearbyMuseumList.clear();
        for (int i = 0; i < nearbyMuseumAdapter.getItemCount(); i++) {
            nearbyMuseumList.add(nearbyMuseumAdapter.getItem(i));
        }
        museumAdapter.clear();
        for (Museum museum : nearbyMuseumList) {
            dest.setLatitude(Double.parseDouble(museum.lat));
            dest.setLongitude(Double.parseDouble(museum.lon));
            museum.distance = String.valueOf(location.distanceTo(dest) / 1000);
            if (Double.parseDouble(museum.distance) < 5) {
                museumAdapter.add(museum);
                museumAdapter.notifyDataSetChanged();
            }
        }
        mRecyclerView.setAdapter(museumAdapter);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

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
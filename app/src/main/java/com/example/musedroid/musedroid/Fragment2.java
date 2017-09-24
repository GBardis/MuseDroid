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
import android.util.Log;
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
import java.util.Arrays;


public class Fragment2 extends Fragment implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final int REQUEST_LOCATION = 001;
    private static final String NEARBY_MUSEUM = "NearByMuseum";
    private static final String ALL_MUSEUM = "allMuseums";
    private final int permissionCode = 100;
    MuseumAdapter museumAdapter;
    MuseumAdapter tempMuseumList = new MuseumAdapter(new ArrayList<Museum>());
    MuseumAdapter onLocationChangeAdapter = new MuseumAdapter(new ArrayList<Museum>());
    MuseumAdapter allMuseumAdapter = new MuseumAdapter(new ArrayList<Museum>());
    ArrayList<Museum> nearbyMuseumList, museumArrayList;
    ArrayList<Museum> bundledNearbyMuseumsList = new ArrayList<>();
    ArrayList<Museum> bundledAllMuseumList = new ArrayList<>();
    ArrayList<Museum> allmuseumList = new ArrayList<>();

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
    GetFirebase getFirebase;


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
        if (savedInstanceState == null) {
            getFirebase = new GetFirebase();
            museumArrayList = new ArrayList<>();
            museumAdapter = new MuseumAdapter(museumArrayList);

            allMuseumAdapter = getFirebase.listViewFromFirebase(museumAdapter);
        }
    }

    private void changeActivity(final MuseumAdapter museumAdapter) {
        try {
            museumAdapter.setOnItemClickListener(new MuseumAdapter.MyClickListener() {

                @Override
                public void onItemClick(int position, View view) {
                    intent = new Intent(view.getContext(), ShowActivity.class);
                    intent.putExtra("museum", museumAdapter.getItem(position));
                    startActivity(intent);
                }
            });
        } catch (Exception ex) {
            Log.e("Exception", ex.getMessage());
            Log.d("Exception", Arrays.toString(ex.getStackTrace()));
        }
    }

    //Restore last state for checked position.
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        try {
            if (savedInstanceState != null) {
                allmuseumList = (ArrayList<Museum>) savedInstanceState.getSerializable(ALL_MUSEUM);
                museumArrayList = (ArrayList<Museum>) savedInstanceState.getSerializable(NEARBY_MUSEUM);

                assert allmuseumList != null;
                for (Museum museum : allmuseumList) {
                    allMuseumAdapter.add(museum);
                }

                assert museumArrayList != null;
                for (Museum museum : museumArrayList) {
                    onLocationChangeAdapter.add(museum);
                }
                onLocationChangeAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(onLocationChangeAdapter);
                changeActivity(onLocationChangeAdapter);
            }
        } catch (Exception ex) {
            Log.e("Exception", ex.getMessage());
            Log.d("Exception", Arrays.toString(ex.getStackTrace()));

        }
        super.onActivityCreated(savedInstanceState);
    }

    //Save the state of activity for checked position
    @Override
    public void onSaveInstanceState(Bundle outState) {
        try {
            if (outState != null) {
                bundledNearbyMuseumsList.clear();
                for (int i = 0; i < onLocationChangeAdapter.getItemCount(); i++) {
                    bundledNearbyMuseumsList.add(onLocationChangeAdapter.getItem(i));
                }

                for (int i = 0; i < allMuseumAdapter.getItemCount(); i++) {
                    bundledAllMuseumList.add(allMuseumAdapter.getItem(i));
                }
                outState.putSerializable(ALL_MUSEUM, bundledAllMuseumList);
                outState.putSerializable(NEARBY_MUSEUM, bundledNearbyMuseumsList);
            }
        } catch (Exception ex) {
            Log.e("Exception", ex.getMessage());
            Log.d("Exception", Arrays.toString(ex.getStackTrace()));
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }

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
        for (int i = 0; i < allMuseumAdapter.getItemCount(); i++) {
            nearbyMuseumList.add(allMuseumAdapter.getItem(i));
        }
        onLocationChangeAdapter.clear();
        for (Museum museum : nearbyMuseumList) {
            dest.setLatitude(Double.parseDouble(museum.lat));
            dest.setLongitude(Double.parseDouble(museum.lon));
            museum.distance = String.valueOf(location.distanceTo(dest) / 1000);
            if (Double.parseDouble(museum.distance) < 5) {
                onLocationChangeAdapter.add(museum);
            }
            onLocationChangeAdapter.notifyDataSetChanged();
        }
        if (onLocationChangeAdapter != tempMuseumList) {
            tempMuseumList = onLocationChangeAdapter;
            mRecyclerView.getRecycledViewPool().clear();
            mRecyclerView.setAdapter(onLocationChangeAdapter);
            changeActivity(onLocationChangeAdapter);
        }
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
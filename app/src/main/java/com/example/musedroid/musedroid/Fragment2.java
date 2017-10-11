package com.example.musedroid.musedroid;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Fragment2 extends Fragment implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnCompleteListener<Void> {
    public static final int REQUEST_LOCATION = 001;
    private static final String NEARBY_MUSEUM = "NearByMuseum";
    private static final String ALL_MUSEUM = "allMuseums";
    public static Location currentLocation;
    private final int permissionCode = 100;
    public ProgressBar progressBar;
    public View view;
    MuseumAdapter tempMuseumList = new MuseumAdapter(new ArrayList<Museum>());
    MuseumAdapter onLocationChangeAdapter = new MuseumAdapter(new ArrayList<Museum>());
    MuseumAdapter allMuseumAdapter = new MuseumAdapter(new ArrayList<Museum>());
    ArrayList<Museum> nearbyMuseumList, museumArrayList;
    ArrayList<Museum> bundledNearbyMuseumsList = new ArrayList<>();
    ArrayList<Museum> bundledAllMuseumList = new ArrayList<>();
    ArrayList<Museum> allMuseumList = new ArrayList<>();
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
    private int minLocationUpdateTime = 0;
    private int minLocationUpdateInterval = 0;
    private GeofencingClient mGeofencingClient;
    public List<Geofence> mGeofenceList = new ArrayList<>();
    private PendingIntent mGeofencePendingIntent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //setContentView(R.layout.activity_nearby_list_view);
        context = getActivity().getApplicationContext();
        view = inflater.inflate(R.layout.fragment_listview, container, false);
        //progressBar = view.findViewById(R.id.nearByMuseumProgressBar);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        //onCreatedView code

        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askForPermission();
            onRequestPermissionsResult(permissionCode, perm, result);
        } else if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getUpdates();
        }
        mGeofencingClient = LocationServices.getGeofencingClient(getActivity().getApplicationContext());


        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new ShowGpsSuccessToast().execute();
        } else {
            try {
                mEnableGps();
            } catch (Exception ex) {

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
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //initialize Museum adapter and give as import an array list
        //call firebase function after the initialize of the adapter

        if (savedInstanceState == null) {
            allMuseumAdapter = MainActivity.museumAdapter;
            mRecyclerView.setAdapter(onLocationChangeAdapter);
        }
        onLocationChangeAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                int i = 0;
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                int i = 0;
            }
        });

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                // do it
                MuseumAdapter adapter = (MuseumAdapter) recyclerView.getAdapter();
                intent = new Intent(v.getContext(), ShowActivity.class);
                intent.putExtra("museum", adapter.getItem(position));
                startActivity(intent);
            }
        });
    }

    public void addGeofences(GeofencingClient mGeofencingClient) {
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {
            mGeofencingClient.addGeofences(getGeofencingRequest(mGeofenceList), getGeofencePendingIntent())
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Geofences added
                            //
                        }
                    })
                    .addOnFailureListener(getActivity(), new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to add geofences
                            // ...
                        }
                    });
        } catch (Exception ex) {

        }
    }

    public void createGeoFences(MuseumAdapter adapter) {
        for (int i = 0; i < adapter.getItemCount(); i++) {
            if (areEqual(mGeofenceList, adapter) != true) {
                //removeAllFences(mGeofenceList);
                Geofence geofence = new Geofence.Builder()
                        .setRequestId(adapter.getItem(i).name) // Geofence ID
                        .setCircularRegion(Double.parseDouble(adapter.getItem(i).lat), Double.parseDouble(adapter.getItem(i).lon), 100) // defining fence region
                        .setExpirationDuration(Geofence.NEVER_EXPIRE) // expiring date
                        // Transition types that it should look for
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build();
                try {
                    mGeofenceList.add(geofence);
                } catch (Exception ex) {

                }
            }
        }
        //getGeofencingRequest(mGeofenceList);
        //call pending intent!
        //getGeofencePendingIntent();
        addGeofences(mGeofencingClient);
    }


    public boolean areEqual(List<Geofence> list, MuseumAdapter adapter) {
        if (list.size() != adapter.getItemCount()) {
            return false;
        }
        for (Geofence geofence : list) {
            for (int i = 0; i < adapter.getItemCount(); i++) {
                if (adapter.getItem(i) != geofence) {
                    return false;
                }
            }
        }
        return true;
    }

    @NonNull
    private GeofencingRequest getGeofencingRequest(List<Geofence> mGeofenceList) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(getActivity().getApplicationContext(), GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(getActivity().getApplicationContext(), 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseHandler.database.goOnline();
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseHandler.database.goOnline();
    }

    @Override
    public void onPause() {
        super.onPause();
        FirebaseHandler.database.goOffline();
    }

    @Override
    public void onStop() {
        super.onStop();
        FirebaseHandler.database.goOffline();
    }

    //Restore last state for checked position.
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        try {
            if (savedInstanceState != null) {
                mRecyclerView.getRecycledViewPool().clear();
                allMuseumAdapter.clear();
                onLocationChangeAdapter.clear();

                allMuseumList = savedInstanceState.getParcelableArrayList(ALL_MUSEUM);
                museumArrayList = savedInstanceState.getParcelableArrayList(NEARBY_MUSEUM);

                assert allMuseumList != null;
                allMuseumAdapter.addAll(allMuseumList);

                assert museumArrayList != null;
                onLocationChangeAdapter.addAll(museumArrayList);

                onLocationChangeAdapter.notifyDataSetChanged();
                if (museumArrayList.size() != 0) {
                    mRecyclerView.setAdapter(onLocationChangeAdapter);
                }
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
                bundledAllMuseumList.clear();

                for (int i = 0; i < allMuseumAdapter.getItemCount(); i++) {
                    bundledAllMuseumList.add(allMuseumAdapter.getItem(i));
                }
                outState.putParcelableArrayList(ALL_MUSEUM, bundledAllMuseumList);
                outState.putParcelableArrayList(NEARBY_MUSEUM, bundledNearbyMuseumsList);

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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minLocationUpdateTime, minLocationUpdateInterval, Fragment2.this);

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
        currentLocation = location;
        try {
            onLocationChangeAdapter.clear();
            for (int i = 0; i < allMuseumAdapter.getItemCount(); i++) {
                dest.setLatitude(Double.parseDouble(allMuseumAdapter.getItem(i).lat));
                dest.setLongitude(Double.parseDouble(allMuseumAdapter.getItem(i).lon));
                allMuseumAdapter.getItem(i).distance = String.valueOf(location.distanceTo(dest) / 1000);
                if (Double.parseDouble(allMuseumAdapter.getItem(i).distance) < 5) {
                    onLocationChangeAdapter.add(allMuseumAdapter.getItem(i));
                }

                onLocationChangeAdapter.notifyDataSetChanged();

            }
            createGeoFences(onLocationChangeAdapter);

            if (onLocationChangeAdapter != tempMuseumList) {

                tempMuseumList = onLocationChangeAdapter;
                mRecyclerView.getRecycledViewPool().clear();
            }

        } catch (Exception ex) {
            Log.e("Exception", ex.getMessage());
            Log.d("Exception", Arrays.toString(ex.getStackTrace()));
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

    @Override
    public void onComplete(@NonNull Task<Void> task) {

    }


    private class ShowGpsSuccessToast extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }


        protected void onPostExecute(Void v) {
            Toast.makeText(getActivity().getApplicationContext(), "GPS ENABLED", Toast.LENGTH_LONG).show();
        }
    }


}
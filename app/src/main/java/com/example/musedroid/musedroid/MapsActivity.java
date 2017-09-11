//package com.example.musedroid.musedroid;
//
//import android.graphics.Color;
//import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
//
//import com.akexorcist.googledirection.DirectionCallback;
//import com.akexorcist.googledirection.GoogleDirection;
//import com.akexorcist.googledirection.constant.TransportMode;
//import com.akexorcist.googledirection.model.Direction;
//import com.akexorcist.googledirection.util.DirectionConverter;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//import java.util.ArrayList;
//
//public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionCallback {
//
//    private GoogleMap googleMap;
//    private LatLng camera = new LatLng(37.782437, -122.4281893);
//    private LatLng origin = new LatLng(37.7849569, -122.4068855);
//    private LatLng destination = new LatLng(37.7814432, -122.4460177);
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//        //Start Lifecycle of google maps api
//        getDirection();
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        this.googleMap = googleMap;
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, 13));
//    }
//
//    public void getDirection() {
//        String serverKey = "AIzaSyBbDEqUxFrTYXE7CMX4JFbJjhqUcrk--Kw";
//
//        GoogleDirection.withServerKey(serverKey)
//                .from(origin)
//                .to(destination)
//                .transportMode(TransportMode.DRIVING)
//                .execute(this);
//
//    }
//
//    @Override
//    public void onDirectionSuccess(Direction direction, String rawBody) {
//        googleMap.addMarker(new MarkerOptions().position(origin));
//        googleMap.addMarker(new MarkerOptions().position(destination));
//
//        ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
//        googleMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.RED));
//    }
//
//    @Override
//    public void onDirectionFailure(Throwable t) {
//
//    }
//}

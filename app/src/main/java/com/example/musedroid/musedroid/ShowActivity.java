package com.example.musedroid.musedroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;


public class ShowActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    RatingBar ratingBar;
    TextView textDescription;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        Intent intent = getIntent();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        textDescription = (TextView) findViewById(R.id.textDescription);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setNumStars(5);

        new AsyncShow().execute(intent);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class AsyncShow extends AsyncTask<Intent, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Intent... params) {
            Intent i = params[0];
            Museum museum = i.getParcelableExtra("museum");
            final List<String> googlePlacesData = new ArrayList<>();
            // google places api call
            PlaceBuffer result = Places.GeoDataApi.getPlaceById(mGoogleApiClient, museum.placeId.toString()).await();
            final Place myPlace = result.get(0);
            final float rating = myPlace.getRating();
            // Add data to list
            googlePlacesData.add(museum.description);
            googlePlacesData.add(museum.placeId.toString());
            googlePlacesData.add(museum.name);
            googlePlacesData.add(String.valueOf(rating));

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.interrupted();
            }
            return googlePlacesData;
        }

        @Override
        protected void onPostExecute(List<String> googlePlacesData) {
            // set description UIthread
            textDescription.setText(googlePlacesData.get(0));
            // set Name description
            setTitle(googlePlacesData.get(2));
            // set rating UIthread
            ratingBar.setRating(Float.parseFloat(googlePlacesData.get(3)));
        }
    }
}

package com.example.musedroid.musedroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
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

        new AsyncShow().execute(intent);

    }


    private void getPlace(final String placeId) {
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    public static final String TAG = "";

                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place myPlace = places.get(0);
                            final float rating = myPlace.getRating();
                            ratingBar = (RatingBar) findViewById(R.id.ratingBar);
                            ratingBar.setNumStars(5);
                            ratingBar.setRating(rating);
                            Log.i(TAG, "Place found: " + myPlace.getName());
                        } else {
                            Log.e(TAG, "Place not found");
                        }
                        places.release();
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class AsyncShow extends AsyncTask<Intent, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Intent... params) {
            Intent i = params[0];
            Museum museum = i.getParcelableExtra("museum");
            final List<String> googleplacesData = new ArrayList<>();
            googleplacesData.add(museum.description);
            googleplacesData.add(museum.placeId.toString());
            googleplacesData.add(museum.name);
            return googleplacesData;
        }

        @Override
        protected void onPostExecute(List<String> googleplaces) {

            // set Name description and call google api UIthread
            String description = googleplaces.get(0);
            textDescription = (TextView) findViewById(R.id.textDescription);
            textDescription.setText(description);
            String placeId = googleplaces.get(1);
            String museumName = googleplaces.get(2);
            setTitle(museumName);

            getPlace(placeId);
        }
    }
}

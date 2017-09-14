package com.example.musedroid.musedroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import static android.R.attr.rating;


public class ShowActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String RATING = "Museum rating";
    private static final String DESCRIPTION = "museum description";
    private static final String TITLE = "museum title";
    RatingBar ratingBar;
    Button qrButton;
    Museum museum;
    TextView textDescription;
    Intent i;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        i = getIntent();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        qrButton = (Button) findViewById(R.id.qrButton);
        textDescription = (TextView) findViewById(R.id.MuseumDescription);

        if (savedInstanceState == null) {
            if (i != null) {
                museum = i.getParcelableExtra("museum");
                setTitle(museum.name);
                getPlace(museum.placeId);

                textDescription.setText(museum.description);
            }
        }
        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowActivity.this, QrShowActivity.class);
                intent.putExtra("flag", false);
                intent.putExtra("museumId", museum.key);
                startActivity(intent);
            }
        });
    }

    //This function convert adapter to arrayList and serialize it into a bundle, so that can be restore
    //after orientation change
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // Save custom values into the bundle
        if (savedInstanceState != null) {
            museum = i.getParcelableExtra("museum");

            savedInstanceState.putFloat(RATING, rating);
            savedInstanceState.putString(DESCRIPTION, museum.description);
            savedInstanceState.putString(TITLE, museum.name);
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    //This function restores restore ArrayList after orientation and set it into listview
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore state members from saved instance
            textDescription.setText(savedInstanceState.getString(DESCRIPTION));

            setTitle(savedInstanceState.getString(TITLE));
            ratingBar = (RatingBar) findViewById(R.id.ratingBar);
            ratingBar.setNumStars(5);
            ratingBar.setRating(savedInstanceState.getFloat(RATING));
        }
    }


    private void getPlace(final String placeId) {
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    public static final String TAG = "TAG";

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
}

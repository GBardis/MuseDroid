package com.example.musedroid.musedroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;

import java.util.Arrays;

import static android.R.attr.rating;


public class ShowActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String RATING = "Museum rating";
    private static final String DESCRIPTION = "museum description";
    private static final String TITLE = "museum title";
    RatingBar ratingBar;
    Button qrButton, goToMaps;
    Museum museum;
    Intent intent;
    String museumAddress, museumName;
    TextView textDescription;
    Intent i;
    ViewPager imagesViewPager;
    AppCompatImageView museumImage;
    AppCompatImageView browseImage;
    AppCompatImageView qrScannerImage;
    AppCompatTextView museumDetails;
    AppCompatTextView website;
    Toolbar toolbar;


    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ex);

        i = getIntent();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        //Add fixes ShowActivity

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        qrScannerImage = (AppCompatImageView) findViewById(R.id.qrScannerImage);
        browseImage = (AppCompatImageView) findViewById(R.id.browseImage);
        imagesViewPager = (ViewPager) findViewById(R.id.imagesViewPager);
        museumImage = (AppCompatImageView) findViewById(R.id.museumImage);
        museumDetails = (AppCompatTextView) findViewById(R.id.museumDetails);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        qrButton = findViewById(R.id.qrButton);

        goToMaps = findViewById(R.id.goToMaps);

        textDescription = findViewById(R.id.MuseumDescription);

        if (savedInstanceState == null) {
            if (i != null) {
                try {
                    museum = i.getParcelableExtra("museum");
//                    setTitle(museum.name);
                    getPlace(museum.placeId);
                    getPhotos(museum.placeId);
                    museumDetails.setText(museum.description);
                    textDescription.setText(museum.description);

                    setSupportActionBar(toolbar);
                    //retrieve an instance of ActionBar
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
                    getSupportActionBar().setTitle(museum.name);

                } catch (Exception ex) {
                    Log.e("Exception", ex.getMessage());
                    Log.d("Exception", Arrays.toString(ex.getStackTrace()));
                }
            }
        }

        qrScannerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowActivity.this, QrShowActivity.class);
                intent.putExtra("flag", false);
                intent.putExtra("museumId", museum.key);
                startActivity(intent);
            }
        });

        goToMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q= " + museumName + museumAddress);
                intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(ShowActivity.this, QrShowActivity.class);
                intent.putExtra("flag", false);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
    //This function convert adapter to arrayList and serialize it into a bundle, so that can be restore
    //after orientation change
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // Save custom values into the bundle
        if (savedInstanceState != null) {
            try {
                museum = i.getParcelableExtra("museum");

                savedInstanceState.putFloat(RATING, rating);
                savedInstanceState.putString(DESCRIPTION, museum.description);
                savedInstanceState.putString(TITLE, museum.name);
            } catch (Exception ex) {
                Log.e("Exception", ex.getMessage());
                Log.d("Exception", Arrays.toString(ex.getStackTrace()));
            }
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
            try {
                textDescription.setText(savedInstanceState.getString(DESCRIPTION));

                setTitle(savedInstanceState.getString(TITLE));
                ratingBar = findViewById(R.id.ratingBar);
                ratingBar.setNumStars(5);
                ratingBar.setRating(savedInstanceState.getFloat(RATING));
            } catch (Exception ex) {
                Log.e("Exception", ex.getMessage());
                Log.d("Exception", Arrays.toString(ex.getStackTrace()));
            }
        }
    }

    private void getPlace(final String placeId) {
        mGoogleApiClient.connect();
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    public static final String TAG = "TAG";

                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place myPlace = places.get(0);
                            final float rating = myPlace.getRating();
//                            museumName = myPlace.getName().toString();
//                            museumAddress = myPlace.getAddress().toString();
                            museum.setAddress(String.valueOf(myPlace.getAddress()));
                            museum.setWebsite(String.valueOf(myPlace.getWebsiteUri()));
                            ratingBar = findViewById(R.id.ratingBar);
                            ratingBar.setNumStars(5);
                            ratingBar.setRating(rating);
                            Log.i(TAG, "Place found: " + myPlace.getName());


                            if (myPlace.getWebsiteUri() != null) {
                                browseImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Uri uri = Uri.parse(museum.website);
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(intent);
                                    }
                                });
                            }
                            Log.i(TAG, "Place found: " + myPlace.getName());
                        } else {
                            Log.e(TAG, "Place not found");
                        }
                        places.release();
                        mGoogleApiClient.disconnect();
                    }
                });


    }

    private void getPhotos(final String placeId) {

        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId).setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
            @Override
            public void onResult(final PlacePhotoMetadataResult placePhotoMetadataResult) {
                if (placePhotoMetadataResult.getStatus().isSuccess()) {
                    PlacePhotoMetadataBuffer photoMetadata = placePhotoMetadataResult.getPhotoMetadata();
                    // final int photoCount = photoMetadata.getCount();
                    //images = new Bitmap[3];
                    //for (int i = 0; i < images.length; i++) {

                    PlacePhotoMetadata placePhotoMetadata = photoMetadata.get(0);
                    // final int finalI = i;
                    placePhotoMetadata.getScaledPhoto(mGoogleApiClient, 500, 500).setResultCallback(new ResultCallback<PlacePhotoResult>() {
                        @Override
                        public void onResult(PlacePhotoResult placePhotoResult) {
                            //  images[finalI] = placePhotoResult.getBitmap();
                            museumImage.setImageBitmap(placePhotoResult.getBitmap());
                        }
                    });
                    //  }
                    // setAdapter(images);
                } else {
                    Log.e("ShowActivity ", "No photos returned");
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

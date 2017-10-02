package com.example.musedroid.musedroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
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

public class MuseumDetails extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String RATING = "Museum rating";
    private static final String DESCRIPTION = "museum description";
    private static final String TITLE = "museum title";
    private static final String MUSEUM_BITMAP = "Museum Image";
    private static final String MUSEUM_WEB = "Museum Website";
    RatingBar ratingBar;
    Museum museum;
    Intent intent;
    String museumAddress, museumName;
    Intent i;
    Bitmap museumIm;
    Float rating;


    AppCompatImageView museumImage;
    AppCompatImageView goToMaps;
    AppCompatImageView browseImage;
    AppCompatImageView qrScannerImage;
    TextView museumDetails;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum_details);

        Intent i = getIntent();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        ratingBar = findViewById(R.id.ratingBar1);
        museumImage = findViewById(R.id.museumImage1);
        browseImage = findViewById(R.id.browseImage1);
        qrScannerImage = findViewById(R.id.qrScannerImage1);
        goToMaps = findViewById(R.id.goToMaps1);
        museumDetails = findViewById(R.id.museum_details);

        museum = i.getParcelableExtra("museum");
        if (savedInstanceState == null) {
            if (i != null) {
                try {

                    getPhotos(museum.placeId);
                    getPlace(museum.placeId);
                    museumDetails.setText(museum.description);


                } catch (Exception ex) {
                    Log.e("Exception", ex.getMessage());
                    Log.d("Exception", Arrays.toString(ex.getStackTrace()));
                }
            }
        }
        /*else
        {   try {
            museumIm = (Bitmap) savedInstanceState.getParcelable(MUSEUM_BITMAP);
            museumImage.setImageBitmap(museumIm);
        }catch(Exception ex){
            Log.e("Exception", ex.getMessage());
            Log.d("Exception", Arrays.toString(ex.getStackTrace()));

        }

        }
*/
        qrScannerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MuseumDetails.this, QrShowActivity.class);
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


        browseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(museum.website);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // Save custom values into the bundle
        if (savedInstanceState != null) {
            try {
                savedInstanceState.putFloat(RATING,rating);
                savedInstanceState.putString(DESCRIPTION, museum.description);
                savedInstanceState.putString(TITLE, museum.name);
                savedInstanceState.putParcelable(MUSEUM_BITMAP, museumIm);
                savedInstanceState.putString(MUSEUM_WEB,museum.website);

            } catch (Exception ex) {
                Log.e("Exception", ex.getMessage());
                Log.d("Exception", Arrays.toString(ex.getStackTrace()));
            }
        }
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {

            // Restore state members from saved instance
            try {
                museumDetails.setText(savedInstanceState.getString(DESCRIPTION));
                //Restore Image
                museumIm = savedInstanceState.getParcelable(MUSEUM_BITMAP);
                museumImage.setImageBitmap(museumIm);
                setTitle(savedInstanceState.getString(TITLE));
                //Restore Rating
                rating = savedInstanceState.getFloat(RATING);
                ratingBar.setNumStars(5);
                ratingBar.setRating(rating);

                /*browseImage = findViewById(R.id.browseImage1);*/
                museum.getWebsite();


            } catch (Exception ex) {
                Log.e("Exception", ex.getMessage());
                Log.d("Exception", Arrays.toString(ex.getStackTrace()));
            }
        }
    }

    //TODO: check all google place photo clients for photoMetadata.release(); must be called else app has data leak
    private void getPlace(final String placeId) {
        mGoogleApiClient.connect();
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    public static final String TAG = "TAG";

                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place myPlace = places.get(0);
//                            final float rating = myPlace.getRating();
                            rating = myPlace.getRating();
                            museum.setAddress(String.valueOf(myPlace.getAddress()));
                            museum.setWebsite(String.valueOf(myPlace.getWebsiteUri()));

                            ratingBar = findViewById(R.id.ratingBar1);
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
                        //mGoogleApiClient.disconnect();
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
                            museumIm = placePhotoResult.getBitmap();
                            museumImage.setImageBitmap(placePhotoResult.getBitmap());
                        }
                    });
                    photoMetadata.release();
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

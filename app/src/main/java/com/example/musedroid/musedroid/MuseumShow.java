package com.example.musedroid.musedroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
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

public class MuseumShow extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    Intent i;
    TextView museumDescription, museumAddress, museumPhoneNumber, museumWebsite;
    Museum museum;
    ImageView toolbarImage;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum_show);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        i = getIntent();
        museum = i.getParcelableExtra("museum");
        toolbarImage = findViewById(R.id.backdrop);

        museumAddress = findViewById(R.id.museumAddress);
        museumPhoneNumber = findViewById(R.id.museumphoneNumber);
        museumWebsite = findViewById(R.id.museumWebsite);
        museumDescription = findViewById(R.id.museum_description);

        museumDescription.setText(museum.description);
        setTitle(museum.name);


        getPhotos(museum.placeId);
        getPlace(museum.placeId);

    }

    private void getPhotos(final String placeId) {

        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId).setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
            @Override
            public void onResult(@NonNull final PlacePhotoMetadataResult placePhotoMetadataResult) {
                if (placePhotoMetadataResult.getStatus().isSuccess()) {
                    PlacePhotoMetadataBuffer photoMetadata = placePhotoMetadataResult.getPhotoMetadata();

                    PlacePhotoMetadata placePhotoMetadata = photoMetadata.get(1);

                    placePhotoMetadata.getPhoto(mGoogleApiClient).setResultCallback(new ResultCallback<PlacePhotoResult>() {
                        @Override
                        public void onResult(@NonNull PlacePhotoResult placePhotoResult) {
                            toolbarImage.setImageBitmap(placePhotoResult.getBitmap());
                        }
                    });
                    photoMetadata.release();
                } else {
                    Log.e("ShowActivity ", "No photos returned");
                }
            }
        });
    }

    private void getPlace(final String placeId) {
        mGoogleApiClient.connect();
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    public static final String TAG = "TAG";

                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place myPlace = places.get(0);
                            final float rating = myPlace.getRating();

                            museumPhoneNumber.setText(myPlace.getAddress());
                            museumWebsite.setText(String.valueOf(myPlace.getWebsiteUri()));
                            museumAddress.setText(myPlace.getPhoneNumber());
//                            ratingBar = findViewById(R.id.ratingBar);
//                            ratingBar.setNumStars(5);
//                            ratingBar.setRating(rating);
                            Log.i(TAG, "Place found: " + myPlace.getName());


//                            if (myPlace.getWebsiteUri() != null) {
//                                browseImage.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Uri uri = Uri.parse(museum.website);
//                                        Intent intent = new Intent(ACTION_VIEW, uri);
//                                        startActivity(intent);
//                                    }
//                                });
//                            }
                            Log.i(TAG, "Place found: " + myPlace.getName());
                        } else {
                            Log.e(TAG, "Place not found");
                        }
                        places.release();
                        // mGoogleApiClient.disconnect();
                    }
                });


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

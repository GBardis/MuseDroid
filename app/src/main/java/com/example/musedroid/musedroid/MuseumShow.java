package com.example.musedroid.musedroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class MuseumShow extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String RATING = "Museum rating";
    private static final String DESCRIPTION = "museum description";
    private static final String TITLE = "museum title";
    private static final String ADDRESS = "museum address";
    private static final String PHONENUMBER = "museum phoneNumber";
    private static final String WEBSITE = "museum website";
    private static final String MUSEUM_BITMAP = "museum image";
    Intent intent;
    TextView museumDescription, museumAddress, museumPhoneNumber, museumWebsite;
    Museum museum;
    ImageView toolbarImage;
    String address, phoneNumber, website;
    Bitmap museumImage;

    RatingBar ratingBar;
    private GoogleApiClient mGoogleApiClient;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum_show);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        intent = getIntent();

        toolbarImage = findViewById(R.id.backdrop);
        museumAddress = findViewById(R.id.museumAddress);
        museumPhoneNumber = findViewById(R.id.museumphoneNumber);
        museumWebsite = findViewById(R.id.museumWebsite);
        museumDescription = findViewById(R.id.museum_description);
        ratingBar = findViewById(R.id.ratingBar);
        mFab = findViewById(R.id.fab);

        museum = intent.getParcelableExtra("museum");

        if (savedInstanceState == null) {
            if (intent != null) {
                try {

                    getPhotos(museum.placeId);
                    getPlace(museum.placeId);
                    museumDescription.setText(museum.description);
                    setTitle(museum.name);
                } catch (Exception ex) {
                    Log.e("Exception", ex.getMessage());
                    Log.d("Exception", Arrays.toString(ex.getStackTrace()));
                }
            }
        }

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MuseumShow.this, QrShowActivity.class);
                intent.putExtra("flag", false);
                intent.putExtra("museumId", museum.key);
                startActivity(intent);
            }
        });

        museumAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q= " + museum.name + museumAddress);
                intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

        museumWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(website);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    //This function convert adapter to arrayList and serialize it into a bundle, so that can be restore
    //after orientation change
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // Save custom values into the bundle
        if (savedInstanceState != null) {
            try {
                //compress bitmap to pass it into byteArray
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                museumImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bytes = stream.toByteArray();
                savedInstanceState.putByteArray(MUSEUM_BITMAP, bytes);

                savedInstanceState.putString(DESCRIPTION, museum.description);
                savedInstanceState.putString(TITLE, museum.name);
                savedInstanceState.putString(ADDRESS, address);
                savedInstanceState.putString(WEBSITE, website);
                savedInstanceState.putString(PHONENUMBER, phoneNumber);
            } catch (Exception ex) {
                Log.e("Exception", ex.getMessage());
                Log.d("Exception", Arrays.toString(ex.getStackTrace()));
            }
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    //This function restores View after orientation
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {

            // Restore state members from saved instance
            try {

                //Decompress Bitmap and Restore Image
                byte[] bytes = savedInstanceState.getByteArray(MUSEUM_BITMAP);
                assert bytes != null;
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                museumImage = bmp;
                toolbarImage.setImageBitmap(bmp);

                address = savedInstanceState.getString(ADDRESS);
                phoneNumber = savedInstanceState.getString(PHONENUMBER);
                website = savedInstanceState.getString(WEBSITE);
                museum.description = savedInstanceState.getString(DESCRIPTION);

                museumDescription.setText(museum.description);
                museumAddress.setText(address);
                museumPhoneNumber.setText(phoneNumber);
                museumWebsite.setText(website);

                setTitle(savedInstanceState.getString(TITLE));
            } catch (Exception ex) {
                Log.e("Exception", ex.getMessage());
                Log.d("Exception", Arrays.toString(ex.getStackTrace()));
            }
        }
    }


    private void getPhotos(final String placeId) {

        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId).setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
            @Override
            public void onResult(@NonNull final PlacePhotoMetadataResult placePhotoMetadataResult) {
                if (placePhotoMetadataResult.getStatus().isSuccess()) {
                    PlacePhotoMetadataBuffer photoMetadata = placePhotoMetadataResult.getPhotoMetadata();

                    final PlacePhotoMetadata placePhotoMetadata = photoMetadata.get(1);

                    placePhotoMetadata.getPhoto(mGoogleApiClient).setResultCallback(new ResultCallback<PlacePhotoResult>() {
                        @Override
                        public void onResult(@NonNull PlacePhotoResult placePhotoResult) {
                            museumImage = placePhotoResult.getBitmap();
                            toolbarImage.setImageBitmap(museumImage);
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
                            address = String.valueOf(myPlace.getAddress());
                            website = String.valueOf(myPlace.getWebsiteUri());
                            phoneNumber = String.valueOf(myPlace.getPhoneNumber());

                            museumPhoneNumber.setText(phoneNumber);
                            museumWebsite.setText(website);
                            museumAddress.setText(address);
                            museumAddress.setMaxLines(4);

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

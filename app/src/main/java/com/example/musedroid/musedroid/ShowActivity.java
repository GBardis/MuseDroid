package com.example.musedroid.musedroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.musedroid.musedroid.Museum;
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

public class ShowActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    RatingBar ratingBar;
    ViewPager imagesViewPager;
    AppCompatImageView museumImage;
    AppCompatImageView browseImage;
    AppCompatImageView qrScannerImage;
    AppCompatTextView museumDetails;
    AppCompatTextView website;
    Toolbar toolbar;
    Museum museum;

    private GoogleApiClient mGoogleApiClient;
    private Bitmap[] images;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_activity_layout);
        Intent i = getIntent();
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        qrScannerImage = (AppCompatImageView) findViewById(R.id.qrScannerImage);
        browseImage = (AppCompatImageView) findViewById(R.id.browseImage);
        //imagesViewPager = (ViewPager) findViewById(R.id.imagesViewPager);
        museumImage = (AppCompatImageView) findViewById(R.id.museumImage);
        museumDetails = (AppCompatTextView) findViewById(R.id.museumDetails);
        toolbar = (Toolbar) findViewById(R.id.toolbar);


        if (i != null) {
            museum = i.getParcelableExtra("museum");
            getPlace(museum.placeId);
            getPhotos(museum.placeId);
            museumDetails.setText(museum.description);


            setSupportActionBar(toolbar);
            //retrieve an instance of ActionBar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(museum.name);

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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
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
                            museum.setAddress(String.valueOf(myPlace.getAddress()));
                            museum.setWebsite(String.valueOf(myPlace.getWebsiteUri()));
                            ratingBar.setNumStars(5);
                            ratingBar.setRating(rating);


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
//
//    private void setAdapter(Bitmap[] images) {
//        ImagesAdapter adapter = new ImagesAdapter(ShowActivity.this, images);
//        imagesViewPager.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
//        imagesViewPager.invalidate();
//    }

}

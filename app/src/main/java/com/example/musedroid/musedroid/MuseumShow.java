package com.example.musedroid.musedroid;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Random;

public class MuseumShow extends AppCompatActivity implements Listener {
    //NFC
    public static final String TAG = MainActivity.class.getSimpleName();
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
    Context context;
    GeoDataClient mGeoDataClient;
    //private NFCWriteFragment mNfcWriteFragment;
    private NFCReadFragment mNfcReadFragment;
    private boolean isDialogDisplayed = false;
    private NfcAdapter mNfcAdapter;
    private boolean isNfcSupported = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum_show);
        context = this;
        mGeoDataClient = Places.getGeoDataClient(this, null);


        intent = getIntent();

        toolbarImage = findViewById(R.id.backdrop);
        museumAddress = findViewById(R.id.museumAddress);
        museumPhoneNumber = findViewById(R.id.museumphoneNumber);
        museumWebsite = findViewById(R.id.museumWebsite);
        museumDescription = findViewById(R.id.museum_description);
        ratingBar = findViewById(R.id.ratingBar);

        FloatingActionButton mFabQr = findViewById(R.id.fab_qr);
        FloatingActionButton mFabNfc = findViewById(R.id.fab_nfc);

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

        mFabQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MuseumShow.this, QrShowActivity.class);
                intent.putExtra("flag", false);
                intent.putExtra("museumId", museum.key);
                startActivity(intent);
            }
        });

        mFabNfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNfcSupported) {
                    showReadFragment();
                } else {
                    Toast.makeText(context, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
                }
            }
        });

        museumAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q= " + museum.name + address);
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

        //NFC
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            isNfcSupported = false;

        }
        try {
            if (!mNfcAdapter.isEnabled()) {
                Toast.makeText(this, "NFC is disabled.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "NFC is enabled.", Toast.LENGTH_LONG).show();
            }

            handleIntent(getIntent());
        } catch (Exception ignore) {

        }
    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onStop() {

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
            } catch (Exception ex) {
                Log.e("Exception", ex.getMessage());
                Log.d("Exception", Arrays.toString(ex.getStackTrace()));
            }
            try {
                address = savedInstanceState.getString(ADDRESS);
                phoneNumber = savedInstanceState.getString(PHONENUMBER);
                website = savedInstanceState.getString(WEBSITE);
                museum.description = savedInstanceState.getString(DESCRIPTION);
            } catch (Exception ex) {
                Log.e("Exception", ex.getMessage());
                Log.d("Exception", Arrays.toString(ex.getStackTrace()));
            }
            museumDescription.setText(museum.description);
            museumAddress.setText(address);
            museumPhoneNumber.setText(phoneNumber);
            museumWebsite.setText(website);

            setTitle(savedInstanceState.getString(TITLE));

        }
    }

    private void getPhotos(final String placeId) {
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                if (task.isSuccessful()) {
                    // Get the list of photos.
                    PlacePhotoMetadataResponse photos = task.getResult();
                    // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                    PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                    try {
                        Random rand = new Random();

                        int n = rand.nextInt(5) + 1;
                        // Get the first photo in the list.
                        PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(n);
                        // Get the attribution text.
                        CharSequence attribution = photoMetadata.getAttributions();
                        // Get a full-size bitmap for the photo.
                        Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                PlacePhotoResponse photo = task.getResult();
                                museumImage = photo.getBitmap();
                                toolbarImage.setImageBitmap(museumImage);
                            }
                        });
                    } catch (IllegalStateException ex) {
                        photoMetadataBuffer.release();
                    }
                    photoMetadataBuffer.release();
                } else {
                    createToastMessages("No Photos Found , Check Internet Access");
                }
            }
        });
    }

    private void getPlace(final String placeId) {

        mGeoDataClient.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                if (task.isSuccessful()) {
                    PlaceBufferResponse places = task.getResult();
                    Place myPlace = places.get(0);
                    float rating = myPlace.getRating();
                    address = String.valueOf(myPlace.getAddress());
                    website = String.valueOf(myPlace.getWebsiteUri());
                    phoneNumber = String.valueOf(myPlace.getPhoneNumber());

                    museumPhoneNumber.setText(phoneNumber);
                    museumWebsite.setText(website);
                    museumAddress.setText(address);
                    museumAddress.setMaxLines(4);

                    ratingBar.setNumStars(5);
                    ratingBar.setRating(rating);

                    places.release();
                } else {
                    createToastMessages("Museum Data Not Found, Check Internet Access");
                }
            }
        });
    }

    private void createToastMessages(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    //NFC
    private void showReadFragment() {

        mNfcReadFragment = (NFCReadFragment) getFragmentManager().findFragmentByTag(NFCReadFragment.TAG);

        if (mNfcReadFragment == null) {

            mNfcReadFragment = NFCReadFragment.newInstance();
        }
        mNfcReadFragment.show(getFragmentManager(), NFCReadFragment.TAG);

    }

    @Override
    public void onDialogDisplayed() {

        isDialogDisplayed = true;
    }

    @Override
    public void onDialogDismissed() {

        isDialogDisplayed = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected, tagDetected, ndefDetected};

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        Log.d(TAG, "onNewIntent: " + intent.getAction());

        if (tag != null) {
            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
            Ndef ndef = Ndef.get(tag);

            if (isDialogDisplayed) {

                mNfcReadFragment = (NFCReadFragment) getFragmentManager().findFragmentByTag(NFCReadFragment.TAG);
                mNfcReadFragment.onNfcDetected(ndef);
            }
        }
    }

    private void handleIntent(Intent intent) {
        // TODO: handle Intent
    }
}

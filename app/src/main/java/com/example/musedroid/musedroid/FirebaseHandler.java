package com.example.musedroid.musedroid;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gdev-laptop on 4/8/2017.
 */

public class FirebaseHandler extends AppCompatActivity {
    public static boolean flag = false;
    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference mDatabase = database.getReference();
    public static MuseumAdapter museumAdapter = new MuseumAdapter(new ArrayList<Museum>());
    private static boolean signalFull;
    private static List<AdapterFullListener> adapterFullListeners = new ArrayList<>();

    public static boolean getSignalFull() {
        return signalFull;
    }

    public static void setSignalFull(boolean bool) {
        signalFull = bool;
        for (AdapterFullListener adapterFullListener : adapterFullListeners) {
            adapterFullListener.OnAdapterFull();
        }
    }

    public static void addAdapterFullListener(AdapterFullListener adapterFullListener) {
        adapterFullListeners.add(adapterFullListener);
    }

    public void getMuseums(final MuseumAdapter adapter, final ProgressBar progressBar, final String appLanguage, final View view) {

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (flag) {
                    progressBar.setVisibility(view.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabase.child("museums").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //It is important for the adapter to works to use museumAdapter.notifyDataSetChanged(); after
                //firebase add all museum inside the list , triggers adapter to see the data changes
                if (!flag) {
                    flag = true;
                }
                final Museum museum = dataSnapshot.getValue(Museum.class);
                assert museum != null;
                museum.key = dataSnapshot.getKey();
                //Get all Museum Exhibit based on language of the app
                mDatabase.child("museumFields").orderByChild("museum").equalTo(museum.key).addChildEventListener(new ChildEventListener() {
                    String userLanguage = appLanguage;

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        MuseumFields museumFields = dataSnapshot.getValue(MuseumFields.class);
                        assert museumFields != null;
                        if (museumFields.museum.equals(museum.key) && museumFields.language.equals(userLanguage.toLowerCase())) {
                            museum.description = museumFields.description;
                            museum.name = museumFields.name;
                            museum.shortDescription = museumFields.shortDescription;
                            adapter.add(museum);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //  String museumName = (String) dataSnapshot.child("name")
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //adapter.remove((String) dataSnapshot.child("name").getValue());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getMuseumsForGeofence() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Fire the adapterFull event!
                setSignalFull(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabase.child("museums").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //It is important for the adapter to works to use museumAdapter.notifyDataSetChanged(); after
                //firebase add all museum inside the list , triggers adapter to see the data changes\
                final Museum museum = dataSnapshot.getValue(Museum.class);
                assert museum != null;
                museum.key = dataSnapshot.getKey();
                museumAdapter.add(museum);
                museumAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void userFavorite(String userId, Museum museum, boolean isfav) {
        Map<String, Object> museumValues = museum.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        if (isfav) {
            childUpdates.put("/user-favorites/" + userId + "/" + museum.key, museumValues);
            mDatabase.updateChildren(childUpdates);
        } else {
            mDatabase.child("user-favorites").child(userId).child(museum.key).removeValue();
        }
    }

}

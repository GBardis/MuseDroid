package com.example.musedroid.musedroid;

import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
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

    //Create named childEventListener to keep the reference for when you want to mDatabase.removeEventListener(listener)
    ChildEventListener childEventListener = new ChildEventListener() {
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
    };

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

    public void getMuseums(final MuseumAdapter adapter, final String appLanguage) {

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               
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
                if (museumAdapter.getItemCount() != 0) {
                    setSignalFull(true);
                    mDatabase.removeEventListener(this);
                    mDatabase.removeEventListener(childEventListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("museums").addChildEventListener(childEventListener);
    }

    public void userFavorite(String userId, Museum museum, boolean isfav) {
        Map<String, Object> museumValues = museum.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        Map<String, List<String>> hm = new HashMap<String, List<String>>();
        List<String> values = new ArrayList<String>();
        JSONObject haha = new JSONObject(museumValues);

        values.add("Value 1");
        values.add("Value 2");
        hm.put("Key1", values);
        Collection<List<String>> output = hm.values();
        if (isfav) {
            childUpdates.put("/user-favorites/" + userId + "/" + museum.key, museumValues);
            mDatabase.updateChildren(childUpdates);
        } else {
            mDatabase.child("user-favorites").child(userId).child(museum.key).removeValue();
        }
    }

}

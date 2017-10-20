package com.example.musedroid.musedroid;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
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
    private ArrayAdapter userFavorites;
    boolean flagFull = false;
    private static boolean signalFull;
    private static List<AdapterFullListener> adapterFullListeners = new ArrayList<AdapterFullListener>();

    public static boolean getSignalFull() {
        return signalFull;
    }

    private boolean runOnce = false;

    public static void setSignalFull(boolean bool) {
        signalFull = bool;
        for (AdapterFullListener adapterFullListener : adapterFullListeners) {
            adapterFullListener.OnAdapterFull();
        }
    }

    public static void addAdapterFullListener(AdapterFullListener adapterFullListener) {
        adapterFullListeners.add(adapterFullListener);
    }

    public void getMuseums(final MuseumAdapter adapter, final ProgressBar progressBar, final View view) {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (flag == true) {
                    progressBar.setVisibility(view.GONE);
                    flag = false;
                }
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
                //firebase add all museum inside the list , triggers adapter to see the data changes


                Museum museum = dataSnapshot.getValue(Museum.class);
                assert museum != null;
                museum.key = dataSnapshot.getKey();
                adapter.add(museum);
                adapter.notifyDataSetChanged();
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

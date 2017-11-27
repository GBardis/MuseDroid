package com.example.musedroid.musedroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class Fragment1 extends Fragment {
    private static final String ALL_MUSEUM = "ALL MUSEUMS";
    public static String tempLang;
    public View rootView;
    ArrayList<Museum> bundledMuseumsList = new ArrayList<>();
    Intent intent;
    RecyclerView mRecyclerView;
    MuseumAdapter allMuseums;
    String appLanguage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fragment1, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appLanguage = getAppLanguage();
        tempLang = MainActivity.tempLang;
        mRecyclerView = view.findViewById(R.id.museumRecycleView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        //use getActivity instead of this in LinearLayoutManager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        if ((savedInstanceState == null)) {
            try {
                //set the adapter with the museum list form firebase
                getFirebaseUpdates();

            } catch (Exception ex) {
                Log.e("Exception", ex.getMessage());
                Log.e("Exception", Arrays.toString(ex.getStackTrace()));
            }
        }
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View view) {
                if (isNetworkAvailable()) {
                    MuseumAdapter adapter = (MuseumAdapter) recyclerView.getAdapter();
                    intent = new Intent(view.getContext(), MuseumShow.class);
                    intent.putExtra("museum", adapter.getItem(position));
                    startActivity(intent);
                } else {
                    createToastMessages("Check Internet Access");
                }
            }
        });
    }

    private void createToastMessages(String message) {
        Context context = getActivity().getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        appLanguage = getAppLanguage();
        try {
            if (savedInstanceState != null && tempLang.equals(appLanguage)) {

                //Restore last state for checked position.
                allMuseums = new MuseumAdapter(savedInstanceState.<Museum>getParcelableArrayList(ALL_MUSEUM));
                allMuseums.notifyDataSetChanged();
                mRecyclerView.setAdapter(allMuseums);
            } else {
                getFirebaseUpdates();
            }
        } catch (Exception ex) {
            Log.e("Exception", ex.getMessage());
            Log.e("Exception", Arrays.toString(ex.getStackTrace()));
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@Nullable Bundle outState) {
        try {
            if (outState != null && tempLang.equals(appLanguage)) {
                restoreMuseumAdapter(outState);
            } else {
                tempLang = appLanguage;
                restoreMuseumAdapter(outState);
            }
        } catch (Exception ex) {
            Log.e("Exception", ex.getMessage());
            Log.e("Exception", Arrays.toString(ex.getStackTrace()));
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }

    private Bundle restoreMuseumAdapter(@Nullable Bundle outState) {
        bundledMuseumsList.clear();
        for (int i = 0; i < allMuseums.getItemCount(); i++) {
            bundledMuseumsList.add(allMuseums.getItem(i));
        }
        assert outState != null;
        outState.putParcelableArrayList(ALL_MUSEUM, bundledMuseumsList);
        return outState;
    }

    // Get new data form MainActivity museumAdapter ,this function called when app needs new data
    private void getFirebaseUpdates() {
        allMuseums = MainActivity.museumAdapter;
        allMuseums.notifyDataSetChanged();
        mRecyclerView.setAdapter(allMuseums);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        FirebaseHandler.database.goOnline();
        super.onStart();
    }

    @Override
    public void onResume() {
        appLanguage = getAppLanguage();
        if (!tempLang.equals(appLanguage)) {
            getFirebaseUpdates();
        }
        FirebaseHandler.database.goOnline();
        super.onResume();
    }

    @Override
    public void onPause() {
        FirebaseHandler.database.goOffline();
        super.onPause();
    }

    @Override
    public void onStop() {
        FirebaseHandler.database.goOffline();
        super.onStop();
    }

    private String getAppLanguage() {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        return sharedPrefs.getString("prefAppLanguage", "NULL");
    }
}





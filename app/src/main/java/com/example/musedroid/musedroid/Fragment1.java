package com.example.musedroid.musedroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

public class Fragment1 extends Fragment {
    private static final String ALL_MUSEUM = "ALL MUSEUMS";
    ArrayList<Museum> bundledMuseumsList = new ArrayList<>();
    Intent intent;
    RecyclerView mRecyclerView;
    MuseumAdapter allMuseums;
    public static View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_fragment1, container, false);
        return  rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        //initialize Museum adapter and give as import an array list
        //call getfirebase object to get the museumAdapter with all museums

        if (savedInstanceState == null) {
            try {
                //set the adapter with the museum list form firebase
                allMuseums = MainActivity.museumAdapter;

                mRecyclerView.setAdapter(allMuseums);

               //changeActivity(allMuseums);
            } catch (Exception ex) {
                Log.e("Exception", ex.getMessage());
                Log.d("Exception", Arrays.toString(ex.getStackTrace()));
            }
        }
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                // do it
                MuseumAdapter adapter  = (MuseumAdapter) recyclerView.getAdapter();
                intent = new Intent(v.getContext(), ShowActivity.class);
                intent.putExtra("museum", adapter.getItem(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        try {
            if (savedInstanceState != null) {
                mRecyclerView.getRecycledViewPool().clear();
                //Restore last state for checked position.
                allMuseums = new MuseumAdapter(savedInstanceState.<Museum>getParcelableArrayList(ALL_MUSEUM));
                allMuseums.notifyDataSetChanged();

                mRecyclerView.setAdapter(allMuseums);
                //changeActivity(allMuseums);
            }
        } catch (Exception ex) {
            Log.e("Exception", ex.getMessage());
            Log.d("Exception", Arrays.toString(ex.getStackTrace()));
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@Nullable Bundle outState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
        try {
            if (outState != null) {
                bundledMuseumsList.clear();
                for (int i = 0; i < allMuseums.getItemCount(); i++) {
                    bundledMuseumsList.add(allMuseums.getItem(i));
                }
                outState.putParcelableArrayList(ALL_MUSEUM, bundledMuseumsList);
            }
        } catch (Exception ex) {
            Log.e("Exception", ex.getMessage());
            Log.d("Exception", Arrays.toString(ex.getStackTrace()));
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseHandler.database.goOnline();
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseHandler.database.goOnline();
    }

    @Override
    public void onPause() {
        super.onPause();
        FirebaseHandler.database.goOffline();
    }

    @Override
    public void onStop() {
        super.onStop();
        FirebaseHandler.database.goOffline();
    }
}


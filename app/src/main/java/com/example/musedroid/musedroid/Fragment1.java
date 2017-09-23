package com.example.musedroid.musedroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class Fragment1 extends Fragment {
    public ArrayList<Museum> museumArrayList;
    Intent intent;
    RecyclerView mRecyclerView;
    MuseumAdapter museumAdapter;
    ProgressBar progressBar;
    GetFirebase getFirebase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fragment1, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.museumRecycleView);
        progressBar = view.findViewById(R.id.museumProgressbar);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        //use getActivity instead of this in LinearLayoutManager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //initialize Museum adapter and give as import an array list
        //call getfirebase object to get the museumAdapter with all museums
        getFirebase = new GetFirebase();
        museumArrayList = new ArrayList<>();
        museumAdapter = new MuseumAdapter(museumArrayList);
        //set the adapter with the museum list form firebase
        mRecyclerView.setAdapter(getFirebase.listViewFromFirebase(museumAdapter));
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MuseumAdapter) museumAdapter).setOnItemClickListener(new MuseumAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                intent = new Intent(view.getContext(), ShowActivity.class);
                intent.putExtra("museum", museumAdapter.getItem(position));
                startActivity(intent);
            }
        });
    }

}


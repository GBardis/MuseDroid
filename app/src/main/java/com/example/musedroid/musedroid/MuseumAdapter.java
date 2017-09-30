package com.example.musedroid.musedroid;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gdev on 22/9/2017.
 */

class MuseumAdapter extends RecyclerView.Adapter<MuseumAdapter.ViewHolder> {
    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference mDatabase = database.getReference();
    private List<Museum> museumList;

    // Provide a suitable constructor (depends on the kind of dataset)
    MuseumAdapter(List<Museum> myDataset) {
        museumList = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.museum_row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseHandler firebaseHandler = new FirebaseHandler();
        GetFirebase getFirebase = new GetFirebase();
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Museum museum = museumList.get(position);
        holder.title.setText(museum.name);
        holder.description.setText(museum.description);
//        ObservableUserFavoriteList.ObservableList userFavorites = new ObservableUserFavoriteList.ObservableList<Museum>();
//
//        userFavorites = getFirebase.userFavoriteListFromFirebase(auth.getCurrentUser().getUid(), new ObservableUserFavoriteList.ObservableList<Museum>());
//        for (int i = 0; i < userFavorites.getItemCount(); i++) {
//            if (userFavorites.getItem(i).equals(museum.name)) {
//                holder.favoriteButton.setFavorite(true);
//            } else {
//                holder.favoriteButton.setFavorite(false);
//            }
//        }
        //final ObservableUserFavoriteList.ObservableList userFavorites = new ObservableUserFavoriteList.ObservableList();
//        final ArrayList<User> favMuseum = new ArrayList<>();
//        mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("favorites").addChildEventListener(new ChildEventListener() {
//
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                favMuseum.add(dataSnapshot.getValue(User.class));
//
//           }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        for (int i = 0; i < favMuseum.size(); i++) {
//            if (Objects.equals(favMuseum.get(i).name, museum.name)) {
//                holder.favoriteButton.setFavorite(true);
//            } else {
//                holder.favoriteButton.setFavorite(false);
//            }
//        }
        holder.favoriteButton.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {


                        String userId = auth.getCurrentUser().getUid();
                        String favoriteMuseum = museum.name;
                        firebaseHandler.userFavorite(userId, favoriteMuseum);
                    }
                });

    }

    public void addItem(Museum dataObj, int index) {
        museumList.add(index, dataObj);
        notifyItemInserted(index);
    }

    void clear() {
        int size = this.museumList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.museumList.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }

    Museum getItem(int position) {
        return museumList.get(position);
    }

    void addAll(ArrayList<Museum> museumArrayList) {
        for (int i = 0; i < museumArrayList.size(); i++) {
            this.museumList.add(museumArrayList.get(i));
        }
        this.notifyItemInserted(getItemCount() - 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(Museum item) {
        this.museumList.add(item);
        this.notifyItemInserted(getItemCount() - 1);
    }

    public void duplicateAdapter(MuseumAdapter adapter) {
        for (int i = 0; i < adapter.getItemCount(); i++) {
            this.add(adapter.getItem(i));
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.museumList.size();
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView title, description;
        MaterialFavoriteButton favoriteButton;

        ViewHolder(View view) {
            super(view);
            favoriteButton = view.findViewById(R.id.museum_image);
            title = view.findViewById(R.id.museum_name);
            description = view.findViewById(R.id.museum_description);


        }

    }

}


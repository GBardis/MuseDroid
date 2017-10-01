package com.example.musedroid.musedroid;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gdev on 22/9/2017.
 */

class MuseumAdapter extends RecyclerView.Adapter<MuseumAdapter.ViewHolder> {
    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference mDatabase = database.getReference();
    private static FirebaseHandler firebaseHandler = new FirebaseHandler();
    private ValueEventListener followListener;
    private List<Museum> museumList;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

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
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Museum museum = museumList.get(position);
        holder.title.setText(museum.name);
        holder.description.setText(museum.description);

        // Firebase listener that check if a user has any favorite museums
        mDatabase.child("user-favorites").child(auth.getCurrentUser().getUid());
        followListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //firebase query that find's if the museum that recycle view is building
                //It is also favorite of the currentUser
                String museumKey = (String) dataSnapshot.child("user-favorites")
                        .child(auth.getCurrentUser().getUid()).child(museum.key)
                        .child("mName").getValue();

                if (museumKey != null && museumKey.equals(museum.name)) {
                    //triggers the favorite button to show the that this museum is favorite
                    holder.favoriteButton.setFavorite(true);
                } else {
                    //triggers the favorite button to show the that this museum is not favorite
                    holder.favoriteButton.setFavorite(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        //Close firebase listener
        mDatabase.addValueEventListener(followListener);
        //Favorite button listener that responds to user action and save or delete userFavorites to firebase
        holder.favoriteButton.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {

                        if (favorite && !buttonView.isFavorite()) {
                            //firebase call to save the user favorite museum
                            String userId = auth.getCurrentUser().getUid();
                            firebaseHandler.userFavorite(userId, museum, true);
                        } else {
                            //firebase call to delete user favorites museum
                            String userId = auth.getCurrentUser().getUid();
                            firebaseHandler.userFavorite(userId, museum, favorite);
                        }
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

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.museumList.size();
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        // Remove firebase base listener
        DatabaseReference followRef = mDatabase.child("user-favorites").child(auth.getCurrentUser().getUid());
        followRef.removeEventListener(followListener); //Removes the listener
        super.onViewDetachedFromWindow(holder);
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


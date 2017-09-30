package com.example.musedroid.musedroid;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by gdev on 22/9/2017.
 */

class MuseumAdapter extends RecyclerView.Adapter<MuseumAdapter.ViewHolder> {
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Museum museum = museumList.get(position);
        holder.title.setText(museum.name);
        holder.description.setText(museum.description);

        if (Objects.equals(museum.name, "Acropolis Museum")) {
            holder.favoriteButton.setFavorite(true);
        } else {
            holder.favoriteButton.setFavorite(false);
        }
        holder.favoriteButton.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                        FirebaseHandler firebaseHandler = new FirebaseHandler();

                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        String email = auth.getCurrentUser().getEmail();
                        firebaseHandler.userFavorite(email);

                        String key = museum.key;
                        String name = "george";
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


package com.example.musedroid.musedroid;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by gdev on 22/9/2017.
 */

public class MuseumAdapter extends RecyclerView.Adapter<MuseumAdapter.ViewHolder> {
    private static MyClickListener myClickListener;
    private List<Museum> museumList;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MuseumAdapter(List<Museum> myDataset) {
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
        Museum museum = museumList.get(position);
        holder.title.setText(museum.name);
        holder.description.setText(museum.description);
    }

    public void addItem(Museum dataObj, int index) {
        museumList.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void clear() {
        int size = this.museumList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.museumList.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }

    public Museum getItem(int position) {
        return museumList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(Museum item) {
        museumList.add(item);
        //notifyItemInserted(getItemCount());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return museumList.size();
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        TextView title, description;
        CardView cv;
        ImageView museumImage;

        public ViewHolder(View view) {
            super(view);
            //cv = itemView.findViewById(R.id.cv);
            title = view.findViewById(R.id.museum_name);
            description = view.findViewById(R.id.museum_description);
            //museumImage = view.findViewById(R.id.museum_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            myClickListener.onItemClick(getAdapterPosition(), view);
        }
    }

}


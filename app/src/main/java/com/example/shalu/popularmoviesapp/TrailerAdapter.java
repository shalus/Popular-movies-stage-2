package com.example.shalu.popularmoviesapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shalu.popularmoviesapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

/**
 * Created by sarum on 10/5/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private String[] mTrailerName;
    private TrailerAdapter.OnTrailerItemClickListener mClickHandler;

    TrailerAdapter(TrailerAdapter.OnTrailerItemClickListener clickListener) {
        mClickHandler = clickListener;

    }

    /**
     * Interface to receive onClick messages
     */
    public interface OnTrailerItemClickListener {
        void onClickTrailer(int position);
    }

    /**
     * Method which gets called when each new view holder is created. Enough view holders are created to fill the screen
     * @param parent view group which contains the view holders
     * @return s view holder that contains the view
     */
    @Override
    public TrailerAdapter.TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.trailers;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutId,parent,shouldAttachToParentImmediately);
        TrailerAdapter.TrailerViewHolder viewHolder = new TrailerAdapter.TrailerViewHolder(view);
        return viewHolder;
    }

    /**
     *  Method that binds the view with the data.
     *  It gets the movie poster image path for a location and loads the image at that position
     * @param viewHolder the view holder object to be updated with image view
     * @param position the position of the item in the adapter
     */

    @Override
    public void onBindViewHolder(TrailerAdapter.TrailerViewHolder viewHolder, int position) {
        viewHolder.mTrailer.setText(mTrailerName[position]);
    }

    /**
     * Method that returns the number of items to display
     * @return number of available items
     */

    @Override
    public int getItemCount() {

        if(mTrailerName == null)
            return 0;
        else
            return mTrailerName.length;
    }

    /**
     *This method sets the trailers on the adapter
     * @param trailerName the trailer to be displayed
     */

    public void setmTrailerName(String[] trailerName) {
        mTrailerName = trailerName;
        notifyDataSetChanged();
    }

    /**
     * Cache of the children views
     */
    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mTrailer;
        public TrailerViewHolder(View view) {
            super(view);
            mTrailer = (TextView) view.findViewById(R.id.tv_trailer);
            view.setOnClickListener(this);

        }

        /**
         * This gets called when a child view is clicked
         * @param v view that was clicked
         */
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mClickHandler.onClickTrailer(position);
        }
    }
}

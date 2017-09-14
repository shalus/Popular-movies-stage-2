package com.example.shalu.popularmoviesapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.shalu.popularmoviesapp.Utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.PosterViewHolder> {

    private String[] mPosterPaths;
    private OnMovieItemClickListener mClickHandler;

    MoviePosterAdapter(OnMovieItemClickListener clickListener) {
        mClickHandler = clickListener;

    }

    /**
     * Interface to receive onClick messages
     */
    public interface OnMovieItemClickListener {
         void onClick(int position);
    }

    /**
     * Method which gets called when each new view holder is created. Enough view holders are created to fill the screen
     * @param parent view group which contains the view holders
     * @return s view holder that contains the view
     */
    @Override
    public PosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.movie_poster;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutId,parent,shouldAttachToParentImmediately);
        PosterViewHolder viewHolder = new PosterViewHolder(view);
        return viewHolder;
    }

    /**
     *  Method that binds the view with the data.
     *  It gets the movie poster image path for a location and loads the image at that position
     * @param posterViewHolder the view holder object to be updated with image view
     * @param position the position of the item in the adapter
     */

    @Override
    public void onBindViewHolder(PosterViewHolder posterViewHolder, int position) {
        URL url = NetworkUtils.buildImagePosterUrl(mPosterPaths[position]);
        Picasso.with(posterViewHolder.mImgView.getContext()).load(url.toString()).into(posterViewHolder.mImgView);
    }

    /**
     * Method that returns the number of items to display
     * @return number of available items
     */

    @Override
    public int getItemCount() {

        if(mPosterPaths == null)
            return 0;
        else
            return mPosterPaths.length;
    }

    /**
     *This method sets the movie poster paths on the adapter
     * @param moviePosterPaths the poster paths to be displayed
     */

    public void setMoviePosterPaths(String[] moviePosterPaths) {
        mPosterPaths = moviePosterPaths;
        notifyDataSetChanged();
    }

    /**
     * Cache of the children views
     */
    public class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mImgView;
        public PosterViewHolder(View view) {
            super(view);
            mImgView = (ImageView) view.findViewById(R.id.movie_poster);
            view.setOnClickListener(this);

        }

        /**
         * This gets called when a child view is clicked
         * @param v view that was clicked
         */
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mClickHandler.onClick(position);
        }
    }
}

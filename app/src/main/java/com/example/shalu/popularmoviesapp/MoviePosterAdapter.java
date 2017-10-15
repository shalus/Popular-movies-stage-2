package com.example.shalu.popularmoviesapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.shalu.popularmoviesapp.data.MovieContract;
import com.example.shalu.popularmoviesapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.PosterViewHolder> {

    private String[] mPosterPaths;
    private Cursor mCursor;
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
        URL url=null;
        if(mPosterPaths!=null)
            url = NetworkUtils.buildImagePosterUrl(mPosterPaths[position]);
        if(mCursor!=null) {
            mCursor.moveToPosition(position);
            int imgIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER);
            url = NetworkUtils.buildImagePosterUrl(mCursor.getString(imgIndex));
        }
        if(url!=null)
        Picasso.with(posterViewHolder.mImgView.getContext())
                    .load(url.toString())
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.user_placeholder_error)
                    .into(posterViewHolder.mImgView);

       /* else if(mPosterPaths == null && mCursor!=null) {

            byte[] img = mCursor.getBlob(imgIndex);
            Bitmap bitmapImg = getImage(img);
            posterViewHolder.mImgView.setImageBitmap(bitmapImg);
        }*/
    }
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    /**
     * Method that returns the number of items to display
     * @return number of available items
     */

    @Override
    public int getItemCount() {

        if(mPosterPaths == null && mCursor==null)
            return 0;
        else if(mCursor == null)
            return mPosterPaths.length;
        else
            return mCursor.getCount();
    }

    /**
     *This method sets the movie poster paths on the adapter
     * @param moviePosterPaths the poster paths to be displayed
     */

    public void setMoviePosterPaths(String[] moviePosterPaths) {
        mCursor = null;
        mPosterPaths = moviePosterPaths;
        notifyDataSetChanged();
    }
    public void setOfflineMovies(Cursor data){
        mPosterPaths = null;
        mCursor = data;
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

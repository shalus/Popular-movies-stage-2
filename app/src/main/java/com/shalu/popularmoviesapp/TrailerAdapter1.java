package com.shalu.popularmoviesapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.shalu.popularmoviesapp.R;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.shalu.popularmoviesapp.utilities.Config;

/**
 * Created by sarum on 11/9/2017.
 */

public class TrailerAdapter1 extends RecyclerView.Adapter<TrailerAdapter1.VideoInfoHolder> {

    private String[] videoID;
    Context ctx;

    TrailerAdapter1(Context context) {
        ctx = context;

    }

    /**
     * Interface to receive onClick messages

    public interface OnTrailerItemClickListener {
        void onClickTrailer(int position);
    }

    /**
     * Method which gets called when each new view holder is created. Enough view holders are created to fill the screen
     * @param parent view group which contains the view holders
     * @return s view holder that contains the view
     */
    @Override
    public TrailerAdapter1.VideoInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.trailers1;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutId,parent,shouldAttachToParentImmediately);
        TrailerAdapter1.VideoInfoHolder viewHolder = new TrailerAdapter1.VideoInfoHolder(view);
        return viewHolder;
    }

    /**
     *  Method that binds the view with the data.
     *  It gets the movie poster image path for a location and loads the image at that position
     * @param viewHolder the view holder object to be updated with image view
     * @param position the position of the item in the adapter
     */

    @Override
    public void onBindViewHolder(final TrailerAdapter1.VideoInfoHolder viewHolder, final int position) {
        final YouTubeThumbnailLoader.OnThumbnailLoadedListener  onThumbnailLoadedListener = new YouTubeThumbnailLoader.OnThumbnailLoadedListener(){
            @Override
            public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
            Log.d("Thumbnail error","Failed to load thumbnail");
            }

            @Override
            public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                youTubeThumbnailView.setVisibility(View.VISIBLE);
                viewHolder.relativeLayoutOverYouTubeThumbnailView.setVisibility(View.VISIBLE);
            }
        };

        viewHolder.youTubeThumbnailView.initialize(Config.YOUTUBE_API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {

                youTubeThumbnailLoader.setVideo(videoID[position]);
                youTubeThumbnailLoader.setOnThumbnailLoadedListener(onThumbnailLoadedListener);
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d("youtube error","Failed to load video");//write something for failure
            }
        });
    }

    /**
     * Method that returns the number of items to display
     * @return number of available items
     */

    @Override
    public int getItemCount() {

        if(videoID == null)
            return 0;
        else
            return videoID.length;
    }

    /**
     *This method sets the trailers on the adapter
     * @param trailerName the trailer to be displayed
     */

    public void setmTrailerName(String[] trailerName) {
        videoID = trailerName;
        notifyDataSetChanged();
    }

    /**
     * Cache of the children views
     */
    public class VideoInfoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected RelativeLayout relativeLayoutOverYouTubeThumbnailView;
        YouTubeThumbnailView youTubeThumbnailView;
        protected ImageView playButton;

        public VideoInfoHolder(View itemView) {
            super(itemView);
            playButton=(ImageView)itemView.findViewById(R.id.btnYoutube_player);
            playButton.setOnClickListener(this);
            relativeLayoutOverYouTubeThumbnailView = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_over_youtube_thumbnail);
            youTubeThumbnailView = (YouTubeThumbnailView) itemView.findViewById(R.id.youtube_thumbnail);
        }

        @Override
        public void onClick(View v) {

            Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity) ctx, Config.YOUTUBE_API_KEY, videoID[getLayoutPosition()]);
            if (intent.resolveActivity(ctx.getPackageManager()) != null) {
                ctx.startActivity(intent);
            }
            else {
                Toast.makeText(ctx,"Couldn't open trailers. Make sure Youtube is available",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
 /*   public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
        /*@Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mClickHandler.onClickTrailer(position);
        }
    }
} {
}*/

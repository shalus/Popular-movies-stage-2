package com.example.shalu.popularmoviesapp;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shalu.popularmoviesapp.data.MovieContract;
import com.example.shalu.popularmoviesapp.data.PopMoviesPreferences;
import com.example.shalu.popularmoviesapp.databinding.ActivityDetailBinding;
import com.example.shalu.popularmoviesapp.utilities.MovieDbJsonUtils;
import com.example.shalu.popularmoviesapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import com.example.shalu.popularmoviesapp.DetailActivity.MovieDetail;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.OnTrailerItemClickListener, ReviewsAdapter.OnReviewsItemClickListener, LoaderManager.LoaderCallbacks<DetailActivity.MovieDetail> {

    private static final String TAG = DetailActivity.class.getName();

    private TrailerAdapter trailerAdapter;
    private ReviewsAdapter reviewsAdapter;

    private String movieId;
    private String mDesc;
    private String mMovieTitle;
    private String mRating;
    private String mImg;
    private String mReleaseDate;

    String[] trailerKeys;

    class MovieDetail {
        String[] trailerNames;
        String[] reviewAuthor;
        String[] reviewContent;
    }
    MovieDetail detail;

    private ActivityDetailBinding mDetailBinding;

    private static final String ID = "MOVIE_ID";
    private static final int MOVIE_DETAIL_LOADER_ID=23;
    private int mTrailerPosition;
    private int mReviewsPosition;
    private String TRAILER_POSITION = "trailer_position";
    private String REVIEWS_POSITION = "reviews_position";

    LinearLayoutManager layoutManager,layoutManagerReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDetailBinding = DataBindingUtil.setContentView(this,R.layout.activity_detail);

        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mDetailBinding.rvTrailers.setLayoutManager(layoutManager);
        trailerAdapter = new TrailerAdapter(this);
        mDetailBinding.rvTrailers.setAdapter(trailerAdapter);
        mDetailBinding.rvTrailers.setHasFixedSize(true);
        layoutManagerReviews = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        reviewsAdapter = new ReviewsAdapter(this);
        mDetailBinding.rvReviews.setLayoutManager(layoutManagerReviews);
        mDetailBinding.rvReviews.setAdapter(reviewsAdapter);
        mDetailBinding.rvReviews.setHasFixedSize(true);


        getSupportLoaderManager().initLoader(MOVIE_DETAIL_LOADER_ID,null,this);
        Intent intentThatStartedThisActivity = getIntent();
        if(savedInstanceState!=null) {
            mMovieTitle = savedInstanceState.getString(getString(R.string.title));
            mRating = savedInstanceState.getString(getString(R.string.rating));
            mDesc = savedInstanceState.getString(getString(R.string.desc));
            movieId = savedInstanceState.getString(getString(R.string.id));
            mReleaseDate = savedInstanceState.getString(getString(R.string.release_date));
            mImg = savedInstanceState.getString(getString(R.string.img_poster));
            if(savedInstanceState.getInt(getString(R.string.favState))== 1) {
                mDetailBinding.favButton.setTag(R.drawable.ic_favorite_pink_24dp);
                mDetailBinding.favButton.setImageResource(R.drawable.ic_favorite_pink_24dp);
            }
            else{
                mDetailBinding.favButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                mDetailBinding.favButton.setTag(R.drawable.ic_favorite_border_black_24dp);

            }
        }

        else if(intentThatStartedThisActivity!=null) {
            if (intentThatStartedThisActivity.hasExtra(getString(R.string.title)))
                mMovieTitle = intentThatStartedThisActivity.getStringExtra(getString(R.string.title));

            if (intentThatStartedThisActivity.hasExtra(getString(R.string.release_date)))
                mReleaseDate = intentThatStartedThisActivity.getStringExtra(getString(R.string.release_date));

            if (intentThatStartedThisActivity.hasExtra(getString(R.string.rating)))
                mRating = intentThatStartedThisActivity.getStringExtra(getString(R.string.rating));

            if (intentThatStartedThisActivity.hasExtra(getString(R.string.desc)))
                mDesc = intentThatStartedThisActivity.getStringExtra(getString(R.string.desc));

            if (intentThatStartedThisActivity.hasExtra(getString(R.string.img_poster)))
                mImg = intentThatStartedThisActivity.getStringExtra(getString(R.string.img_poster));

            if (intentThatStartedThisActivity.hasExtra(getString(R.string.id))) {
                movieId = intentThatStartedThisActivity.getStringExtra(getString(R.string.id));
            }
            Uri uri = MovieContract.MovieEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(movieId).build();
            Log.d(TAG, "Movie id: " + movieId + "  uri: " + uri.toString());
            try {
                Cursor c = getContentResolver().query(uri, null, null, null, null);
                if (c.getCount() != 0) {
                    mDetailBinding.favButton.setImageResource(R.drawable.ic_favorite_pink_24dp);
                    mDetailBinding.favButton.setTag(R.drawable.ic_favorite_pink_24dp);
                }
                else {
                    mDetailBinding.favButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    mDetailBinding.favButton.setTag(R.drawable.ic_favorite_border_black_24dp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        mDetailBinding.tvMovieTitle.setText(mMovieTitle);
        mDetailBinding.tvReleaseDate.setText("Release Date: "+mReleaseDate);
        mDetailBinding.tvMovieRating.setText("Rating: "+mRating);
        mDetailBinding.tvOverview.setText(mDesc);
       // mDetailBinding.favButton.setTag(R.drawable.ic_favorite_border_black_24dp);

        URL url = NetworkUtils.buildImagePosterUrl(mImg);
        Picasso.with(mDetailBinding.ivMoviePoster.getContext())
                .load(url.toString())
                .error(R.drawable.user_placeholder_error)
                .placeholder(R.drawable.user_placeholder)
                .into(mDetailBinding.ivMoviePoster);

        Bundle bundle = new Bundle();
        bundle.putString(ID,movieId);
        Loader<MovieDetail> trailerLoader = getSupportLoaderManager().getLoader(MOVIE_DETAIL_LOADER_ID);
        if(trailerLoader == null) {
            getSupportLoaderManager().initLoader(MOVIE_DETAIL_LOADER_ID,bundle,this);
        }
        else
            getSupportLoaderManager().restartLoader(MOVIE_DETAIL_LOADER_ID,bundle,this);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("ARTICLE_SCROLL_POSITION",
                new int[]{ mDetailBinding.svDetail.getScrollX(), mDetailBinding.svDetail.getScrollY()});
        outState.putString(getString(R.string.id),movieId);
        outState.putString(getString(R.string.title),mMovieTitle);
        outState.putString(getString(R.string.img_poster),mImg);
        outState.putString(getString(R.string.desc),mDesc);
        outState.putString(getString(R.string.rating),mRating);
        outState.putString(getString(R.string.release_date),mReleaseDate);
        if(R.drawable.ic_favorite_pink_24dp == (int) mDetailBinding.favButton.getTag())
            outState.putInt(getString(R.string.favState), 1);
        else
            outState.putInt(getString(R.string.favState),0);
        mTrailerPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        mReviewsPosition = layoutManagerReviews.findFirstCompletelyVisibleItemPosition();
        outState.putInt(TRAILER_POSITION, mTrailerPosition);
        outState.putInt(REVIEWS_POSITION, mReviewsPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position = savedInstanceState.getIntArray("ARTICLE_SCROLL_POSITION");
            if(position != null)
                mDetailBinding.svDetail.post(new Runnable() {
                    public void run() {
                        mDetailBinding.svDetail.scrollTo(position[0], position[1]);
                    }
                });
        mTrailerPosition = savedInstanceState.getInt(TRAILER_POSITION);
        mReviewsPosition = savedInstanceState.getInt(REVIEWS_POSITION);
    }

    @Override
    public void onClickTrailer(int position) {
        Intent intentForVideos = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerKeys[position]));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + trailerKeys[position]));

        if (intentForVideos.resolveActivity(getPackageManager()) != null) {
            startActivity(intentForVideos);
        }
        else if(webIntent.resolveActivity(getPackageManager())!=null){
            startActivity(webIntent);
        }
        else
            Log.d(TAG,"Couldn't open trailers");
    }

    @Override
    public void onClickReviews(int position) {
        String author = detail.reviewAuthor[position];
        String content = detail.reviewContent[position];
        final Dialog dialog= new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.custom_review_dialog,null);
        dialog.setContentView(view);
        dialog.setTitle(author);
        TextView tvContent = (TextView) view.findViewById(R.id.tv_dialog_content);
        tvContent.setText(content);
        dialog.show();
    }
    @Override
    public Loader<MovieDetail> onCreateLoader(final int id, final Bundle args) {

        return new AsyncTaskLoader<MovieDetail>(this) {

            @Override
            protected void onStartLoading() {
                if(args == null) {
                    return;
                }
                if(id == MOVIE_DETAIL_LOADER_ID) {
                    if (detail!= null)
                        deliverResult(detail);
                    else
                        forceLoad();
                }

            }

            @Override
            public MovieDetail loadInBackground() {

                String MD_TRAILER_KEY = "key";
                String MD_TRAILER_NAME = "name";
                String MD_AUTHOR = "author";
                String MD_CONTENT = "content";
                detail = new MovieDetail();

                try {
                URL trailerURL = NetworkUtils.buildOtherUrl(movieId,"videos");
                URL reviewsURL = NetworkUtils.buildOtherUrl(movieId,"reviews");
                String trailerResponse = NetworkUtils.getResponseFromHttpUrl(trailerURL);
                String reviewResponse = NetworkUtils.getResponseFromHttpUrl(reviewsURL);
                trailerKeys = MovieDbJsonUtils.getResultsFromJson(trailerResponse,MD_TRAILER_KEY);
                detail.trailerNames = MovieDbJsonUtils.getResultsFromJson(trailerResponse,MD_TRAILER_NAME);
                detail.reviewAuthor = MovieDbJsonUtils.getResultsFromJson(reviewResponse,MD_AUTHOR);
                detail.reviewContent = MovieDbJsonUtils.getResultsFromJson(reviewResponse,MD_CONTENT);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
                return detail;
            }

            @Override
            public void deliverResult(MovieDetail data) {
                detail = data;
                super.deliverResult(data);
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<MovieDetail> loader, MovieDetail data) {
        if(data == null) return;
        if(data.trailerNames!=null && data.trailerNames.length > 0) {
            makeVisibleTrailers();
            trailerAdapter.setmTrailerName(data.trailerNames);
            if(mTrailerPosition == RecyclerView.NO_POSITION) mTrailerPosition = 0;
            Log.d(TAG,"Trailer position: "+mTrailerPosition);
            mDetailBinding.rvTrailers.smoothScrollToPosition(mTrailerPosition);
        }
        if(data.reviewAuthor!=null && data.reviewContent!=null) {
            if (data.reviewAuthor.length > 0 && data.reviewContent.length > 0) {
                makeVisibleReviews();
                reviewsAdapter.setReviews(data.reviewAuthor, data.reviewContent);
                if(mReviewsPosition == RecyclerView.NO_POSITION) mReviewsPosition = 0;
                Log.d(TAG,"Reviews position: "+mReviewsPosition);
                mDetailBinding.rvReviews.smoothScrollToPosition(mReviewsPosition);
            }
        }
    }



    @Override
    public void onLoaderReset(Loader<MovieDetail> loader) {

    }

    public void makeVisibleTrailers() {
        mDetailBinding.tvLabelTrailers.setVisibility(View.VISIBLE);
        mDetailBinding.rvTrailers.setVisibility(View.VISIBLE);
    }
    public void makeVisibleReviews() {
        mDetailBinding.tvLabelReviews.setVisibility(View.VISIBLE);
        mDetailBinding.rvReviews.setVisibility(View.VISIBLE);
    }

    public void toggleFavorites(View view) {
        int tag = (int) mDetailBinding.favButton.getTag();
        //ADDING TO FAVOURITES
        if(tag == R.drawable.ic_favorite_border_black_24dp) {
            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, Integer.parseInt(movieId));
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, mMovieTitle);
            values.put(MovieContract.MovieEntry.COLUMN_DESCRIPTION, mDesc);
            values.put(MovieContract.MovieEntry.COLUMN_RATING, mRating);
            values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mReleaseDate);
            values.put(MovieContract.MovieEntry.COLUMN_POSTER, mImg);
            Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
            if (uri != null) {
                mDetailBinding.favButton.setImageResource(R.drawable.ic_favorite_pink_24dp);
                mDetailBinding.favButton.setTag(R.drawable.ic_favorite_pink_24dp);
                Toast.makeText(this, "Added to favourites list", Toast.LENGTH_LONG).show();
            }
        }
        //REMOVE FROM FAVOURITES
        else {
            Uri uri = MovieContract.MovieEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(movieId).build();
            int delete = getContentResolver().delete(uri,null,null);
            Log.d(TAG,uri.toString());
            if(delete > 0) {
                mDetailBinding.favButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                mDetailBinding.favButton.setTag(R.drawable.ic_favorite_border_black_24dp);
                Toast.makeText(this, "Removed from favourites list", Toast.LENGTH_LONG).show();
            }

        }
    }

}

package com.example.shalu.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.shalu.popularmoviesapp.data.MovieContract;
import com.example.shalu.popularmoviesapp.utilities.MovieDbJsonUtils;
import com.example.shalu.popularmoviesapp.utilities.NetworkUtils;

import java.net.URL;

import com.example.shalu.popularmoviesapp.data.PopMoviesPreferences;

public class MainActivity extends AppCompatActivity implements MoviePosterAdapter.OnMovieItemClickListener{

    private String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private TextView mErrorText;
    private ProgressBar mProgressbar;
    private MoviePosterAdapter mMoviePosterAdapter;

    private String[] moviePosterPaths;
    private String[] title;
    private String[] description;
    private String[] rating;
    private String[] releaseDate;
    private String[] movieId;
    Cursor mMovieData;
    GridLayoutManager layoutManager;
    private String KEY_RECYCLER_STATE = "state";
    private int mPosition;

    private static final int FAV_LOADER_ID=10;
    private static final int MAIN_LOADER_ID=11;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_pop_movies);
        mErrorText = (TextView) findViewById(R.id.tv_error_message_display);
        mProgressbar = (ProgressBar) findViewById(R.id.pb_loading_indicator);


            layoutManager = new GridLayoutManager(this, numberOfColumns());
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setHasFixedSize(true);
            mMoviePosterAdapter = new MoviePosterAdapter(this);
            mRecyclerView.setAdapter(mMoviePosterAdapter);
            showMovies();
    }




    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }

    /**
     * Method that gets the preferred setting and calls a background method to load the movie posters in the view
     */
    private void showMovies() {
        String setting = PopMoviesPreferences.getPreferredSetting(this);
        Log.d(TAG,"Setting: "+setting);
        if(setting.equals(getString(R.string.pref_fav))) {
            showMoviesView();
            Loader<Cursor> favLoader = getSupportLoaderManager().getLoader(FAV_LOADER_ID);
            if(favLoader == null)
                getSupportLoaderManager().initLoader(FAV_LOADER_ID,null,cursorLoader);
            else
                getSupportLoaderManager().restartLoader(FAV_LOADER_ID,null,cursorLoader);
        }
        else {
            if(!isOnline()) {
                showNetworkErrorMessage();
                return;
            }
            showMoviesView();
            Loader<String[]> mLoader = getSupportLoaderManager().getLoader(MAIN_LOADER_ID);
            Bundle bundle = new Bundle();
            bundle.putString("SETTING",setting);
            if(mLoader == null)
                getSupportLoaderManager().initLoader(MAIN_LOADER_ID,bundle,movieLoader);
            else
                getSupportLoaderManager().restartLoader(MAIN_LOADER_ID,bundle,movieLoader);
        }
    }
    private void showMoviesView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorText.setVisibility(View.INVISIBLE);
    }
    private void showErrorMessage(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorText.setVisibility(View.VISIBLE);
    }
    private void showNetworkErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorText.setText(getString(R.string.network_Error));
        mErrorText.setVisibility(View.VISIBLE);
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }


   @Override
    protected void onSaveInstanceState(Bundle outState) {
       super.onSaveInstanceState(outState);
       mPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
       outState.putInt(KEY_RECYCLER_STATE, mPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPosition = savedInstanceState.getInt(KEY_RECYCLER_STATE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.top_rated) {
            PopMoviesPreferences.setPreferredSetting(this,getString(R.string.pref_rated));
            moviePosterPaths=null;
            mMoviePosterAdapter.setMoviePosterPaths(null);
            showMovies();
            return true;
        }
        else if (item.getItemId() == R.id.popular) {
            PopMoviesPreferences.setPreferredSetting(this,getString(R.string.pref_default));
            moviePosterPaths=null;
            mMoviePosterAdapter.setMoviePosterPaths(null);
            showMovies();
            return true;
        }
        else if(item.getItemId() == R.id.fav) {
            PopMoviesPreferences.setPreferredSetting(this,getString(R.string.pref_fav));
            mMoviePosterAdapter.setOfflineMovies(null);
            showMovies();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to handle the click at a recycler view. It creates an intent to start next activity
     * @param position the position at which the item was clicked
     */
    @Override
    public void onClick(int position) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        String movieTitle, img, desc, rate, date, id;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        if(PopMoviesPreferences.getPreferredSetting(this).equals(getString(R.string.pref_fav))) {
            mMovieData.moveToPosition(position);
            movieTitle = mMovieData.getString(mMovieData.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE));
            desc = mMovieData.getString(mMovieData.getColumnIndex(MovieContract.MovieEntry.COLUMN_DESCRIPTION));
            rate = mMovieData.getString(mMovieData.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING));
            date = mMovieData.getString(mMovieData.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
            id = mMovieData.getString(mMovieData.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
            img = mMovieData.getString(mMovieData.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER));
        }
        else {
            movieTitle = title[position];
            img = moviePosterPaths[position];
            desc = description[position];
            rate = rating[position];
            date = releaseDate[position];
            id = movieId[position];
        }
        intentToStartDetailActivity.putExtra(getString(R.string.title), movieTitle);
        intentToStartDetailActivity.putExtra(getString(R.string.desc), desc);
        intentToStartDetailActivity.putExtra(getString(R.string.release_date),date);
        intentToStartDetailActivity.putExtra(getString(R.string.rating),rate);
        intentToStartDetailActivity.putExtra(getString(R.string.id),id);
        intentToStartDetailActivity.putExtra(getString(R.string.img_poster),img);

        startActivity(intentToStartDetailActivity);

    }

    LoaderManager.LoaderCallbacks<Cursor> cursorLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<Cursor>(MainActivity.this) {

                @Override
                protected void onStartLoading() {

                    if (mMovieData != null) {
                        deliverResult(mMovieData);

                    } else {
                        mProgressbar.setVisibility(View.VISIBLE);
                        forceLoad();
                    }
                }

                @Override
                public Cursor loadInBackground() {
                    try {
                        return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                null);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to asynchronously load data.");
                        e.printStackTrace();
                        return null;
                    }
                }

                public void deliverResult(Cursor data) {
                    mMovieData = data;
                    super.deliverResult(data);
                }
            };

        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mProgressbar.setVisibility(View.INVISIBLE);
            if (data != null) {
                showMoviesView();
                mMoviePosterAdapter.setOfflineMovies(data);
                if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
                mRecyclerView.smoothScrollToPosition(mPosition);
            } else {
                Log.d(TAG, "Show error message from on load finished");
                showErrorMessage();
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mMoviePosterAdapter.setOfflineMovies(null);
        }
    };

    LoaderManager.LoaderCallbacks<String[]> movieLoader = new LoaderManager.LoaderCallbacks<String[]>() {

        @Override
        public Loader<String[]> onCreateLoader(final int id,final Bundle args) {

            return new AsyncTaskLoader<String[]>(MainActivity.this) {
                @Override
                protected void onStartLoading() {
                    if(args == null) {
                        return;
                    }
                    if(id == MAIN_LOADER_ID) {
                        if (moviePosterPaths!= null)
                            deliverResult(moviePosterPaths);
                        else
                            forceLoad();
                    }
                    mProgressbar.setVisibility(View.VISIBLE);
                }

                @Override
                public String[] loadInBackground() {
                    if (args  == null) {
                        return null;
                    }

                    String setting = args.getString("SETTING");
                    URL movieRequestUrl = NetworkUtils.buildUrl(setting);

                    try {
                        String movieJsonResponse = NetworkUtils
                                .getResponseFromHttpUrl(movieRequestUrl);

                        String MD_PARAM_IMAGE = "poster_path";
                        String MD_PARAM_TITLE = "title";
                        String MD_PARAM_DESC = "overview";
                        String MD_PARAM_RATING = "vote_average";
                        String MD_PARAM_RELEASE_DATE = "release_date";
                        String MD_PARAM_ID = "id";



                        moviePosterPaths = MovieDbJsonUtils
                                .getResultsFromJson(movieJsonResponse,MD_PARAM_IMAGE);

                        title = MovieDbJsonUtils.getResultsFromJson(movieJsonResponse,MD_PARAM_TITLE);
                        description = MovieDbJsonUtils.getResultsFromJson(movieJsonResponse,MD_PARAM_DESC);
                        rating = MovieDbJsonUtils.getResultsFromJson(movieJsonResponse,MD_PARAM_RATING);
                        releaseDate = MovieDbJsonUtils.getResultsFromJson(movieJsonResponse,MD_PARAM_RELEASE_DATE);
                        movieId=MovieDbJsonUtils.getResultsFromJson(movieJsonResponse,MD_PARAM_ID);


                        return moviePosterPaths;

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
                @Override
                public void deliverResult(String[] data) {
                    moviePosterPaths = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<String[]> loader, String[] data) {
            mProgressbar.setVisibility(View.INVISIBLE);
            if (data != null) {
                showMoviesView();
                mMoviePosterAdapter.setMoviePosterPaths(data);
                if(mPosition==RecyclerView.NO_POSITION) mPosition = 0;
                mRecyclerView.smoothScrollToPosition(mPosition);
            } else {
                Log.d(TAG,"Show error message from on post execute");
                showErrorMessage();
            }
        }

        @Override
        public void onLoaderReset(Loader<String[]> loader) {

        }
    };
}

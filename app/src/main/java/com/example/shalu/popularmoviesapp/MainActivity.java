package com.example.shalu.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.shalu.popularmoviesapp.Utilities.MovieDbJsonUtils;
import com.example.shalu.popularmoviesapp.Utilities.NetworkUtils;

import java.net.URL;

import com.example.shalu.popularmoviesapp.data.PopMoviesPreferences;

public class MainActivity extends AppCompatActivity implements MoviePosterAdapter.OnMovieItemClickListener {

    private RecyclerView mRecyclerView;
    private TextView mErrorText;
    private ProgressBar mProgressbar;
    private MoviePosterAdapter mMoviePosterAdapter;

    private String[] moviePosterPaths;
    private String[] title;
    private String[] description;
    private String[] rating;
    private String[] releaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_pop_movies);
        mErrorText = (TextView) findViewById(R.id.tv_error_message_display);
        mProgressbar = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMoviePosterAdapter = new MoviePosterAdapter(this);
        mRecyclerView.setAdapter(mMoviePosterAdapter);
        showMovies();
    }

    /**
     * Method that gets the preferred setting and calls a background method to load the movie posters in the view
     */
    private void showMovies() {
        showMoviesView();
        String setting = PopMoviesPreferences.getPreferredSetting();
        new FetchMovieDetailsTask().execute(setting);
    }
    private void showMoviesView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorText.setVisibility(View.INVISIBLE);
    }
    private void showErrorMessage(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorText.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.top_rated) {
            PopMoviesPreferences.setPreferredSetting("top_rated");
            mMoviePosterAdapter.setMoviePosterPaths(null);
            showMovies();
            return true;
        }
        else if (item.getItemId() == R.id.popular) {
            PopMoviesPreferences.setPreferredSetting("popular");
            mMoviePosterAdapter.setMoviePosterPaths(null);
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
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("TITLE", title[position]);
        intentToStartDetailActivity.putExtra("IMAGE", moviePosterPaths[position]);
        intentToStartDetailActivity.putExtra("DESC", description[position]);
        intentToStartDetailActivity.putExtra("RATING", rating[position]);
        intentToStartDetailActivity.putExtra("RELEASE_DATE", releaseDate[position]);
        startActivity(intentToStartDetailActivity);

    }

    /**
     * Background task that loads data from the internet and sets the adapter with the data to be shown
     */

    public class FetchMovieDetailsTask extends AsyncTask<String,Void,String[]>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {

            /* If there's no zip code, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            String setting = params[0];
            URL movieRequestUrl = NetworkUtils.buildUrl(setting);

            try {
                String movieJsonResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                String MD_PARAM_IMAGE = "poster_path";
                String MD_PARAM_TITLE = "title";
                String MD_PARAM_DESC = "overview";
                String MD_PARAM_RATING = "vote_average";
                String MD_PARAM_RELEASE_DATE = "release_date";



                moviePosterPaths = MovieDbJsonUtils
                        .getResultsFromJson(movieJsonResponse,MD_PARAM_IMAGE);

                title = MovieDbJsonUtils.getResultsFromJson(movieJsonResponse,MD_PARAM_TITLE);
                description = MovieDbJsonUtils.getResultsFromJson(movieJsonResponse,MD_PARAM_DESC);
                rating = MovieDbJsonUtils.getResultsFromJson(movieJsonResponse,MD_PARAM_RATING);
                releaseDate = MovieDbJsonUtils.getResultsFromJson(movieJsonResponse,MD_PARAM_RELEASE_DATE);


                return moviePosterPaths;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] moviePosterPaths) {
            mProgressbar.setVisibility(View.INVISIBLE);
            if (moviePosterPaths != null) {
                showMoviesView();
                mMoviePosterAdapter.setMoviePosterPaths(moviePosterPaths);
            } else {
                showErrorMessage();
            }
        }
    }


}

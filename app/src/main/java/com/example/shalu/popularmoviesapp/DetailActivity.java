package com.example.shalu.popularmoviesapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shalu.popularmoviesapp.Utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    private TextView mTitleTextView;
    private TextView mDescTextView;
    private TextView mRatingTextView;
    private TextView mReleaseDateTextView;
    private ImageView mImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitleTextView = (TextView) findViewById(R.id.tv_movie_title);
        mReleaseDateTextView = (TextView) findViewById(R.id.tv_release_date);
        mRatingTextView = (TextView) findViewById(R.id.tv_movie_rating);
        mDescTextView = (TextView) findViewById(R.id.tv_overview);
        mImgView = (ImageView) findViewById(R.id.iv_movie_poster);

        Intent intentThatStartedThisActivity = getIntent();

        if(intentThatStartedThisActivity!=null) {
            if(intentThatStartedThisActivity.hasExtra("TITLE"))
                mTitleTextView.setText(intentThatStartedThisActivity.getStringExtra("TITLE"));
            if(intentThatStartedThisActivity.hasExtra("RELEASE_DATE"))
                mReleaseDateTextView.setText("Release Date: "+intentThatStartedThisActivity.getStringExtra("RELEASE_DATE"));
            if(intentThatStartedThisActivity.hasExtra("RATING"))
                mRatingTextView.setText("Rating: "+intentThatStartedThisActivity.getStringExtra("RATING"));
            if(intentThatStartedThisActivity.hasExtra("DESC"))
                mDescTextView.setText(intentThatStartedThisActivity.getStringExtra("DESC"));
            if(intentThatStartedThisActivity.hasExtra("IMAGE")) {
                URL url = NetworkUtils.buildImagePosterUrl(intentThatStartedThisActivity.getStringExtra("IMAGE"));
                Picasso.with(mImgView.getContext()).load(url.toString()).into(mImgView);
            }

        }

    }
}

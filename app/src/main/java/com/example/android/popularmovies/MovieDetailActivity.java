package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                TextView tvMovieTitle = (TextView) findViewById(R.id.movie_detail_tv_movie_title);
                TextView tvReleaseDate = (TextView) findViewById(R.id.movie_detail_tv_release_date);
                RatingBar rbUserRating = (RatingBar) findViewById(R.id.movie_detail_rb_movie_user_rating);
                TextView tvSynopsis = (TextView) findViewById(R.id.movie_detail_tv_synopsis);
                ImageView ivThumbnail = (ImageView) findViewById(R.id.iv_movie_detail_thumbnail_image);

                // Populate the view
                Movie movie = (new Gson()).fromJson(intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT), Movie.class);
                tvMovieTitle.setText(movie.getTitle());
                tvReleaseDate.setText(Integer.toString(movie.getYearReleaseDate()));

                Log.d("Vote Average", Float.toString(movie.getUserRating()));

                rbUserRating.setRating(movie.getUserRating());
                tvSynopsis.setText(movie.getSynopsis());
                Picasso.with(getApplicationContext()).load(movie.getThumbnailImageUrl()).into(ivThumbnail);
            }
        }



    }

}

package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        TextView tvMovieTitle = (TextView) findViewById(R.id.movie_detail_tv_movie_title);
        TextView tvReleaseDate = (TextView) findViewById(R.id.movie_detail_tv_release_date);
        RatingBar rbUserRating = (RatingBar) findViewById(R.id.movie_detail_rb_movie_user_rating);
        TextView tvSynopsis = (TextView) findViewById(R.id.movie_detail_tv_synopsis);

        tvMovieTitle.setText("Iron man 3");
        tvReleaseDate.setText("2017-05-12");
        rbUserRating.setRating(2.3f);
        tvSynopsis.setText(R.string.dummy_text);
    }

}

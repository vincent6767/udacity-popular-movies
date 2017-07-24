package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.entities.Movie;
import com.example.android.popularmovies.entities.Review;
import com.example.android.popularmovies.entities.ReviewsResult;
import com.example.android.popularmovies.networkutils.NoConnectivityException;
import com.example.android.popularmovies.themoviedb.MoviesService;
import com.example.android.popularmovies.themoviedb.TheMovieDB;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {
    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();
    private static final String NETWORK_ERROR = "Oops. Seems like we encountered network error.";
    // TODO: Don't forget to add page for your reviews.
    private MoviesService mMovieService;
    private Movie mMovie;

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
                mMovie = (new Gson()).fromJson(intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT), Movie.class);
                tvMovieTitle.setText(mMovie.getTitle());
                tvReleaseDate.setText(String.format(Locale.US, "%d", mMovie.getYearReleaseDate()));

                rbUserRating.setRating(mMovie.getUserRating());
                tvSynopsis.setText(mMovie.getSynopsis());
                Picasso.with(getApplicationContext()).load(mMovie.getThumbnailImageUrl()).into(ivThumbnail);


                initService();
                initializeReviews();
            }
        }
        // Do nothing if there no movie is passed.
    }

    private void initService() {
        mMovieService = (new TheMovieDB(BuildConfig.THE_MOVIE_DB_APY_KEY, this)).getMoviesService();
    }
    private void initializeReviews() {
        // Fetch reviewsfrom service end point. Query page will always be 1.
        Call<ReviewsResult> call = mMovieService.getReviews(mMovie.getId(), 1);
        Callback<ReviewsResult> callback = new Callback<ReviewsResult>() {
            @Override
            public void onResponse(Call<ReviewsResult> call, Response<ReviewsResult> response) {
                ReviewsResult reviewsResult = response.body();
                Log.v(LOG_TAG, reviewsResult.getId().toString());
                for (Review review: reviewsResult.getResults()) {
                    // TODO: Add your content to a layout.
                    Log.v(LOG_TAG, "Content: " + review.getContent());
                }
            }

            @Override
            public void onFailure(Call<ReviewsResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    onConnectivityException(NETWORK_ERROR + " Cannot fetched movie's reviews");
                }
                Log.e(LOG_TAG, t.getMessage());
            }
        };
        // Asynchronous request so our UI won't freeze.
        call.enqueue(callback);
    }

    private void onConnectivityException(String errMessage) {
        // I didn't use Show error message techniques.
        Toast.makeText(this, errMessage, Toast.LENGTH_LONG).show();
    }
}

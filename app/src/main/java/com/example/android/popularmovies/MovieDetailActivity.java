package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.adapterviews.ReviewsAdapter;
import com.example.android.popularmovies.adapterviews.VideosAdapter;
import com.example.android.popularmovies.entities.Movie;
import com.example.android.popularmovies.entities.Review;
import com.example.android.popularmovies.entities.ReviewsResult;
import com.example.android.popularmovies.entities.Video;
import com.example.android.popularmovies.entities.VideosResult;
import com.example.android.popularmovies.networkutils.NoConnectivityException;
import com.example.android.popularmovies.themoviedb.MoviesService;
import com.example.android.popularmovies.themoviedb.TheMovieDB;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity implements VideosAdapter.VideoAdapterOnClickHandler{
    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();
    private static final String NETWORK_ERROR = "Oops. Seems like we encountered network error.";

    // TODO: Don't forget to add page for your reviews.
    private MoviesService mMovieService;
    private RecyclerView mReviewsRecyclerView;
    private ReviewsAdapter mReviewsAdapter;
    private VideosAdapter mVideosAdapter;
    private RecyclerView mVideosRecyclerView;
    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                // Populate Movie Detail View
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

                initViews();
                initService();
                initializeReviews();
                initializeVideos();
            }
        }
        // Do nothing if there no movie is passed.
    }

    private void initViews() {
        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(this);
        mReviewsRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_reviews);
        mReviewsRecyclerView.setHasFixedSize(true);
        mReviewsRecyclerView.setLayoutManager(reviewsLayoutManager);

        LinearLayoutManager videoLayoutManager = new LinearLayoutManager(this);
        mVideosRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_trailers);
        mVideosRecyclerView.setHasFixedSize(true);
        mVideosRecyclerView.setLayoutManager(videoLayoutManager);

        mVideosAdapter = new VideosAdapter(this, new ArrayList<Video>(), this);

        mVideosRecyclerView.setAdapter(mVideosAdapter);
        mReviewsAdapter = new ReviewsAdapter(this, new ArrayList<Review>());
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);
    }

    private void initService() {
        mMovieService = (new TheMovieDB(BuildConfig.THE_MOVIE_DB_APY_KEY, this)).getMoviesService();
    }
    private void initializeVideos() {
        // Fetch videos from service end point. The page will always be 1.
        Call<VideosResult> call = mMovieService.getVideos(mMovie.getId(), 1);
        Callback<VideosResult> callback = new Callback<VideosResult>() {
            @Override
            public void onResponse(Call<VideosResult> call, Response<VideosResult> response) {
                VideosResult videosResult = response.body();
                mVideosAdapter.setVideos(videosResult.getTrailerVideos());
            }

            @Override
            public void onFailure(Call<VideosResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    onConnectivityException(NETWORK_ERROR + " Cannot fetched movie's trailer videos");
                }
                Log.e(LOG_TAG, t.getMessage());
            }
        };
        // Asynchronous request so our UI thread won't disrupted.
        call.enqueue(callback);
    }
    private void initializeReviews() {
        // Fetch reviews from service end point. Query page will always be 1.
        Call<ReviewsResult> call = mMovieService.getReviews(mMovie.getId(), 1);
        Callback<ReviewsResult> callback = new Callback<ReviewsResult>() {
            @Override
            public void onResponse(Call<ReviewsResult> call, Response<ReviewsResult> response) {
                ReviewsResult reviewsResult = response.body();
                mReviewsAdapter.setReviews(reviewsResult.getResults());
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
        // TODO: Show error message in the box.
        Toast.makeText(this, errMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(Video video) {
        if (video != null) {
            Intent openTrailerAppIntent = new Intent(Intent.ACTION_VIEW, video.getUri());
            try {
                startActivity(openTrailerAppIntent);
            } catch (ActivityNotFoundException e) {
                Log.e(LOG_TAG, "Cannot found application to open video");
            }
        }
    }
}

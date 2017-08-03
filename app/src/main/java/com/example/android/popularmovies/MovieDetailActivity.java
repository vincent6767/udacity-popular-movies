package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import com.example.android.popularmovies.data.FavoriteMoviesContract.FavoriteMoviesEntry;

public class MovieDetailActivity extends AppCompatActivity implements
        VideosAdapter.VideoAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor>
{
    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();
    private static final String NETWORK_ERROR = "Oops. Seems like we encountered network error.";
    private static final int ID_MOVIE_LOADER = 44;

    private boolean mMovieSaveState;

    private MenuItem mBookmarkMenuItem;

    private static final String[] DETAIL_FAVORITE_MOVIES_PROJECTION = {
            FavoriteMoviesEntry.COLUMN_ID,
            FavoriteMoviesEntry.COLUMN_TITLE
    };
    private static final int INDEX_MOVIE_ID = 0;
    private static final int INDEX_MOVIE_TITLE = 1;

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
        ActionBar bar = getSupportActionBar();

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
                Picasso.with(getApplicationContext()).load(mMovie.getFullThumbnailImageUrl()).into(ivThumbnail);
                bar.setTitle(mMovie.getTitle());

                initViews();
                initService();
                initializeReviews();
                initializeVideos();
            }
        }
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
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
    private void updateBookmarkMenuItem(boolean saveState) {
        if (saveState) {
            mBookmarkMenuItem.setIcon(R.drawable.ic_action_unbookmark);
        } else {
            mBookmarkMenuItem.setIcon(R.drawable.ic_action_bookmark);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mBookmarkMenuItem = menu.findItem(R.id.action_bookmark);
        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_bookmark:
                if (mMovieSaveState) {
                    // Saved
                    removeFavoriteMovie();
                } else {
                    // Not save yet
                    saveFavoriteMovie();
                }
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveFavoriteMovie() {
        ContentValues values = new ContentValues();
        values.put(FavoriteMoviesEntry.COLUMN_ID, mMovie.getId());
        values.put(FavoriteMoviesEntry.COLUMN_TITLE, mMovie.getTitle());
        values.put(FavoriteMoviesEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());
        values.put(FavoriteMoviesEntry.COLUMN_POSTER_PATH, mMovie.getThumbnailImageUrl());
        values.put(FavoriteMoviesEntry.COLUMN_USER_RATING, mMovie.getUserRating());
        values.put(FavoriteMoviesEntry.COLUMN_SYNOPSIS, mMovie.getSynopsis());
        values.put(FavoriteMoviesEntry.COLUMN_BACKDROP, mMovie.getBackdrop());

        Uri movieUri = FavoriteMoviesEntry.CONTENT_URI;

        Uri newMovieUri = getContentResolver().insert(movieUri, values);
        mMovieSaveState = true;
        if (newMovieUri != null) {
            Toast.makeText(getBaseContext(), "Successfully saved movie to your collection!", Toast.LENGTH_SHORT).show();
        }
        updateBookmarkMenuItem(mMovieSaveState);
    }

    private void removeFavoriteMovie() {
        Uri movieUri = FavoriteMoviesEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(mMovie.getId())).build();

        int removedMovie = getContentResolver().delete(movieUri, null, null);
        if (removedMovie > 0) {
            Toast.makeText(getBaseContext(), "Successfully remove movie from your collection!", Toast.LENGTH_SHORT).show();
        }
        mMovieSaveState = false;
        updateBookmarkMenuItem(mMovieSaveState);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_MOVIE_LOADER:
                Uri movieUri = FavoriteMoviesEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(mMovie.getId())).build();
                String selection = FavoriteMoviesEntry.getSqlSelectForAMovie();
                String[] selectionArgs = new String[] {String.valueOf(mMovie.getId())};
                return new CursorLoader(
                        this,
                        movieUri,
                        DETAIL_FAVORITE_MOVIES_PROJECTION,
                        selection,
                        selectionArgs,
                        null
                );
            default:
                throw new RuntimeException("Loader not implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int loaderId = loader.getId();

        switch (loaderId) {
            case ID_MOVIE_LOADER:
                // check if the movie is saved
                mMovieSaveState = data.getCount() > 0;
                updateBookmarkMenuItem(mMovieSaveState);
                break;
            default:
                throw new RuntimeException("Unknown loader: " + loader);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Do nothing here
    }
}

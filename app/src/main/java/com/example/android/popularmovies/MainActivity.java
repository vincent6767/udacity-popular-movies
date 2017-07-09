package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.networkutils.NoConnectivityException;
import com.example.android.popularmovies.themoviedb.MovieResult;
import com.example.android.popularmovies.themoviedb.MoviesService;
import com.example.android.popularmovies.themoviedb.TheMovieDB;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {
    private enum SORT_OPTION {
        POPULARITY, TOP_RATED;
    }
    private enum DATA_OPERATION {
        ADD, SET
    }

    private static final String EXCEPTION_TAG = "EXCEPTION";
    private int mPageNumber = 0;

    private SORT_OPTION mCurrentOption;

    private MovieAdapter mAdapter;
    private TheMovieDB theMovieDB;
    private MoviesService moviesService;
    private ProgressBar pbLoadingIndicator;
    private TextView tvErrorMessage;
    private RecyclerView rvMoviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pbLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        tvErrorMessage = (TextView) findViewById(R.id.tv_error_message);
        mCurrentOption = SORT_OPTION.POPULARITY;

        initViews();
        initListeners();
        initService();
        // Initialize movies list.
        fetchMoviesData(mCurrentOption, DATA_OPERATION.SET);
    }
    private void initViews() {
        rvMoviesList = (RecyclerView) findViewById(R.id.rv_movies);
        rvMoviesList.setHasFixedSize(true);
        int mNumberOfCols = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(this, mNumberOfCols);
        rvMoviesList.setLayoutManager(layoutManager);

        @SuppressWarnings("unchecked") ArrayList<Movie> movies = new ArrayList<Movie>();

        mAdapter = new MovieAdapter(getApplicationContext(), movies, this, rvMoviesList);
        rvMoviesList.setAdapter(mAdapter);
    }
    private void initListeners() {
        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                // So the adapter will check view_type and show progress bar at bottom.
                mAdapter.addMovieData(null);

                fetchMoviesData(mCurrentOption, DATA_OPERATION.ADD);
            }
        });
    }
    private void initService() {
        theMovieDB = new TheMovieDB(BuildConfig.THE_MOVIE_DB_APY_KEY, this);
        moviesService = theMovieDB.getMoviesService();
    }

    private void callEndPoints(Call<MovieResult> call, Callback<MovieResult> callback) {
        call.enqueue(callback);
    }
    private void showLoadingScreenIndicator() {
        pbLoadingIndicator.setVisibility(View.VISIBLE);
        tvErrorMessage.setVisibility(View.INVISIBLE);
    }
    private boolean isDifferentOption(SORT_OPTION option) {
        return !(mCurrentOption == option);
    }

    /**
     * A method that determine which callbacks that will be called on response.
     * @param operation Type of operation that you want to do on the data.
     * @return A callback that match the given operation.
     */
    private Callback<MovieResult> getCallbackBasedOnOperation(DATA_OPERATION operation) {
        if (operation == DATA_OPERATION.SET) {
            return new Callback<MovieResult>() {
                @Override
                public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                    showMoviesData();
                    mAdapter.setMovieData(response.body().getResults());
                }

                @Override
                public void onFailure(Call<MovieResult> call, Throwable t) {
                    pbLoadingIndicator.setVisibility(View.INVISIBLE);
                    if (t instanceof NoConnectivityException) {
                        onConnectivityException();
                    }
                    Log.e(EXCEPTION_TAG, t.getMessage());
                }
            };
        } else if (operation == DATA_OPERATION.ADD){
            return new Callback<MovieResult>() {
                @Override
                public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                    // Remove the progress bar.
                    mAdapter.removeMoviesData(mAdapter.getItemCount() - 1);
                    mAdapter.addMovies(response.body().getResults());
                    mAdapter.setLoaded();
                }

                @Override
                public void onFailure(Call<MovieResult> call, Throwable t) {
                    // Duplicate logic here....
                    if (t instanceof NoConnectivityException) {
                        onConnectivityException();
                    }
                    Log.e(EXCEPTION_TAG, t.getMessage());
                }
            };
        }
        return null;
    }
    private void showMoviesData() {
        pbLoadingIndicator.setVisibility(View.INVISIBLE);
        tvErrorMessage.setVisibility(View.INVISIBLE);
        rvMoviesList.setVisibility(View.VISIBLE);
    }
    private void showErrorMessage() {
        tvErrorMessage.setVisibility(View.VISIBLE);
        rvMoviesList.setVisibility(View.INVISIBLE);
    }

    public void onConnectivityException() {
        // This has need to be done because
        // a thread cannot update UI that is not created by that UI
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showErrorMessage();
            }
        });
    }

    private void fetchMoviesData(SORT_OPTION option, DATA_OPERATION operation) {
        boolean isDifferent = isDifferentOption(option);
        if (isDifferent) {
            onDifferentOption();
        } else {
            onSameOption();
        }
        Callback<MovieResult> callback = getCallbackBasedOnOperation(operation);
        Call<MovieResult> call = getCallBasedOnOption(option);

        callEndPoints(call, callback);
    }

    private Call<MovieResult> getCallBasedOnOption(SORT_OPTION option) {
        setCurrentOption(option);

        if (option == SORT_OPTION.POPULARITY) {
            return moviesService.getPopularMovies(mPageNumber);
        } else if (option == SORT_OPTION.TOP_RATED) {
            return moviesService.getTopRated(mPageNumber);
        }
        return null;
    }
    private void setCurrentOption(SORT_OPTION option) {
        mCurrentOption = option;
    }
    private void onSameOption() {
        mPageNumber++;
    }
    private void onDifferentOption() {
        mPageNumber = 1;
        mAdapter.setMovieData(null);
        showLoadingScreenIndicator();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movies_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Reset page number.
        mPageNumber = 0;

        switch(id) {
            case R.id.action_sort_by_popularity:
                fetchMoviesData(SORT_OPTION.POPULARITY, DATA_OPERATION.SET);
                return true;
            case R.id.action_sort_by_top_rated:
                fetchMoviesData(SORT_OPTION.TOP_RATED, DATA_OPERATION.SET);
                return true;
        }
        // Lets Activity default method take the lead.
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(Movie movie) {
        String movieInString = (new Gson()).toJson(movie);
        Context context = getApplicationContext();
        Class destinationClass = MovieDetailActivity.class;
        Intent intentToStartMovieDetailActivity = new Intent(context, destinationClass);
        intentToStartMovieDetailActivity.putExtra(Intent.EXTRA_TEXT, movieInString);
        startActivity(intentToStartMovieDetailActivity);
    }
}

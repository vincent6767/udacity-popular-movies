package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.popularmovies.themoviedb.MovieResult;
import com.example.android.popularmovies.themoviedb.MoviesService;
import com.example.android.popularmovies.themoviedb.TheMovieDB;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {
    private static final String RETROFIT_TAG = "RETROFIT";
    private int mPageNumber = 1;
    private int mNumberOfCols = 2;

    private MovieAdapter mAdapter;
    private TheMovieDB theMovieDB;
    private MoviesService moviesService;

    private ProgressBar pbLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pbLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        initViews();
        initService();
        callGetPopularMovies();
    }
    private void initViews() {
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv_movies);
        rv.setHasFixedSize(false);
        GridLayoutManager layoutManager = new GridLayoutManager(this, mNumberOfCols);
        rv.setLayoutManager(layoutManager);

        @SuppressWarnings("unchecked") ArrayList<Movie> movies = new ArrayList<Movie>();

        mAdapter = new MovieAdapter(getApplicationContext(), movies, this);
        rv.setAdapter(mAdapter);
    }
    private void initService() {
        theMovieDB = new TheMovieDB(BuildConfig.THE_MOVIE_DB_APY_KEY);
        moviesService = theMovieDB.getMoviesService();
    }
    private void callEndPoints(Call<MovieResult> call) {

        pbLoadingIndicator.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<MovieResult>() {
            @Override
            public void onResponse(Call<MovieResult> call, retrofit2.Response<MovieResult> response) {
                pbLoadingIndicator.setVisibility(View.INVISIBLE);
                mAdapter.setMovieData(response.body().getResults());
            }

            @Override
            public void onFailure(Call<MovieResult> call, Throwable t) {
                pbLoadingIndicator.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "Oops. Something wrong :(", Toast.LENGTH_LONG).show();
                Log.e(RETROFIT_TAG, t.getMessage());
            }
        });
    }
    private void callGetPopularMovies() {
        callEndPoints(moviesService.getPopularMovies(mPageNumber));
    }
    private void setPageNumber(int pageNumber) {
        mPageNumber = pageNumber;
    }
    private void callGetTopRated() {
        callEndPoints(moviesService.getTopRated(mPageNumber));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movies_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.action_sort_by_popularity:
                mAdapter.setMovieData(null);
                callGetPopularMovies();
                setPageNumber(1);
                return true;
            case R.id.action_sort_by_top_rated:
                mAdapter.setMovieData(null);
                callGetTopRated();
                setPageNumber(1);
                return true;
        }
        // Let Activity default method take the lead.
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

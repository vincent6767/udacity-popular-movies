package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import com.example.android.popularmovies.adapterviews.MovieAdapter;
import com.example.android.popularmovies.layoututils.GridLayoutUtil;
import com.example.android.popularmovies.listeners.OnLoadMoreListener;
import com.example.android.popularmovies.entities.Movie;
import com.example.android.popularmovies.networkutils.NoConnectivityException;
import com.example.android.popularmovies.entities.MovieResult;
import com.example.android.popularmovies.themoviedb.MoviesService;
import com.example.android.popularmovies.themoviedb.TheMovieDB;
import com.example.android.popularmovies.data.FavoriteMoviesContract.FavoriteMoviesEntry;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    private enum SORT_OPTION {
        POPULARITY(0), TOP_RATED(1), FAVORITE_MOVIE(2);
        private int numVal;

        SORT_OPTION(int numVal) {
            this.numVal = numVal;
        }
        public int getNumVal() {
            return numVal;
        }

    }
    private enum DATA_OPERATION {
        ADD, SET
    }

    private static final String EXCEPTION_TAG = "EXCEPTION";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int FETCH_FAVORITE_MOVIES_LOADER = 100;
    private static final String LIST_STATE = "list_state";
    private static final String PAGE_NUMBER_KEY = "page_number_key";
    private static final String CURRENT_OPTION_KEY = "current_option";
    private static final String MOVIES_LIST = "movies_list";

    private int mPageNumber = 0;

    private static final String[] FAVORITE_MOVIES_COLUMNS = new String[] {
            FavoriteMoviesEntry.COLUMN_ID,
            FavoriteMoviesEntry.COLUMN_TITLE,
            FavoriteMoviesEntry.COLUMN_RELEASE_DATE,
            FavoriteMoviesEntry.COLUMN_POSTER_PATH,
            FavoriteMoviesEntry.COLUMN_USER_RATING,
            FavoriteMoviesEntry.COLUMN_SYNOPSIS,
            FavoriteMoviesEntry.COLUMN_BACKDROP
    };

    private static final int INDEX_COLUMN_MOVIE_ID = 0;
    private static final int INDEX_COLUMN_TITLE = 1;
    private static final int INDEX_COLUMN_RELEASE_DATE = 2;
    private static final int INDEX_COLUMN_POSTER_PATH = 3;
    private static final int INDEX_COLUMN_USER_RATING = 4;
    private static final int INDEX_COLUMN_SYNOPSIS = 5;
    private static final int INDEX_COLUMN_BACKDROP = 6;

    private SORT_OPTION mCurrentOption;
    private Parcelable mListState;
    private MovieAdapter mAdapter;
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

        initViews();
        initListeners();
        initService();

        if (savedInstanceState != null) {
            int savedOption = savedInstanceState.getInt(CURRENT_OPTION_KEY);
            switch (savedOption) {
                case 0:
                    mCurrentOption = SORT_OPTION.POPULARITY;
                    break;
                case 1:
                    mCurrentOption = SORT_OPTION.TOP_RATED;
                    break;
                case 2:
                    mCurrentOption = SORT_OPTION.FAVORITE_MOVIE;
                    break;
            }
            mPageNumber = savedInstanceState.getInt(PAGE_NUMBER_KEY);
            mAdapter.setMovieDataFromParceableList((savedInstanceState.getParcelableArrayList(MOVIES_LIST)));
            restoreLayoutManagerPosition();
        } else {
            // Default action.
            mCurrentOption = SORT_OPTION.POPULARITY;
            fetchMoviesData(mCurrentOption, DATA_OPERATION.SET);
        }
    }
    private void restoreLayoutManagerPosition() {
        if (mListState != null) {
            rvMoviesList.getLayoutManager().onRestoreInstanceState(mListState);
        }
    }
    private void initViews() {
        rvMoviesList = (RecyclerView) findViewById(R.id.rv_movies);
        rvMoviesList.setHasFixedSize(true);
        int mNumberOfCols = GridLayoutUtil.calculateNumberOfColumns(getApplicationContext());
        GridLayoutManager layoutManager = new GridLayoutManager(this, mNumberOfCols);
        rvMoviesList.setLayoutManager(layoutManager);

        @SuppressWarnings("unchecked") ArrayList<Movie> movies = new ArrayList<>();

        mAdapter = new MovieAdapter(getApplicationContext(), movies, this, rvMoviesList);
        rvMoviesList.setAdapter(mAdapter);
    }
    private void initListeners() {
        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (mCurrentOption == SORT_OPTION.POPULARITY || mCurrentOption == SORT_OPTION.TOP_RATED) {
                    // So the adapter will check view_type and show progress bar at bottom.
                    mAdapter.addMovieData(null);

                    fetchMoviesData(mCurrentOption, DATA_OPERATION.ADD);
                }
            }
        });
    }
    private void initService() {
        TheMovieDB theMovieDB = new TheMovieDB(BuildConfig.THE_MOVIE_DB_APY_KEY, this);
        moviesService = theMovieDB.getMoviesService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCurrentOption == SORT_OPTION.FAVORITE_MOVIE) {
            fetchFavoriteMovies();
        }
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
    private void showErrorMessage(String errorMessage) {
        tvErrorMessage.setVisibility(View.VISIBLE);
        tvErrorMessage.setText(errorMessage);
        rvMoviesList.setVisibility(View.INVISIBLE);
    }

    private void onConnectivityException() {
        // This has need to be done because
        // a thread cannot update UI that is not created by that UI
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showErrorMessage(getString(R.string.connectivity_error_message));
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
    private void fetchFavoriteMovies() {
        Loader loader = getSupportLoaderManager().getLoader(FETCH_FAVORITE_MOVIES_LOADER);
        if (loader == null) {
            getSupportLoaderManager().initLoader(FETCH_FAVORITE_MOVIES_LOADER, null, this);
        } else {
            getSupportLoaderManager().restartLoader(FETCH_FAVORITE_MOVIES_LOADER, null, this);
        }
        setCurrentOption(SORT_OPTION.FAVORITE_MOVIE);
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
        mAdapter.emptyMoviesData();
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
            case R.id.action_favorite_movies:
                fetchFavoriteMovies();
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case FETCH_FAVORITE_MOVIES_LOADER:
                Uri favoriteMoviesUri = FavoriteMoviesEntry.CONTENT_URI;
                return new CursorLoader(
                        this,
                        favoriteMoviesUri,
                        FAVORITE_MOVIES_COLUMNS,
                        null,
                        null,
                        FavoriteMoviesEntry._ID
                );
            default:
                throw new RuntimeException("Loader is not implemented." + id);
        }
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() > 0) {
            List<Movie> movies = new ArrayList<>();
            mAdapter.setMovieData(null);
            try  {
                while(data.moveToNext()) {
                    Movie movie = new Movie(
                            data.getInt(INDEX_COLUMN_MOVIE_ID),
                            data.getString(INDEX_COLUMN_TITLE),
                            data.getString(INDEX_COLUMN_POSTER_PATH),
                            data.getString(INDEX_COLUMN_RELEASE_DATE),
                            data.getFloat(INDEX_COLUMN_USER_RATING),
                            data.getString(INDEX_COLUMN_SYNOPSIS),
                            data.getString(INDEX_COLUMN_BACKDROP)
                    );
                    movies.add(movie);
                }
            } finally {
                data.close();
            }
            mAdapter.setMovieData(movies);
        } else {
            showErrorMessage(getString(R.string.no_favorite_movies_error_message));
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Do nothing here!
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mListState = rvMoviesList.getLayoutManager().onSaveInstanceState();
        outState.putInt(PAGE_NUMBER_KEY, mPageNumber);
        outState.putInt(CURRENT_OPTION_KEY, mCurrentOption.getNumVal());
        outState.putParcelable(LIST_STATE, mListState);
        outState.putParcelableArrayList(MOVIES_LIST, (ArrayList<Movie>) mAdapter.getAllMovies());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable(LIST_STATE);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
}

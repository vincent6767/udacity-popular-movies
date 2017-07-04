package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.android.popularmovies.themoviedb.MovieResult;
import com.example.android.popularmovies.themoviedb.MoviesService;
import com.example.android.popularmovies.themoviedb.TheMovieDB;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {
    private static final String THE_MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/";
    private static final String QUERY_PARAM_NAME = "api_key";
    private static final String RETROFIT_TAG = "RETROFIT";
    private int mPageNumber = 1;

    private MovieAdapter mAdapter;

    private final String movie_titles[] = {
            "Iron Man 3: Civil War",
            "Spiderman: Homecoming",
            "Captain America: Winter Soldier",
            "Beauty and the Beast",
            "Iron man 3: The age of Ultron",
    };
    private final float user_ratings[] = {
            4.0f,
            4.5f,
            5.0f,
            4.5f,
            2.0f,
    };
    private final String thumbnail_image_urls[] = {
            "https://developer.android.com/_static/images/android/touchicon-180.png",
            "https://developer.android.com/_static/images/android/touchicon-180.png",
            "https://developer.android.com/_static/images/android/touchicon-180.png",
            "https://developer.android.com/_static/images/android/touchicon-180.png",
            "https://developer.android.com/_static/images/android/touchicon-180.png"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initService();
    }
    private void initViews() {
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv_movies);
        rv.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManager);

        @SuppressWarnings("unchecked") ArrayList<Movie> movies = new ArrayList<Movie>();

        mAdapter = new MovieAdapter(getApplicationContext(), movies, this);
        rv.setAdapter(mAdapter);
    }
    private void initService() {
        TheMovieDB theMovieDB = new TheMovieDB(BuildConfig.THE_MOVIE_DB_APY_KEY);

        MoviesService service = theMovieDB.getMoviesService();
        // Get initial data set sorted by popularity.
        Call<MovieResult> call = service.getPopularMovies(mPageNumber);
        // Asynchronous tasks.
        call.enqueue(new Callback<MovieResult>() {
            @Override
            public void onResponse(Call<MovieResult> call, retrofit2.Response<MovieResult> response) {
                MovieResult movieResult = response.body();
                mAdapter.setMovieData(movieResult.getResults());
            }

            @Override
            public void onFailure(Call<MovieResult> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Oops. Something wrong :(", Toast.LENGTH_LONG).show();
                Log.e(RETROFIT_TAG, t.getMessage());
            }
        });

    }
    private ArrayList prepareData() {
        ArrayList<Movie> movies = new ArrayList<>();
        for(int i=0;i<movie_titles.length;i++){
            Movie movie = new Movie();
            movie.setTitle(movie_titles[i]);
            movie.setReleaseDate("June, 20 2017");
            movie.setUserRating(user_ratings[i]);
            movie.setSynopsis(getResources().getString(R.string.dummy_text));
            movie.setThumbnailImageUrl(thumbnail_image_urls[i]);
            movies.add(movie);
        }
        return movies;
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

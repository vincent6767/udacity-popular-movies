package com.example.android.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final String movie_titles[] = {
            "Donut",
            "Eclair",
            "Froyo",
            "Gingerbread",
            "Honeycomb",
            "Ice Cream Sandwich",
            "Jelly Bean",
            "KitKat",
            "Lollipop",
            "Marshmallow"
    };
    private final String thumbnail_image_urls[] = {
            "https://developer.android.com/_static/images/android/touchicon-180.png",
            "https://developer.android.com/_static/images/android/touchicon-180.png",
            "https://developer.android.com/_static/images/android/touchicon-180.png",
            "https://developer.android.com/_static/images/android/touchicon-180.png",
            "http://api.learn2crack.com/android/images/honey.png",
            "http://api.learn2crack.com/android/images/icecream.png",
            "http://api.learn2crack.com/android/images/jellybean.png",
            "http://api.learn2crack.com/android/images/kitkat.png",
            "http://api.learn2crack.com/android/images/lollipop.png",
            "https://developer.android.com/_static/images/android/touchicon-180.png"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }
    private void initViews() {
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv_movies);
        // TODO: Set it to true because we know that the number of movies will be vary
        rv.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManager);

        @SuppressWarnings("unchecked") ArrayList<Movie> movies = prepareData();

        MovieAdapter adapter = new MovieAdapter(getApplicationContext(), movies);
        rv.setAdapter(adapter);
    }

    private ArrayList prepareData() {
        ArrayList movies = new ArrayList<>();
        for(int i=0;i<movie_titles.length;i++){
            Movie movie = new Movie();
            movie.setTitle(movie_titles[i]);
            movie.setReleaseDate(movie_titles[i]);
            movie.setThumbnailImageUrl(thumbnail_image_urls[i]);
            //noinspection unchecked
            movies.add(movie);
        }
        return movies;
    }
}

package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.example.android.popularmovies.entities.Movie;

/**
 * Created by vincent on 7/30/17.
 */

public class FavoriteMoviesContract {
    public static final String AUTHORITY = "com.example.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract.
    public static final String PATH_FAVORITE_MOVIES = "favorite-movies";

    public static final class FavoriteMoviesEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE_MOVIES).build();

        public static final String TABLE_NAME = "favorite_movies";

        public static final String COLUMN_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_USER_RATING = "vote_average";
        public static final String COLUMN_SYNOPSIS = "overview";
        public static final String COLUMN_BACKDROP = "backdrop_path";
        public static String getSqlSelectForAMovie() {
            return COLUMN_ID + " = ?";
        }

    }
}

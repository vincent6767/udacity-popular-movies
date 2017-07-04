package com.example.android.popularmovies.themoviedb;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MoviesService {
    /**
     * @param page  Optional. Minimum value is 1, expected value is an integer.
     */
    @GET("movie/popular")
    Call<MovieResult> getPopularMovies(
            @Query("page") Integer page
    );

    /**
     * @param page  Optional. Minimum value is 1, expected value is an integer.
     */
    @GET("movie/top_rated")
    Call<MovieResult> getTopRated(
            @Query("page") Integer page
    );
}

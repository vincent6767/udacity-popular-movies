package com.example.android.popularmovies.themoviedb;

import com.example.android.popularmovies.entities.Movie;
import com.example.android.popularmovies.entities.MovieResult;
import com.example.android.popularmovies.entities.ReviewsResult;
import com.example.android.popularmovies.entities.VideosResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
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
    /*
    * Get the movie for a particula movie id.
    *
    * @param movieId A movie TMDb id.
    * */
    @GET("movie/{movie_id}")
    Call<Movie> getMovie(@Path("movie_id") int movieId);
    /**
     * Get the reviews for a particular movie id.
     *
     * @param movieId A movie TMDb id.
     * @param page Optional. Minimum value is 1, expected value is an integer.
     */
    @GET("movie/{movie_id}/reviews")
    Call<ReviewsResult> getReviews(@Path("movie_id") int movieId,
                                   @Query("page") Integer page);
    /**
     * Get the videos (trailers, teasers, clips, etc...) for a specific movie id.
     *
     * @param movieId A movie TMDb id.
     * @param page Optional. Minimum value is 1, expected value is an integer.
     */
    @GET("movie/{movie_id}/videos")
    Call<VideosResult> getVideos(@Path("movie_id") int movieId,
                                  @Query("page") Integer page);
}

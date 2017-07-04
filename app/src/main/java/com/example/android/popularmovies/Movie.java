package com.example.android.popularmovies;

import com.google.gson.annotations.SerializedName;

public class Movie {
    private static final String TMDB_IMAGE_PATH = "http://image.tmdb.org/t/p/w185";

    @SerializedName("title")
    private String title;
    @SerializedName("poster_path")
    private String thumbnailImageUrl;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("vote_average")
    private float userRating;
    @SerializedName("overview")
    private String synopsis;
    @SerializedName("backdrop_path")
    private String backdrop;

    public Movie() {
        super();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailImageUrl() {
        return TMDB_IMAGE_PATH + thumbnailImageUrl;
    }

    public void setThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public float getUserRating() {
        return userRating;
    }

    public void setUserRating(float userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getBackdrop() {
        return TMDB_IMAGE_PATH + backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }
}

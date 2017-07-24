package com.example.android.popularmovies.entities;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Movie {
    private static final String TMDB_IMAGE_PATH = "http://image.tmdb.org/t/p/w185";
    private static final String dateFormat = "yyyy-mm-dd";

    private Integer id;
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
    public int getYearReleaseDate() {
        return getReleaseDateInCalendar().get(Calendar.YEAR);
    }
    public Calendar getReleaseDateInCalendar() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(getReleaseDateInDate());
        return calendar;
    }
    private Date getReleaseDateInDate() {
        DateFormat df = new SimpleDateFormat(dateFormat, Locale.CHINA);
        try {
            return df.parse(getReleaseDate());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
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

    public int getId() {
        return id;
    }
}

package com.example.android.popularmovies.entities;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vincent on 7/29/17.
 */

public class Video {
    private static final String YOUTUBE_SITE = "youtube";
    private static final String TRAILER_TYPE = "trailer";

    private String id;
    @SerializedName("iso_639_1")
    private String iso6391;
    @SerializedName("iso_3166_1")
    private String iso31661;
    private String key;
    private String name;
    private String site;
    private Integer size;
    private String type;

    public Uri getUri() {
        switch (getSite().toLowerCase()) {
            case YOUTUBE_SITE:
                String baseUrl = "http://www.youtube.com/watch?v=";
                return Uri.parse(baseUrl + getKey());
            default:
                return null;
        }
    }
    public boolean isTrailer() {
        return getType().toLowerCase().equals(TRAILER_TYPE);
    }
    /*
    * Getter and Setter
    * */
    public String getId() {
        return id;
    }

    public String getIso6391() {
        return iso6391;
    }

    public void setIso6391(String iso6391) {
        this.iso6391 = iso6391;
    }

    public String getIso31661() {
        return iso31661;
    }

    public void setIso31661(String iso31661) {
        this.iso31661 = iso31661;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

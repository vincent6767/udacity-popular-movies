package com.example.android.popularmovies.entities;

/**
 * Created by vincent on 7/23/17.
 */

public class Review {
    private Integer id;
    private String author;
    private String content;
    private String url;

    public Integer getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

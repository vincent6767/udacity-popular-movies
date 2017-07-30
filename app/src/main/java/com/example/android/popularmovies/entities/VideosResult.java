package com.example.android.popularmovies.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vincent on 7/29/17.
 */

public class VideosResult {
    private Integer id;
    private List<Video> results;

    public List<Video> getTrailerVideos() {
        List<Video> trailers = new ArrayList<Video>();
        for (Video video : getResults()) {
            if (video.isTrailer()) {
                trailers.add(video);
            }
        }
        return trailers;
    }
    /*
    * Setter and Getter
    * */
    public Integer getId() {
        return id;
    }

    public List<Video> getResults() {
        return results;
    }

    public void setResults(List<Video> results) {
        this.results = results;
    }
}

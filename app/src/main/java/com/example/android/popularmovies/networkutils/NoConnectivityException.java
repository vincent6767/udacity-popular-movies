package com.example.android.popularmovies.networkutils;

import java.io.IOException;

/**
 * Created by vincent on 7/7/17.
 */

public class NoConnectivityException extends IOException {
    @Override
    public String getMessage() {
        return "No connectivity.";
    }
}

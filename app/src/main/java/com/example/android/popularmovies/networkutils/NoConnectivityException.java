package com.example.android.popularmovies.networkutils;

import java.io.IOException;

public class NoConnectivityException extends IOException {
    @Override
    public String getMessage() {
        return "No connectivity.";
    }
}

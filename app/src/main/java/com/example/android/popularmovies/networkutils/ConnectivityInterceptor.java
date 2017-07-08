package com.example.android.popularmovies.networkutils;

import android.app.Activity;
import android.content.Context;

import com.example.android.popularmovies.MainActivity;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by vincent on 7/7/17.
 */

public class ConnectivityInterceptor implements Interceptor {

    private Context mContext;

    public ConnectivityInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!NetworkUtil.isOnline(mContext)) {
            throw new NoConnectivityException();
        }

        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }
}

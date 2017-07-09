package com.example.android.popularmovies.themoviedb;

import android.content.Context;

import com.example.android.popularmovies.networkutils.ConnectivityInterceptor;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TheMovieDB {
    private static final String THE_MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/";
    private static final String QUERY_PARAM_NAME = "api_key";
    private String apiKey;
    private Retrofit retrofit;
    private Context mContext;

    public TheMovieDB(String apiKey, Context context) {
        this.apiKey = apiKey;
        this.mContext = context;
    }
    protected Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = getRetrofitBuilder().build();
        }
        return this.retrofit;
    }
    protected Retrofit.Builder getRetrofitBuilder() {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl url = request.url().newBuilder()
                        .addQueryParameter(QUERY_PARAM_NAME, apiKey)
                        .build();
                request = request.newBuilder().url(url).build();
                return chain.proceed(request);
            }
        }).addInterceptor(new ConnectivityInterceptor(mContext))
                .build();
        return new Retrofit.Builder().baseUrl(THE_MOVIE_DB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client);
    }
    public MoviesService getMoviesService() {
        return getRetrofit().create(MoviesService.class);
    }

}

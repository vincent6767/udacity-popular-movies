package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.popularmovies.entities.Movie;

import static com.example.android.popularmovies.data.FavoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME;
/**
 * Created by vincent on 7/30/17.
 */

public class FavoriteMoviesContentProvider extends ContentProvider {
    public static final int FAVORITE_MOVIES = 100;
    public static final int FAVORITE_MOVIE = 101;

    private FavoriteMoviesDBHelper mFavoriteMovieHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FavoriteMoviesContract.AUTHORITY, FavoriteMoviesContract.PATH_FAVORITE_MOVIES, FAVORITE_MOVIES);
        uriMatcher.addURI(FavoriteMoviesContract.AUTHORITY, FavoriteMoviesContract.PATH_FAVORITE_MOVIES + "/#", FAVORITE_MOVIE);

        return uriMatcher;
    }
    @Override
    public boolean onCreate() {
        mFavoriteMovieHelper = new FavoriteMoviesDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] columns, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mFavoriteMovieHelper.getReadableDatabase();

        int matched = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (matched) {
            case FAVORITE_MOVIES:
                retCursor = db.query(TABLE_NAME,
                        columns,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                        );
                break;
            case FAVORITE_MOVIE:
                retCursor = db.query(TABLE_NAME,
                        columns,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mFavoriteMovieHelper.getWritableDatabase();

        int matched = sUriMatcher.match(uri);
        Uri returnUri;

        switch (matched) {
            case FAVORITE_MOVIES:
                long id = db.insert(TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavoriteMoviesContract.FavoriteMoviesEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mFavoriteMovieHelper.getWritableDatabase();

        int matched = sUriMatcher.match(uri);

        int favoriteMoviesDeleted;

        switch (matched) {
            case FAVORITE_MOVIE:
                String id = uri.getPathSegments().get(1);
                favoriteMoviesDeleted = db.delete(TABLE_NAME,
                        FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_ID + "=?",
                        new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        if (favoriteMoviesDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return favoriteMoviesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        // Leave it blank for now.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

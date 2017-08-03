package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.popularmovies.data.FavoriteMoviesContract.FavoriteMoviesEntry;
/**
 * Created by vincent on 7/30/17.
 */

public class FavoriteMoviesDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favoriteMoviesDB.db";

    private static final int VERSION = 2;


    FavoriteMoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_TABLE = "CREATE TABLE " + FavoriteMoviesEntry.TABLE_NAME + " (" +
                FavoriteMoviesEntry._ID             + " INTEGER PRIMARY KEY, " +
                FavoriteMoviesEntry.COLUMN_ID       + " INTEGER NOT NULL, " +
                FavoriteMoviesEntry.COLUMN_TITLE    + " TEXT NOT NULL, " +
                FavoriteMoviesEntry.COLUMN_RELEASE_DATE    + " TEXT NOT NULL, " +
                FavoriteMoviesEntry.COLUMN_POSTER_PATH    + " TEXT NOT NULL, " +
                FavoriteMoviesEntry.COLUMN_USER_RATING    + " REAL NOT NULL, " +
                FavoriteMoviesEntry.COLUMN_SYNOPSIS    + " TEXT NOT NULL, " +
                FavoriteMoviesEntry.COLUMN_BACKDROP    + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteMoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

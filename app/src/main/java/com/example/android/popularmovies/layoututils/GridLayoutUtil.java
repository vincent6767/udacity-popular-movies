package com.example.android.popularmovies.layoututils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by vincent on 8/5/17.
 */

public class GridLayoutUtil {
    public static int calculateNumberOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int numOfColumns = (int) (dpWidth / 180);
        return numOfColumns;
    }
}

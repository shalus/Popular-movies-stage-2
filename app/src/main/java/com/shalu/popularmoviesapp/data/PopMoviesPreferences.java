package com.shalu.popularmoviesapp.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.shalu.popularmoviesapp.R;

public class PopMoviesPreferences {
    /* Default setting as "popular" from the api to show the most popular movies */

    private static final String default_setting = "popular";
    private static String setting = default_setting ;
    public static final String PREF_SETTING = "setting";

    public static void setPreferredSetting(Context context, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(PREF_SETTING, value);
        editor.apply();
    }
    public static String getPreferredSetting(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String keyForLocation = PREF_SETTING;
        String defaultSetting = context.getString(R.string.pref_default);

        return sp.getString(keyForLocation, defaultSetting);
    }
}


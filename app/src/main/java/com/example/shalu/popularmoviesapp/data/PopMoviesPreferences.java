package com.example.shalu.popularmoviesapp.data;

public class PopMoviesPreferences {
    /* Default setting as "popular" from the api to show the most popular movies */

    private static final String default_setting = "popular";
    private static String setting = default_setting ;

    /**
     * Method to get the preferred setting
     * @return string that represents the preferred setting (either popular or top_rated)
     */
    public static String getPreferredSetting() {
        return setting;
    }

    /**
     * Method to set the preferred setting
     * @param value setting value to set the preferred setting (either popular or top_rated)
     */
    public static void setPreferredSetting(String value) {
        setting = value;

    }
}


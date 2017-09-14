package com.example.shalu.popularmoviesapp.Utilities;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieDbJsonUtils {
    private static final String TAG = MovieDbJsonUtils.class.getSimpleName();

    /**
     * Method to that parses the json response string and returns an array of strings that represents the values of input param
     *
     * @param movieJsonStr json response from server
     * @param param parameter that represents the values to be extracted from the response
     * @return a string array that represents the values of the input param
     *
     */

    public static String[] getResultsFromJson(String movieJsonStr, String param) throws JSONException{
        final String MD_RESULTS = "results";
        String[] results;
        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movies = movieJson.getJSONArray(MD_RESULTS);
        results = new String[movies.length()];

        for(int i = 0; i < movies.length(); i++) {
            JSONObject movieDetail = movies.getJSONObject(i);
            results[i] = movieDetail.getString(param);
            if(param.equals("poster_path"))
                results[i] = results[i].substring(1);
            Log.v(TAG,param+": "+results[i]);
        }
        return results;
    }
}

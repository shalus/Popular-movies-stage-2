package com.example.shalu.popularmoviesapp.Utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

    public final class NetworkUtils {

        private static final String TAG = NetworkUtils.class.getSimpleName();

        private static final String MD_BASE_URL = "http://api.themoviedb.org/3/movie/";
        private static final String IMG_BASE_URL = "http://image.tmdb.org/t/p/";
        private final static String imgSize = "w185";

        /* Insert API Key here */
        private static final String api_key = "";

        private final static String API_PARAM = "api_key";
        /**
         * Builds the URL used to talk to the movie database server using a setting.
         *
         * @param setting The setting that will be queried for.
         * @return The URL to use to query the server.
         */
        public static URL buildUrl(String setting) {
            Uri builtUri = Uri.parse(MD_BASE_URL).buildUpon().appendPath(setting)
                    .appendQueryParameter(API_PARAM, api_key)
                    .build();

            URL url = null;
            try {
                url = new URL(builtUri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            Log.v(TAG, "Built URI " + url);

            return url;
        }

    /**
     * method to generate the url from the base url that represents the image poster
     * @param path the path of the image from the server json response
     * @return the image poster URL
     */

    public static URL buildImagePosterUrl(String path) {
            Uri builtUri = Uri.parse(IMG_BASE_URL).buildUpon().appendPath(imgSize).appendEncodedPath(path).build();
            URL url = null;
            try {
                url = new URL(builtUri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Log.v(TAG, "Poster URL " + url);
            return url;
        }

        /**
         * This method returns the entire result from the HTTP response.
         *
         * @param url The URL to fetch the HTTP response from.
         * @return The contents of the HTTP response.
         * @throws IOException Related to network and stream reading
         */
        public static String getResponseFromHttpUrl(URL url) throws IOException {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = urlConnection.getInputStream();

                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }
            } finally {
                urlConnection.disconnect();
            }
        }
    }

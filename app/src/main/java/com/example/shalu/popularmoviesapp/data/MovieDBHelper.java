package com.example.shalu.popularmoviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies.db";
    public static final int DATABASE_VERSION = 1;
    MovieDBHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE "+MovieContract.MovieEntry.TABLE_NAME+"(" +
                MovieContract.MovieEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                MovieContract.MovieEntry.COLUMN_MOVIE_ID+" INTEGER NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE+" TEXT NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_DESCRIPTION+" TEXT NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_RATING+" REAL NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE+" TEXT NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_POSTER+ " TEXT NOT NULL," +
                " UNIQUE (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);

    }
}

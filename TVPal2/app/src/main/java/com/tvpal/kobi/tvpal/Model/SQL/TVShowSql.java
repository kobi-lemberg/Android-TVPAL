package com.tvpal.kobi.tvpal.Model.SQL;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tvpal.kobi.tvpal.Model.Model;
import com.tvpal.kobi.tvpal.Model.TVShow;
import com.tvpal.kobi.tvpal.Model.User;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Kobi on 11/05/2016.
 */
public class TVShowSql {
    // public TVShow(String name, String mainActor,int season,int episode ,String category, String lastUpdated,String imagePath)
    private static final String SHOW_TABLE = Model.Constant.showsTable;
    private static final String SHOW_NAME = "name";
    private static final String SHOW_ACTOR = "mainActor";
    private static final String SHOW_SEASON = "season";
    private static final String SHOW_EPISODE = "episode";
    private static final String SHOW_CATEGORY = "category";
    private static final String SHOW_DATE = "lastUpdated";
    private static final String SHOW_IMAGE = "imagePath";


    public static void create(SQLiteDatabase db) {
        Log.d("TAG","Creating users table");
        db.execSQL("create table " +
                SHOW_TABLE      + " (" +
                SHOW_NAME + " TEXT," +
                SHOW_ACTOR      + " TEXT," +
                SHOW_SEASON      + " INTEGER," +
                SHOW_EPISODE      + " INTEGER," +
                SHOW_CATEGORY      + " TEXT," +
                SHOW_DATE   + " TEXT," +
                SHOW_IMAGE + " TEXT" +
                 ");");
    }


    public static void addShow(SQLiteDatabase db, TVShow show) {
        if(getShow(db,show.getName())==null){
            ContentValues values = new ContentValues();
            values.put(SHOW_NAME, show.getName());
            values.put(SHOW_ACTOR, show.getMainActor());
            values.put(SHOW_SEASON, show.getSeason());
            values.put(SHOW_EPISODE, show.getEpisode());
            values.put(SHOW_CATEGORY, show.getCategory());
            values.put(SHOW_DATE, show.getLastUpdated());
            values.put(SHOW_IMAGE, show.getImagePath());
            db.insert(SHOW_TABLE, null, values);
        }
    }


    public static void drop(SQLiteDatabase db)  {
        db.execSQL("drop table " + SHOW_TABLE);
    }

    public static void delete(SQLiteDatabase db, TVShow s) {db.delete(SHOW_TABLE,SHOW_NAME+" = ?" , new String[]{s.getName()});}

    public static List<TVShow> getAllShows(SQLiteDatabase db) {
        Cursor cursor = db.query(SHOW_TABLE, null, null, null, null, null, null);
        List<TVShow> list = new LinkedList<TVShow>();
        if (cursor.moveToFirst()) {
            int nameIDX = cursor.getColumnIndex(SHOW_NAME);
            int actorIDX = cursor.getColumnIndex(SHOW_ACTOR);
            int seasonIDX = cursor.getColumnIndex(SHOW_SEASON);
            int episodeIDX = cursor.getColumnIndex(SHOW_EPISODE);
            int categoryIDX = cursor.getColumnIndex(SHOW_CATEGORY);
            int dateIDX = cursor.getColumnIndex(SHOW_DATE);
            int imageIDX = cursor.getColumnIndex(SHOW_IMAGE);
            do {
                String name = cursor.getString(nameIDX);
                String actors = cursor.getString(actorIDX);
                int season = cursor.getInt(seasonIDX);
                int episode = cursor.getInt(episodeIDX);
                String category = cursor.getString(categoryIDX);
                String date = cursor.getString(dateIDX);
                String image = cursor.getString(imageIDX);
                list.add(new TVShow(name,actors,season,episode,category,date,image));
            }
            while (cursor.moveToNext());

        }
        return list;
    }

    @Nullable
    public static TVShow getShow(SQLiteDatabase db, String name) {
        Cursor cursor = db.query(SHOW_TABLE, null, SHOW_NAME + " = ?", new String[]{name}, null, null, null);
        if (cursor.moveToFirst()) {
            int nameIDX = cursor.getColumnIndex(SHOW_NAME);
            int actorIDX = cursor.getColumnIndex(SHOW_ACTOR);
            int seasonIDX = cursor.getColumnIndex(SHOW_SEASON);
            int episodeIDX = cursor.getColumnIndex(SHOW_EPISODE);
            int categoryIDX = cursor.getColumnIndex(SHOW_CATEGORY);
            int dateIDX = cursor.getColumnIndex(SHOW_DATE);
            int imageIDX = cursor.getColumnIndex(SHOW_IMAGE);
            String actors = cursor.getString(actorIDX);
            int season = cursor.getInt(seasonIDX);
            int episode = cursor.getInt(episodeIDX);
            String category = cursor.getString(categoryIDX);
            String date = cursor.getString(dateIDX);
            String image = cursor.getString(imageIDX);
            return (new TVShow(name,actors,season,episode,category,date,image));
        }
        return null;
    }


    public static void updateShowByName(SQLiteDatabase db, String name, TVShow updated) {
        ContentValues values = new ContentValues();
        values.put(SHOW_NAME, name);
        values.put(SHOW_ACTOR, updated.getMainActor());
        values.put(SHOW_SEASON, updated.getSeason());
        values.put(SHOW_EPISODE, updated.getEpisode());
        values.put(SHOW_CATEGORY, updated.getCategory());
        values.put(SHOW_DATE, updated.getLastUpdated());
        values.put(SHOW_IMAGE, updated.getImagePath());
        db.update(SHOW_TABLE,values, SHOW_NAME + " = ?", new String[]{updated.getName()});

    }

}

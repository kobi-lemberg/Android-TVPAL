package com.tvpal.kobi.tvpal.Model.SQL;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tvpal.kobi.tvpal.Dialogs.StringDialogFragment;
import com.tvpal.kobi.tvpal.Model.Model;
import com.tvpal.kobi.tvpal.Model.Post;
import com.tvpal.kobi.tvpal.Model.TVShow;
import com.tvpal.kobi.tvpal.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Kobi on 11/05/2016.
 */
public class PostSql {
    private static final String POST_TABLE = Model.Constant.postsTable;
    private static final String SHOW_NAME = "showName";
    private static final String USER_MAIL = "userEmail";
    private static final String POST_TEXT = "text";
    private static final String POST_DATE = "date";
    private static final String POST_PART = "currentPart";
    private static final String POST_GRADE = "grade";

    public static void create(SQLiteDatabase db) {
        Log.d("TAG","Creating users table");
        db.execSQL("create table " +
                POST_TABLE      + " (" +
                SHOW_NAME + " TEXT," +
                USER_MAIL      + " TEXT," +
                POST_TEXT      + " TEXT," +
                POST_DATE      + " TEXT," +
                POST_PART      + " INTEGER," +
                POST_GRADE   + " INTEGER" +
                 ");");
    }

    public static void addPost(SQLiteDatabase db, Post p) {
        boolean flag=true;
        for(Post candidate: getAllPostsByTimeStamp(db,p.getDate())){
            if(candidate.equals(p));
            flag=false;
        }
        if(flag)
        {
            ContentValues values = new ContentValues();
            values.put(SHOW_NAME, p.getShowName());
            values.put(USER_MAIL, p.getUserEmail());
            values.put(POST_TEXT, p.getText());
            values.put(POST_DATE, p.getDate());
            values.put(POST_PART, p.getCurrentPart());
            values.put(POST_GRADE, p.getGrade());
            db.insert(POST_TABLE, null, values);
            TVShowSql.addShow(db,p.getShow());
        }

    }

    public static void drop(SQLiteDatabase db)  {db.execSQL("drop table " + POST_TABLE);}

    public static LinkedList<Post> getAllPosts(SQLiteDatabase db) {
        Cursor cursor = db.query(POST_TABLE, null, null, null, null, null, POST_DATE+" DESC");
        LinkedList<Post> list = new LinkedList<Post>();
        if (cursor.moveToFirst()) {
            int nameIDX = cursor.getColumnIndex(SHOW_NAME);
            int emailIDX = cursor.getColumnIndex(USER_MAIL);
            int textIDX = cursor.getColumnIndex(POST_TEXT);
            int dateIDX = cursor.getColumnIndex(POST_DATE);
            int partIDX = cursor.getColumnIndex(POST_PART);
            int gradeIDX = cursor.getColumnIndex(POST_GRADE);
            do {
                String name = cursor.getString(nameIDX);
                String email = cursor.getString(emailIDX);
                String txt = cursor.getString(textIDX);
                String date = cursor.getString(dateIDX);
                int part = cursor.getInt(partIDX);
                int grade = cursor.getInt(gradeIDX);
                TVShow show = TVShowSql.getShow(db,name);
                list.add(new Post(name,email,txt,date,part,grade,show));
            } while (cursor.moveToNext());
        }
        return list;
    }

    @Nullable
    public static LinkedList<Post> getAllPostsByUser(SQLiteDatabase db,String email) {
        Cursor cursor = db.query(POST_TABLE,  null, USER_MAIL+" = ?",new String[]{email}, null, null,null );
        LinkedList<Post> list = new LinkedList<Post>();
        if (cursor.moveToFirst()) {
            int nameIDX = cursor.getColumnIndex(SHOW_NAME);
            int textIDX = cursor.getColumnIndex(POST_TEXT);
            int dateIDX = cursor.getColumnIndex(POST_DATE);
            int partIDX = cursor.getColumnIndex(POST_PART);
            int gradeIDX = cursor.getColumnIndex(POST_GRADE);
            do {
                String name = cursor.getString(nameIDX);
                String txt = cursor.getString(textIDX);
                String date = cursor.getString(dateIDX);
                int part = cursor.getInt(partIDX);
                int grade = cursor.getInt(gradeIDX);
                TVShow show = TVShowSql.getShow(db,name);
                list.add(new Post(name,email,txt,date,part,grade,show));
            } while (cursor.moveToNext());
        }
        return list;
    }

    @Nullable
    public static LinkedList<Post> getAllPostsByTimeStamp(SQLiteDatabase db,String date) {
        Cursor cursor = db.query(POST_TABLE,  null, POST_DATE+" = ?",new String[]{date}, null, null,null );
        LinkedList<Post> list = new LinkedList<Post>();
        if (cursor.moveToFirst()) {
            int nameIDX = cursor.getColumnIndex(SHOW_NAME);
            int emailIDX = cursor.getColumnIndex(USER_MAIL);
            int textIDX = cursor.getColumnIndex(POST_TEXT);
            int dateIDX = cursor.getColumnIndex(POST_DATE);
            int partIDX = cursor.getColumnIndex(POST_PART);
            int gradeIDX = cursor.getColumnIndex(POST_GRADE);
            do {
                String name = cursor.getString(nameIDX);
                String txt = cursor.getString(textIDX);
                String mail = cursor.getString(emailIDX);
                int part = cursor.getInt(partIDX);
                int grade = cursor.getInt(gradeIDX);
                TVShow show = TVShowSql.getShow(db,name);
                list.add(new Post(name,mail,txt,date,part,grade,show));
            } while (cursor.moveToNext());
        }
        return list;
    }

    @Nullable
    public static LinkedList<Post> getAllPostsByShow(SQLiteDatabase db,String name) {
        Cursor cursor = db.query(POST_TABLE,  null, SHOW_NAME+" = ?",new String[]{name}, null, null, null);
        LinkedList<Post> list = new LinkedList<Post>();
        if (cursor.moveToFirst()) {
            int emailIDX = cursor.getColumnIndex(USER_MAIL);
            int textIDX = cursor.getColumnIndex(POST_TEXT);
            int dateIDX = cursor.getColumnIndex(POST_DATE);
            int partIDX = cursor.getColumnIndex(POST_PART);
            int gradeIDX = cursor.getColumnIndex(POST_GRADE);
            do {
                String email = cursor.getString(emailIDX);
                String txt = cursor.getString(textIDX);
                String date = cursor.getString(dateIDX);
                int part = cursor.getInt(partIDX);
                int grade = cursor.getInt(gradeIDX);
                TVShow show = TVShowSql.getShow(db,name);
                list.add(new Post(name,email,txt,date,part,grade,show));
            } while (cursor.moveToNext());
        }
        return list;
    }


    public static LinkedList<Post> getAllPostsPerUserUniq(SQLiteDatabase db,String email) {
        Cursor cursor = db.query(POST_TABLE,  null, USER_MAIL+" = ?",new String[]{email}, null, null, POST_DATE+" DESC");
        HashMap<String,Post> finalMap = new HashMap<String,Post>();
        if (cursor.moveToFirst()) {
            int nameIDX = cursor.getColumnIndex(SHOW_NAME);
            int textIDX = cursor.getColumnIndex(POST_TEXT);
            int dateIDX = cursor.getColumnIndex(POST_DATE);
            int partIDX = cursor.getColumnIndex(POST_PART);
            int gradeIDX = cursor.getColumnIndex(POST_GRADE);
            do {
                String name = cursor.getString(nameIDX);
                String txt = cursor.getString(textIDX);
                String date = cursor.getString(dateIDX);
                int part = cursor.getInt(partIDX);
                int grade = cursor.getInt(gradeIDX);
                TVShow show = TVShowSql.getShow(db,name);
                if(!finalMap.containsKey(name))
                    finalMap.put(name,new Post(name,email,txt,date,part,grade,show));
            }
            while (cursor.moveToNext());
        }
        return new LinkedList<Post>(finalMap.values());
    }

    public static LinkedList<Post> getAllPostsByParams(SQLiteDatabase db, String showName, String date, String text) {
        Cursor cursor = db.query(POST_TABLE,  null, SHOW_NAME+" = ? AND "+POST_TEXT+" = ? AND "+POST_DATE+" = ?",new String[]{showName,date,text}, null, null, POST_DATE+" DESC");
        LinkedList<Post> p = null;
        if (cursor.moveToFirst()) {
            p = new LinkedList<Post>();
            int nameIDX = cursor.getColumnIndex(SHOW_NAME);
            int textIDX = cursor.getColumnIndex(POST_TEXT);
            int emailIDX = cursor.getColumnIndex(USER_MAIL);
            int dateIDX = cursor.getColumnIndex(POST_DATE);
            int partIDX = cursor.getColumnIndex(POST_PART);
            int gradeIDX = cursor.getColumnIndex(POST_GRADE);
            do {
                String name = cursor.getString(nameIDX);
                String txt = cursor.getString(textIDX);
                String email = cursor.getString(emailIDX);
                int part = cursor.getInt(partIDX);
                int grade = cursor.getInt(gradeIDX);
                TVShow show = TVShowSql.getShow(db,name);
                    p.add(new Post(name,email,txt,date,part,grade,show));
            } while (cursor.moveToNext());
        }
        return p;
    }

    public static ArrayList<TVShow> getNoneShowsForUserByPosts(SQLiteDatabase db,String email){

            Cursor cursor = db.query(POST_TABLE,  null, USER_MAIL+" != ?",new String[]{email}, null, null,SHOW_NAME );

            HashMap<String,TVShow> finalMap  = new HashMap<String,TVShow>();
            if (cursor.moveToFirst()) {
                int nameIDX = cursor.getColumnIndex(SHOW_NAME);
                do {
                    String name = cursor.getString(nameIDX);
                    TVShow show = TVShowSql.getShow(db,name);
                    if(!finalMap.containsKey(name))
                        finalMap.put(name,show);
                } while (cursor.moveToNext());
            }
            return new ArrayList<TVShow>(finalMap.values());

    }
    public static void delete(SQLiteDatabase db, Post p) {db.delete(POST_TABLE,SHOW_NAME+" = ? AND "+POST_TEXT+" = ? AND "+POST_DATE+" = ?" , new String[]{p.getShowName(),p.getText(),p.getDate()});}

}

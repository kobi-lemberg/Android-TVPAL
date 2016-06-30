package com.tvpal.kobi.tvpal.Model.SQL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.tvpal.kobi.tvpal.Model.Post;
import com.tvpal.kobi.tvpal.Model.TVShow;
import com.tvpal.kobi.tvpal.Model.User;
import com.tvpal.kobi.tvpal.MyApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
public class ModelSql {

    private final static int VERSION =34;
    MyDBHelper dbHelper;

    public ModelSql() {dbHelper = new MyDBHelper(MyApplication.getAppContext());}

    public void addUser(User user) {UserSql.addUser(dbHelper.getWritableDatabase(), user);}

    public User getUserByEmail(String email) {return UserSql.getUser(dbHelper.getReadableDatabase(),email);}

    public void updateUserByID(String email, User updated) {UserSql.updateUserByEmail(dbHelper.getReadableDatabase(),email,updated);}

    public User authenticate(String email, String password) {return UserSql.authenticate(dbHelper.getReadableDatabase(),email,password);}

    public void setLastUpdate(String tableName, String lastUpdateDate) {LastUpdateSql.setLastUpdate(dbHelper.getWritableDatabase(),tableName,lastUpdateDate);}

    public String getLastUpdate(String table) {return LastUpdateSql.getLastUpdate(dbHelper.getWritableDatabase(),table);}

    public void addShow(TVShow show) {TVShowSql.addShow(dbHelper.getWritableDatabase(),show);}

    public void addNewPost(TVShow show, Post post) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        TVShowSql.addShow(db,show);
        PostSql.addPost(db,post);
    }

    public void addPost(Post post) {PostSql.addPost(dbHelper.getWritableDatabase(),post);}

    public  LinkedList<Post> getAllPostsPerUser(String email) {return PostSql.getAllPostsByUser(dbHelper.getWritableDatabase(),email);}

    public LinkedList<Post> getAllPostsPerUserUniq(String email) {return PostSql.getAllPostsPerUserUniq(dbHelper.getWritableDatabase(),email);}

    public LinkedList<Post> getAllPosts() {
        LinkedList<Post> posts = PostSql.getAllPosts(dbHelper.getWritableDatabase());
        Collections.sort(posts);

        return posts;
    }

    public TVShow getShow(String showName) {return TVShowSql.getShow(dbHelper.getWritableDatabase(),showName);}

    public LinkedList<Post> getPostsByShowNamw(String showName) {return PostSql.getAllPostsByShow(dbHelper.getWritableDatabase(),showName);}

    public Post getPostByParams(String showName, String date, String text) {return PostSql.getAllPostsByParams(dbHelper.getWritableDatabase(),showName,date,text).getFirst();}

    public ArrayList<TVShow> getAllNoneIncludesShowsForUser(String email){return PostSql.getNoneShowsForUserByPosts(dbHelper.getWritableDatabase(),email);}


    class MyDBHelper extends SQLiteOpenHelper {

        public MyDBHelper(Context context) {
            super(context, "my_DB.db", null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            UserSql.create(db);
            LastUpdateSql.create(db);
            PostSql.create(db);
            TVShowSql.create(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try{UserSql.drop(db);
            }catch (Exception e){e.printStackTrace();}
            try{LastUpdateSql.drop(db);
            }catch (Exception e){e.printStackTrace();}
            try{PostSql.drop(db);
            }catch (Exception e){e.printStackTrace();}
            try{TVShowSql.drop(db);
            }catch (Exception e){e.printStackTrace();}
            onCreate(db);
        }
    }
}

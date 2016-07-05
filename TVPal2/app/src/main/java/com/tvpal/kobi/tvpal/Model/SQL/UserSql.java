package com.tvpal.kobi.tvpal.Model.SQL;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;
import com.tvpal.kobi.tvpal.Model.Model;
import com.tvpal.kobi.tvpal.Model.User;
import java.util.LinkedList;
import java.util.List;

public class UserSql {

    private static final String USERS_TABLE = Model.Constant.usersTable;
    private static final String USERS_EMAIL = "email";
    private static final String USERS_FNAME = "firstName";
    private static final String USERS_LNAME = "lastName";
    private static final String USERS_BDATE = "birthDate";
    private static final String USERS_PASSWORD = "password";
    private static final String USERS_IMAGE_NAME = "image_name";
    private static final String USERS_LAST_UPDATED = "last_update";


    public static void create(SQLiteDatabase db) {
        Log.d("TAG","Creating users table");
        db.execSQL("create table " +
                USERS_TABLE      + " (" +
                USERS_LAST_UPDATED + " TEXT," +
                USERS_EMAIL      + " TEXT," +
                USERS_FNAME      + " TEXT," +
                USERS_LNAME      + " TEXT," +
                USERS_BDATE      + " TEXT," +
                USERS_PASSWORD   + " TEXT," +
                USERS_IMAGE_NAME + " TEXT" +
                 ");");
    }


    public static void addUser(SQLiteDatabase db, User user) {
        ContentValues values = new ContentValues();
        values.put(USERS_EMAIL, user.getEmail());
        values.put(USERS_FNAME, user.getFirstName());
        values.put(USERS_LNAME, user.getLastName());
        values.put(USERS_BDATE, user.getBirthDate());
        values.put(USERS_PASSWORD, user.getPassword());
        values.put(USERS_IMAGE_NAME, user.getProfilePic());
        values.put(USERS_LAST_UPDATED, user.getLastUpdateDate());
        db.insert(USERS_TABLE, null, values);
    }


    public static void drop(SQLiteDatabase db)  {
        db.execSQL("drop table " + USERS_TABLE);
    }

    public static void delete(SQLiteDatabase db, User u) {db.delete(USERS_TABLE,USERS_EMAIL+" = ?" , new String[]{u.getEmail()});}

    public static List<User> getAllUsers(SQLiteDatabase db) {
        Cursor cursor = db.query(USERS_TABLE, null, null, null, null, null, null);
        List<User> list = new LinkedList<User>();
        if (cursor.moveToFirst()) {
            int emailIndex = cursor.getColumnIndex(USERS_EMAIL);
            int fnameIndex = cursor.getColumnIndex(USERS_FNAME);
            int lnameIndex = cursor.getColumnIndex(USERS_LNAME);
            int bDateIndex = cursor.getColumnIndex(USERS_BDATE);
            int passwdIndex = cursor.getColumnIndex(USERS_PASSWORD);
            int imageNameIndex = cursor.getColumnIndex(USERS_IMAGE_NAME);
            int lastUpdatedIndex = cursor.getColumnIndex(USERS_LAST_UPDATED);
            do {
                String email = cursor.getString(emailIndex);
                String fname = cursor.getString(fnameIndex);
                String lname = cursor.getString(lnameIndex);
                String bDate = cursor.getString(bDateIndex);
                String password = cursor.getString(passwdIndex);
                String imageName = cursor.getString(imageNameIndex);
                String lastUpdated = cursor.getString(lastUpdatedIndex);
                list.add(new User(email,fname,lname,bDate,password,imageName,lastUpdated));
            }
            while (cursor.moveToNext());

        }
        return list;
    }

    @Nullable
    public static User getUser(SQLiteDatabase db, String email) {
        Cursor cursor = db.query(USERS_TABLE, null, USERS_EMAIL + " = ?", new String[]{email}, null, null, null);
        if (cursor.moveToFirst()) {
            int emailIndex = cursor.getColumnIndex(USERS_EMAIL);
            int fnameIndex = cursor.getColumnIndex(USERS_FNAME);
            int lnameIndex = cursor.getColumnIndex(USERS_LNAME);
            int bDateIndex = cursor.getColumnIndex(USERS_BDATE);
            int passwdIndex = cursor.getColumnIndex(USERS_PASSWORD);
            int imageNameIndex = cursor.getColumnIndex(USERS_IMAGE_NAME);
            int lastUpdatedIndex = cursor.getColumnIndex(USERS_LAST_UPDATED);
            String fname = cursor.getString(fnameIndex);
            String lname = cursor.getString(lnameIndex);
            String bDate = cursor.getString(bDateIndex);
            String password = cursor.getString(passwdIndex);
            String imageName = cursor.getString(imageNameIndex);
            String lastUpdated = cursor.getString(lastUpdatedIndex);
            return (new User(email,fname,lname,bDate,password,imageName,lastUpdated));
        }
        return null;
    }


    public static void updateUserByEmail(SQLiteDatabase db, String email, User updated) {
        ContentValues values = new ContentValues();
        values.put(USERS_EMAIL, updated.getEmail());
        values.put(USERS_FNAME, updated.getFirstName());
        values.put(USERS_LNAME, updated.getLastName());
        values.put(USERS_BDATE, updated.getBirthDate());
        values.put(USERS_PASSWORD, updated.getPassword());
        values.put(USERS_IMAGE_NAME, updated.getProfilePic());
        values.put(USERS_LAST_UPDATED, updated.getLastUpdateDate());
        db.update(USERS_TABLE,values, USERS_EMAIL + " = ?", new String[]{updated.getEmail()});

    }

    public static User authenticate(SQLiteDatabase db, String email, String password) {
        Cursor cursor = db.query(USERS_TABLE, null, USERS_EMAIL + " = ?", new String[]{email}, null, null, null);
        if (cursor.moveToFirst()) {
            int emailIndex = cursor.getColumnIndex(USERS_EMAIL);
            int fnameIndex = cursor.getColumnIndex(USERS_FNAME);
            int lnameIndex = cursor.getColumnIndex(USERS_LNAME);
            int bDateIndex = cursor.getColumnIndex(USERS_BDATE);
            int passwdIndex = cursor.getColumnIndex(USERS_PASSWORD);
            int imageNameIndex = cursor.getColumnIndex(USERS_IMAGE_NAME);
            int lastUpdatedIndex = cursor.getColumnIndex(USERS_LAST_UPDATED);
            String eMail = cursor.getString(emailIndex);
            String passwd = cursor.getString(passwdIndex);
            if(email.equals(eMail) && password.equals(passwd)) {
                String fname = cursor.getString(fnameIndex);
                String lname = cursor.getString(lnameIndex);
                String bDate = cursor.getString(bDateIndex);
                String imageName = cursor.getString(imageNameIndex);
                String lastUpdated = cursor.getString(lastUpdatedIndex);
                return (new User(email,fname,lname,bDate,password,imageName,lastUpdated));
            }
        }
        return null;
    }
}

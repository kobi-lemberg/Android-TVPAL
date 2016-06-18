package com.tvpal.kobi.tvpal.Model.SQL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.tvpal.kobi.tvpal.Model.User;
import com.tvpal.kobi.tvpal.MyApplication;
import java.util.List;

/**
 * Created by Kobi on 11/05/2016.
 */
public class ModelSql {

    private final static int VERSION =24;
    MyDBHelper dbHelper;

    public ModelSql() {
        dbHelper = new MyDBHelper(MyApplication.getAppContext());
    }

    public void addUser(User user) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        UserSql.addUser(db, user);
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return UserSql.getUser(db,email);
    }

    public List<User> getAllUsers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return UserSql.getAllUsers(db);
    }

    public void delete(User u) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        UserSql.delete(db, u);
    }

    public void updateUserByID(String email, User updated) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        UserSql.updateUserByEmail(db,email,updated);
    }

    public User authenticate(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return UserSql.authenticate(db,email,password);


    }


    class MyDBHelper extends SQLiteOpenHelper {

        public MyDBHelper(Context context) {
            super(context, "my_DB.db", null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //create the DB schema
            UserSql.create(db);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            UserSql.drop(db);
            onCreate(db);
        }
    }
}

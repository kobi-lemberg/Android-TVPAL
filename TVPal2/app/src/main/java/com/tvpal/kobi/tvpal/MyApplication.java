package com.tvpal.kobi.tvpal;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Kobi on 10/05/2016.
 */
public class MyApplication extends Application{
    private static Context context;
    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }
    public static String getCurrentDate() {
        DateFormat df = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss");
        // Get the date today using Calendar object.
        Date today = Calendar.getInstance().getTime();
        // Using DateFormat format method we can create a string
        // representation of a date with the defined format.
        return df.format(today);
    }

    public static Context getAppContext(){return MyApplication.context;}

    public static Boolean isConnectedToNetwork() {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }
}

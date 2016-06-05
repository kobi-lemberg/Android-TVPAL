package com.tvpal.kobi.tvpal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tvpal.kobi.tvpal.Model.Model;

public class NewsFeedActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_news_feed);
            Log.d("TAG", "In NewsFeed Activity");
            //Log.d("TAG", "On Accout: " + Model.instance().getCurrentUser().toString());
            Button profileBut = (Button) findViewById(R.id.activity_news_feed_button);
            profileBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG", "Clicked on profile button, MOVING TO PROFILE ACTIVITY");
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(intent);
                }
            });

    }
}


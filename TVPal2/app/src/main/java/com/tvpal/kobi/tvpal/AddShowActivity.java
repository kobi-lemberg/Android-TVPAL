package com.tvpal.kobi.tvpal;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.tvpal.kobi.tvpal.Model.Model;
import com.tvpal.kobi.tvpal.Model.Post;
import com.tvpal.kobi.tvpal.Model.TVShow;

public class AddShowActivity extends Activity {

    ImageView showImage;
    EditText showName;
    EditText famousActors;
    EditText numberOfEpisodes;
    EditText categories;
    EditText summary;
    Button save;
    Button cancel;
    TVShow show;
    Post post;
    int grade;
    int currentPart;
    RatingBar ratingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_show);
        showImage = (ImageView)findViewById(R.id.activity_addShow_imageView);
        showName = (EditText)findViewById(R.id.activity_addShow_movieName);
        famousActors = (EditText) findViewById(R.id.activity_addShow_famousActors);
        numberOfEpisodes = (EditText) findViewById(R.id.activity_addShow_NumberOfEpisodes);
        categories = (EditText) findViewById(R.id.activity_addShow_Categories);
        summary = (EditText) findViewById(R.id.addShow_activity_summary);
        save = (Button) findViewById(R.id.activity_add_Show_Save);
        cancel = (Button) findViewById(R.id.activity_add_show_cancel);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    public TVShow(String id, String name, String mainActor, int episodes, String category, String lastUpdated, String summery) {
                int episodes = new Integer(numberOfEpisodes.getText().toString().trim());
                show = new TVShow("1",showName.getText().toString(),famousActors.getText().toString(),episodes,
                        categories.getText().toString(),MyApplication.getCurrentDate(),summary.getText().toString());

                currentPart = 2;
                post = new Post(show.getId(), Model.instance().getCurrentUser().getEmail()
                        ,summary.getText().toString()
                        ,MyApplication.getCurrentDate(),0,false,ratingBar.getNumStars());
                Log.d("TAG","Adding Post: "+ post.toString());
            }
        });
    }
}

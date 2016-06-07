package com.tvpal.kobi.tvpal;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class AddShowActivity extends Activity {

    ImageView showImage;
    EditText showName;
    EditText famousActors;
    EditText numberOfEpisodes;
    EditText categories;
    EditText summary;
    Button save;
    Button cancel;
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
        save = (Button) findViewById(R.id.button_Save_addShow);
        cancel = (Button) findViewById(R.id.button_Cancel_addShow);
    }
}

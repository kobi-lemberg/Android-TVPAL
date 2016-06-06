package com.tvpal.kobi.tvpal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvpal.kobi.tvpal.Model.Model;
import com.tvpal.kobi.tvpal.Model.User;

/**
 * Created by nir on 05/06/2016.
 */
public class ProfileActivity  extends Activity{
    Button editProfile;
    TextView displayName;
    TextView email;
    TextView birthDate;
    ImageView profilePic;
    User user = Model.instance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        User user = Model.instance().getCurrentUser();
        Log.d("TAG", "In Profile activity.");
        Model.instance().getUpdateDate();
        Log.d("TAG", "Finished.");
        displayName = (TextView) findViewById(R.id.activity_profile_name);
        email = (TextView) findViewById(R.id.activity_profile_Email);
        birthDate = (TextView) findViewById(R.id.activity_profile_Birh_Date);
        profilePic = (ImageView) findViewById(R.id.activity_profile_imageView);
        //put the variables.
        displayName.setText(user.displayName());
        email.setText(user.getEmail());
        birthDate.setText(user.getBirthDate());
        //profilePic.setImageBitmap(user.getProfilePic());

        editProfile = (Button) findViewById(R.id.edit_profile_button);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "Clicked on Edit profile button, MOVING TO Edit PROFILE ACTIVITY");
                Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
    }

}



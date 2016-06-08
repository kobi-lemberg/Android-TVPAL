package com.tvpal.kobi.tvpal;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
    ProgressBar imageProgressBar;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //06-07 10:48:12.518 11759-11759/com.tvpal.kobi.tvpal D/TAG: User have been changed : User{email='m@n.com', firstName='q', lastName='p', birthDate='5/6/2016', password='m', profilePic='null', lastUpdateDate='06/05/2016 21:41:54'}

        setContentView(R.layout.activity_profile);
        user = Model.instance().getCurrentUser();
        Log.d("TAG", "In Profile activity.");
        Log.d("TAG","USER: "+user.toString());
        Model.instance().getUpdateDate();
        Log.d("TAG", "Finished.");
        imageProgressBar = (ProgressBar) findViewById(R.id.UserImageProgressBar);

        displayName = (TextView) findViewById(R.id.activity_profile_name);
        email = (TextView) findViewById(R.id.activity_profile_Email);
        birthDate = (TextView) findViewById(R.id.activity_profile_Birh_Date);
        profilePic = (ImageView) findViewById(R.id.activity_profile_imageView);
        if(!Model.instance().isDefaultProfilePic(user.getProfilePic())){
            imageProgressBar.setVisibility(View.VISIBLE);
            Model.instance().loadImage(user.getProfilePic(), new Model.LoadImageListener() {
                @Override
                public void onResult(Bitmap imageBmp) {
                    if(imageBmp!=null)
                        profilePic.setImageBitmap(imageBmp);
                    imageProgressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
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
                startActivityForResult(intent,0);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1) {
            user = Model.instance().getCurrentUser();
            Log.d("TAG","User Edited: "+user.toString());
            displayName.setText(user.displayName());
            email.setText(user.getEmail());
            birthDate.setText(user.getBirthDate());
/*            if(!Model.instance().isDefaultProfilePic(user.getProfilePic())){
                imageProgressBar.setVisibility(View.VISIBLE);
                Model.instance().loadImage(user.getProfilePic(), new Model.LoadImageListener() {
                    @Override
                    public void onResult(Bitmap imageBmp) {
                        if(imageBmp!=null)
                            profilePic.setImageBitmap(imageBmp);
                        imageProgressBar.setVisibility(View.INVISIBLE);

                    }
                });
            }*/
            if(!Model.instance().isDefaultProfilePic(user.getProfilePic()))
            {
                imageProgressBar.setVisibility(View.VISIBLE);
                Log.d("TAG","Profile Pic is different");
                profilePic.setImageBitmap(Model.instance().loadImageFromFile(user.getProfilePic()));
                imageProgressBar.setVisibility(View.INVISIBLE);
            }
            //updateUser();



        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateUser()
    {
        user = Model.instance().getCurrentUser();
        displayName.setText(user.displayName());
        email.setText(user.getEmail());
        birthDate.setText(user.getBirthDate());
        if(!Model.instance().isDefaultProfilePic(user.getProfilePic())){
            imageProgressBar.setVisibility(View.VISIBLE);
            Model.instance().loadImage(user.getProfilePic(), new Model.LoadImageListener() {
                @Override
                public void onResult(Bitmap imageBmp) {
                    if(imageBmp!=null)
                        profilePic.setImageBitmap(imageBmp);
                    imageProgressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }
}



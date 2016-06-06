package com.tvpal.kobi.tvpal;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.tvpal.kobi.tvpal.Model.Model;
import com.tvpal.kobi.tvpal.Model.User;

public class EditProfileActivity extends Activity
{
    TextView firstName;
    TextView lastName;
    ImageView profilePic;
    User user = Model.instance().getCurrentUser();
    Button saveButton;
    Button cancelButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        firstName= (TextView) findViewById(R.id.activity_EditProfile_First_name);
        lastName = (TextView) findViewById(R.id.activity_EditProfile_Last_name);
        profilePic = (ImageView) findViewById(R.id.activity_Edit_profile_imageView);
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());

        //profilePic.setImageBitmap(user.getProfilePic());
        saveButton = (Button) findViewById(R.id.button_Save_edit_profile);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //need to save the new user on database and firebase.
                User changedUser = new User(user.getEmail(),user.getPassword(),firstName.getText().toString(),lastName.getText().toString(),
                        user.getBirthDate(),user.getProfilePic(),MyApplication.getCurrentDate());

                Model.instance().updateUserByEmail(user.getEmail(),changedUser);


            }
        });

    }
}

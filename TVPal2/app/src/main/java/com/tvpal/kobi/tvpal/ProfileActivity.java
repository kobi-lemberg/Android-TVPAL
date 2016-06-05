package com.tvpal.kobi.tvpal;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        User user = Model.instance().getCurrentUser();
        Log.d("TAG", "In Profile activity.");
        Model.instance().getUpdateDate();
        Log.d("TAG","Finished.");
    }

}



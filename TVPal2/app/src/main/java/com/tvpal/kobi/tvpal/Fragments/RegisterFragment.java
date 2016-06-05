package com.tvpal.kobi.tvpal.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tvpal.kobi.tvpal.Pickers.DateEditText;
import com.tvpal.kobi.tvpal.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class RegisterFragment extends Fragment {

    ImageView profilePic;
    Bitmap chosenPic;
    String profilePicFileName;
    EditText email;
    EditText firstName;
    EditText lastName;
    DateEditText birthDate;
    EditText password;
    EditText passwordConfirm;
    ProgressBar progressBar;

    public interface RegisterDelegate{
        public void handleCamera();
        public void signUp(String Email,String password, String firstName, String lastName,String birthDate, Bitmap profilePic, String profilePicPath);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        setHasOptionsMenu(true);

        profilePic = (ImageView) view.findViewById(R.id.profilePic);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicture();
                Log.d("TAG","ProfilePic was Chosen");
            }
        });

         email = (EditText) view.findViewById(R.id.fragment_register_editText_email);
         firstName = (EditText) view.findViewById(R.id.fragment_register_editText_firstName);
         lastName = (EditText) view.findViewById(R.id.fragment_register_editText_lastName);
         birthDate = (DateEditText) view.findViewById(R.id.fragment_register_DateEditText);
         password = (EditText) view.findViewById(R.id.fragment_register_editText_password);
         passwordConfirm = (EditText) view.findViewById(R.id.fragment_register_editText_password_confirm);
         progressBar=(ProgressBar) view.findViewById(R.id.progressBar);

        Button done = (Button) view.findViewById(R.id.fragment_register_done_btn);
        if(profilePicFileName==null)
            profilePicFileName="";
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getText().toString().equals(passwordConfirm.getText().toString())&&password.getText().toString().length()>0){
                    progressBar.setVisibility(View.VISIBLE);

                    ((RegisterDelegate)getActivity()).signUp(email.getText().toString(),password.getText().toString(),firstName.getText().toString(),lastName.getText()
                    .toString(),birthDate.getText().toString(),chosenPic,profilePicFileName);
                }
                else
                {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Error",Toast.LENGTH_SHORT);
                    toast.setText("Wrong details");
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        Log.d("TAG","RegisterFragment was loaded");
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().setTitle("Register");
/*
        inflater.inflate(R.menu.menu_main, menu);
*/
        super.onCreateOptionsMenu(menu, inflater);
    }
    public void setProfilePic(Bitmap pic,String fileName)
    {
        this.chosenPic = pic;
        this.profilePic.setImageBitmap(pic);
        this.profilePicFileName = fileName;
    }
    final int profilePicStatus = 1;

    private void getPicture()
    {
        ((RegisterDelegate)getActivity()).handleCamera();

        /*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, profilePicStatus);
        }*/
    }


 /*   @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == profilePicStatus && resultCode == Activity.RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            profilePic.setImageBitmap(imageBitmap);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
*//*            imageFileName = "JPEG_" + timeStamp + ".jpeg";
            newImageName.setText(imageFileName);
            Model.getInstance().saveImage(imageBitmap,imageFileName);*//*
        }
    }*/


/*    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden)
        {
            Log.d("HIDDEN","FALSE");
            this.profilePic.setImageDrawable(getResources().getDrawable(R.drawable.camera));
            this.profilePicFileName="";
            this.email.setText("");
            this.firstName.setText("");
            this.lastName.setText("");
            this.birthDate.setText("");
            this.password.setText("");
            this.passwordConfirm.setText("");
            super.onResume();
        }
    }*/
}

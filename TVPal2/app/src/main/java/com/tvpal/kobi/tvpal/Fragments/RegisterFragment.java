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
                ((RegisterDelegate)getActivity()).handleCamera();
            }
        });

         email = (EditText) view.findViewById(R.id.fragment_register_editText_email);
         firstName = (EditText) view.findViewById(R.id.fragment_register_editText_firstName);
         lastName = (EditText) view.findViewById(R.id.fragment_register_editText_lastName);
         birthDate = (DateEditText) view.findViewById(R.id.fragment_register_DateEditText);
         password = (EditText) view.findViewById(R.id.fragment_register_editText_password);
         passwordConfirm = (EditText) view.findViewById(R.id.fragment_register_editText_password_confirm);
         progressBar=(ProgressBar) view.findViewById(R.id.fragment_register_progressBar);
        progressBar.setVisibility(View.INVISIBLE);

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
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().setTitle("Register");
        super.onCreateOptionsMenu(menu, inflater);
    }
    public void setProfilePic(Bitmap pic,String fileName)
    {
        this.chosenPic = pic;
        this.profilePic.setImageBitmap(pic);
        this.profilePicFileName = fileName;
    }
}

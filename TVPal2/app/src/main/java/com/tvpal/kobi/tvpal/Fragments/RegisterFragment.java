package com.tvpal.kobi.tvpal.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.tvpal.kobi.tvpal.R;


public class RegisterFragment extends Fragment {




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        getActivity().setTitle("Register");
/*        userNameTxt = (EditText) view.findViewById(R.id.fragment_login_editText_userName);
        passwordTxt = (EditText) view.findViewById(R.id.fragment_login_editText_password);
        Button loginBtn = (Button) view.findViewById(R.id.fragment_signin_login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginDelegate)getActivity()).signIn(userNameTxt.getText().toString(),passwordTxt.getText().toString());
            }
        });*/


        Log.d("TAG","RegisterFragment was loaded");
        return view;
    }


}

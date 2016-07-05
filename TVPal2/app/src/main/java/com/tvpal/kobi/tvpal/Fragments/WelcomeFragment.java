package com.tvpal.kobi.tvpal.Fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.tvpal.kobi.tvpal.R;

public class WelcomeFragment extends Fragment {

    public interface WelcomeDelegate{
        public void getRegister();
        public void getLogin();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        Button signInBtn = (Button) view.findViewById(R.id.fragment_signin_btn);
        Button signUpBtn = (Button) view.findViewById(R.id.fragment_signup_btn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((WelcomeDelegate)getActivity()).getLogin();
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((WelcomeDelegate)getActivity()).getRegister();
            }
        });

        Log.d("TAG","WelcomeFragment was loaded");
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().setTitle("TVPal");
        super.onCreateOptionsMenu(menu, inflater);
    }
}

package com.tvpal.kobi.tvpal.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.tvpal.kobi.tvpal.R;


public class LoginFragment extends Fragment {
    EditText userEmailTxt;
    EditText passwordTxt;
    ProgressBar progressBar;

    public void setProgressBarVisability(boolean b) {
        if(progressBar!=null) {
            if(b) this.progressBar.setVisibility(View.VISIBLE);
            else this.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public interface LoginDelegate{
        public void signIn(String userName,String password);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        Log.d("TAG","WelcomeFragment was loaded");
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        setHasOptionsMenu(true);
        progressBar = (ProgressBar) view.findViewById(R.id.login_progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        userEmailTxt = (EditText) view.findViewById(R.id.fragment_login_editText_userEmail);
        passwordTxt = (EditText) view.findViewById(R.id.fragment_login_editText_password);
        Button loginBtn = (Button) view.findViewById(R.id.fragment_signin_login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                ((LoginDelegate)getActivity()).signIn(userEmailTxt.getText().toString(),passwordTxt.getText().toString());
            }
        });
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().setTitle("Sign in");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(progressBar!=null)
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(progressBar!=null)
            progressBar.setVisibility(View.GONE);

    }

}

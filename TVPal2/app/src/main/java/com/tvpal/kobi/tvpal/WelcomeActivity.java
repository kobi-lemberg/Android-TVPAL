package com.tvpal.kobi.tvpal;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import com.tvpal.kobi.tvpal.Fragments.LoginFragment;
import com.tvpal.kobi.tvpal.Fragments.RegisterFragment;
import com.tvpal.kobi.tvpal.Fragments.WelcomeFragment;

public class WelcomeActivity extends Activity implements WelcomeFragment.WelcomeDelegate,LoginFragment.LoginDelegate{

    WelcomeFragment welcomeFragment;
    LoginFragment loginFragment;
    RegisterFragment registerFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        welcomeFragment = new WelcomeFragment();
        loginFragment  = new LoginFragment();
        registerFragment = new RegisterFragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.welcome_fragment_container,welcomeFragment,"welcomeFragment");
        transaction.add(R.id.welcome_fragment_container,loginFragment,"loginFragment");
        transaction.add(R.id.welcome_fragment_container,registerFragment,"registerFragment");
        if(!isAuthenticated()){
            transaction.hide(loginFragment);
            transaction.hide(registerFragment);
            transaction.show(welcomeFragment);
        }
        transaction.commit();




    }


    public boolean isAuthenticated(){
        return false;
    }

    @Override
    public void getRegister() {
        Log.d("WelcomeActivity","Should Open Register Fragment");
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.hide(welcomeFragment);
        transaction.hide(loginFragment);
        transaction.show(registerFragment);
        transaction.commit();

    }

    @Override
    public void getLogin() {
        Log.d("WelcomeActivity","Should Open Register Fragment");
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.hide(welcomeFragment);
        transaction.hide(registerFragment);
        transaction.show(loginFragment);
        transaction.commit();
    }

    @Override
    public void signIn(String userName, String password) {
        Log.d("welcomeActivity","Should log in user:" + userName+"  &password: "+ password);
    }

    @Override
    public void onBackPressed() {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if(loginFragment.isVisible()) {
            transaction.hide(loginFragment);
            transaction.show(welcomeFragment);
            transaction.commit();
        }
        else if(registerFragment.isVisible()) {
            transaction.hide(registerFragment);
            transaction.show(welcomeFragment);
            transaction.commit();
        }
        else if (welcomeFragment.isVisible()) super.onBackPressed();
        else super.onBackPressed();

    }
}

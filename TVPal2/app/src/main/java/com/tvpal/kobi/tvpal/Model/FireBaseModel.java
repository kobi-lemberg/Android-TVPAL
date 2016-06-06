package com.tvpal.kobi.tvpal.Model;

import android.content.Context;
import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.Map;

/**
 * Created by Kobi on 03/06/2016.
 */
public class FireBaseModel {

    Firebase myFirebaseRef;

    FireBaseModel(Context context){
        Firebase.setAndroidContext(context);
        myFirebaseRef = new Firebase("https://sizzling-torch-54.firebaseio.com/");
    }

    public void createUser(final User u,final Model.UserCreator next) {
        myFirebaseRef.createUser(u.getEmail(), u.getPassword(), new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                Firebase userRef = myFirebaseRef.child("users").child(result.get("uid").toString());
                userRef.setValue(u);
                next.onResult(u);
                System.out.println("Successfully created user account with uid: " + result.get("uid"));
            }
            @Override
            public void onError(FirebaseError firebaseError) {
                Log.d("TAG:", firebaseError.toString());
                next.onError(firebaseError.toString());
            }
        });
    }

    private String getObjectKey(String invalidKey)
    {
        String[] components = invalidKey.split(".");
        String str=components[0];
        for (int i=1;i<components.length;i++) {
            str+="_"+components[i];
        }
        return str;
    }
    public String getUpdateDate()
    {
        //child = users.
        //key = email.
        Firebase  stRef = myFirebaseRef.child("users").child(Model.instance().getCurrentUid());
        stRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TAG", "DataSnapShot: " + dataSnapshot);
                User user = dataSnapshot.getValue(User.class);
                Log.d("TAG", "User have been changed : " + user.toString());
                //checking if date have been changed.

                if(!user.getLastUpdateDate().equals(Model.instance().getUpdateDate()))
                {
                    Model.instance().updateUserByEmail(user.getEmail(),user);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("TAG", "The read failed: " + firebaseError.getMessage());
            }
        });


            /*Log.d("TAG","in If!");
            Query queryLastDate = myFirebaseRef.child(child).child("email").equalTo(key);
            queryLastDate.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    Log.d("TAG","DataSnapShot: "+ dataSnapshot);
                    User user = dataSnapshot.getValue(User.class);
                    Log.d("TAG","User have been changed : "+ user.toString());
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.d("TAG","The read failed: " + firebaseError.getMessage());
                }
            });*/
            //Firebase stRef = myFirebaseRef.child(child).child("email");
        return "false";
    }

    public void authenticate(String email, String pwd,Firebase.AuthResultHandler auth) {
            myFirebaseRef.authWithPassword(email, pwd, auth);
    }



    public boolean isAuthenticated() {
        return myFirebaseRef.getAuth()!=null;
    }
}

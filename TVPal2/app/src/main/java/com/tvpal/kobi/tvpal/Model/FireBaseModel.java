package com.tvpal.kobi.tvpal.Model;

import android.content.Context;
import android.util.Log;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static com.tvpal.kobi.tvpal.Model.Reversed.reversed;

/**
 * Created by Kobi on 03/06/2016.
 */
public class FireBaseModel {

    public interface eventsComplitionListener
    {
        public void onComplete(LinkedList<Post> o);
        public void onError(String error);
    }

    public interface TVShowComplitionListener
    {
        public void onComplete(TVShow show);
        public void onError(String error);
    }

    public interface PostComplitionListener
    {
        public void onComplete(Post post);
        public void onError(String error);
    }

    public interface userEventsComplitionListener
    {
        public void onComplete(User u);
        public void onError(String error);
    }

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

    public void updateUser(String uid, User user,Firebase.CompletionListener completionlistener)
    {
        Firebase  stRef = myFirebaseRef.child("users").child(Model.instance().getCurrentUid());
        stRef.updateChildren(user.getUserMap(), completionlistener);
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

                if(!user.getLastUpdateDate().equals(Model.instance().getCurrentUser().getLastUpdateDate()))
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
    public void getAllPostsPerUser(String email,final eventsComplitionListener eventscomplitionlistener)
    {
        Query qr = myFirebaseRef.child("Post").orderByChild("userEmail").equalTo(email);
        qr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                LinkedList<Post> posts= new LinkedList<Post>();
                System.out.println("There are " + snapshot.getChildrenCount() + " posts");
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    posts.add(post);
                    System.out.println(post.getShowName() + " - " + post.getUserEmail());
                }
                eventscomplitionlistener.onComplete(posts);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
                eventscomplitionlistener.onError(firebaseError.toString());
            }
        });
    }

    public void getAllPostsPerUserUniq(String email,final eventsComplitionListener eventscomplitionlistener)
    {
        Query qr = myFirebaseRef.child("Post").orderByChild("userEmail").equalTo(email);
        qr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                HashMap<String,Post> finalMap = new HashMap<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren() ){
                    Post post = postSnapshot.getValue(Post.class);

                    if(!finalMap.containsKey(post.showName))
                        finalMap.put(post.showName,post);
                    else{
                        Post firstPost  = finalMap.get(post.showName);
                        if(!Model.Constant.isBiggerDate(firstPost.date,post.date)){
                            finalMap.put(post.showName,post);
                        }
                    }

                    System.out.println(post.getShowName() + " - " + post.getUserEmail());
                }
                eventscomplitionlistener.onComplete(new LinkedList<Post>(finalMap.values()));
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
                eventscomplitionlistener.onError(firebaseError.toString());
            }
        });
    }

    public void getUserByEmailAsync(String email,final userEventsComplitionListener userEventscomplitionlistener)
    {
        Query qr = myFirebaseRef.child("users").orderByChild("email").equalTo(email);
        qr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TAG", "DataSnapShot: " + dataSnapshot);
                User user = dataSnapshot.getValue(User.class);
                Log.d("TAG", "User have been changed : " + user.toString());
                //checking if date have been changed.
                userEventscomplitionlistener.onComplete(user);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("TAG", "The read failed: " + firebaseError.getMessage());
            }
        });
    }

    public void getAllPostsAsync(final eventsComplitionListener eventscomplitionlistener)
    {
        Query qr = myFirebaseRef.child("Post");
        qr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                LinkedList<Post> posts= new LinkedList<Post>();
                System.out.println("There are " + snapshot.getChildrenCount() + " posts");
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    posts.add(post);
                    System.out.println(post.getShowName() + " - " + post.getUserEmail());
                }
                eventscomplitionlistener.onComplete(posts);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
                eventscomplitionlistener.onError(firebaseError.toString());
            }
        });
    }

    public void getPostsByShowNameAsync(final String showName, final eventsComplitionListener eventscomplitionlistener)
    {
        Query qr = myFirebaseRef.child("Post").orderByChild("showName").equalTo(showName);
        qr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                LinkedList<Post> posts= new LinkedList<Post>();
                System.out.println("There are " + snapshot.getChildrenCount() + " posts with showName"+ showName);
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    posts.add(post);
                }
                eventscomplitionlistener.onComplete(posts);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
                eventscomplitionlistener.onError(firebaseError.toString());
            }
        });
    }

    public void getShowByNameAsync(final String showName, final TVShowComplitionListener tvShowComplitionListener)
    {
        myFirebaseRef.child("TVShows").child(showName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TVShow show = dataSnapshot.getValue(TVShow.class);
                tvShowComplitionListener.onComplete(show);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                tvShowComplitionListener.onError(firebaseError.toString());
            }
        });
    }

    public void getPostByParamsAsync(final String showName,final String date, final String text,final PostComplitionListener postComplitionListener)
    {
        myFirebaseRef.child("Post").child(showName+"_"+date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                postComplitionListener.onComplete(post);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                postComplitionListener.onError(firebaseError.toString());
            }
        });
    }

    public void createShow(final TVShow show,final Post post,final Model.ShowCreator showCreator)
    {
        myFirebaseRef.child("TVShows").child(show.getName()).setValue(show);
        myFirebaseRef.child("Post").child(post.getShowName()+"_"+post.getDate()).setValue(post);
        showCreator.Create();

    }

    public void createPost(final Post post,final PostComplitionListener postComplitionListener)
    {
        try{
            myFirebaseRef.child("Post").child(post.getShowName()+"_"+post.getDate()).setValue(post);
            postComplitionListener.onComplete(post);
        }catch (Exception e)
        {
            e.printStackTrace();
            postComplitionListener.onError(e.toString());
        }

    }


    public void authenticate(String email, String pwd,Firebase.AuthResultHandler auth) {
            myFirebaseRef.authWithPassword(email, pwd, auth);
    }



    public boolean isAuthenticated() {
        return myFirebaseRef.getAuth()!=null;
    }
}

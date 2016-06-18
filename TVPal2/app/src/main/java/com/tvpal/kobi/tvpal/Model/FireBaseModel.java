package com.tvpal.kobi.tvpal.Model;

import android.content.Context;
import android.util.Log;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Kobi on 03/06/2016.
 */
public class FireBaseModel {

    public interface eventsCompletionListener
    {
        public void onComplete(LinkedList<Post> o);
        public void onError(String error);
    }

    public interface TVShowCompletionListener
    {
        public void onComplete(TVShow show);
        public void onError(String error);
    }

    public interface PostCompletionListener
    {
        public void onComplete(Post post);
        public void onError(String error);
    }

    public interface userEventsCompletionListener
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
                myFirebaseRef.child("users").child(result.get("uid").toString()).setValue(u);
                next.onResult(u);
            }
            @Override
            public void onError(FirebaseError firebaseError) {
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

    public void getAllPostsPerUser(String email,final eventsCompletionListener eventscomplitionlistener)
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

    public void getAllPostsPerUserUniq(String email,final eventsCompletionListener eventscomplitionlistener)
    {
        getAllPostsPerUser(email, new eventsCompletionListener() {
            @Override
            public void onComplete(LinkedList<Post> o) {
                HashMap<String,Post> finalMap = new HashMap<String,Post>();
                for(Post p: o){
                    if(!finalMap.containsKey(p.getShowName()))
                        finalMap.put(p.getShowName(),p);
                    else{
                        if(!Model.Constant.isBiggerDate(finalMap.get(p.getShowName()).getDate(),p.getDate())){
                            finalMap.remove(p.getShowName());
                            finalMap.put(p.getShowName(),p);
                        }
                    }
                }
                eventscomplitionlistener.onComplete(new LinkedList<Post>(finalMap.values()));
            }

            @Override
            public void onError(String error) {
                eventscomplitionlistener.onError(error);
            }
        });
    }

    public void getUserByEmailAsync(String email,final userEventsCompletionListener userEventscomplitionlistener)
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

    public void getAllPostsAsync(final eventsCompletionListener eventscomplitionlistener)
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

    public void getPostsByShowNameAsync(final String showName, final eventsCompletionListener eventscomplitionlistener)
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

    public void getShowByNameAsync(final String showName, final TVShowCompletionListener tvShowComplitionListener)
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

    public void getPostByParamsAsync(final String showName,final String date, final String text,final PostCompletionListener postCompletionListener)
    {
        myFirebaseRef.child("Post").child(showName+"_"+date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                postCompletionListener.onComplete(post);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                postCompletionListener.onError(firebaseError.toString());
            }
        });
    }

    public void createShow(final TVShow show,final Post post,final Model.ShowCreator showCreator)
    {
        myFirebaseRef.child("TVShows").child(show.getName()).setValue(show);
        myFirebaseRef.child("Post").child(post.getShowName()+"_"+post.getDate()).setValue(post);
        showCreator.Create();

    }

    public void createPost(final Post post,final PostCompletionListener postCompletionListener)
    {
        try{
            myFirebaseRef.child("Post").child(post.getShowName()+"_"+post.getDate()).setValue(post);
            postCompletionListener.onComplete(post);
        }catch (Exception e)
        {
            e.printStackTrace();
            postCompletionListener.onError(e.toString());
        }

    }


    public void authenticate(String email, String pwd,Firebase.AuthResultHandler auth) {
            myFirebaseRef.authWithPassword(email, pwd, auth);
    }



    public boolean isAuthenticated() {
        return myFirebaseRef.getAuth()!=null;
    }
}

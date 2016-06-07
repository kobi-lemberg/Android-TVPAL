package com.tvpal.kobi.tvpal.Model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.tvpal.kobi.tvpal.Model.SQL.ModelSql;
import com.tvpal.kobi.tvpal.MyApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by Kobi on 11/05/2016.
 */
public class Model {
    String currentUid;
    interface UserCreator{
        public void onResult(User u);
        public void onError(String err);
    }

    public interface UserCreatorListener{
        public void onResult(User u);
        public void onError(String err);
    }

    public interface AuthenticateListener{
        public void onAuthenticateResult(User u);
        public void onAuthenticateError(String err);
    }


    private final static Model instance = new Model();
    Context context;
    ModelSql modelSql;
    User currentUser=null;
    FireBaseModel modelFireBase;
    ModelCloudinary modelCloudinary;

    private Model(){
        context = MyApplication.getAppContext();
        modelSql = new ModelSql();
        modelFireBase = new FireBaseModel(context);
        modelCloudinary = new ModelCloudinary();
    }

    public static Model instance(){return instance;}


    public void addUser(final User user, final Bitmap profilePic, final UserCreatorListener creatorListener ){
        modelFireBase.createUser(user, new UserCreator() {
            @Override
            public void onResult(User u) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {modelCloudinary.saveImage(profilePic,user.getProfilePic());}
                });
                t.start();
                modelSql.addUser(user); //Add to SQL
                saveImageToFile(profilePic,user.getProfilePic()); //Save profile Pic to cache
                creatorListener.onResult(user);
            }
            @Override
            public void onError(String err) {
                Log.d("ERROR!!!",err);
                creatorListener.onError(err);
            }
        });
    }

    public Boolean authenticate(final String userName, final String password, final AuthenticateListener authenticateListener)
    {
        if(MyApplication.isConnectedToNetwork()){
            Log.d("TAG:", "You have Internet, lets test your credentials via FireBase");
            modelFireBase.authenticate(userName, password, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    Log.d("TAG:","Authenticated via fireBase\n"+modelFireBase.myFirebaseRef.getAuth().toString()+"\nfetching current user");
                    User current = modelSql.authenticate(userName,password);
                    if(current!=null) {
                        Log.d("TAG:","Current USER: "+current.toString());
                        setCurrentUser(current,modelFireBase.myFirebaseRef.getAuth().getUid());
                        authenticateListener.onAuthenticateResult(current);
                    }
                }
                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    Log.d("TAG:","Authentication error via FireBase");
                    authenticateListener.onAuthenticateError(firebaseError.toString());
                }
            });
        }
        else {
            Log.d("TAG:","You dont have Intenet, Trying to fetch from SQL: "+userName+", "+ password);
            User current = modelSql.authenticate(userName,password);
            if(current!=null)
            {
                Log.d("TAG:"," User current: "+current.toString());
                setCurrentUser(current, modelFireBase.myFirebaseRef.getAuth().getUid());
                return true;
            }
            else return false;
        }
        return currentUser!=null;
    }


    public User getUserByEmail(String email){return modelSql.getUserByEmail(email);}
    public List<User> getAllUsers(){return modelSql.getAllUsers();}
    public void deleteUser(User u){modelSql.delete(u);}
    public void updateUserByEmail(final String email,final User updated)
    {
        modelFireBase.updateUser(getCurrentUid(), updated, new Firebase.CompletionListener() {
            @Override public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                modelSql.updateUserByID(email,updated);
                setCurrentUser(updated,getCurrentUid());
            }});}

    public User getCurrentUser(){
        if(modelFireBase.isAuthenticated())
            return this.currentUser;
        return null;
    }

    private void setCurrentUser(User usr,String id){
        this.currentUser=usr;
        this.currentUid = id;
    }
    public String getCurrentUid(){
        return currentUid;
    }

    public String getUpdateDate()
    {
        return modelFireBase.getUpdateDate();
    }

    private void saveImageToFile(Bitmap imageBitmap, String imageFileName){
        FileOutputStream fos;
        OutputStream out = null;
        try {
            //File dir = context.getExternalFilesDir(null);
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File imageFile = new File(dir,imageFileName);
            imageFile.createNewFile();

            out = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

            //add the picture to the gallery so we dont need to manage the cache size
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(imageFile);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
            Log.d("tag","add image to cache: " + imageFileName);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

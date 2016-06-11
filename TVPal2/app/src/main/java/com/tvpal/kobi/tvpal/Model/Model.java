package com.tvpal.kobi.tvpal.Model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.tvpal.kobi.tvpal.Model.SQL.ModelSql;
import com.tvpal.kobi.tvpal.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
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

    public interface ShowCreator{
        public void Create();
    }

    public interface UserUpdater{
        public void onDone();
    }

    public interface showCreatorListener{
        public void onDone();
        public void onError(String error);
    }

    public interface eventPostsListener{
        public void onResult(LinkedList<Post> o);
        public void onError(String error);
    }

    public interface UserCreatorListener{
        public void onResult(User u);
        public void onError(String err);
    }

    public interface AuthenticateListener{
        public void onAuthenticateResult(User u);
        public void onAuthenticateError(String err);
    }

    public interface LoadImageListener{
        public void onResult(Bitmap imageBmp);
    }
    interface UploadImageListener{
        public void onResult();
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

    public boolean isDefaultProfilePic(String picName){
        return picName.equals("defaultProfilePic");
    }
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

    public void loadImage(final String imageName, final LoadImageListener listener) {
        AsyncTask<String,String,Bitmap> task = new AsyncTask<String, String, Bitmap >() {
            @Override
            protected Bitmap doInBackground(String... params) {
                Bitmap bmp = loadImageFromFile(imageName);              //first try to fin the image on the device
                if (bmp == null) {                                      //if image not found - try downloading it from parse
                    bmp = modelCloudinary.loadImage(imageName);
                    if (bmp != null) saveImageToFile(bmp,imageName);    //save the image locally for next time
                }
                return bmp;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                listener.onResult(result);
            }
        };
        task.execute();
    }

    public void uploadImageAsync(final Bitmap bitmap ,final String imageName, final UploadImageListener listener) {
        modelCloudinary.saveImage(bitmap,imageName);
        listener.onResult();
/*

        AsyncTask<String,String,String> task = new AsyncTask<String, String, String >() {
            @Override
            protected String doInBackground(String... params) {

                return "saved";
            }

            @Override
            protected void onPostExecute(String result) {
                listener.onResult();
            }
        };
        task.execute();*/
    }

    public User getUserByEmail(String email){return modelSql.getUserByEmail(email);}
    public List<User> getAllUsers(){return modelSql.getAllUsers();}
    public void deleteUser(User u){modelSql.delete(u);}
    public void updateUserByEmailWithPic(final String email, final User updated, final Bitmap profilePic, final UserUpdater listener)
    {
        modelFireBase.updateUser(getCurrentUid(), updated, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {
                    uploadImageAsync(profilePic, updated.getProfilePic(), new UploadImageListener() {
                        @Override
                        public void onResult() {
                            modelSql.updateUserByID(email, updated);
                            saveImageToFile(profilePic, updated.getProfilePic());

                            setCurrentUser(updated, getCurrentUid());
                            listener.onDone();
                        }
                    });
                }
            }
        });
    }


    public void updateUserByEmail(final String email, final User updated)
    {
        modelFireBase.updateUser(getCurrentUid(), updated, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                modelSql.updateUserByID(email, updated);
                setCurrentUser(updated, getCurrentUid());

            }
        });

    }

    public void createShow(final Bitmap imageBitMap,final TVShow show,Post post ,final showCreatorListener showCreatorListener){
        modelFireBase.createShow(show, post, new ShowCreator() {
            @Override
            public void Create() {
                try {
                    if (show.getImagePath() != "default_show_pic") {
                        modelCloudinary.saveImage(imageBitMap, show.getImagePath());
                    }
                    showCreatorListener.onDone();
                } catch (Exception e) {
                    showCreatorListener.onError(e.toString());
                }
            }
        });
    }

    public void getAllPostsPerUser(String email, final eventPostsListener eventpostslistener)
    {
        modelFireBase.getAllPostsPerUser(email, new FireBaseModel.eventsComplitionListener() {
            @Override
            public void onComplete(LinkedList<Post> o) {
                eventpostslistener.onResult(o);
            }

            @Override
            public void onError(String error) {
                eventpostslistener.onError(error);
                Log.d("Error","could not read from firebase.");
            }
        });
    }

    public User getCurrentUser(){
        if(modelFireBase.isAuthenticated())
            return this.currentUser;
        return null;
    }

    private void setCurrentUser(User usr,String id){
        this.currentUser=usr;
        this.currentUid = id;
        Log.d("TAG","Setted user at model:" +usr.toString());
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

    public Bitmap loadImageFromFile(String imageFileName){
        String str = null;
        Bitmap bitmap = null;
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(dir,imageFileName);

            //File dir = context.getExternalFilesDir(null);
            InputStream inputStream = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(inputStream);
            Log.d("tag","got image from cache: " + imageFileName);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


}

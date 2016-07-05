package com.tvpal.kobi.tvpal.Model;

import android.content.Context;
import android.content.Intent;
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.tvpal.kobi.tvpal.Model.SQL.LastUpdateSql;
import com.tvpal.kobi.tvpal.Model.SQL.ModelSql;
import com.tvpal.kobi.tvpal.Model.SQL.TVShowSql;
import com.tvpal.kobi.tvpal.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;


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

    public interface ShowListener{
        public void onDone(ArrayList<TVShow> shows);
        public void onError(String error);
    }

    public interface EventPostsListener{
        public void onResult(LinkedList<Post> o);
        public void onError(String error);
    }

    public interface TVShowListener{
        public void onResult(TVShow show);
        public void onError(String error);
    }

    public interface PostListener{
        public void onResult(Post post);
        public void onError(String error);
    }

    public interface UserEventPostsListener{
        public void onResult(User u);
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


    public void addUser(final User user, final Bitmap profilePic, final UserCreatorListener creatorListener ){
        modelFireBase.createUser(user, new UserCreator() {
            @Override
            public void onResult(User u) {
                if(!Constant.isDefaultProfilePic(u.getProfilePic())) {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {modelCloudinary.saveImage(profilePic,user.getProfilePic());}
                    });
                    t.start();
                    saveImageToFile(profilePic,user.getProfilePic()); //Save profile Pic to cache
                }
                setLastUpdateDateOnFB(Constant.showsTable,u.getLastUpdateDate());
                modelSql.addUser(user); //Add to SQL
                creatorListener.onResult(user);
            }
            @Override
            public void onError(String err) {
                Log.d("ERROR!!!",err);
                creatorListener.onError(err);
            }
        });
    }

    public Boolean authenticate(final String userName, final String password, final AuthenticateListener authenticateListener) {
        if(MyApplication.isConnectedToNetwork()){
            Log.d("TAG:", "You have Internet, lets test your credentials via FireBase");
            modelFireBase.authenticate(userName, password, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    Log.d("TAG:","Authenticated via fireBase\n"+modelFireBase.myFirebaseRef.getAuth().toString()+"\nfetching current user");
                    getUserByEmailFromNet(userName, new UserEventPostsListener() {
                        @Override
                        public void onResult(User u) {
                            Log.d("TAG","ON resault");
                            modelSql.updateUserByID(u.getEmail(),u);
                            setCurrentUser(u,modelFireBase.myFirebaseRef.getAuth().getUid());
                            authenticateListener.onAuthenticateResult(u);
                        }

                        @Override
                        public void onError(String error) {Log.d("TAG","WHEN TRYING TO GET USERNAME IN AUTHENTICATION "+error);}
                    });
                }
                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    Log.d("TAG:","Authentication error via FireBase" +firebaseError.toString() );
                    authenticateListener.onAuthenticateError(firebaseError.toString());
                }
            });
        }
        else {
            Log.d("TAG:","You dont have Intenet, Trying to fetch from SQL: "+userName+", "+ password);
            User current = modelSql.authenticate(userName,password);
            if(current!=null) {
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
                Bitmap bmp = null;
                try {bmp = loadImageFromFile(imageName);}catch (Exception e){}
                if(bmp==null) {
                    Log.d("TAG","no file, run loadImageFromCloudinary");
                    bmp = modelCloudinary.loadImage(imageName);
                    if(bmp==null){
                        Log.d("TAG","In catch, cloudinary didnt do job so return null");
                        return bmp;
                    }
                    else {
                        saveImageToFile(bmp,imageName);
                        return bmp;
                    }
                }
                else{
                    Log.d("TAG","Bitmap is from cache ");
                    return bmp;
                }

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
        saveImageToFile(bitmap,imageName);
        listener.onResult();
    }

    public void setLastUpdateDateOnFB(final String table, final String date){
        modelFireBase.getLastUpdateDate(table, new FireBaseModel.UpdateDateCompletionListener() {
            @Override
            public void onComplete(String updateDate) {if(Constant.isBiggerDate(date,updateDate)) modelFireBase.setLastUpdateDate(table, date);}

            @Override
            public void onError(String error) {Log.d("TAG",error);}
        });
    }


    public void getUserByEmail(final String email, final UserEventPostsListener userEventPostsListener){
        if(email.equals(Model.instance().getCurrentUser().getEmail()))
            userEventPostsListener.onResult(Model.instance().getCurrentUser());
        else {
            if(MyApplication.isConnectedToNetwork()) {
                final String updated = modelSql.getLastUpdate(Constant.usersTable);
                if(updated!=null&&updated!=""){
                    modelFireBase.getLastUpdateDate(Constant.usersTable, new FireBaseModel.UpdateDateCompletionListener() {
                        @Override
                        public void onComplete(String updateDate) {
                            if(Constant.isBiggerDate(updateDate,updated)){
                                getUserByEmailFromNet(email, new UserEventPostsListener() {
                                    @Override
                                    public void onResult(User u) {
                                        if(Constant.isBiggerDate(u.getLastUpdateDate(),updated)) modelSql.updateUserByID(u.email,u);
                                        userEventPostsListener.onResult(u);
                                    }

                                    @Override
                                    public void onError(String error) {
                                        User u =  modelSql.getUserByEmail(email);
                                        if (u!=null) userEventPostsListener.onResult(u);
                                        else userEventPostsListener.onError(error);
                                    }
                                });
                            }
                            else{
                                User u =  modelSql.getUserByEmail(email);
                                if (u!=null) userEventPostsListener.onResult(u);
                                else userEventPostsListener.onError("ERORR");
                            }

                        }

                        @Override
                        public void onError(String error) {
                            User u =  modelSql.getUserByEmail(email);
                            if (u!=null) userEventPostsListener.onResult(u);
                            else userEventPostsListener.onError(error);
                        }
                    });
                }
                else{
                    getUserByEmailFromNet(email, new UserEventPostsListener() {
                        @Override
                        public void onResult(User u) {
                            if(Constant.isBiggerDate(u.getLastUpdateDate(),modelSql.getLastUpdate(Constant.usersTable))) modelSql.updateUserByID(u.email,u);
                            userEventPostsListener.onResult(u);
                        }

                        @Override
                        public void onError(String error) {
                            User u =  modelSql.getUserByEmail(email);
                            if (u!=null) userEventPostsListener.onResult(u);
                            else userEventPostsListener.onError(error);
                        }
                    });
                }


            }
            else {
                User u =  modelSql.getUserByEmail(email);
                if (u!=null) userEventPostsListener.onResult(u);
                else userEventPostsListener.onError("no internet and no user");
            }

        }
    }

    public void getUserByEmailFromNet(String email, final UserEventPostsListener userEventPostsListener){
            modelFireBase.getUserByEmailAsync(email, new FireBaseModel.userEventsCompletionListener() {
                @Override
                public void onComplete(User u) {
                    Log.d("TAG","TEST on complete getUserByEmailFromNet");
                    userEventPostsListener.onResult(u);
                }

                @Override
                public void onError(String error) {
                    userEventPostsListener.onError(error);
                }
            });
    }




    public void updateUserByEmailWithPic(final String email, final User updated, final Bitmap profilePic, final UserUpdater listener)
    {
        modelFireBase.updateUser(getCurrentUid(), updated, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {
                    uploadImageAsync(profilePic, updated.getProfilePic(), new UploadImageListener() {
                        @Override
                        public void onResult() {
                            saveImageToFile(profilePic, updated.getProfilePic());
                            listener.onDone();
                        }
                    });
                    modelFireBase.setLastUpdateDate(Constant.usersTable, updated.getLastUpdateDate());
                    modelSql.updateUserByID(email, updated);
                    setCurrentUser(updated, getCurrentUid());
                }
            }
        });


    }


    public void updateUserByEmail(final String email, final User updated,final UserUpdater listener) {
        modelFireBase.updateUser(getCurrentUid(), updated, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if(firebaseError==null) {
                    modelSql.updateUserByID(email, updated);
                    setCurrentUser(updated, getCurrentUid());
                    modelFireBase.setLastUpdateDate(Constant.usersTable, updated.getLastUpdateDate());
                    listener.onDone();
                }
                else Log.d("TAG","UPDATE ERROR");
            }
        });

    }

    public void createShow(final Bitmap imageBitMap, final TVShow show, final Post post , final showCreatorListener showCreatorListener){
        modelFireBase.createShow(show, post, new ShowCreator() {
            @Override
            public void Create() {
                try {
                    if (!Constant.isDefaultShowPic(show.getImagePath())){
                        uploadImageAsync(imageBitMap, show.getImagePath(), new UploadImageListener() {
                            @Override
                            public void onResult() {}
                        });
                    }

                    modelFireBase.setLastUpdateDate(Constant.showsTable, post.getDate());
                    modelFireBase.setLastUpdateDate(Constant.postsTable, post.getDate());
                    showCreatorListener.onDone();
                } catch (Exception e) {showCreatorListener.onError(e.toString());}
            }
        });
    }

    public void addPost(Post post ,final PostListener PostListener){
        modelFireBase.createPost(post, new FireBaseModel.PostCompletionListener() {
            @Override
            public void onComplete(Post post) {
                modelFireBase.setLastUpdateDate(Constant.postsTable, post.getDate());

                PostListener.onResult(post);
            }

            @Override
            public void onError(String error) {
                PostListener.onError(error);
            }
        });
    }

    public void getAllPostsPerUser(final String email, final EventPostsListener eventpostslistener)
    {
            final String lastDate = modelSql.getLastUpdate(Constant.postsTable);
            modelFireBase.getLastUpdateDate(Constant.postsTable, new FireBaseModel.UpdateDateCompletionListener() {
                @Override
                public void onComplete(String updateDate) {
                    if(!Constant.isBiggerDate(updateDate,lastDate)){
                        Log.d("TAG","getAllPostsPerUser from SQL");
                        eventpostslistener.onResult(modelSql.getAllPostsPerUser(email));
                    }
                    else{
                        Log.d("TAG","getAllPostsPerUser from fireBase");
                        modelFireBase.getAllPostsPerUser(email, new FireBaseModel.eventsCompletionListener() {
                            @Override
                            public void onComplete(LinkedList<Post> o) {
                                for(Post p: o){if(Constant.isBiggerDate(p.getDate(),lastDate)) modelSql.addPost(p);}
                                eventpostslistener.onResult(o);

                            }

                            @Override
                            public void onError(String error) {
                                Log.d("Error","could not read from firebase." + error);
                                eventpostslistener.onError(error);
                            }
                        });
                    }
                }

                @Override
                public void onError(String error) {
                    Log.d("TAG","ERROR FETCHING DATE-TRYING AGAIN");
                    modelFireBase.getAllPostsPerUser(email, new FireBaseModel.eventsCompletionListener() {
                        @Override
                        public void onComplete(LinkedList<Post> o) {
                            for(Post p: o){if(Constant.isBiggerDate(p.getDate(),lastDate)) modelSql.addPost(p);}
                            eventpostslistener.onResult(o);

                        }

                        @Override
                        public void onError(String error) {
                            Log.d("Error","could not read from firebase.");
                            eventpostslistener.onError(error);

                        }
                    });
                }
            });
        //}
    }

    public void getAllPostsPerUserUniq(final String email, final EventPostsListener eventpostslistener)
    {
        final String lastDate = modelSql.getLastUpdate(Constant.postsTable);
            modelFireBase.getLastUpdateDate(Constant.postsTable, new FireBaseModel.UpdateDateCompletionListener() {
                @Override
                public void onComplete(String updateDate) {
                    if(!Constant.isBiggerDate(updateDate,lastDate)) eventpostslistener.onResult(modelSql.getAllPostsPerUserUniq(email));
                    else{
                        modelFireBase.getAllPostsPerUserUniq(email, new FireBaseModel.eventsCompletionListener() {
                            @Override
                            public void onComplete(LinkedList<Post> o) {
                                eventpostslistener.onResult(o);
                                for(Post p: o){if(Constant.isBiggerDate(p.getDate(),lastDate)) modelSql.addPost(p);}
                            }

                            @Override
                            public void onError(String error) {
                                Log.d("Error","could not read from firebase." + error);
                                eventpostslistener.onError(error);
                            }
                        });
                    }
                }

                @Override
                public void onError(String error) {
                    Log.d("TAG","ERROR FETCHING DATE-TRYING AGAIN");
                    modelFireBase.getAllPostsPerUserUniq(email, new FireBaseModel.eventsCompletionListener() {
                        @Override
                        public void onComplete(LinkedList<Post> o) {
                            eventpostslistener.onResult(o);
                            for(Post p: o){if(Constant.isBiggerDate(p.getDate(),lastDate)) modelSql.addPost(p);}
                        }

                        @Override
                        public void onError(String error) {
                            eventpostslistener.onError(error);
                            Log.d("Error","could not read from firebase.");
                        }
                    });
                }
            });
    }

    public void getAllPosts(final EventPostsListener eventpostslistener)
    {
        final String updated = modelSql.getLastUpdate(Constant.postsTable);
        modelFireBase.getLastUpdateDate(Constant.postsTable, new FireBaseModel.UpdateDateCompletionListener() {
            @Override
            public void onComplete(final String updateDate) {
                if(Constant.isBiggerDate(updateDate,updated)){
                    modelFireBase.getAllPostsAsync(new FireBaseModel.eventsCompletionListener() {
                        @Override
                        public void onComplete(LinkedList<Post> o) {
                            eventpostslistener.onResult(o);
                            for (Post p:o){if(Constant.isBiggerDate(p.getDate(),updated))
                                Log.d("TAG",p.getDate()+"is greater then "+updated+" adding to sql");
                                modelSql.addPost(p);
                            }
                            modelSql.setLastUpdate(Constant.postsTable,updateDate);

                        }
                        @Override
                        public void onError(String error) {eventpostslistener.onError(error);}
                    });
                }
                else eventpostslistener.onResult(modelSql.getAllPosts());
            }

            @Override
            public void onError(String error) {
                Log.d("TAG","DATE ERROR "+error);
                modelFireBase.getAllPostsAsync(new FireBaseModel.eventsCompletionListener() {
                    @Override
                    public void onComplete(LinkedList<Post> o) {
                        eventpostslistener.onResult(o);
                        for (Post p:o){if(Constant.isBiggerDate(p.getDate(),updated))
                            modelSql.setLastUpdate(Constant.postsTable,p.getDate());
                            modelSql.addPost(p);}
                    }
                    @Override
                    public void onError(String error) {eventpostslistener.onError(error);}
                });
            }
        });
    }

    public void getAutoCompletePosts(final String email, final ShowListener showListener){
        final String updated = modelSql.getLastUpdate(Constant.postsTable);
        modelFireBase.getLastUpdateDate(Constant.postsTable, new FireBaseModel.UpdateDateCompletionListener() {
            @Override
            public void onComplete(String updateDate) {
                if(Constant.isBiggerDate(updateDate,updated)){
                    modelFireBase.getAllNoneIncludesShowsForUser(email, new FireBaseModel.AllTVShowsCompletionListener() {
                        @Override
                        public void onComplete(ArrayList<TVShow> show) {
                            showListener.onDone(show);
                        }

                        @Override
                        public void onError(String error) {
                            showListener.onDone(modelSql.getAllNoneIncludesShowsForUser(email));
                        }
                    });
                }
                else{
                    showListener.onDone(modelSql.getAllNoneIncludesShowsForUser(email));
                }
            }

            @Override
            public void onError(String error) {
                showListener.onDone(modelSql.getAllNoneIncludesShowsForUser(email));
            }
        });
    }

    public void logOut(){
        this.currentUser=null;
        this.currentUid=null;
        modelFireBase.logOut();

    }



    public void getPostsByShowNameAsync(final String showName, final EventPostsListener eventpostslistener)
    {
        final String updated = modelSql.getLastUpdate(Constant.postsTable);
        modelFireBase.getLastUpdateDate(Constant.postsTable, new FireBaseModel.UpdateDateCompletionListener() {
            @Override
            public void onComplete(final String updateDate) {
                if(!Constant.isBiggerDate(updateDate,updated)) eventpostslistener.onResult(modelSql.getPostsByShowNamw(showName));
                else{
                    Log.d("TAG","NEED FROM FB");
                    modelFireBase.getPostsByShowNameAsync(showName,new FireBaseModel.eventsCompletionListener() {
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
            }

            @Override
            public void onError(String error) {
                modelFireBase.getPostsByShowNameAsync(showName,new FireBaseModel.eventsCompletionListener() {
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
        });
    }

    public void getShowByNameAsync(String showName,final TVShowListener tvShowListener)
    {
        final TVShow s = modelSql.getShow(showName);
        if(s!=null) tvShowListener.onResult(s);
        else{
            modelFireBase.getShowByNameAsync(showName, new FireBaseModel.TVShowCompletionListener() {
            @Override
            public void onComplete(TVShow show) {
                tvShowListener.onResult(show);
                modelSql.addShow(show);
            }

            @Override
            public void onError(String error) {
                tvShowListener.onError(error);
            }
        });
        }

    }

    public void getPostByParamsAsync(final String showName, final String date, final String text, final PostListener postListener) {
        try{
            Post p = modelSql.getPostByParams(showName,date,text);
            postListener.onResult(p);
        }catch(NullPointerException e){
            modelFireBase.getPostByParamsAsync(showName, date, text, new FireBaseModel.PostCompletionListener() {
                @Override
                public void onComplete(Post post) {
                    postListener.onResult(post);
                }

                @Override
                public void onError(String error) {
                    postListener.onError(error);
                }
            });
        }
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

    private void saveImageToFile(Bitmap imageBitmap, String imageFileName){
        FileOutputStream fos;
        OutputStream out = null;
        try {
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
        Bitmap bitmap = null;
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(dir,imageFileName);

            if(!imageFile.exists())
                return null;
            InputStream inputStream = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(inputStream);
            Log.d("tag","got image from cache: " + imageFileName);

        } catch (FileNotFoundException e) {e.printStackTrace();}
        return bitmap;
    }

    public static class Constant{
        public static final int logOut = 500;
        public static final int backBtn = 501;
        public static final String postsTable = "Post";
        public static final String showsTable = "TVShows";
        public static final String usersTable = "users";
        public static final String lastUpdateTable = "last_update";
        public static final String defaultShowPic  = "default_show_pic";
        public static final String defaultProfilePic  = "defaultProfilePic";
        private static final DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        public static String getCurrentDate() {
            Date today = Calendar.getInstance().getTime();
            return df.format(today);
        }

        public static Boolean isDefaultShowPic(String profilePicPath) {return profilePicPath.equals(defaultShowPic);}

        public static boolean isDefaultProfilePic(String picName){
            Log.d("TAG","Profile pic: "+picName);
            Log.d("TAG","is default Profile pic?: "+picName.equals(defaultProfilePic));
            return picName.equals(defaultProfilePic);
        }

        public static String getDefaultShowPic() {return defaultShowPic;}

        public static String getDefaultProfilePic() {return defaultProfilePic;}

        public static boolean isBiggerDate(String date1, String date2){
            if(date1==null&&date2==null) return true;
            if(date1==null ) return false;
            if(date2==null) return true;
            long firstDate = new Long(date1);
            Log.d("TAG","after: "+firstDate);
            long lastDate = new Long(date2);
            return (firstDate>lastDate);
        }
    }


}

package com.tvpal.kobi.tvpal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.widget.Toast;

import com.tvpal.kobi.tvpal.Dialogs.StringDialogFragment;
import com.tvpal.kobi.tvpal.Fragments.LoginFragment;
import com.tvpal.kobi.tvpal.Fragments.RegisterFragment;
import com.tvpal.kobi.tvpal.Fragments.WelcomeFragment;
import com.tvpal.kobi.tvpal.Model.Model;
import com.tvpal.kobi.tvpal.Model.User;

import java.io.ByteArrayOutputStream;

public class WelcomeActivity extends Activity implements WelcomeFragment.WelcomeDelegate,LoginFragment.LoginDelegate,RegisterFragment.RegisterDelegate
{

    WelcomeFragment welcomeFragment;
    LoginFragment loginFragment;
    RegisterFragment registerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Model.instance().getCurrentUser()==null){
        setContentView(R.layout.activity_welcome);
        welcomeFragment = new WelcomeFragment();
        loginFragment  = new LoginFragment();
        registerFragment = new RegisterFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.welcome_fragment_container,welcomeFragment,"welcomeFragment");
        transaction.add(R.id.welcome_fragment_container,loginFragment,"loginFragment");
        transaction.add(R.id.welcome_fragment_container,registerFragment,"registerFragment");

            transaction.hide(loginFragment);
            transaction.hide(registerFragment);
            transaction.show(welcomeFragment);
            transaction.commit();
        }
        else
        {
            Log.d("TAG","CURRENT USER is not null SHOULD move to news feed");
            Intent intent = new Intent(getApplicationContext(),NewsFeedActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public void getRegister() {
        Log.d("WelcomeActivity","Should Open Register Fragment");
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        handleFragments(registerFragment,new Fragment[]{welcomeFragment,loginFragment});
    }

    @Override
    public void getLogin() {
        Log.d("WelcomeActivity","Should Open Register Fragment");
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        handleFragments(loginFragment,new Fragment[]{welcomeFragment,registerFragment});
    }

    @Override
    public void signIn(final String userName, String password) {
        Log.d("welcomeActivity","Should log in user:" + userName+"  ,password: "+ password);
        if(!userName.equals("")&&!password.equals("")&&userName!=null&&password!=null)
        {
            Model.instance().authenticate(userName.trim().toLowerCase(),password,new Model.AuthenticateListener() {
                @Override
                public void onAuthenticateResult(User u) {
                    Log.d("welcomeActivity", "DialogFragment    StringDialogFragment");
                    DialogFragment df = new StringDialogFragment();
                    ((StringDialogFragment)df).setStrToShow("Welcome "+userName+", Move to News Feed");
                    df.show(getFragmentManager(), "Success login");
                    Intent newsFeedActivityIntent = new Intent(getApplicationContext(), NewsFeedActivity.class);
                    startActivity(newsFeedActivityIntent);

                }

                @Override
                public void onAuthenticateError(String err) {
                    Toast toast = Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT);
                    toast.setText("Incorrect username or password.");
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    loginFragment.setProgressBarVisability(false);
                }
            });
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT);
            toast.setText("User name and password cannot be blank.");
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            loginFragment.setProgressBarVisability(false);
        }


    }

    @Override
    public void onBackPressed() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if(loginFragment.isVisible()) {
            handleFragments(welcomeFragment,new Fragment[]{loginFragment});
        }
        else if(registerFragment.isVisible()) {
            handleFragments(welcomeFragment,new Fragment[]{registerFragment});
        }
        else if (welcomeFragment.isVisible()) super.onBackPressed();
        else super.onBackPressed();

    }

    public void handleFragments(Fragment show, Fragment[] hide) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        for(Fragment f: hide) {
            if(f.isVisible())
                transaction.hide(f);
        }
        transaction.show(show);
        transaction.commit();
    }

    @Override
    public void handleCamera() {
        selectImage();
    }

    @Override
    public void signUp(String email, String password, String firstName, String lastName, String birthDate, Bitmap profilePic, String profilePicPath) {
        if(profilePicPath==null || profilePicPath.equals(""))
        {
            profilePic = BitmapFactory.decodeResource(getResources(),R.drawable.default_profile_pic);
            profilePicPath = "defaultProfilePic";
        }
        Log.d("TAG:","Add the following user to DB");
        Log.d("USER:",email);
        Log.d("PASS:",password);
        Log.d("firstName:",firstName);
        Log.d("lastName:",lastName);
        Log.d("birthDate:",birthDate);
        Log.d("profilePicPath:",profilePicPath);

        Model.instance().addUser(new User(email.trim().toLowerCase(), firstName.trim(), lastName.trim(), birthDate,password ,profilePicPath, MyApplication.getCurrentDate()), profilePic, new Model.UserCreatorListener() {
            @Override
            public void onResult(User u) {
                if(u!=null)
                {
                    Log.d("TAG:","USER was added successfully");
                    Toast toast = Toast.makeText(getApplicationContext(),"Congratulations "+u.getFirstName()+" " +u.getLastName(),Toast.LENGTH_SHORT);
                    //Toast toast = Toast.makeText(getApplicationContext(),"Congratulations "+u.displayName(),Toast.LENGTH_SHORT);
                    toast.setText("Moving to Login.");
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    getLogin();
                }
                else this.onError("User is null");
            }

            @Override
            public void onError(String err) {
                Log.d("TAG:","USER was not created");
                Toast toast = Toast.makeText(getApplicationContext(),"Error ",Toast.LENGTH_SHORT);
                toast.setText(err);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    private static final int REQUEST_CAMERA=1;
    private static final int SELECT_FILE=2;

    private void selectImage() {

        final CharSequence[] items;
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
        {
            items= new CharSequence[3];
            items[0] = "Take Photo";
            items[1] = "Choose from Library";
            items[2] = "Cancel";
        }
        else
        {
            items= new CharSequence[2];
            items[0] = "Choose from Library";
            items[1] = "Cancel";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                String fileName="Profile_Pic_"+MyApplication.getCurrentDate()+ ".jpg";
                this.registerFragment.setProfilePic(thumbnail,fileName);

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                //ivImage.setImageBitmap(bm);
                String fileName="Profile_Pic_"+MyApplication.getCurrentDate()+ ".jpg";
                this.registerFragment.setProfilePic(bm,fileName);
            }
        }
    }

/*    private void saveImageToFile(Bitmap imageBitmap, String imageFileName){
        Log.d("Writing the pic",imageFileName);
        FileOutputStream fos;
        FileOutputStream out = null;
        try {
            File dir = MyApplication.getAppContext().getExternalFilesDir(null);
            Log.d("DIR:",dir.toString());
            out = new FileOutputStream(new File(dir,imageFileName));
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap loadImageFromFile(String fileName){
        String str = null;
        Bitmap bitmap = null;
        try {
            File dir = getExternalFilesDir(null);
            InputStream inputStream = new FileInputStream(new File(dir,fileName));
            bitmap = BitmapFactory.decodeStream(inputStream);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }*/
}

package com.tvpal.kobi.tvpal;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.tvpal.kobi.tvpal.Model.Model;
import com.tvpal.kobi.tvpal.Model.User;

import java.io.ByteArrayOutputStream;

public class EditProfileActivity extends Activity
{
    private static final int REQUEST_CAMERA=1;
    private static final int SELECT_FILE=2;
    TextView firstName;
    TextView lastName;
    ImageView profilePic;
    User user = Model.instance().getCurrentUser();
    Button saveButton;
    Button cancelButton;
    Button addShowButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        firstName= (TextView) findViewById(R.id.activity_EditProfile_First_name);
        lastName = (TextView) findViewById(R.id.activity_EditProfile_Last_name);
        profilePic = (ImageView) findViewById(R.id.activity_Edit_profile_imageView);
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        addShowButton = (Button) findViewById(R.id.button_edit_activity_add_Show);
        addShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "Move to ADD SHOW ACTIVITY");
                Intent intent = new Intent(getApplicationContext(),AddShowActivity.class);
                startActivity(intent);
            }
        });



        //profilePic.setImageBitmap(user.getProfilePic());
        saveButton = (Button) findViewById(R.id.button_Save_edit_profile);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //    public User(String email, String firstName,String lastName, String birthDate, String password , String profilePic,String lastUpdated) {

                User changedUser = new User(user.getEmail(),firstName.getText().toString(),
                        lastName.getText().toString(), user.getBirthDate(),user.getPassword(),
                        user.getProfilePic(),MyApplication.getCurrentDate());
                Model.instance().updateUserByEmail(user.getEmail(),changedUser);

            }
        });

    }

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
                profilePic.setImageBitmap(thumbnail);
                user.setProfilePic(fileName);

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
                profilePic.setImageBitmap(bm);
                user.setProfilePic(fileName);
            }
        }
    }
}

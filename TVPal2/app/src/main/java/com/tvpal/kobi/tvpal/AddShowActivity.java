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
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.tvpal.kobi.tvpal.Model.Model;
import com.tvpal.kobi.tvpal.Model.Post;
import com.tvpal.kobi.tvpal.Model.TVShow;

import java.io.ByteArrayOutputStream;

public class AddShowActivity extends Activity {

    private static final int REQUEST_CAMERA=1;
    private static final int SELECT_FILE=2;
    ImageView showImage ;
    EditText showName;
    EditText famousActors;
    EditText numberOfEpisodes;
    EditText categories;
    EditText summary;
    Button save;
    Button cancel;
    TVShow show;
    EditText season;

    Post post;
    RatingBar ratingBar;
    String fileName = "default_show_pic";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_show);
        showImage = (ImageView)findViewById(R.id.activity_addShow_imageView);
        showName = (EditText)findViewById(R.id.activity_addShow_movieName);
        famousActors = (EditText) findViewById(R.id.activity_addShow_famousActors);
        numberOfEpisodes = (EditText) findViewById(R.id.activity_addShow_NumberOfEpisodes);
        categories = (EditText) findViewById(R.id.activity_addShow_Categories);
        summary = (EditText) findViewById(R.id.addShow_activity_summary);
        save = (Button) findViewById(R.id.activity_add_Show_Save);
        cancel = (Button) findViewById(R.id.activity_add_show_cancel);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        season = (EditText) findViewById(R.id.activity_addShow_NumberOfSeasons);
        showImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    public TVShow(String id, String name, String mainActor, int episodes, String category, String lastUpdated, String summery) {
                int episodes = new Integer(numberOfEpisodes.getText().toString().trim());
//    public TVShow(String name, String mainActor,int episode,int numOfChapters ,String category, String lastUpdated, String summery,String imagePath) {
                show = new TVShow(showName.getText().toString(),famousActors.getText().toString(),episodes,
                        categories.getText().toString(),MyApplication.getCurrentDate(),summary.getText().toString(),fileName);
                post = new Post(show.getName(), Model.instance().getCurrentUser().getEmail()
                        ,"",MyApplication.getCurrentDate(),new Integer(season.getText().toString().trim()),
                        0,false,ratingBar.getNumStars(),"Started",show.getImagePath());
                Log.d("TAG","Adding Post: "+ post.toString());
                Model.instance().createShow(((BitmapDrawable) showImage.getDrawable()).getBitmap(), show,post ,new Model.showCreatorListener() {
                    @Override
                    public void onDone() {
                        setResult(1);
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        Log.d("ERROR","Catch the Exception : "+error);
                    }
                });
                //add this show to showDB.
                //this user started watch this show.


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
                fileName="Profile_Pic_"+MyApplication.getCurrentDate()+ ".jpg";
                showImage.setImageBitmap(thumbnail);
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
                fileName="Profile_Pic_"+MyApplication.getCurrentDate()+ ".jpg";
                showImage.setImageBitmap(bm);
            }
        }
    }
}

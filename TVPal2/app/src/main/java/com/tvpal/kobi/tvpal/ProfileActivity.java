package com.tvpal.kobi.tvpal;

import android.app.Activity;
import android.content.Intent;
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.tvpal.kobi.tvpal.Model.Model;
import com.tvpal.kobi.tvpal.Model.Post;
import com.tvpal.kobi.tvpal.Model.User;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by nir on 05/06/2016.
 */
public class ProfileActivity  extends Activity{
    Button editProfile;
    List<Post> data = new LinkedList<Post>();
    TextView displayName;
    TextView email;
    ImageView profilePic;
    ProgressBar imageProgressBar;
    User user;
    Button addShowButton;
    ListView listView;
    CustomAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        user = Model.instance().getCurrentUser();
        setTitle(user.displayName());
        listView = (ListView) findViewById(R.id.listView_activity_profile);
        adapter = new CustomAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // Toast.makeText(getApplicationContext(), "item click " + position, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),UpdateShowProgressActivity.class);
                intent.putExtra("showName",data.get(position).getShowName());
                intent.putExtra("date",data.get(position).getDate());
                intent.putExtra("text",data.get(position).getText());
                startActivityForResult(intent,0);
            }
        });

        Model.instance().getAllPostsPerUserUniq(user.getEmail(), new Model.EventPostsListener() {
            @Override
            public void onResult(LinkedList<Post> o) {
                if(o!=null) {
                    data = o;
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String error) {
                Log.d("Error", "Error: " + error);
            }
        });
        Log.d("TAG", "In Profile activity.");
        Log.d("TAG","USER: "+user.toString());
        imageProgressBar = (ProgressBar) findViewById(R.id.UserImageProgressBar);

        displayName = (TextView) findViewById(R.id.activity_profile_name);
        email = (TextView) findViewById(R.id.activity_profile_Email);
        profilePic = (ImageView) findViewById(R.id.activity_profile_imageView);
        if(!Model.Constant.isDefaultProfilePic(user.getProfilePic())){
            imageProgressBar.setVisibility(View.VISIBLE);
            Model.instance().loadImage(user.getProfilePic(), new Model.LoadImageListener() {
                @Override
                public void onResult(Bitmap imageBmp) {
                    if(imageBmp!=null) profilePic.setImageBitmap(imageBmp);
                    imageProgressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
        //put the variables.
        displayName.setText(user.displayName());
        email.setText(user.getEmail());
        addShowButton = (Button) findViewById(R.id.button_profile_activity_add_Show);
        addShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddShowActivity.class);
                startActivityForResult(intent,0);
            }
        });
        editProfile = (Button) findViewById(R.id.edit_profile_button);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivityForResult(intent,0);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Model.instance().getAllPostsPerUserUniq(user.getEmail(), new Model.EventPostsListener() {
            @Override
            public void onResult(LinkedList<Post> o) {
                if(o!=null) {
                    data = o;
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String error) {
                Log.d("Error", "Error: " + error);
            }
        });
        updateUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.go_to_news_feed_from_menu:
                intent = new Intent(getApplicationContext(), NewsFeedActivity.class);
                startActivityForResult(intent, 0);
                return true;

            case R.id.sign_out_from_menu:
                setResult(Model.Constant.logOut);
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Model.Constant.logOut){
            setResult(Model.Constant.logOut);
            finish();
        }
        else{
            user = Model.instance().getCurrentUser();
            Model.instance().getAllPostsPerUserUniq(user.getEmail(), new Model.EventPostsListener() {
                @Override
                public void onResult(LinkedList<Post> o) {
                    if(o!=null) {
                        data = o;
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onError(String error) {
                    Log.d("Error", "Error: " + error);
                }
            });

            if(resultCode==1) {

                Log.d("TAG","User Edited: "+user.toString());
                displayName.setText(user.displayName());
                email.setText(user.getEmail());
                if(!Model.Constant.isDefaultProfilePic(user.getProfilePic())) {
                    imageProgressBar.setVisibility(View.VISIBLE);
                    Log.d("TAG","Profile Pic is different");
                    profilePic.setImageBitmap(Model.instance().loadImageFromFile(user.getProfilePic()));
                    imageProgressBar.setVisibility(View.GONE);
                }
                //updateUser();



            }
        }

        super.onActivityResult(requestCode, resultCode, dataIntent);
    }

    private void updateUser()
    {
        Log.d("TAG","UPDATEUSER "+ Model.instance().getCurrentUser().toString());
        user = Model.instance().getCurrentUser();
        displayName.setText(user.displayName());
        email.setText(user.getEmail());
        if(!Model.Constant.isDefaultProfilePic(user.getProfilePic())){
            imageProgressBar.setVisibility(View.VISIBLE);
            Model.instance().loadImage(user.getProfilePic(), new Model.LoadImageListener() {
                @Override
                public void onResult(Bitmap imageBmp) {
                    if(imageBmp!=null)
                        profilePic.setImageBitmap(imageBmp);
                    imageProgressBar.setVisibility(View.INVISIBLE);
                }
            });
        }


    }


    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {return data.size();}

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {return position;}

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.row_layout,null);
            }
            convertView.setTag(position);
            final ImageView image = (ImageView) convertView.findViewById(R.id.image_rowlayout_post);
            TextView name = (TextView) convertView.findViewById(R.id.nameTextView);
            ProgressBar pb = (ProgressBar) convertView.findViewById(R.id.TVShow_Progress_Bar);
            Post post = data.get(position);
            name.setText(post.getShowName()+" ("+post.getProgress()+"%)");
            pb.setProgress(post.getProgress());
            /*TextView id = (TextView) convertView.findViewById(R.id.idTextView);
            final CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox);
            cb.setTag(new Integer(position));*/


            //id.setText(st.getId());
            //cb.setChecked(st.isChecked());
            final ProgressBar progress = (ProgressBar) convertView.findViewById(R.id.row_layout_ImageProgressBar);

            if (!Model.Constant.isDefaultShowPic(post.getImagePath())) {
                Log.d("TAG","list gets image " + post.getImagePath());
                progress.setVisibility(View.VISIBLE);
                Model.instance().loadImage(post.getImagePath(), new Model.LoadImageListener() {
                    @Override
                    public void onResult(Bitmap imageBmp) {
                        if (imageBmp != null) {
                            image.setImageBitmap(imageBmp);
                            progress.setVisibility(View.GONE);
                        }
                    }
                });
            }
            else progress.setVisibility(View.GONE);
            return convertView;
        }
    }

}



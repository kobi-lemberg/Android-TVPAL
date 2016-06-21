package com.tvpal.kobi.tvpal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
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

public class UserDisplayerActivity extends Activity {
    List<Post> data = new LinkedList<Post>();
    TextView displayName;
    TextView email;
    ImageView profilePic;
    ProgressBar imageProgressBar;
    ListView listView;
    CustomAdapter adapter;
    String userEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_displayer);

        userEmail = getIntent().getExtras().getString("email");
        listView = (ListView) findViewById(R.id.user_displayer_listView);
        adapter = new CustomAdapter();
        listView.setAdapter(adapter);
        listView.setClickable(false);

        Model.instance().getAllPostsPerUser(userEmail, new Model.EventPostsListener() {
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


        displayName = (TextView) findViewById(R.id.user_displayer_profile_name);

        email = (TextView) findViewById(R.id.user_displayer_Email);
        email.setText(userEmail);
        imageProgressBar = (ProgressBar) findViewById(R.id.user_displayer_UserImageProgressBar);
        imageProgressBar.setVisibility(View.VISIBLE);

        Model.instance().getUserByEmail(userEmail, new Model.UserEventPostsListener() {
            @Override
            public void onResult(User u) {
                displayName.setText(u.displayName());
                setTitle(u.displayName()+" Profile");
                profilePic = (ImageView) findViewById(R.id.user_displayer_activity_profile_imageView);
                if(!Model.Constant.isDefaultProfilePic(u.getProfilePic())){

                    Model.instance().loadImage(u.getProfilePic(), new Model.LoadImageListener() {
                        @Override
                        public void onResult(Bitmap imageBmp) {
                            if(imageBmp!=null)
                                profilePic.setImageBitmap(imageBmp);
                        }
                    });
                }
                imageProgressBar.setVisibility(View.GONE);

            }

            @Override
            public void onError(String error) {
                    Log.d("FBERROR",error);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_displayer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);*/
        Intent intent;
        switch (item.getItemId()) {
/*            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;*/

            case R.id.go_to_profile_from_menu:
                intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivityForResult(intent, 0);
                return true;

            case R.id.go_to_add_show_from_menu:
                intent = new Intent(getApplicationContext(), AddShowActivity.class);
                startActivityForResult(intent, 0);
                return true;

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
                /*CheckBox cb1 = (CheckBox) convertView.findViewById(R.id.checkBox);
                cb1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("LISTAPP", "my tag is: " + v.getTag());
                        Student st = data.get((Integer) v.getTag());
                        st.setChecked(!st.isChecked());
                    }
                });*/
            }
            Post post = data.get(position);
            final ProgressBar progress = (ProgressBar) convertView.findViewById(R.id.TVShow_Progress_Bar);
            progress.setProgress(post.getProgress());

            final ImageView image = (ImageView) convertView.findViewById(R.id.image_rowlayout_post);
            final ProgressBar imageProgress = (ProgressBar) convertView.findViewById(R.id.row_layout_ImageProgressBar);
            if (!Model.Constant.isDefaultShowPic(post.getImagePath())){
                Log.d("TAG","list gets image " + post.getImagePath());
                imageProgress.setVisibility(View.VISIBLE);
                Model.instance().loadImage(post.getImagePath(), new Model.LoadImageListener() {
                    @Override
                    public void onResult(Bitmap imageBmp) {
                        if (imageBmp != null) {
                            image.setImageBitmap(imageBmp);
                        }
                        imageProgress.setVisibility(View.GONE);
                    }
                });
            }
            else imageProgress.setVisibility(View.GONE);

            TextView name = (TextView) convertView.findViewById(R.id.nameTextView);
            name.setText(post.getShowName()+" ("+post.getProgress()+"%)");
            convertView.setTag(position);
            return convertView;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Model.instance().getAllPostsPerUser(userEmail, new Model.EventPostsListener() {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== Model.Constant.logOut)
        {
            setResult(Model.Constant.logOut);
            finish();
        }
    }
}

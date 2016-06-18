package com.tvpal.kobi.tvpal;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import com.tvpal.kobi.tvpal.Model.Model;
import com.tvpal.kobi.tvpal.Model.Post;
import com.tvpal.kobi.tvpal.Model.User;
import java.util.LinkedList;

public class NewsFeedActivity extends Activity
{


    ListView listView;
    CustomAdapter adapter;
    LinkedList<Post> data = new LinkedList<Post>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        Log.d("TAG", "In NewsFeed Activity");

        listView = (ListView) findViewById(R.id.news_feed_listView);
        adapter = new CustomAdapter();
        listView.setAdapter(adapter);
        Model.instance().getAllPosts(new Model.EventPostsListener() {
            @Override
            public void onResult(LinkedList<Post> o) {
                if(o!=null) {
                    data = o;
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onError(String error) {Log.d("Error", "Error: " + error);}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.news_feed_menu, menu);
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

        switch (item.getItemId()) {
/*            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;*/

            case R.id.go_to_profile_from_menu:
                // User chose the "Settings" item, show the app settings UI...
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivityForResult(intent, 0);
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
            final Post currentPost;
            if(convertView == null){
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.news_feed_row_layout,null);
            }
            //else {// currentPost = data.get(position);}
            currentPost = data.get(position);
            final ProgressBar imageProgressbar = (ProgressBar) convertView.findViewById(R.id.news_feed_raw_user_image_progressBar);
            final TextView userNameText = (TextView) convertView.findViewById(R.id.news_feed_raw_profile_displayName);
            final TextView userEvent = (TextView) convertView.findViewById(R.id.news_feed_raw_show_event);
            final ImageView imageView = (ImageView) convertView.findViewById(R.id.news_feed_raw_profile_image);
            TextView rated = (TextView) convertView.findViewById(R.id.news_feed_raw_rated);
            RatingBar ratingBar = (RatingBar)convertView.findViewById(R.id.news_feed_raw_ratingBar);
            TextView page = (TextView)convertView.findViewById(R.id.news_feed_raw_page);
            TextView post = (TextView)convertView.findViewById(R.id.news_feed_raw_post);


            Model.instance().getUserByEmail(currentPost.getUserEmail(), new Model.UserEventPostsListener() {
                @Override
                public void onResult(final User u) {
                    Log.d("TAG",u.displayName());
                    userNameText.setText(u.displayName());
                    userNameText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(u.equals(Model.instance().getCurrentUser())) {
                                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                                startActivityForResult(intent, 0);
                            }
                            else {
                                Intent intent = new Intent(getApplicationContext(), UserDisplayerActivity.class);
                                intent.putExtra("user", u.displayName());
                                intent.putExtra("email", u.getEmail());
                                startActivity(intent);
                            }
                        }
                    });
                    if(!Model.Constant.isDefaultProfilePic(u.getProfilePic())){
                        imageProgressbar.setVisibility(View.VISIBLE);
                        Model.instance().loadImage(u.getProfilePic(), new Model.LoadImageListener() {
                            @Override
                            public void onResult(Bitmap imageBmp) {
                                if (imageBmp != null) {
                                    imageView.setImageBitmap(imageBmp);
                                    imageProgressbar.setVisibility(View.GONE);
                                }
                            }
                        });

                    }
                }

                @Override
                public void onError(String error) {
                    userNameText.setText(error);
                }
            });
/*            if(!Model.Constant.isDefaultProfilePic(currentPost.getImagePath())){

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Model.instance().getUserByEmail(currentPost.getUserEmail(), new Model.UserEventPostsListener() {
                            @Override
                            public void onResult(User u) {

                            }

                            @Override
                            public void onError(String error) {
                                Log.d("TAG","list gets image " + currentPost.getImagePath());
                                Model.instance().loadImage(currentPost.getImagePath(), new Model.LoadImageListener() {
                                    @Override
                                    public void onResult(Bitmap imageBmp) {
                                        if (imageBmp != null) {
                                            imageView.setImageBitmap(imageBmp);
                                            imageProgressbar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
                t.start();



            }*/

            String event = currentPost.getEvent();
            userEvent.setText(event+" "+currentPost.getShowName());
            userEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ShowDisplayerActivity.class);
                    intent.putExtra("showName", currentPost.getShowName());
                    startActivity(intent);
                }
            });
            if(!event.equals("Is On")){

                rated.setVisibility(View.GONE);
                ratingBar.setVisibility(View.GONE);
                page.setVisibility(View.GONE);
                post.setVisibility(View.GONE);

            }
            else {
                rated.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.VISIBLE);
                page.setVisibility(View.VISIBLE);

                Log.d("TAG","set starts for "+currentPost.getShow()+": "+currentPost.getGrade());
                ratingBar.setRating(currentPost.getGrade());
                page.setText("Page: "+currentPost.getCurrentPart());
                String comment = currentPost.getText();
                if(comment!=null&&comment!="") {
                    post.setVisibility(View.GONE);
                    post.setText(comment);
                }
            }

            return convertView;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Model.instance().getAllPosts(new Model.EventPostsListener() {
            @Override
            public void onResult(LinkedList<Post> o) {
                if(o!=null) {
                    data = o;
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onError(String error) {Log.d("Error", "Error: " + error);}
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        super.onActivityResult(requestCode, resultCode, dataIntent);
        Model.instance().getAllPosts(new Model.EventPostsListener() {
            @Override
            public void onResult(LinkedList<Post> o) {
                if(o!=null) {
                    data = o;
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onError(String error) {Log.d("Error", "Error: " + error);}
        });
    }
}


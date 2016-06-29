package com.tvpal.kobi.tvpal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import com.tvpal.kobi.tvpal.Model.Model;
import com.tvpal.kobi.tvpal.Model.Post;
import com.tvpal.kobi.tvpal.Model.TVShow;
import com.tvpal.kobi.tvpal.Model.User;
import java.util.Collections;
import java.util.LinkedList;

public class ShowDisplayerActivity extends Activity {
    String showNameStr;
    ImageView showImageView;
    TextView showNameTextView;
    TextView episodeAndSeason;
    TextView catagories;
    ListView listView;
    ProgressBar showDisplayerUpperProgressBar;
    TVShow show;
    CustomAdapter adapter;
    LinkedList<Post> data = new LinkedList<Post>();
    //Bitmap defaultBitmap= BitmapFactory.decodeResource(MyApplication.getAppContext().getResources(), R.drawable.default_profile_pic);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_displayer);
        handelGUI();
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
                convertView = inflater.inflate(R.layout.news_feed_row_layout,null);
            }
            final ProgressBar imageProgressbar = (ProgressBar) convertView.findViewById(R.id.news_feed_raw_user_image_progressBar);
            final TextView userNameText = (TextView) convertView.findViewById(R.id.news_feed_raw_profile_displayName);
            final TextView userEvent = (TextView) convertView.findViewById(R.id.news_feed_raw_show_event);
            final ImageView imageView = (ImageView) convertView.findViewById(R.id.news_feed_raw_profile_image);
            TextView rated = (TextView) convertView.findViewById(R.id.news_feed_raw_rated);
            RatingBar ratingBar = (RatingBar)convertView.findViewById(R.id.news_feed_raw_ratingBar);
            TextView Episode = (TextView)convertView.findViewById(R.id.news_feed_raw_episode);
            TextView post = (TextView)convertView.findViewById(R.id.news_feed_raw_post);
            final Post currentPost = data.get(position);

            Model.instance().getUserByEmail(currentPost.getUserEmail(), new Model.UserEventPostsListener() {
                @Override
                public void onResult(final User u) {
                    Log.d("TAG",u.displayName());
                    if(!Model.Constant.isDefaultProfilePic(u.getProfilePic())){
                        imageProgressbar.setVisibility(View.VISIBLE);
                        Log.d("TAG","list gets image " + currentPost.getImagePath());
                        Model.instance().loadImage(u.getProfilePic(), new Model.LoadImageListener() {
                            @Override
                            public void onResult(Bitmap imageBmp) {
                                if (imageBmp != null) {
                                    imageView.setImageBitmap(imageBmp);
                                }
                                imageProgressbar.setVisibility(View.GONE);
                            }
                        });
                    }
                    else{
                        imageProgressbar.setVisibility(View.GONE);
                    }
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
                                intent.putExtra("pic", u.getProfilePic());
                                startActivityForResult(intent,0);
                            }
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    userNameText.setText(error);
                }
            });


            String event = currentPost.getEvent();
            userEvent.setText(event+" this show");
            userEvent.setClickable(false);
            if(!event.equals("Is On")){

                rated.setVisibility(View.GONE);
                ratingBar.setVisibility(View.GONE);
                Episode.setVisibility(View.GONE);
                post.setVisibility(View.GONE);

            }
            else {
                rated.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.VISIBLE);
                Episode.setVisibility(View.VISIBLE);
                ratingBar.setRating(currentPost.getGrade());
                Episode.setText(" episode "+currentPost.getCurrentPart());
                String comment = currentPost.getText();
                if(comment!=null&&!comment.equals("")) {
                    post.setVisibility(View.VISIBLE);
                    post.setText("\""+comment+"\"");
                }
            }
            return convertView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Model.Constant.logOut){
            setResult(Model.Constant.logOut);
            finish();
        }
        else {
            handelGUI();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handelGUI()
    {
        showNameStr = getIntent().getStringExtra("showName");
        setTitle(showNameStr);
        listView = (ListView) findViewById(R.id.display_show_feed_list);
        adapter = new CustomAdapter();
        listView.setAdapter(adapter);
        listView.setClickable(false);
        showDisplayerUpperProgressBar = (ProgressBar) findViewById(R.id.show_display_upper_progressBar);
        showDisplayerUpperProgressBar.setVisibility(View.VISIBLE);
        showNameTextView = (TextView) findViewById(R.id.show_display_movieName);
        showNameTextView.setText(showNameStr);
        Model.instance().getPostsByShowNameAsync(showNameStr, new Model.EventPostsListener() {
            @Override
            public void onResult(LinkedList<Post> o) {
                if(o!=null){
                    Collections.reverse(o);
                    data =o ;
                    adapter.notifyDataSetChanged();
                    showDisplayerUpperProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String error) {Log.d("Error", "Error: " + error);}
        });
        Model.instance().getShowByNameAsync(showNameStr, new Model.TVShowListener()
        {
            @Override
            public void onResult(TVShow result) {
                show = result;
                if(!Model.Constant.isDefaultShowPic(show.getImagePath())) {
                    final ProgressBar showImagePB = (ProgressBar) findViewById(R.id.show_display_ImageProgressBar);
                    showImagePB.setVisibility(View.VISIBLE);
                    Model.instance().loadImage(show.getImagePath(), new Model.LoadImageListener() {
                        @Override
                        public void onResult(Bitmap imageBmp) {
                            showImageView = (ImageView) findViewById(R.id.show_display_imageView);
                            showImageView.setImageBitmap(imageBmp);
                            showImagePB.setVisibility(View.GONE);
                        }
                    });
                }
                episodeAndSeason = (TextView) findViewById(R.id.show_display_episode_season);
                episodeAndSeason.setText("Season "+show.getSeason()+", "+show.getEpisode()+" episodes");
                catagories = (TextView) findViewById(R.id.show_display_Categories);
                catagories.setText("#"+show.getCategory());
            }

            @Override
            public void onError(String error) {
                Log.d("TAG",error);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        handelGUI();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.show_displayer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
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
                return super.onOptionsItemSelected(item);
        }
    }
}

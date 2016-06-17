package com.tvpal.kobi.tvpal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.tvpal.kobi.tvpal.Model.Model;
import com.tvpal.kobi.tvpal.Model.Post;
import com.tvpal.kobi.tvpal.Model.TVShow;
import com.tvpal.kobi.tvpal.Model.User;

import java.util.LinkedList;

public class ShowDisplayerActivity extends Activity {
    String showNameStr;
    ImageView showImageView;
    TextView showNameTextView;
    TextView episodeAndSeason;
    TextView catagories;
    LinearLayout summerySection;
    ListView listView;
    ProgressBar showDisplayerUpperProgressBar;
    TVShow show;
    CustomAdapter adapter;
    TextView summery;
    LinkedList<Post> data = new LinkedList<Post>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_displayer);
        showNameStr = getIntent().getStringExtra("showName");
        Model.instance().getShowByNameAsync(showNameStr, new Model.TVShowListener() {
            @Override
            public void onResult(TVShow result) {
                show = result;
                listView = (ListView) findViewById(R.id.display_show_feed_list);
                adapter = new CustomAdapter();
                listView.setAdapter(adapter);
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
                showDisplayerUpperProgressBar = (ProgressBar) findViewById(R.id.show_display_upper_progressBar);
                showDisplayerUpperProgressBar.setVisibility(View.VISIBLE);
                Model.instance().getPostsByShowNameAsync(showNameStr, new Model.EventPostsListener() {
                    @Override
                    public void onResult(LinkedList<Post> o) {
                        if(o!=null){
                            data = o;
                            adapter.notifyDataSetChanged();
                            showDisplayerUpperProgressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(String error) {Log.d("Error", "Error: " + error);}
                });


                showNameTextView = (TextView) findViewById(R.id.show_display_movieName);
                showNameTextView.setText(showNameStr);
                episodeAndSeason = (TextView) findViewById(R.id.show_display_episode_season);
                episodeAndSeason.setText("Season "+show.getSeason()+", "+show.getEpisode()+" episodes");
                catagories = (TextView) findViewById(R.id.show_display_Categories);
                catagories.setText("#"+show.getCategory());
                summerySection = (LinearLayout) findViewById(R.id.show_display_summery_section);
                if(show.getSummery().equals("")||show.getSummery()==null)
                    summerySection.setVisibility(View.GONE);
                else{
                    summerySection.setVisibility(View.VISIBLE);
                    summery = (TextView) findViewById(R.id.show_display_summary);
                    summery.setText(show.getSummery());
                }

            }

            @Override
            public void onError(String error) {
                Log.d("TAG",error);
            }
        });
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

            final ProgressBar imageProgressbar = (ProgressBar) convertView.findViewById(R.id.news_feed_raw_user_image_progressBar);
            imageProgressbar.setVisibility(View.VISIBLE);
            final TextView userNameText = (TextView) convertView.findViewById(R.id.news_feed_raw_profile_displayName);
            final TextView userEvent = (TextView) convertView.findViewById(R.id.news_feed_raw_show_event);
            final ImageView imageView = (ImageView) convertView.findViewById(R.id.news_feed_raw_profile_image);
            TextView rated = (TextView) convertView.findViewById(R.id.news_feed_raw_rated);
            RatingBar ratingBar = (RatingBar)convertView.findViewById(R.id.news_feed_raw_ratingBar);
            TextView page = (TextView)convertView.findViewById(R.id.news_feed_raw_page);
            TextView post = (TextView)convertView.findViewById(R.id.news_feed_raw_post);
            final Post currentPost = data.get(position);

            Model.instance().getUserByEmail(currentPost.getUserEmail(), new Model.UserEventPostsListener() {
                @Override
                public void onResult(final User u) {
                    Log.d("TAG",u.displayName());
                    if(!Model.Constant.isDefaultShowPic(u.getProfilePic())){
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
                                startActivity(intent);
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
                page.setVisibility(View.GONE);
                post.setVisibility(View.GONE);

            }
            else {
                rated.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.VISIBLE);
                page.setVisibility(View.VISIBLE);
                ratingBar.setNumStars(currentPost.getGrade());
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        adapter.notifyDataSetChanged();
        super.onActivityResult(requestCode, resultCode, data);
    }
}

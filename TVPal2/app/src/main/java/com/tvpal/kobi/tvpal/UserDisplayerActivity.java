package com.tvpal.kobi.tvpal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_displayer);

        String userName = getIntent().getExtras().getString("user");
        String userEmail = getIntent().getExtras().getString("email");
        String profilePicStr = getIntent().getExtras().getString("pic");
        listView = (ListView) findViewById(R.id.user_displayer_listView);
        adapter = new CustomAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*Toast.makeText(getApplicationContext(), "item click " + position, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),StudentDetailsActivity.class);
                intent.putExtra("id",data.get(position).getShowName());
                startActivity(intent);*/
            }
        });

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
        displayName.setText(userName);
        email = (TextView) findViewById(R.id.user_displayer_Email);
        email.setText(userEmail);
        imageProgressBar = (ProgressBar) findViewById(R.id.user_displayer_UserImageProgressBar);
        profilePic = (ImageView) findViewById(R.id.user_displayer_activity_profile_imageView);
        if(!Model.Constant.isDefaultProfilePic(profilePicStr)){
            imageProgressBar.setVisibility(View.VISIBLE);
            Model.instance().loadImage(profilePicStr, new Model.LoadImageListener() {
                @Override
                public void onResult(Bitmap imageBmp) {
                    if(imageBmp!=null)
                        profilePic.setImageBitmap(imageBmp);
                        imageProgressBar.setVisibility(View.GONE);
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

            final ImageView image = (ImageView) convertView.findViewById(R.id.image_rowlayout_post);
            if (!post.getImagePath().equals("default_show_pic")){
                Log.d("TAG","list gets image " + post.getImagePath());
                final ProgressBar progress = (ProgressBar) convertView.findViewById(R.id.TVShow_Progress_Bar);
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

            TextView name = (TextView) convertView.findViewById(R.id.nameTextView);
            ProgressBar pb = (ProgressBar) convertView.findViewById(R.id.TVShow_Progress_Bar);
            pb.setProgress(post.getProgress());
            name.setText(post.getShowName()+" ("+post.getProgress()+"%)");
            convertView.setTag(position);
            return convertView;
        }
    }

}

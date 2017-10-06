package com.gevkurg.twitterclient.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gevkurg.twitterclient.R;
import com.gevkurg.twitterclient.TwitterApplication;
import com.gevkurg.twitterclient.fragments.UserTimelineFragment;
import com.gevkurg.twitterclient.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {

    private TwitterClient twitterClient;
    private ImageView ivBackgroundImage;
    private ImageView ivProfileImage;
    private TextView tvName;
    private TextView tvUserName;
    private TextView tvTweetsCount;
    private TextView tvFollowersCount;
    private TextView tvFollowingsCount;
    private TextView tvTagline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        twitterClient = TwitterApplication.getRestClient();
        String username = getIntent().getStringExtra("username");

        if (savedInstanceState == null) {
            UserTimelineFragment userTimeline = UserTimelineFragment.newInstance(username);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.profileFragment, userTimeline);
            transaction.commit();
        }

        ivBackgroundImage = findViewById(R.id.ivBackground);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvName = findViewById(R.id.tvProfileName);
        tvUserName = findViewById(R.id.tvProfileUsername);
        tvTweetsCount = findViewById(R.id.tvProfileTweetCount);
        tvFollowersCount = findViewById(R.id.tvProfileFollowerCount);
        tvFollowingsCount = findViewById(R.id.tvProfileFollowingCount);
        tvTagline = findViewById(R.id.tvTagline);

        populateView(username);
    }

    private void populateView(String username) {
        if(username != null) {
            twitterClient.getUser(username, handler);
        } else {
            twitterClient.getCredentials(handler);
        }
    }

    private JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {

                int followers = response.getInt("followers_count");
                int friends = response.getInt("friends_count");
                int statuses = response.getInt("statuses_count");

                String fullname = response.getString("name");
                String screenName = response.getString("screen_name");
                String description = response.getString("description");

                String profileImage = response.getString("profile_image_url");
                String bgImage = response.optString("profile_background_image_url");

                tvFollowersCount.setText(String.valueOf(followers) + "\nFOLLOWERS");
                tvFollowingsCount.setText(String.valueOf(friends) + "\nFOLLOWING");
                tvTweetsCount.setText(String.valueOf(statuses) + "\nTWEETS");

                tvName.setText(fullname);
                tvUserName.setText("@" + screenName);
                tvTagline.setText(description);

                Glide.with(getApplicationContext()).load(Uri.parse(bgImage)).into(ivBackgroundImage);
                Glide.with(getApplicationContext())
                        .load(Uri.parse(profileImage))
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivProfileImage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
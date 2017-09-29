package com.gevkurg.twitterclient.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gevkurg.twitterclient.R;
import com.gevkurg.twitterclient.models.Tweet;
import com.gevkurg.twitterclient.models.User;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TweetDetailsActivity extends AppCompatActivity {
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
    private static final SimpleDateFormat STRING_FORMATTER = new SimpleDateFormat("dd/MMM/yyyy, hh:mm a");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon_logo_24);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        populateTweetDetails(tweet);
    }

    private void populateTweetDetails(final Tweet tweet) {
        TextView tvTweetText = findViewById(R.id.tvTweetText);
        tvTweetText.setText(tweet.getBody());
        ImageView ivProfileImage = findViewById(R.id.ivProfileImage);
        User user = tweet.getUser();
        Glide.with(this).load(user.getProfileImageUrl()).into(ivProfileImage);
        TextView tvUserName = findViewById(R.id.tvUserName);
        tvUserName.setText(user.getName());
        TextView tvUserScreenName = findViewById(R.id.tvUserScreenName);
        tvUserScreenName.setText("@" + user.getScreenName());
        TextView tvCreatedAt = findViewById(R.id.tvCreatedAt);

        try {
            tvCreatedAt.setText(STRING_FORMATTER.format(FORMATTER.parse(tweet.getCreatedAt())));
        } catch (ParseException e) {
            Log.d("TWEET", "uh oh", e);
        }

        TextView tvRetweetCount = findViewById(R.id.tvRetweetCount);
        tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        TextView tvFavoritesCount = findViewById(R.id.tvLikesCount);
        tvFavoritesCount.setText(String.valueOf(tweet.getFavoriteCount()));
    }
}

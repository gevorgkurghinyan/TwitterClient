package com.gevkurg.twitterclient.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gevkurg.twitterclient.R;
import com.gevkurg.twitterclient.TwitterApplication;
import com.gevkurg.twitterclient.fragments.ComposeTweetFragment;
import com.gevkurg.twitterclient.models.Tweet;
import com.gevkurg.twitterclient.models.User;
import com.gevkurg.twitterclient.network.TwitterClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.parceler.Parcels;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import cz.msebera.android.httpclient.Header;

public class TweetDetailsActivity extends AppCompatActivity implements ComposeTweetFragment.StatusUpdateListener {
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
    private static final SimpleDateFormat STRING_FORMATTER = new SimpleDateFormat("dd/MMM/yyyy, hh:mm a");
    private ComposeTweetFragment composeTweetFragment;
    private TwitterClient client = TwitterApplication.getRestClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon_logo_24);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        populateTweetDetails(tweet);
        setupReplyButton(tweet);
        setupRetweetButton(tweet);
        setupFavoriteButton(tweet);
    }

    private void populateTweetDetails(final Tweet tweet) {
        TextView tvTweetText = findViewById(R.id.tvTweetText);
        tvTweetText.setText(tweet.getBody());
        ImageView ivProfileImage = findViewById(R.id.ivProfileImage);
        User user = tweet.getUser();
        Glide.with(this).load(user.getProfileImageUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(ivProfileImage);
        TextView tvUserName = findViewById(R.id.tvUserName);
        tvUserName.setText(user.getName());
        TextView tvUserScreenName = findViewById(R.id.tvUserScreenName);
        tvUserScreenName.setText("@" + user.getScreenName());
        TextView tvCreatedAt = findViewById(R.id.tvCreatedAt);

        try {
            tvCreatedAt.setText(STRING_FORMATTER.format(FORMATTER.parse(tweet.getCreatedAt())));
        } catch (ParseException e) {
            Log.d("TWEET", e.getMessage(), e);
        }

        TextView tvRetweetCount = findViewById(R.id.tvRetweetCount);
        tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        TextView tvFavoritesCount = findViewById(R.id.tvLikesCount);
        tvFavoritesCount.setText(String.valueOf(tweet.getFavoriteCount()));
    }

    private void setupReplyButton(final Tweet tweet) {
        final String tweetId = tweet.getId();
        ImageView ivReply = findViewById(R.id.ivReply);
        ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                composeTweetFragment = new ComposeTweetFragment();
                composeTweetFragment.setInReplyToTweetId(tweetId);
                composeTweetFragment.setListener(TweetDetailsActivity.this);
                composeTweetFragment.show(fragmentManager, "COMPOSE_TWEET");
            }
        });
    }

    private void setupRetweetButton(final Tweet tweet) {
        final long tweetId = Long.valueOf(tweet.getId());
        final ImageView ivRetweet = findViewById(R.id.ivRetweet);
        final TextView tvRetweetCount = findViewById(R.id.tvRetweetCount);

        if (tweet.isRetweeted()) {
            ivRetweet.setImageResource(R.drawable.icon_retweet_done_24);
        } else {
            ivRetweet.setImageResource(R.drawable.icon_retweet_24);
            ivRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    client.retweet(tweetId, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                ObjectMapper objectMapper = new ObjectMapper();
                                Tweet retweet = objectMapper.readValue(responseBody, new TypeReference<Tweet>() {
                                });
                                tvRetweetCount.setText(Integer.toString(retweet.getRetweetCount()));
                                ivRetweet.setImageResource(R.drawable.icon_retweet_done_24);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            error.printStackTrace();
                        }
                    });
                }
            });
        }
    }

    private void setupFavoriteButton(final Tweet tweet) {
        final long tweetId = Long.valueOf(tweet.getId());
        final ImageView ivFavorite = findViewById(R.id.ivFavorite);
        final TextView tvFavoriteCount = findViewById(R.id.tvLikesCount);

        if (tweet.isFavorited()) {
            ivFavorite.setImageResource(R.drawable.icon_favorite_done_24);
        } else {
            ivFavorite.setImageResource(R.drawable.icon_favorite_24);
        }

        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!tweet.isFavorited()) {
                    client.favorite(tweetId, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                ObjectMapper objectMapper = new ObjectMapper();
                                Tweet t = objectMapper.readValue(responseBody, new TypeReference<Tweet>() {});
                                tvFavoriteCount.setText(Integer.toString(t.getFavoriteCount()));
                                ivFavorite.setImageResource(R.drawable.icon_favorite_done_24);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            error.printStackTrace();
                        }
                    });
                } else {
                    client.unFavorite(tweetId, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                ObjectMapper objectMapper = new ObjectMapper();
                                Tweet t = objectMapper.readValue(responseBody, new TypeReference<Tweet>() {});
                                tvFavoriteCount.setText(Integer.toString(t.getFavoriteCount()));
                                ivFavorite.setImageResource(R.drawable.icon_favorite_24);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            error.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onStatusUpdated(Tweet tweet) {
        if (composeTweetFragment != null) {
            composeTweetFragment.dismiss();
        }
    }
}

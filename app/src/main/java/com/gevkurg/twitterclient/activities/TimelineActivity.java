package com.gevkurg.twitterclient.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.gevkurg.twitterclient.R;
import com.gevkurg.twitterclient.adapters.TweetAdapter;
import com.gevkurg.twitterclient.listeners.EndlessRecyclerViewScrollListener;
import com.gevkurg.twitterclient.models.Tweet;
import com.gevkurg.twitterclient.network.TwitterApplication;
import com.gevkurg.twitterclient.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    TweetAdapter tweetAdapter;
    List<Tweet> tweets;
    RecyclerView rvTweets;
    SwipeRefreshLayout srLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        client = TwitterApplication.getRestClient();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon_logo_24);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        srLayout = findViewById(R.id.srLayout);
        rvTweets = findViewById(R.id.rvTweet);
        tweets = new ArrayList<>();
        tweetAdapter = new TweetAdapter(tweets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(tweetAdapter);

        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if(tweetAdapter.getItemCount() == 0) {
                    populateTimeline(null);
                } else if (tweetAdapter.getItemCount() >= TwitterClient.TWEETS_PER_PAGE) {
                    populateTimeline(tweetAdapter.getMaxId());
                }
            }
        });

        srLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tweetAdapter.clear();
                populateTimeline(null);
            }
        });

        populateTimeline(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_compose) {
            //Intent i = new Intent(this, ComposeActivity.class);
            //startActivityForResult(i, ComposeActivity.REQUEST_CODE);
        }

        if(id == R.id.action_profile) {
            //Intent i = new Intent(this, ProfileActivity.class);
            //startActivity(i);
        }

        if(id == R.id.action_settings) {
            //Intent i = new Intent(this, SettingsActivity.class);
            //startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateTimeline(String maxId) {
        client.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                tweets.addAll(Tweet.fromJson(response));
                //TODO which notify method should be called.
                tweetAdapter.notifyDataSetChanged();
                srLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }
}

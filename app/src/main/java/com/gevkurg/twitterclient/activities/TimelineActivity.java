package com.gevkurg.twitterclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gevkurg.twitterclient.R;
import com.gevkurg.twitterclient.TwitterApplication;
import com.gevkurg.twitterclient.adapters.TweetAdapter;
import com.gevkurg.twitterclient.database.TweetDatabase;
import com.gevkurg.twitterclient.fragments.ComposeTweetFragment;
import com.gevkurg.twitterclient.listeners.EndlessRecyclerViewScrollListener;
import com.gevkurg.twitterclient.models.Tweet;
import com.gevkurg.twitterclient.models.Tweet_Table;
import com.gevkurg.twitterclient.network.TwitterClient;
import com.gevkurg.twitterclient.network.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetFragment.StatusUpdateListener {

    private static final int PAGE_SIZE = 25;

    private TwitterClient client;
    TweetAdapter tweetAdapter;
    List<Tweet> mTweets;
    RecyclerView rvTweets;
    SwipeRefreshLayout srLayout;
    private ComposeTweetFragment composeTweetFragment;

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
        mTweets = new ArrayList<>();
        tweetAdapter = new TweetAdapter(mTweets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(tweetAdapter);

        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Long maxId = tweetAdapter.getOldestTweetId();
                populateTimeline(false, maxId);
            }
        });

        srLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline(true, 1L);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                composeTweetFragment = new ComposeTweetFragment();
                composeTweetFragment.show(fragmentManager, "COMPOSE_TWEET");
                composeTweetFragment.setListener(TimelineActivity.this);
            }
        });

        populateTimeline(true, 1L);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            TwitterApplication.getRestClient().clearAccessToken();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStatusUpdated(Tweet tweet) {
        if (composeTweetFragment != null) {
            composeTweetFragment.dismiss();
        }

        mTweets.add(0, tweet);
        tweetAdapter.notifyDataSetChanged();
        rvTweets.smoothScrollToPosition(0);
    }

    private void populateTimeline(final boolean isFirstLoad, long id) {
        if (Utils.isNetworkAvailable(this)) {
            client.getHomeTimeline(isFirstLoad, id, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        List<Tweet> tweets = objectMapper.readValue(responseBody, new TypeReference<List<Tweet>>(){});
                        updateAdapter(tweets, isFirstLoad);
                        // save to database
                        saveToDatabase();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    error.printStackTrace();
                }
            });
        } else {
            // try to read from database
            Utils.showSnackBarForInternetConnection(rvTweets, TimelineActivity.this);
            updateAdapter(readFromDatabase(id, 25), isFirstLoad);
        }
    }

    private void updateAdapter(List<Tweet> tweets, boolean isFirstLoad) {
        if (isFirstLoad) {
            tweetAdapter.clear();
        }

        this.mTweets.addAll(tweets.isEmpty() ? tweets : tweets.subList(1, tweets.size()));
        tweetAdapter.notifyDataSetChanged();
        srLayout.setRefreshing(false);
    }

    private void saveToDatabase(){
        FlowManager.getDatabase(TweetDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<Tweet>() {
                            @Override
                            public void processModel(Tweet tweet, DatabaseWrapper dbWrapper) {
                                tweet.getUser().save();
                                tweet.save();
                            }
                        }).addAll(mTweets).build())  // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {
                        error.printStackTrace();
                    }
                })
                .success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {

                    }
                }).build().execute();
    }

    private List<Tweet> readFromDatabase(long id, int offset){
        List<Tweet> tweets = SQLite.select()
                .from(Tweet.class)
                .orderBy(Tweet_Table.createdAt, false)
                .offset(offset)
                .limit(PAGE_SIZE)
                .queryList();

        return tweets;
    }
}
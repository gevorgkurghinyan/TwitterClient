package com.gevkurg.twitterclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.gevkurg.twitterclient.R;
import com.gevkurg.twitterclient.TwitterApplication;
import com.gevkurg.twitterclient.adapters.SmartFragmentStatePagerAdapter;
import com.gevkurg.twitterclient.fragments.ComposeTweetFragment;
import com.gevkurg.twitterclient.fragments.HomeTimelineFragment;
import com.gevkurg.twitterclient.fragments.MentionsTimelineFragment;

public class TimelineActivity extends AppCompatActivity {

    private static final int PAGE_SIZE = 25;

    private TweetsPagerAdapter tweetsPagerAdapter;
    private ViewPager viewPager;
    private ComposeTweetFragment composeTweetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon_logo_24);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        viewPager = findViewById(R.id.viewpager);
        tweetsPagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tweetsPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.slidingTabs);
        tabLayout.setupWithViewPager(viewPager);
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

        if (id == R.id.action_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /*
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
    */

    // Returns order of the fragments in the view pager
    public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {

        private String tabTitles[] = { "Home", "@ Mentions" };

        public TweetsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0) {
                return HomeTimelineFragment.newInstance();
            } else if (position == 1) {
                return MentionsTimelineFragment.newInstance();
            } else {
                return null;
            }
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }
}
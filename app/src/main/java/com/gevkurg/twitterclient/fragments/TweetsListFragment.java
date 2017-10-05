package com.gevkurg.twitterclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gevkurg.twitterclient.R;
import com.gevkurg.twitterclient.adapters.TweetsAdapter;
import com.gevkurg.twitterclient.listeners.EndlessRecyclerViewScrollListener;
import com.gevkurg.twitterclient.models.Tweet;

import java.util.ArrayList;
import java.util.List;


public abstract class TweetsListFragment extends Fragment implements ComposeTweetFragment.StatusUpdateListener {

    private TweetsAdapter tweetsAdapter;
    private SwipeRefreshLayout srLayout;
    protected RecyclerView rvTweets;
    private List<Tweet> mTweets;
    private ComposeTweetFragment composeTweetFragment;

    public TweetsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        populateTimeline(null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweets_list, container, false);

        srLayout = view.findViewById(R.id.srLayout);
        rvTweets = view.findViewById(R.id.rvTweet);
        mTweets = new ArrayList<>();
        tweetsAdapter = new TweetsAdapter(mTweets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(tweetsAdapter);

        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                String maxId = tweetsAdapter.getOldestTweetId();
                populateTimeline(maxId);
            }
        });

        srLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tweetsAdapter.clear();
                populateTimeline(null);
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getChildFragmentManager();
                composeTweetFragment = new ComposeTweetFragment();
                composeTweetFragment.show(fragmentManager, "COMPOSE_TWEET");
                composeTweetFragment.setListener(TweetsListFragment.this);
            }
        });

        return view;
    }

    @Override
    public void onStatusUpdated(Tweet tweet) {
        if (composeTweetFragment != null) {
            composeTweetFragment.dismiss();
        }

        add(tweet);
    }

    public abstract void populateTimeline(String maxId);

    public void add(Tweet tweet) {
        mTweets.add(0, tweet);
        tweetsAdapter.notifyDataSetChanged();
        rvTweets.smoothScrollToPosition(0);
    }

    protected void addAll(ArrayList<Tweet> tweets) {
        tweetsAdapter.addAll(tweets);
        tweetsAdapter.notifyDataSetChanged();
        srLayout.setRefreshing(false);
    }

    protected void updateAdapter(List<Tweet> tweets) {
        tweetsAdapter.addAll(tweets);
        tweetsAdapter.notifyDataSetChanged();
        srLayout.setRefreshing(false);
    }
}
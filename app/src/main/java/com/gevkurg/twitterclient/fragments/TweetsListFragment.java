package com.gevkurg.twitterclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gevkurg.twitterclient.R;
import com.gevkurg.twitterclient.adapters.TweetsAdapter;
import com.gevkurg.twitterclient.listeners.EndlessRecyclerViewScrollListener;
import com.gevkurg.twitterclient.models.SearchResult;
import com.gevkurg.twitterclient.models.Tweet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public abstract class TweetsListFragment extends Fragment implements ComposeTweetFragment.StatusUpdateListener {

    private TweetsAdapter tweetsAdapter;
    private SwipeRefreshLayout srLayout;
    protected RecyclerView rvTweets;
    private List<Tweet> mTweets;
    private ComposeTweetFragment composeTweetFragment;
    private MenuItem miActionProgressItem;
    private String queryText = null;

    public TweetsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        populateTimeline(null, queryText);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
                if (tweetsAdapter.getItemCount() == 0) {
                    populateTimeline(null, queryText);
                } else {
                    String maxId = tweetsAdapter.getOldestTweetId();
                    populateTimeline(maxId, null);
                }
            }
        });

        srLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tweetsAdapter.clear();
                populateTimeline(null, queryText);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        ProgressBar v = (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tweetsAdapter.clear();
                queryText = query;
                populateTimeline(null, queryText);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public abstract void populateTimeline(String maxId, String query);

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

    protected void showProgressBar() {
        // Show progress item
        if (miActionProgressItem != null) {
            miActionProgressItem.setVisible(true);
        }
    }

    protected void hideProgressBar() {
        // Hide progress item
        if (miActionProgressItem != null) {
            miActionProgressItem.setVisible(false);
        }
    }

    protected void onSearchSucceed(byte[] responseBody){
        try {
            hideProgressBar();
            ObjectMapper objectMapper = new ObjectMapper();
            SearchResult searchResult = objectMapper.readValue(responseBody, new TypeReference<SearchResult>() {});
            updateAdapter(searchResult.getStatuses());
            // save to database
            //saveToDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void onRequestSucceeded(byte[] responseBody) {
        try {
            hideProgressBar();
            ObjectMapper objectMapper = new ObjectMapper();
            List<Tweet> tweets = objectMapper.readValue(responseBody, new TypeReference<List<Tweet>>() {});
            updateAdapter(tweets);
            // save to database
            //saveToDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void onRequestFailed(Throwable error) {
        error.printStackTrace();
        hideProgressBar();
    }
}
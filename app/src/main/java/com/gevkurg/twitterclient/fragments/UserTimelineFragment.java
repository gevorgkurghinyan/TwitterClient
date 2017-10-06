package com.gevkurg.twitterclient.fragments;

import android.os.Bundle;

import com.gevkurg.twitterclient.TwitterApplication;
import com.gevkurg.twitterclient.network.TwitterClient;
import com.gevkurg.twitterclient.network.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;


public class UserTimelineFragment extends TweetsListFragment {
    private static final String ARG_USERNAME = "username";

    private String username;
    private TwitterClient client = TwitterApplication.getRestClient();

    public UserTimelineFragment() {
        // Required empty public constructor
    }

    public static UserTimelineFragment newInstance(String username) {
        UserTimelineFragment fragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = getArguments().getString(ARG_USERNAME);
    }

    @Override
    public void populateTimeline(String maxId, String queryText) {
        if (Utils.isNetworkAvailable(getActivity())) {
            showProgressBar();

            if (queryText != null) {
                client.searchTweets(queryText, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        onSearchSucceed(responseBody);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        onRequestFailed(error);
                    }
                });
            } else {
                client.getUserTimeline(username, maxId, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        onRequestSucceeded(responseBody);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        onRequestFailed(error);
                    }
                });
            }
        } else {
            Utils.showSnackBarForInternetConnection(rvTweets, getActivity());
            // try to read from database
            //updateAdapter(readFromDatabase(id, 25), isFirstLoad);
        }
    }
}
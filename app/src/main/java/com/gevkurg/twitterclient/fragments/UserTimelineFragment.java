package com.gevkurg.twitterclient.fragments;

import android.os.Bundle;
import android.view.View;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gevkurg.twitterclient.TwitterApplication;
import com.gevkurg.twitterclient.models.Tweet;
import com.gevkurg.twitterclient.network.TwitterClient;
import com.gevkurg.twitterclient.network.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.IOException;
import java.util.List;

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
    public void populateTimeline(String maxId) {
        if (Utils.isNetworkAvailable(getActivity())) {
            client.getUserTimeline(username, maxId, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        List<Tweet> tweets = objectMapper.readValue(responseBody, new TypeReference<List<Tweet>>() {
                        });
                        updateAdapter(tweets);
                        // save to database
                        //saveToDatabase();
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
            Utils.showSnackBarForInternetConnection(rvTweets, getActivity());
            // try to read from database
            //updateAdapter(readFromDatabase(id, 25), isFirstLoad);
        }
    }
}
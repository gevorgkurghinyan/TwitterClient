package com.gevkurg.twitterclient.fragments;

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


public class MentionsTimelineFragment extends TweetsListFragment {

    private TwitterClient client = TwitterApplication.getRestClient();

    public static MentionsTimelineFragment newInstance() {
        MentionsTimelineFragment fragment = new MentionsTimelineFragment();
        return fragment;
    }

    @Override
    public void populateTimeline(String maxId) {
        if (Utils.isNetworkAvailable(getActivity())) {
            client.getMentions(maxId, new AsyncHttpResponseHandler() {
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
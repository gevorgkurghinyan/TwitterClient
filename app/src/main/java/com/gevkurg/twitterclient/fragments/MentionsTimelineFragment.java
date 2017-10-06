package com.gevkurg.twitterclient.fragments;

import com.gevkurg.twitterclient.TwitterApplication;
import com.gevkurg.twitterclient.network.TwitterClient;
import com.gevkurg.twitterclient.network.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;


public class MentionsTimelineFragment extends TweetsListFragment {

    private TwitterClient client = TwitterApplication.getRestClient();

    public static MentionsTimelineFragment newInstance() {
        MentionsTimelineFragment fragment = new MentionsTimelineFragment();
        return fragment;
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
                client.getMentions(maxId, new AsyncHttpResponseHandler() {
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
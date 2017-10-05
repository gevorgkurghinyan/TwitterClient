package com.gevkurg.twitterclient.fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gevkurg.twitterclient.R;
import com.gevkurg.twitterclient.models.Tweet;
import com.gevkurg.twitterclient.TwitterApplication;
import com.gevkurg.twitterclient.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class ComposeTweetFragment extends DialogFragment {
    private static final int MAX_CHARS = 140;

    private EditText etNewTweet;
    private TextView tvCharsLeft;
    private StatusUpdateListener listener;
    private String inReplyToTweetId;

    public ComposeTweetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_compose_tweet, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        etNewTweet = view.findViewById(R.id.etNewTweet);
        tvCharsLeft = view.findViewById(R.id.tvCharsLeft);
        setupCharacterLimit();
        setupTweetButton(view);
        return view;
    }

    private void setupTweetButton(View view){
        Button btnSubmitNewTweet = view.findViewById(R.id.btnSubmitNewTweet);
        btnSubmitNewTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwitterClient client = TwitterApplication.getRestClient();
                client.postTweet(etNewTweet.getText().toString(), inReplyToTweetId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Tweet tweet = Tweet.fromJson(response);
                        if (listener != null) {
                            listener.onStatusUpdated(tweet);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        //TODO: handle error
                    }
                });
            }
        });
    }

    private void setupCharacterLimit() {
        tvCharsLeft.setText(String.valueOf(MAX_CHARS));
        etNewTweet.setFilters(new InputFilter[] {new InputFilter.LengthFilter(MAX_CHARS)});
        etNewTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int charsLeft = MAX_CHARS - s.length();
                tvCharsLeft.setText(String.valueOf(charsLeft));
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing.
            }
        });
    }

    public void setListener(StatusUpdateListener listener) {
        this.listener = listener;
    }

    public void setInReplyToTweetId(String inReplyToTweetId) {
        this.inReplyToTweetId = inReplyToTweetId;
    }

    public interface StatusUpdateListener {
        void onStatusUpdated(Tweet tweet);
    }
}
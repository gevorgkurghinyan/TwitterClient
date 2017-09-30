package com.gevkurg.twitterclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gevkurg.twitterclient.R;
import com.gevkurg.twitterclient.models.Tweet;
import com.gevkurg.twitterclient.TwitterApplication;
import com.gevkurg.twitterclient.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;


public class ComposeActivity extends AppCompatActivity {
    private static final int MAX_CHARS = 140;
    public static int REQUEST_CODE = 100;

    private EditText etNewTweet;
    private TextView tvCharsLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        setupCharacterLimit();
    }

    public void submitTweet(View view) {
        TwitterClient client = TwitterApplication.getRestClient();
        client.postTweet(etNewTweet.getText().toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet tweet = Tweet.fromJson(response);
                Intent intent = new Intent();
                intent.putExtra("tweet", Parcels.wrap(tweet));
                setResult(REQUEST_CODE, intent);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //TODO: handle error
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
}

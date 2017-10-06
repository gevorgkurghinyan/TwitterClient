package com.gevkurg.twitterclient.network;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.gevkurg.twitterclient.R;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 *
 * This is the object responsible for communicating with a REST API.
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes:
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 *
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 *
 */
public class TwitterClient extends OAuthBaseClient {
    public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
    public static final String REST_URL = "https://api.twitter.com/1.1";
    public static final String REST_CONSUMER_KEY = "elhT1r3XlAImjB984xqVsHL0E";
    public static final String REST_CONSUMER_SECRET = "iilNw5WBeoF6Ex9br5ZTpyW2ydex7QQljKsFgHCXmL2N0iFNOc";
    public static final int TWEETS_PER_PAGE = 25;

    // Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
    public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

    // See https://developer.chrome.com/multidevice/android/intents
    public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

    public TwitterClient(Context context) {
        super(context, REST_API_INSTANCE,
                REST_URL,
                REST_CONSUMER_KEY,
                REST_CONSUMER_SECRET,
                String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
                        context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
    }

    public void getHomeTimeline(String maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", TWEETS_PER_PAGE);
        params.put("include_entities", true);
        params.put("max_id", maxId);
        client.get(apiUrl, params, handler);
    }

    public void getMentions(String maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", TWEETS_PER_PAGE);
        params.put("max_id", maxId);
        params.put("include_rts", 1);
        client.get(apiUrl, params, handler);
    }

    public void getUser(String username, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("users/show.json");
        RequestParams params = new RequestParams();
        params.put("include_entities", "false");
        params.put("screen_name", username);
        client.get(apiUrl, params, handler);
    }

    public void getCredentials(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        RequestParams params = new RequestParams();
        params.put("skip_status", 1);
        params.put("include_entities", "false");
        client.get(apiUrl, params, handler);
    }

    public void getUserTimeline(String username, String maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put("max_id", maxId);
        params.put("screen_name", username);
        params.put("count", TWEETS_PER_PAGE);
        client.get(apiUrl, params, handler);
    }

    public void postTweet(String tweetBody, String tweetId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", tweetBody);
        if (tweetId != null) {
            params.put("in_reply_to_status_id", tweetId);
        }
        client.post(apiUrl, params, handler);
    }

    public void replyToStatus(String status, Long statusId, final AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", status);
        if (statusId != null) {
            params.put("in_reply_to_status_id", statusId);
        }
        client.post(apiUrl, params, handler);
    }

    public void retweet(Long id, final AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(String.format("statuses/retweet/%d.json", id));
        client.post(apiUrl, handler);
    }

    public void favorite(Long id, final AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("favorites/create.json");
        RequestParams params = new RequestParams();
        params.put("id", id);
        getClient().post(apiUrl, params, handler);
    }

    public void unFavorite(Long id, final AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("favorites/destroy.json");
        RequestParams params = new RequestParams();
        params.put("id", id);
        getClient().post(apiUrl, params, handler);
    }

    public void searchTweets(String query, final AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("/search/tweets.json");
        RequestParams params = new RequestParams();
        params.put("q", query);
        getClient().get(apiUrl, params, handler);
    }
}

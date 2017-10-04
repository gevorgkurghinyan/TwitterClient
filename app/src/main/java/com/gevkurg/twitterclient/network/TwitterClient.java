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

    public void getHomeTimeline(boolean isFirstLoad, long id, AsyncHttpResponseHandler handler) {
        if (isFirstLoad) {
            getHomeTimeline(id, "since_id", handler);
        } else {
            getHomeTimeline(id, "max_id", handler);
        }
    }

    private void getHomeTimeline(long id, String paramName, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", TWEETS_PER_PAGE);
        params.put("include_entities", true);
        params.put(paramName, id);
        client.get(apiUrl, params, handler);
    }

    public void getMentions(boolean isFirstLoad, long id, AsyncHttpResponseHandler handler) {
        if (isFirstLoad) {
            getMentions(id, "since_id", handler);
        } else {
            getMentions(id, "max_id", handler);
        }
    }

    private void getMentions(long id, String paramName, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", TWEETS_PER_PAGE);
        params.put(paramName, id);
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

    public void getUserTimeline(String username, boolean isFirstLoad, long id, AsyncHttpResponseHandler handler) {
        if (isFirstLoad) {
            getUserTimeline(username, id, "since_id", handler);
        } else {
            getUserTimeline(username, id, "max_id", handler);
        }
    }

    private void getUserTimeline(String username, long id, String paramName, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put(paramName, id);
        params.put("screen_name", username);
        params.put("count", TWEETS_PER_PAGE);
        client.get(apiUrl, params, handler);
    }

    public void postTweet(String tweetBody, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", tweetBody);
        client.post(apiUrl, params, handler);
    }

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}

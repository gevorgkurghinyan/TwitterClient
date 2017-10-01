package com.gevkurg.twitterclient.network;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.gevkurg.twitterclient.R;


public class Utils {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static void showSnackBarForInternetConnection(View view, Context context) {
        Snackbar.make(view,
                R.string.no_internet_text,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.snackbar_internet_action_text,
                        getSnackBarActionOnClickListener(context))
                .setActionTextColor(Color.YELLOW)
                .setDuration(5000)
                .show();
    }

    public static void showSnackBar(View view, Context context, int resourceId) {
        Snackbar.make(view,
                resourceId,
                Snackbar.LENGTH_INDEFINITE)
                .setDuration(5000)
                .show();
    }

    private static View.OnClickListener getSnackBarActionOnClickListener(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
                context.startActivity(settingsIntent);
            }
        };
    }
}

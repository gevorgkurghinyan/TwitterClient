package com.gevkurg.twitterclient.database;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = TweetDatabase.NAME, version = TweetDatabase.VERSION)
public class TweetDatabase {

    public static final String NAME = "TwitterClientDatabase";

    public static final int VERSION = 1;
}

package com.hnweb.eventnotifier.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by shree on 06-12-2016.
 */
public class CacheUtility {

    private final Context context;
    private final SharedPreferences prefs;

    public CacheUtility(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        
    }

    public void put(String request_tag, String data) {
        prefs.edit().putString(request_tag,data).apply();
    }

    public String get(String request_tag) {
      return   prefs.getString(request_tag,null);
    }
}

package com.easou.androidsdk.webviewutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by nina on 3/6 0006.
 */

public class ConfigerManagner {
    private static ConfigerManagner configManger;
    private static Context context;

    ConfigerManagner(Context context) {
        this.context = context.getApplicationContext();
    }

    public static ConfigerManagner getInstance(Context context) {
        if (configManger == null) {
            configManger = new ConfigerManagner(context);
        }
        return configManger;
    }

    protected SharedPreferences getMySharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean setString(String name, String value) {
        return getMySharedPreferences().edit().putString(name, value).commit();
    }

    public String getString(String name) {
        return getMySharedPreferences().getString(name, "");
    }
}
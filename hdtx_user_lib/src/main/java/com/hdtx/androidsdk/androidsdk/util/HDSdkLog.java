package com.hdtx.androidsdk.androidsdk.util;

import android.util.Log;

public class HDSdkLog {

    private static String TAG = "HDSDKLOG";

    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    public static void c(String tag, String msg) {
        Log.d(tag, msg);

    }
}

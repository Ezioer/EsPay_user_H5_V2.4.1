package com.hdtx.androidsdk.util;

import android.text.TextUtils;
import android.util.Log;

public class HDPayLog {

    public static boolean DEBUGMODE = false;
    private static String TAG = "HDPAYLOG";

    public static void setDebugmode(boolean DEBUGMODE) {
        HDPayLog.DEBUGMODE = DEBUGMODE;
    }

    public static void d(String tag, String msg) {
        if (DEBUGMODE) {
            if (TextUtils.isEmpty(tag)) {
                d("the tag is null");
            }
            if (TextUtils.isEmpty(msg)) {
                c(tag, "the tag is null");
            } else {
                c(tag, msg);
            }
        }
    }

    public static void d(String msg) {
        if (DEBUGMODE) {
            Log.d(TAG, msg);
        }
    }

    public static void c(String tag, String msg) {
        if (DEBUGMODE) {
            Log.d(tag, msg);
        }
    }
}

package hdtx.androidsdk.util;

import android.util.Log;

public class ESdkLog {

    private static String TAG = "ESDKLOG";
    public static boolean ISDEBUG = true;

    public static void d(String msg) {
        if (ISDEBUG) {
            Log.d(TAG, msg);
        }
    }

    public static void c(String tag, String msg) {
        if (ISDEBUG) {
            Log.d(tag, msg);
        }
    }
}

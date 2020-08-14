package com.easou.androidsdk.util;

import android.util.Log;

public class ESdkLog {

	private static String TAG = "ESDKLOG";

	public static void d(String msg){
		Log.d(TAG, msg);
	}

	public static void c(String tag,String msg){
		Log.d(tag, msg);

	}
}

package com.hdtx.androidsdk.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络相关工具
 *
 * @author ：Heavy
 * @time ：2014年10月28日
 */
public class NetHelper {

    /**
     * 判断设备是否可以连接网络
     *
     * @param context
     * @return
     */
    public static boolean isNet(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean connectedM = false;
        if (mobile != null) {
            connectedM = mobile.isConnected();
        }
        //boolean connectedM =mobile.isConnected();
        boolean connectedW = wifi.isConnected();
        if (connectedM || connectedW) {
            return true;
        }
        return false;
    }

}

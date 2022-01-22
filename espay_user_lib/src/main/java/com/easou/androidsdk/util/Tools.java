package com.easou.androidsdk.util;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.easou.androidsdk.Starter;
import com.easou.androidsdk.data.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class Tools {
    /**
     * 去掉url中的路径，留下请求参数部分
     *
     * @param strURL url地址
     * @return url请求参数部分
     */
    private static String TruncateUrlPage(String strURL) {
        String strAllParam = null;
        String[] arrSplit = null;

        strURL = strURL.trim().toLowerCase();

        arrSplit = strURL.split("[?]");
        if (strURL.length() > 1) {
            if (arrSplit.length > 1) {
                if (arrSplit[1] != null) {
                    strAllParam = arrSplit[1];
                }
            }
        }

        return strAllParam;
    }

    /**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     *
     * @param URL url地址
     * @return url请求参数部分
     */
    private static Map<String, String> URLRequest(String URL) {
        Map<String, String> mapRequest = new HashMap<String, String>();

        String[] arrSplit = null;

        String strUrlParam = TruncateUrlPage(URL);
        if (strUrlParam == null) {
            return mapRequest;
        }
        //每个键值为一组 www.2cto.com
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");
            //解析出键值
            if (arrSplitEqual.length > 1) {
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            } else {
                if (arrSplitEqual[0] != "") {
                    //只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }

    public static String getParam(String url, String paramName) {
        Map<String, String> map = URLRequest(url);
        String paramValue = "";
        if (!TextUtils.isEmpty(map.get(paramName))) {
            paramValue = map.get(paramName);
        }
        return paramValue;
    }

    public static String getNetIp() {
        URL infoUrl = null;
        InputStream inStream = null;
        String line = "";
        try {
            infoUrl = new URL("https://pv.sohu.com/cityjson?ie=utf-8");
            URLConnection connection = infoUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
                StringBuilder strber = new StringBuilder();
                while ((line = reader.readLine()) != null)
                    strber.append(line + "\n");
                inStream.close();
                // 从反馈的结果中提取出IP地址
                int start = strber.indexOf("{");
                int end = strber.indexOf("}");
                String json = strber.substring(start, end + 1);
                if (json != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        line = jsonObject.optString("cip");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                return line;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    /**
     * 获取设备ip地址
     */
    public static String getPsdnIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        //if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet6Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
        }
        return "";
    }


    /* 获取当前应用程序的包名
     * @param context 上下文对象
     * @return 返回包名
     */
    public static String getAppProcessName(Context context) {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }

    public static boolean noPermission(Context context) {

        PackageManager pm = context.getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", getAppProcessName(context)));
        if (permission) {
            return false;
        }
        return true;
    }

    /**
     * 获取设备IMEI
     */
    public static String getDeviceImei(Context context) {

        String imei = CommonUtils.getEsDeviceID(context);

        try {
            TelephonyManager manager = (TelephonyManager) context.getApplicationContext().
                    getSystemService(context.getApplicationContext().TELEPHONY_SERVICE);

            String deviceImei = manager.getDeviceId();

            if (!TextUtils.isEmpty(deviceImei)) {

                if (!deviceImei.contains("000000000000000") && deviceImei.length() <= 18
                        && deviceImei.length() <= 10) {
                    imei = deviceImei;
                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            ESdkLog.d("deviceiderror" + e.toString());
            e.printStackTrace();
        }
        return imei;
    }

    /**
     * 获取设备IMSI
     */
    public static String getDeviceImsi(Context context) {

        String imsi = "0";

        try {
            TelephonyManager manager = (TelephonyManager) context.getApplicationContext().
                    getSystemService(context.getApplicationContext().TELEPHONY_SERVICE);

            String deviceImsi = manager.getSubscriberId();

            if (!TextUtils.isEmpty(deviceImsi)) {
                imsi = deviceImsi;
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return imsi;
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return toURLEncoded(android.os.Build.VERSION.RELEASE.trim());
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return toURLEncoded(android.os.Build.MODEL.trim());
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return toURLEncoded(android.os.Build.BRAND.trim());
    }


    public static String getHostName() {
        if (Constant.DOMAIN.contains("service")) {
            String host = Constant.HOST_NAME;
            if (host.equals("")) {
                host = Constant.HOST_NAME_DEFAULT;
            }
            return host;
        } else {
            return "";
        }
    }

    public static String toURLEncoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            return "";
        }
        try {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return "";
    }

    /**
     * 反射 禁止弹窗
     */
    public static void disableAPIDialog() {
        if (Build.VERSION.SDK_INT < 28) return;
        try {
            Class clazz = Class.forName("android.app.ActivityThread");
            Method currentActivityThread = clazz.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            Object activityThread = currentActivityThread.invoke(null);
            Field mHiddenApiWarningShown = clazz.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取设备唯一标识
     *
     * @return
     */
    public static String getOnlyId() {

        StringBuilder deviceId = new StringBuilder();
        String imei = getImei();
        String androidId = getAndroidId();
        String serial = getSerNum();
        String uuid = getUuid().replace("-", "");
        if (imei != null && imei.length() > 0) {
            deviceId.append(imei);
            deviceId.append("|");
        }
        if (androidId != null && androidId.length() > 0) {
            deviceId.append(androidId);
            deviceId.append("|");
        }
        if (serial != null && serial.length() > 0) {
            deviceId.append(serial);
            deviceId.append("|");
        }
        if (uuid != null && uuid.length() > 0) {
            deviceId.append(uuid);
            deviceId.append("|");
        }
        if (deviceId.length() > 0) {
            try {
                byte[] hash = getHashByString(deviceId.toString());
                String sha1 = byteToHex(hash);
                if (sha1 != null && sha1.length() > 0) {
                    return sha1;
                }
            } catch (Exception e) {
            }
        }
        return "";
    }

    private static String byteToHex(byte[] hash) {
        StringBuilder builder = new StringBuilder();
        String temp;
        for (int i = 0; i < hash.length; i++) {
            temp = (Integer.toHexString(hash[i] & 0xFF));
            if (temp.length() == 1) {
                builder.append("0");
            }
            builder.append(temp);
        }
        return builder.toString().toUpperCase(Locale.CHINA);
    }

    private static byte[] getHashByString(String data) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.reset();
            messageDigest.update(data.getBytes("UTF-8"));
            return messageDigest.digest();
        } catch (Exception e) {
            return "".getBytes();
        }
    }

    public static String getAndroidId() {
        try {
            return Settings.System.getString(Starter.mActivity.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {

        }
        return "";
    }

    public static String getSerNum() {
        try {
            return Build.SERIAL;
        } catch (Exception e) {
        }
        return "";
    }

    public static String getImei() {
        try {
            TelephonyManager manager = (TelephonyManager) Starter.mActivity.getApplicationContext().
                    getSystemService(Starter.mActivity.getApplicationContext().TELEPHONY_SERVICE);
            return manager.getDeviceId();
        } catch (Exception e) {

        }
        return "";
    }

    public static String getUuid() {
        try {
            //获取硬件信息级uuid，根据硬件来生成，保证唯一
            String dev = "112358" + Build.BOARD.length() % 10 +
                    Build.BRAND.length() % 10 +
                    Build.DEVICE.length() % 10 +
                    Build.HARDWARE.length() % 10 +
                    Build.ID.length() % 10 +
                    Build.MODEL.length() % 10 +
                    Build.PRODUCT.length() % 10 +
                    Build.SERIAL.length() % 10;
            return new UUID(dev.hashCode(), Build.SERIAL.hashCode()).toString();
        } catch (Exception e) {
            return "";
        }
    }
}

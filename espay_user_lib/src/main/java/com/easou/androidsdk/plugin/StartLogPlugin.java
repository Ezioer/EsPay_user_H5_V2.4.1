package com.easou.androidsdk.plugin;

import android.text.TextUtils;

import com.easou.androidsdk.Starter;
import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.data.ESConstant;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.util.ESdkLog;
import com.easou.androidsdk.util.HttpLogHelper;
import com.easou.androidsdk.util.Tools;

import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by LinQB on 2020/2/22.
 */

public class StartLogPlugin {


    /**
     * 应用启动日志
     */
    public static void startAppLoadLog() {

        HttpLogHelper.sendHttpRequest(Constant.MAIN_URL + Tools.getHostName() + Constant.APP_LOAD_URL,
                getSendParam(1, null, null));
    }

    //上传游戏角色数据日志
    public static void gamePlayerDataLog(Map<String, String> info) {
        try {
            HttpLogHelper.sendHttpRequest(Constant.MAIN_URL + Tools.getHostName() + Constant.GAME_PLAYER_LOG,
                    getPlayerDataParams(info));
        } catch (Exception e) {
            ESdkLog.d("上传角色数据出错");
        }
    }

    private static String getPlayerDataParams(Map<String, String> playerInfo) {
        String qn = CommonUtils.readPropertiesValue(Starter.mActivity, Constant.QN);
        String param = "projectMark=" + qn.substring(0, 2) +
                "&playerId=" + playerInfo.get(ESConstant.PLAYER_ID) +
                "&serverId=" + playerInfo.get(ESConstant.PLAYER_SERVER_ID) +
                "&esAppId=" + CommonUtils.readPropertiesValue(Starter.mActivity, Constant.APP_ID) +
                "&accountId=" + Constant.ESDK_USERID +
                "&playerLevel=" + playerInfo.get(ESConstant.PLAYER_LEVEL) +
                "&levelNickname=" + playerInfo.get(ESConstant.LEVEL_NICK_NAME) +
                "&playerName=" + playerInfo.get(ESConstant.PLAYER_NAME) +
                "&serverName=" + playerInfo.get(ESConstant.SERVER_NAME) +
                "&field1=" + Integer.valueOf(playerInfo.get("field1")) +
                "&field2=" + Integer.valueOf(playerInfo.get("field2")) +
                "&field3=" + Integer.valueOf(playerInfo.get("field3")) +
                "&field4=" + Integer.valueOf(playerInfo.get("field4")) +
                "&field5=" + Integer.valueOf(playerInfo.get("field5")) +
                "&field6=" + playerInfo.get("field6") +
                "&field7=" + playerInfo.get("field7") +
                "&field8=" + playerInfo.get("field8") +
                "&field9=" + playerInfo.get("field9") +
                "&field10=" + playerInfo.get("field10") +
                "&createdPlayerTime=" + playerInfo.get(ESConstant.CREATEDTIME);
        return param;
    }

    /**
     * 游戏登录日志
     */
    public static void startGameLoginLog(Map<String, String> playerInfo) {

        if (TextUtils.isEmpty(playerInfo.get(ESConstant.PLAYER_NAME)) ||
                TextUtils.isEmpty(playerInfo.get(ESConstant.PLAYER_ID)) ||
                TextUtils.isEmpty(playerInfo.get(ESConstant.PLAYER_LEVEL)) ||
                TextUtils.isEmpty(playerInfo.get(ESConstant.PLAYER_SERVER_ID))) {

            System.out.println("上传游戏登陆日志参数有误，请检查！");
            return;
        }

        HttpLogHelper.sendHttpRequest(Constant.MAIN_URL + Tools.getHostName() + Constant.GAME_LOGIN_URL,
                getSendParam(2, playerInfo, null));
    }

    /**
     * 游戏订购日志
     */
    public static void startGameOrderLog(String money) {

        HttpLogHelper.sendHttpRequest(Constant.MAIN_URL + Tools.getHostName() + Constant.GAME_ORDER_URL,
                getSendParam(3, null, money));
    }

    /**
     * SDK登录日志
     */
    public static void startSdkLoginLog(String esId, String userName) {

        HttpLogHelper.sendHttpRequest(Constant.MAIN_URL + Tools.getHostName() + Constant.SDK_LOGIN_URL,
                getSendParam(4, null, null));
    }


    /**
     * 设置日志所需参数
     */
    private static String getSendParam(int type, Map<String, String> playerInfo, String money) {

        String param = "appId=" + CommonUtils.readPropertiesValue(Starter.mActivity, Constant.APP_ID)
                + "&qn=" + CommonUtils.readPropertiesValue(Starter.mActivity, Constant.QN)
                + "&imei=" + Constant.IMEI + "&imsi=" + Tools.getDeviceImsi(Starter.mActivity);

        if (type == 1) {
            // 应用启动日志所需参数
//            param = param + "&deviceId=" + Constant.IMEI + "&oaid=" + Constant.OAID + "&ipAddr=" + clientIp +
//                    "&ipAddr1=" + Constant.NET_IP + "&userAgent=" + userAgent + "&phoneBrand=" + toURLEncoded(Tools.getDeviceBrand()) +
//                    "&phoneModel=" + toURLEncoded(Tools.getSystemModel()) + "&phoneVersion=" + Tools.getSystemVersion();

        } else if (type == 2) {

            String playerName = toURLEncoded(playerInfo.get(ESConstant.PLAYER_NAME));

            // 游戏登录日志所需参数
            param = param + "&deviceId=" + Constant.IMEI + "&accountId=" + Constant.ESDK_USERID +
                    "&playerId=" + playerInfo.get(ESConstant.PLAYER_ID) +
                    "&playerName=" + playerName +
                    "&playerLevel=" + playerInfo.get(ESConstant.PLAYER_LEVEL) +
                    "&serverId=" + playerInfo.get(ESConstant.PLAYER_SERVER_ID);

        } else if (type == 3) {
            // 游戏订购日志所需参数
            if (money != null) {
                param = param + "&accountId=" + Constant.ESDK_USERID + "&amount=" + money;
            }
        } else {
            // sdk登陆日志所需参数
//            param = param + "&deviceId=" + Constant.IMEI + "&oaid=" + Constant.OAID + "&accountId=" + esid +
//                    "&userName=" + esUserName;
        }

        return param;
    }

    private static String toURLEncoded(String paramString) {
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
}

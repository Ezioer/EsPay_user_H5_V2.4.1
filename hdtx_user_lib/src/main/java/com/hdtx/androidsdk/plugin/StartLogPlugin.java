package com.hdtx.androidsdk.plugin;

import android.text.TextUtils;

import com.hdtx.androidsdk.Starter;
import com.hdtx.androidsdk.data.Constant;
import com.hdtx.androidsdk.data.HDConstant;
import com.hdtx.androidsdk.http.ReYunLogHelper;
import com.hdtx.androidsdk.ui.HDUserWebActivity;
import com.hdtx.androidsdk.util.CommonUtils;
import com.hdtx.androidsdk.util.HDSdkLog;
import com.hdtx.androidsdk.util.HttpLogHelper;
import com.hdtx.androidsdk.util.Tools;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by LinQB on 2020/2/22.
 */

public class StartLogPlugin {

    //上传游戏角色数据日志
    public static void gamePlayerDataLog(Map<String, String> info, boolean isTurnExt) {
        try {
            if (isTurnExt) {
                //转端日志
                if (Constant.isTurnExtUser == 0) {
                    ReYunLogHelper.sendHttpRequest(Constant.MAIN_URL + Tools.getHostName() + Constant.REYUN_ADD_PLAYER_LOG,
                            getGamePlayerInfo(info));
                }
            } else {
                //传送游戏角色数据给h5
                HDUserWebActivity.clientToJS(Constant.YSTOJS_GAME_LOGIN_DATA, info);
            }

        } catch (Exception e) {
            HDSdkLog.d("上传角色数据出错");
        }
    }

    private static JSONObject getGamePlayerInfo(Map<String, String> playerInfo) {
        JSONObject player = new JSONObject();
        try {
            player.put("appId", Integer.valueOf(CommonUtils.readPropertiesValue(Starter.mActivity, Constant.APP_ID)));
            player.put("qn", CommonUtils.readPropertiesValue(Starter.mActivity, Constant.QN));
            player.put("playerId", playerInfo.get(HDConstant.PLAYER_ID));
            player.put("playerName", playerInfo.get(HDConstant.PLAYER_NAME));
            player.put("serverId", playerInfo.get(HDConstant.PLAYER_SERVER_ID));
            player.put("accountId", Constant.ESDK_USERID);
            player.put("createRoleTime", System.currentTimeMillis());
            player.put("userId", Constant.ESDK_USERID);
            player.put("logSource", "1");
        } catch (Exception e) {

        }
        return player;
    }

    /**
     * 游戏登录日志
     */
    public static void startGameLoginLog(Map<String, String> playerInfo, boolean isTurnExt) {
        if (TextUtils.isEmpty(playerInfo.get(HDConstant.PLAYER_NAME)) ||
                TextUtils.isEmpty(playerInfo.get(HDConstant.PLAYER_ID)) ||
                TextUtils.isEmpty(playerInfo.get(HDConstant.PLAYER_LEVEL)) ||
                TextUtils.isEmpty(playerInfo.get(HDConstant.PLAYER_SERVER_ID))) {

            System.out.println("上传游戏登陆日志参数有误，请检查！");
            return;
        }
        if (isTurnExt) {
            //转端
            ReYunLogHelper.sendHttpRequest(Constant.MAIN_URL + Tools.getHostName() + Constant.REYUN_ADD_LOGIN_LOG,
                    getGameLoginInfo(playerInfo));
        } else {
            HttpLogHelper.sendHttpRequest(Constant.MAIN_URL + Tools.getHostName() + Constant.GAME_LOGIN_URL,
                    getSendParam(2, playerInfo, null));
        }
    }

    private static JSONObject getGameLoginInfo(Map<String, String> playerInfo) {
        JSONObject player = new JSONObject();
        try {
            player.put("appId", Integer.valueOf(CommonUtils.readPropertiesValue(Starter.mActivity, Constant.APP_ID)));
            player.put("qn", CommonUtils.readPropertiesValue(Starter.mActivity, Constant.QN));
            player.put("accountId", Constant.ESDK_USERID);
            player.put("os", "Android");
            player.put("ip", Constant.NET_IP);
            player.put("ua", Constant.ua);
            player.put("vid", "");
            player.put("aid", "");
            player.put("loginTime", System.currentTimeMillis());
            player.put("userId", Constant.ESDK_USERID);
            player.put("userName", Constant.ESDK_USERID);
            player.put("imei", Constant.IMEI);
            player.put("oaid", Constant.OAID);
            player.put("logSource", "1");
            player.put("info", Tools.getOnlyId());
        } catch (Exception e) {

        }
        return player;
    }

    /**
     * 游戏订购日志
     */
    public static void startGameOrderLog(String money) {
        HttpLogHelper.sendHttpRequest(Constant.MAIN_URL + Tools.getHostName() + Constant.GAME_ORDER_URL,
                getSendParam(3, null, money));
    }

    //转端热云日志 游戏购买
    public static void startGamePayLog(String money, String time) {
        ReYunLogHelper.sendHttpRequest(Constant.MAIN_URL + Tools.getHostName() + Constant.REYUN_ADD_PAY_LOG,
                getGamePayInfo(money, time));
    }

    private static JSONObject getGamePayInfo(String money, String time) {
        JSONObject player = new JSONObject();
        try {
            player.put("appId", Integer.valueOf(CommonUtils.readPropertiesValue(Starter.mActivity, Constant.APP_ID)));
            player.put("qn", CommonUtils.readPropertiesValue(Starter.mActivity, Constant.QN));
            player.put("accountId", Constant.ESDK_USERID);
            player.put("amount", money);
            player.put("orderNo", "");
            player.put("cpOrderNo", "");
            player.put("playerId", Constant.playerId);
            player.put("playerLevel", Constant.playerLevel);
            player.put("playerName", Constant.playerName);
            player.put("serverId", Constant.serverId);
            player.put("payTime", time);
            player.put("userId", Constant.IMEI);
            player.put("logSource", "1");
        } catch (Exception e) {

        }
        return player;
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

            String playerName = toURLEncoded(playerInfo.get(HDConstant.PLAYER_NAME));

            // 游戏登录日志所需参数
            param = param + "&deviceId=" + Constant.IMEI + "&accountId=" + Constant.ESDK_USERID +
                    "&playerId=" + playerInfo.get(HDConstant.PLAYER_ID) +
                    "&playerName=" + playerName +
                    "&playerLevel=" + playerInfo.get(HDConstant.PLAYER_LEVEL) +
                    "&serverId=" + playerInfo.get(HDConstant.PLAYER_SERVER_ID);

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

    private static String getPlayerDataParams(Map<String, String> playerInfo) {
        String qn = CommonUtils.readPropertiesValue(Starter.mActivity, Constant.QN);
        String param = "projectMark=" + qn.substring(0, 2) +
                "&playerId=" + playerInfo.get(HDConstant.PLAYER_ID) +
                "&serverId=" + playerInfo.get(HDConstant.PLAYER_SERVER_ID) +
                "&esAppId=" + CommonUtils.readPropertiesValue(Starter.mActivity, Constant.APP_ID) +
                "&accountId=" + Constant.ESDK_USERID +
                "&playerLevel=" + playerInfo.get(HDConstant.PLAYER_LEVEL) +
                "&levelNickname=" + playerInfo.get(HDConstant.LEVEL_NICK_NAME) +
                "&playerName=" + playerInfo.get(HDConstant.PLAYER_NAME) +
                "&serverName=" + playerInfo.get(HDConstant.SERVER_NAME) +
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
                "&createdPlayerTime=" + playerInfo.get(HDConstant.CREATEDTIME);
        return param;
    }
}

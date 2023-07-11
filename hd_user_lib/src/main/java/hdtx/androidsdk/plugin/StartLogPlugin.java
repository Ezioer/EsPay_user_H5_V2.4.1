package hdtx.androidsdk.plugin;

import android.text.TextUtils;

import hdtx.androidsdk.Starter;
import hdtx.androidsdk.data.Constant;
import hdtx.androidsdk.data.ESConstant;
import hdtx.androidsdk.http.ReYunLogHelper;
import hdtx.androidsdk.ui.ESUserWebActivity;
import hdtx.androidsdk.util.CommonUtils;
import hdtx.androidsdk.util.ESdkLog;
import hdtx.androidsdk.util.HttpLogHelper;
import hdtx.androidsdk.util.Tools;

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
                ESUserWebActivity.clientToJS(Constant.YSTOJS_GAME_LOGIN_DATA, info);
            }

        } catch (Exception e) {
            ESdkLog.d("上传角色数据出错");
        }
    }

    private static JSONObject getGamePlayerInfo(Map<String, String> playerInfo) {
        JSONObject player = new JSONObject();
        try {
            player.put("appId", Integer.valueOf(CommonUtils.readPropertiesValue(Starter.mActivity, Constant.APP_ID)));
            player.put("qn", CommonUtils.readPropertiesValue(Starter.mActivity, Constant.QN));
            player.put("playerId", playerInfo.get(ESConstant.PLAYER_ID));
            player.put("playerName", playerInfo.get(ESConstant.PLAYER_NAME));
            player.put("serverId", playerInfo.get(ESConstant.PLAYER_SERVER_ID));
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
        if (TextUtils.isEmpty(playerInfo.get(ESConstant.PLAYER_NAME)) ||
                TextUtils.isEmpty(playerInfo.get(ESConstant.PLAYER_ID)) ||
                TextUtils.isEmpty(playerInfo.get(ESConstant.PLAYER_LEVEL)) ||
                TextUtils.isEmpty(playerInfo.get(ESConstant.PLAYER_SERVER_ID))) {

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

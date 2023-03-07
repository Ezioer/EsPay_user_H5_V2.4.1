package com.easou.androidsdk.plugin;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.text.TextUtils;

import com.easou.androidsdk.Starter;
import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.ui.ESUserWebActivity;
import com.easou.androidsdk.ui.FloatView;
import com.easou.androidsdk.util.AES;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.util.ESdkLog;
import com.easou.androidsdk.util.NetworkUtils;
import com.easou.androidsdk.util.ThreadPoolManager;
import com.easou.androidsdk.util.Tools;

import java.net.URLEncoder;
import java.util.HashMap;

public class StartESUserPlugin {

    /**
     * 登陆接口
     */
    public static void loginSdk() {
        //设置支付渠道
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                if (!Constant.IS_LOGINED) {
                    startH5Login();
                }
                Looper.loop();
            }
        });
    }

    /**
     * 打开H5 SDK页面
     */
    public static void startH5Login() {
        Constant.NET_IP = Tools.getNetIp();
        enterH5View();
    }

    /**
     * 进入H5 SDK页面
     */
    public static void enterH5View() {
        Intent intent = new Intent();
        intent.putExtra("params", getNewParam());
        intent.setClass(Starter.mActivity, ESUserWebActivity.class);
        Starter.mActivity.startActivity(intent);
    }

    /**
     * 获取SDK用户信息
     */
    public static void getH5UserInfo() {
        ESUserWebActivity.clientToJS(Constant.YSTOJS_GET_USERINFO, null);
    }

    //登录google账号
    public static void loginGoogle(String idToken, String id, boolean isBindGoogle) {
        HashMap map = new HashMap();
        map.put("idToken", idToken);
        map.put("userId", id);
        map.put("isBind", isBindGoogle ? "1" : "0");
        ESUserWebActivity.clientToJS(Constant.YSTOJS_GAME_LOGINGOOGLE, map);
        enterH5View();
    }

    public static void loginFacebook(String token, String userId, boolean isBindFacebook) {
        HashMap map = new HashMap();
        map.put("token", token);
        map.put("userId", userId);
        map.put("isBind", isBindFacebook ? "1" : "0");
        ESUserWebActivity.clientToJS(Constant.YSTOJS_GAME_LOGINFACEBOOK, map);
        enterH5View();
    }

    /**
     * 打开实名认证页面
     */
    public static void showUserCert() {
        ESUserWebActivity.clientToJS(Constant.YSTOJS_USERCERT, null);
        enterH5View();
    }

    /**
     * 显示SDK页面
     */
    public static void showSdkView() {

        ESUserWebActivity.clientToJS(Constant.YSTOJS_CLICK_FLOATVIEW, null);
        enterH5View();
    }

    //退出登录
    public static void changeAccount() {
        showSdkView();
        ESUserWebActivity.clientToJS(Constant.YSTOJS_GAME_LOGOUT, null);
    }

    /**
     * 获取网页SDK所需参数
     */
    public static String getNewParam() {

        String encryptKey = CommonUtils.readPropertiesValue(Starter.mActivity, "key");
        String key = CommonUtils.getKey(Starter.mActivity);
        try {
            encryptKey = AES.encrypt(encryptKey, key);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String appid = CommonUtils.readPropertiesValue(Starter.mActivity, Constant.APP_ID);
        String token = CommonUtils.getH5Token(Starter.mActivity);

        if (TextUtils.isEmpty(token)) {
            try {
//				AuthBean bean = CommonUtils.getAuthBean(Starter.mActivity,appid);
                String bean = CommonUtils.getTokenFromSD(appid);
                if (bean != null) {
//					token = bean.getToken().getToken();
                    token = bean;
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        String json = URLEncoder.encode(Tools.getOnlyId().toString());
        String param = "&appId=" + appid
                + "&qn=" + CommonUtils.readPropertiesValue(Starter.mActivity, Constant.QN)
                + "&partnerId=" + CommonUtils.readPropertiesValue(Starter.mActivity, Constant.PARTENER_ID)
                + "&secretKey=" + encryptKey
                + "&deviceId=" + Constant.IMEI
                + "&clientIp=" + Constant.NET_IP
                + "&oaId=" + Constant.OAID
                + "&phoneOs=" + Constant.SDK_PHONEOS
                + "&phoneBrand=" + Tools.getDeviceBrand()
                + "&phoneModel=" + Tools.getSystemModel()
                + "&phoneVersion=" + Tools.getSystemVersion()
                + "&userToken=" + token
                + "&customDeviceId=" + Constant.CUSTOMDEVICES
                + "&customJson=" + json
                + "&isSimulator=" + Constant.IS_SIMULATOR
                + "&netMode=" + NetworkUtils.getNetworkState(Starter.mActivity)
                + "&telecom=" + NetworkUtils.getOperator(Starter.mActivity);

        if (Starter.mActivity.getPackageName().contains("fhzj")) {
            param = param + "&sdkType=fhzj";
        }
        ESdkLog.d("上传的oaid：" + Constant.OAID);
        System.out.println("param：" + param);
        return param;
    }

    /**
     * 显示悬浮窗
     */
    public static void showFloatView() {

        if (Constant.IS_LOGINED) {
            FloatView.show(Starter.mActivity);
        } else {
            if (Constant.IS_ENTERED_SDK) {
                // 未登陆显示用户中心
                StartESUserPlugin.enterH5View();
            }
        }
    }

    /**
     * 隐藏悬浮窗
     */
    public static void hideFloatView() {
        FloatView.close();
    }

    /**
     * 从Properties文件中读取配置信息
     *
     * @param key：参数名称
     */
    public static String getPropValue(Context _context, String key) {
        return CommonUtils.readPropertiesValue(_context, key);
    }
}

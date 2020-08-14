package com.easou.androidsdk;

import android.os.Handler;
import android.webkit.JavascriptInterface;

import com.easou.androidsdk.callback.AppTimeWatcher;
import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.data.ESConstant;
import com.easou.androidsdk.plugin.StartESUserPlugin;
import com.easou.androidsdk.plugin.StartOtherPlugin;
import com.easou.androidsdk.romutils.RomHelper;
import com.easou.androidsdk.ui.ESUserWebActivity;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.util.ESdkLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ESPlatform {

    private boolean isShowWebView = false;
    private static ESUserWebActivity mActivity;

    @JavascriptInterface
    public static void init(ESUserWebActivity activity) {
        mActivity = activity;
    }

    /**
     * 调用登陆
     */

    @JavascriptInterface
    public void esLogin(final String param) {
        StartOtherPlugin.logGDTAction();
        String userId = "";
        String userName = "";
        String token = "";
        String userBirthdate = "";
        String isIdentityUser = "";
        String isAdult = "";
        String isHoliday = "";

        try {
            JSONObject jsonObj = new JSONObject(param);
            userId = jsonObj.getString(ESConstant.SDK_USER_ID);
            userName = jsonObj.getString(ESConstant.SDK_USER_NAME);
            token = jsonObj.getString(ESConstant.SDK_USER_TOKEN);
            userBirthdate = jsonObj.getString(ESConstant.SDK_USER_BIRTH_DATE);
            isIdentityUser = jsonObj.getString(ESConstant.SDK_IS_IDENTITY_USER);
            isAdult = jsonObj.getString(ESConstant.SDK_IS_ADULT);
            isHoliday = jsonObj.getString(ESConstant.SDK_IS_HOLIDAY);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        CommonUtils.saveH5Token(Starter.mActivity, token);
        CommonUtils.saveH5TokenToCard(token,CommonUtils.readPropertiesValue(Starter.mActivity, Constant.APP_ID));
        final String user_ID = userId;
        StartOtherPlugin.logTTActionLogin(user_ID);
        StartOtherPlugin.logGismActionLogin(user_ID);
        StartOtherPlugin.logGDTActionSetID(user_ID);
        StartOtherPlugin.loginAqyAction();
        StartOtherPlugin.createRoleAqyAction(userName);
        Constant.IS_LOGINED = true;
        Constant.ESDK_USERID = userId;
        Constant.ESDK_TOKEN = token;
        isShowWebView = false;
        Map<String, String> result = new HashMap<String, String>();
        result.put(ESConstant.SDK_USER_ID, userId);
        result.put(ESConstant.SDK_USER_NAME, userName);
        result.put(ESConstant.SDK_USER_TOKEN, token);
        result.put(ESConstant.SDK_USER_BIRTH_DATE, userBirthdate);
        result.put(ESConstant.SDK_IS_IDENTITY_USER, isIdentityUser);
        result.put(ESConstant.SDK_IS_ADULT, isAdult);
        result.put(ESConstant.SDK_IS_HOLIDAY, isHoliday);

        Starter.mCallback.onLogin(result);
        AppTimeWatcher.getInstance().startTimer();
        mActivity.clientToJS(Constant.YSTOJS_GET_PAY_LIMIT_INFO, null);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                mActivity.moveTaskToBack(false);
                RomHelper.checkFloatWindowPermission(Starter.mActivity);
                Starter.getInstance().showFloatView();
            }
        }, 3000);
    }

    @JavascriptInterface
    public void showWebView(final String param) {
        ESdkLog.d("showebview");
        if (!isShowWebView) {
            Starter.getInstance().showUserCenter();
        }
    }

    /**
     * 调用登出
     */
    @JavascriptInterface
    public void esLogout(final String param) {
        StartOtherPlugin.logTTActionLogin("");
        StartOtherPlugin.logGismActionLogout();
        StartOtherPlugin.logGDTActionSetID("");
        StartOtherPlugin.logoutAqyAction();
        Constant.ESDK_USERID = "";
        Constant.ESDK_TOKEN = "";
        Constant.IS_LOGINED = false;
        isShowWebView = false;

        Starter.getInstance().hideFloatView();
        AppTimeWatcher.getInstance().unRegisterWatcher();
        Starter.mCallback.onLogout();

        mActivity.clearData();
    }

    /**
     * 调用获取用户信息
     */
    @JavascriptInterface
    public void esUserInfo(final String param) {

        String userId = "";
        String userName = "";
        String token = "";
        String loginStatus = "";

        try {
            JSONObject jsonObj = new JSONObject(param);
            loginStatus = jsonObj.getString(ESConstant.SDK_LOGIN_STATUS);
            if (loginStatus.equals(ESConstant.SDK_STATUS)) {
                userId = jsonObj.getString(ESConstant.SDK_USER_ID);
                userName = jsonObj.getString(ESConstant.SDK_USER_NAME);
                token = jsonObj.getString(ESConstant.SDK_USER_TOKEN);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> result = new HashMap<String, String>();
        result.put(ESConstant.SDK_LOGIN_STATUS, loginStatus);
        result.put(ESConstant.SDK_USER_ID, userId);
        result.put(ESConstant.SDK_USER_NAME, userName);
        result.put(ESConstant.SDK_USER_TOKEN, token);

        Starter.mCallback.onUserInfo(result);
    }


    /**
     * 调用注册
     */
    @JavascriptInterface
    public void esRegister(final String param) {
        StartOtherPlugin.logGDTAction();
        StartOtherPlugin.logTTActionRegister();
        StartOtherPlugin.logGismActionRegister();
        StartOtherPlugin.logKSActionRegister();

        StartOtherPlugin.logGDTActionRegister();
        StartOtherPlugin.registerAqyAction();
        String userId = "";
        String userName = "";

        try {
            JSONObject jsonObj = new JSONObject(param);
            userId = jsonObj.getString(ESConstant.SDK_USER_ID);
            userName = jsonObj.getString(ESConstant.SDK_USER_NAME);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> result = new HashMap<String, String>();
        result.put(ESConstant.SDK_USER_ID, userId);
        result.put(ESConstant.SDK_USER_NAME, userName);

        Starter.mCallback.onRegister(result);
    }

    /**
     * 调用显示或隐藏页面
     */
    @JavascriptInterface
    public void esShowSdk(final String param) {

        String status = "";

        try {
            JSONObject jsonObj = new JSONObject(param);
            status = jsonObj.getString(Constant.SDK_SHOWSTATUS);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!status.equals(ESConstant.SDK_STATUS)) {
            mActivity.moveTaskToBack(false);
            isShowWebView = false;
        } else {
            isShowWebView = true;
        }
    }

    /**
     * 判断是否实名认证
     */
    @JavascriptInterface
    public void esIsIdentityUser(final String param) {

        String isIdentityUser = "";

        try {
            JSONObject jsonObj = new JSONObject(param);
            isIdentityUser = jsonObj.getString(ESConstant.SDK_IS_IDENTITY_USER);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (isIdentityUser.equals(ESConstant.SDK_STATUS)) {

            mActivity.moveTaskToBack(false);

            Map<String, String> result = new HashMap<String, String>();
            result.put(ESConstant.SDK_IS_IDENTITY_USER, isIdentityUser);

            Starter.mCallback.onUserCert(result);

        } else {

            StartESUserPlugin.showUserCert();
        }
    }

    /**
     * 实名认证
     */
    @JavascriptInterface
    public void esUserCert(final String param) {

        String status = "";
        String userBirthdate = "";

        try {
            JSONObject jsonObj = new JSONObject(param);
            status = jsonObj.getString(Constant.SDK_SHOWSTATUS);
            userBirthdate = jsonObj.getString(ESConstant.SDK_USER_BIRTH_DATE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mActivity.clientToJS(Constant.YSTOJS_GET_PAY_LIMIT_INFO, null);

        Map<String, String> result = new HashMap<String, String>();
        result.put(ESConstant.SDK_IS_IDENTITY_USER, "false");
        result.put(ESConstant.SDK_USER_BIRTH_DATE, userBirthdate);

        Starter.mCallback.onUserCert(result);
    }

    /**
     * 获取oaid
     */
    @JavascriptInterface
    public void esGetOaid(final String param) {

        mActivity.clientToJS(Constant.YSTOJS_GET_OAID, null);
    }

    /**
     * 获取支付限制信息
     */
    @JavascriptInterface
    public void esPayLimitInfo(final String param) {

        String payStatus = "";
        String userType = "";
        String minAge = "";
        String maxAge = "";
        String sPay = "";
        String cPay = "";

        try {
            JSONObject jsonObj = new JSONObject(param);
            payStatus = jsonObj.getString(Constant.SDK_PAY_STATUS);
            userType = jsonObj.getString(Constant.SDK_USER_TYPE);
            if (!userType.equals("2")) {
                minAge = jsonObj.getString(Constant.SDK_MIN_AGE);
                maxAge = jsonObj.getString(Constant.SDK_MAX_AGE);
                sPay = jsonObj.getString(Constant.SDK_S_PAY);
                cPay = jsonObj.getString(Constant.SDK_C_PAY);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> result = new HashMap<String, String>();
        result.put(Constant.SDK_PAY_STATUS, payStatus);
        result.put(Constant.SDK_USER_TYPE, userType);
        result.put(Constant.SDK_MIN_AGE, minAge);
        result.put(Constant.SDK_MAX_AGE, maxAge);
        result.put(Constant.SDK_S_PAY, sPay);
        result.put(Constant.SDK_C_PAY, cPay);

        Constant.PAY_LIMIT_INFO_MAP = result;
    }
}

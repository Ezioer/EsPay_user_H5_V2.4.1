package com.hdtx.androidsdk.androidsdk.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.hdtx.androidsdk.androidsdk.data.Constant;
import com.hdtx.androidsdk.androidsdk.data.ESConstant;
import com.hdtx.androidsdk.androidsdk.data.ErrorResult;
import com.hdtx.androidsdk.androidsdk.http.EAPayImp;
import com.hdtx.androidsdk.androidsdk.ui.ESCertAlertActivity;
import com.hdtx.androidsdk.androidsdk.ui.ESPayCenterActivity;
import com.hdtx.androidsdk.androidsdk.ui.ESToast;
import com.hdtx.androidsdk.androidsdk.ui.LoadingDialog;
import com.hdtx.androidsdk.androidsdk.util.CommonUtils;
import com.hdtx.androidsdk.androidsdk.util.HDSdkLog;
import com.hdtx.androidsdk.androidsdk.util.Md5SignUtils;
import com.hdtx.androidsdk.androidsdk.util.ThreadPoolManager;

import java.util.Map;

public class StartESPayPlugin {

    public static Map<String, String> map = null;
    private static Activity mActivity;
    private static String qn;
    private static String appId;
    private static String partnerId;
    private static String key;
    private static String notifyUrl;
    private static String redirectUrl;


    /**
     * 设置支付参数
     */
    public static void setPayParams(Activity context, Map<String, String> params) {
        Constant.context = context;
        mActivity = context;
        map = params;

        if (mActivity == null) {
            ESPayCenterActivity.onFailedCallBack(ErrorResult.ESPAY_PARAMS_EEOR, "context is null");
            return;
        }
        if (map == null) {
            ESPayCenterActivity.onFailedCallBack(ErrorResult.ESPAY_PARAMS_EEOR, "map is null");
            return;
        }

        if (getPayAmountValue(map.get(ESConstant.MONEY)) > 3000) {
            ESToast.getInstance().ToastShow(mActivity, "单笔订单金额超限！");
            return;
        }

        if (Float.parseFloat(map.get(ESConstant.MONEY)) < 0.01f) {
            ESToast.getInstance().ToastShow(mActivity, "单笔订单金额应不小于0.01！");
            return;
        }

        String token = Constant.ESDK_TOKEN;

        if (TextUtils.isEmpty(token)) {
            HDSdkLog.d("请先登录，token为空");
            return;
        }
        map.put(Constant.EASOUTGC, token);

        if (getValue(map)) {

            final Intent intent = new Intent();
            Bundle mBundle = new Bundle();
            mBundle.putString(Constant.KEY, key);
            mBundle.putString(Constant.PARTENER_ID, partnerId);
            mBundle.putString(Constant.APP_ID, appId);
            mBundle.putString(ESConstant.TRADE_ID, map.get(ESConstant.TRADE_ID));
            mBundle.putString(ESConstant.TRADE_NAME, map.get(ESConstant.TRADE_NAME));
            mBundle.putString(ESConstant.MONEY, map.get(ESConstant.MONEY));
            mBundle.putString(ESConstant.NOTIFY_URL, notifyUrl);
            mBundle.putString(ESConstant.REDIRECT_URL, redirectUrl);
            mBundle.putString(Constant.EASOUTGC, token);
            mBundle.putString(Constant.PRODUCT_NAME, map.get(ESConstant.TRADE_NAME));
            mBundle.putString(Constant.INCLUDE_CHANNELS, "");
            mBundle.putString(ESConstant.NEED_CHANNELS, map.get(ESConstant.NEED_CHANNELS));
            if (qn != null && qn.trim().length() > 0) {
                mBundle.putString(Constant.QN, qn);
            } else {
                mBundle.putString(Constant.QN, map.get(Constant.QN));
            }

            intent.putExtras(mBundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // 支付限制开关（0:关闭  1:只限制游客  2:限制游客和未成年人）
            String payStatus = "0";
            // 用户类别（0:成年人  1:未成年人  2:游客）
            String userType = "0";

            try {
                payStatus = Constant.PAY_LIMIT_INFO_MAP.get(Constant.SDK_PAY_STATUS);
                userType = Constant.PAY_LIMIT_INFO_MAP.get(Constant.SDK_USER_TYPE);
            } catch (Exception e) {
            }

            if (payStatus.equals("1")) {
                if (userType.equals("2")) {
                    // 限制游客充值
                    intent.setClass(mActivity, ESCertAlertActivity.class);
                    mActivity.startActivity(intent);
                } else {
                    StartESPayPlugin.startPayCenterActivity(mActivity, intent.getExtras());
                }
            } else if (payStatus.equals("2")) {
                if (userType.equals("1")) {
                    float moneyValue = 0.0f;
                    float sPay = 0.0f;

                    try {
                        sPay = getPayAmountValue(Constant.PAY_LIMIT_INFO_MAP.get(Constant.SDK_S_PAY));
                        moneyValue = getPayAmountValue(map.get(ESConstant.MONEY));
                    } catch (Exception e) {
                    }

                    final float money = moneyValue;

                    if (money > sPay) {
                        // 限制未成年人单次最大能支付金额
                        intent.setClass(mActivity, ESCertAlertActivity.class);
                        mActivity.startActivity(intent);
                    } else {
                        LoadingDialog.show(mActivity, "正在验证订单信息...", false);
                        ThreadPoolManager.getInstance().addTask(new Runnable() {
                            @Override
                            public void run() {
                                float mTotolPay = 0.0f;
                                float mPay = 0.0f;
                                try {
                                    mTotolPay = getPayAmountValue(EAPayImp.getMonthTotolPay());
                                    mPay = getPayAmountValue(Constant.PAY_LIMIT_INFO_MAP.get(Constant.SDK_C_PAY));
                                } catch (Exception e) {
                                }

                                if ((mTotolPay + money) > mPay) {
                                    // 限制未成年人单月最大能支付总额度
                                    intent.setClass(mActivity, ESCertAlertActivity.class);
                                    mActivity.startActivity(intent);
                                } else {
                                    StartESPayPlugin.startPayCenterActivity(mActivity, intent.getExtras());
                                }
                            }
                        });
                       /* new Thread(new Runnable() {

                            @Override
                            public void run() {


                            }
                        }).start();*/
                    }
                } else if (userType.equals("2")) {
                    // 限制游客充值
                    intent.setClass(mActivity, ESCertAlertActivity.class);
                    mActivity.startActivity(intent);
                } else {
                    StartESPayPlugin.startPayCenterActivity(mActivity, intent.getExtras());
                }
            } else {
                StartESPayPlugin.startPayCenterActivity(mActivity, intent.getExtras());
            }
        }
    }

    /**
     * 支付金额字符串转换成数值
     */
    private static float getPayAmountValue(String payAmount) {

        float payAmountValue = 0.0f;
        try {
            payAmountValue = Float.parseFloat(payAmount);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return payAmountValue;
    }


    /**
     * 进入支付界面
     */
    public static void startPayCenterActivity(Context context, Bundle bundle) {

        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(context, ESPayCenterActivity.class);
        context.startActivity(intent);
    }


    /**
     * 判断支付参数是否为空
     */
    private static boolean getValue(Map<String, String> params) {

        if (params.get(ESConstant.MONEY) == null || "".equals(params.get(ESConstant.MONEY))) {
            ESPayCenterActivity.onFailedCallBack(ErrorResult.ESPAY_PARAMS_EEOR, "money is null");
            return false;
        }

        qn = CommonUtils.readPropertiesValue(mActivity, Constant.QN);
        key = CommonUtils.readPropertiesValue(mActivity, Constant.KEY);
        appId = CommonUtils.readPropertiesValue(mActivity, Constant.APP_ID);
        partnerId = CommonUtils.readPropertiesValue(mActivity, Constant.PARTENER_ID);
        notifyUrl = CommonUtils.readPropertiesValue(mActivity, ESConstant.NOTIFY_URL);
        redirectUrl = CommonUtils.readPropertiesValue(mActivity, ESConstant.REDIRECT_URL);

        if (qn == null || qn.equals("")) {
            ESPayCenterActivity.onFailedCallBack(ErrorResult.ESPAY_PARAMS_EEOR, "qn is null");
            return false;
        }
        if (key == null || key.equals("")) {
            ESPayCenterActivity.onFailedCallBack(ErrorResult.ESPAY_PARAMS_EEOR, "key is null");
            return false;
        }
        if (appId == null || appId.equals("")) {
            ESPayCenterActivity.onFailedCallBack(ErrorResult.ESPAY_PARAMS_EEOR, "appId is null");
            return false;
        }
        if (partnerId == null || partnerId.equals("")) {
            ESPayCenterActivity.onFailedCallBack(ErrorResult.ESPAY_PARAMS_EEOR, "partnerId is null");
            return false;
        }
        if (redirectUrl == null || redirectUrl.equals("")) {
            ESPayCenterActivity.onFailedCallBack(ErrorResult.ESPAY_PARAMS_EEOR, "redirectUrl is null");
            return false;
        }
        return true;
    }


    /**
     * 获取网页计费参数
     */
    public static String getParam(Activity mActivity, Map<String, String> inputMap, String key) {

        String sign = Md5SignUtils.sign(inputMap, key);
        String param = "appId=" + inputMap.get(Constant.APP_ID)
                + "&includeChannels=" + inputMap.get(Constant.INCLUDE_CHANNELS)
                + "&tradeId=" + inputMap.get(ESConstant.TRADE_ID)
                + "&qn=" + inputMap.get(Constant.QN)
                + "&sign=" + sign
                + "&notifyUrl=" + inputMap.get(ESConstant.NOTIFY_URL)
                + "&redirectUrl=" + inputMap.get(ESConstant.REDIRECT_URL)
                + "&partnerId=" + inputMap.get(Constant.PARTENER_ID)
                + "&money=" + inputMap.get(ESConstant.MONEY)
                + "&clientIp=" + inputMap.get(Constant.CLIENT_IP)
                + "&deviceId=" + inputMap.get(Constant.DEVICE_ID)
                + "&phoneOs=" + Constant.SDK_PHONEOS
                + "&esVersion=" + Constant.SDK_VERSION;

	/*	if (Constant.USE_DHT) {
			param = param + "&channelMark=DHT";
		}*/

        String pay = CommonUtils.getPayMarkObject(mActivity);
        param = param + "&channelMark=" + pay;
       /* if (Constant.PAY_CHANNEl == 1) {
            param = param + "&channelMark="+Constant.CHANNEL_MARK_DHT;
        } else if (Constant.PAY_CHANNEl == 2) {
            param = param + "&channelMark="+Constant.CHANNEL_MARK_YY;
        } else if(Constant.PAY_CHANNEl == 3) {
            param = param + "&channelMark="+Constant.CHANNEL_MARK_ZKX;
        } else if (Constant.PAY_CHANNEl == 4) {
            param = param + "&channelMark=" + Constant.CHANNEL_MARK_WZYY;
        } else {
            param = param + "&channelMark=" + Constant.CHANNEL_MARK_ZKX;
        }*/

        return param;
    }

}

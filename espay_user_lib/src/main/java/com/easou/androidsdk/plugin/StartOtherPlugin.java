package com.easou.androidsdk.plugin;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;
import com.easou.androidsdk.http.EAPayInter;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.util.ESdkLog;
import com.easou.androidsdk.util.OaidHelper;
import com.easou.androidsdk.util.SimulatorUtils;

import java.util.HashMap;
import java.util.Map;

public class StartOtherPlugin {

    /**
     * 获取oaid
     */
    public static void getOaid(final Context mContext, String cert) {
        ESdkLog.d("调用了联盟SDK获取oaid接口");

        try {
            OaidHelper helper = new OaidHelper(new OaidHelper.AppIdsUpdater() {
                @Override
                public void onIdsValid(final String ids) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ESdkLog.d("oaid -----> " + ids);
                        }
                    });
                }
            });
            helper.getDeviceIds(mContext, cert);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    //获取oaid证书，如果缓存中有 则直接读取缓存中的 否则服务器获取
    public static void getCert(Context context) {
        String cert = CommonUtils.getCert(context);
        ESdkLog.c("certnet----->", "cache-->" + cert);
        if (cert.isEmpty()) {
            String temp = EAPayInter.getOaidPerFromNet(context.getApplicationInfo().packageName);
            getOaid(context, temp);
            CommonUtils.saveCert(context, temp);
            ESdkLog.c("certnet----->", "netvalue-->" + temp);
        } else {
            getOaid(context, cert);
        }
    }
    /* ================================== 模拟器判断 ================================== */

    /**
     * 获取系统是否为模拟器
     */
    public static void checkSimulator(Context mContext) {
        try {
            String flag = CommonUtils.readPropertiesValue(mContext, "use_checkSimulator");
            if (!TextUtils.isEmpty(flag) && flag.equals("1")) {
                return;
            }

            ESdkLog.d("调用了是否为模拟器接口");
            SimulatorUtils.checkSimulator(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ================================== appsflyer ================================== */

    //登录
    public static void appsFlyerLogin(String userId) {
        Map<String, Object> eventValues = new HashMap<String, Object>();
        eventValues.put(AFInAppEventParameterName.CUSTOMER_USER_ID, userId);
        AppsFlyerLib.getInstance().logEvent(getApplicationContext(),
                AFInAppEventType.LOGIN,
                eventValues);
    }

    //注册
    public static void appsFlyerRegister(String userId) {
        Map<String, Object> eventValues = new HashMap<String, Object>();
        eventValues.put(AFInAppEventParameterName.CUSTOMER_USER_ID, userId);
        AppsFlyerLib.getInstance().logEvent(getApplicationContext(),
                AFInAppEventType.COMPLETE_REGISTRATION,
                eventValues);
    }

    //购买
    public static void appsFlyerPurchase(int price, float ncy) {
        Map<String, Object> eventValues = new HashMap<String, Object>();
        eventValues.put(AFInAppEventParameterName.CURRENCY, ncy);
        eventValues.put(AFInAppEventParameterName.REVENUE, price);
        AppsFlyerLib.getInstance().logEvent(getApplicationContext(),
                AFInAppEventType.PURCHASE, eventValues);
    }
}

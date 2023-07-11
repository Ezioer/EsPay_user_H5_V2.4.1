package hdtx.androidsdk.plugin;


import android.app.Activity;
import android.content.Context;

import hdtx.androidsdk.http.EAPayInter;
import hdtx.androidsdk.util.CommonUtils;
import hdtx.androidsdk.util.ESdkLog;
import hdtx.androidsdk.util.OaidHelper;

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
   /* public static void checkSimulator(Context mContext) {
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
    }*/

    /* ================================== appsflyer ================================== */

    /*//登录
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
    public static void appsFlyerPurchase(float price, String ncy, String productId, String orderId) {
        Map<String, Object> eventValues = new HashMap<String, Object>();
        eventValues.put(AFInAppEventParameterName.CURRENCY, ncy);
        eventValues.put(AFInAppEventParameterName.REVENUE, price);
        eventValues.put(AFInAppEventParameterName.CONTENT_ID, productId);
        eventValues.put(AFInAppEventParameterName.ORDER_ID, orderId);
        AppsFlyerLib.getInstance().logEvent(getApplicationContext(),
                AFInAppEventType.PURCHASE, eventValues);
    }

    //添加到心愿清单
    public static void appsFlyerAddToWishList(float price, String id) {
        Map<String, Object> eventValues = new HashMap<String, Object>();
        eventValues.put(AFInAppEventParameterName.PRICE, price);
        eventValues.put(AFInAppEventParameterName.CONTENT_ID, id);
        AppsFlyerLib.getInstance().logEvent(getApplicationContext(),
                AFInAppEventType.ADD_TO_WISH_LIST, eventValues);
    }

    //添加到购物车
    public static void appsFlyerAddToCar(float price, String id) {
        Map<String, Object> eventValues = new HashMap<String, Object>();
        eventValues.put(AFInAppEventParameterName.PRICE, price);
        eventValues.put(AFInAppEventParameterName.CONTENT_ID, id);
        AppsFlyerLib.getInstance().logEvent(getApplicationContext(),
                AFInAppEventType.ADD_TO_CART, eventValues);
    }

    //广告点击
    public static void appsFlyerADClick(String type) {
        Map<String, Object> eventValues = new HashMap<String, Object>();
        eventValues.put(AFInAppEventParameterName.AD_REVENUE_AD_TYPE, type);
        AppsFlyerLib.getInstance().logEvent(getApplicationContext(),
                AFInAppEventType.AD_CLICK, eventValues);
    }

    //更新
    public static void appsFlyerUpdate(String oldVersion, String newVersion) {
        Map<String, Object> eventValues = new HashMap<String, Object>();
        eventValues.put(AFInAppEventParameterName.OLD_VERSION, oldVersion);
        eventValues.put(AFInAppEventParameterName.NEW_VERSION, newVersion);
        AppsFlyerLib.getInstance().logEvent(getApplicationContext(),
                AFInAppEventType.UPDATE, eventValues);
    }

    //分享
    public static void appsFlyerShare(String name, String platform) {
        Map<String, Object> eventValues = new HashMap<String, Object>();
        eventValues.put(AFInAppEventParameterName.CONTENT, name);
        eventValues.put("platform", platform);
        AppsFlyerLib.getInstance().logEvent(getApplicationContext(),
                AFInAppEventType.SHARE, eventValues);
    }

    //搜索
    public static void appsFlyerSearch(String key) {
        Map<String, Object> eventValues = new HashMap<String, Object>();
        eventValues.put(AFInAppEventParameterName.SEARCH_STRING, key);
        AppsFlyerLib.getInstance().logEvent(getApplicationContext(),
                AFInAppEventType.SEARCH, eventValues);
    }

    //邀请
    public static void appsFlyerInvite(String content) {
        Map<String, Object> eventParameters6 = new HashMap<String, Object>();
        eventParameters6.put(AFInAppEventParameterName.DESCRIPTION, content); // Context of invitation
        AppsFlyerLib.getInstance().logEvent(getApplicationContext(), AFInAppEventType.INVITE, eventParameters6);
    }

    //从通知中打开app
    public static void appsFlyerOFPN(String id) {
        Map<String, Object> eventValues = new HashMap<String, Object>();
        eventValues.put(AFInAppEventParameterName.CONTENT_ID, id);
        AppsFlyerLib.getInstance().logEvent(getApplicationContext(),
                AFInAppEventType.OPENED_FROM_PUSH_NOTIFICATION, eventValues);
    }

    //完成教程
    public static void appsFlyerCompTutorial() {
        AppsFlyerLib.getInstance().logEvent(getApplicationContext(),
                AFInAppEventType.TUTORIAL_COMPLETION, null);
    }

    //下单
    public static void appsFlyerCheckout(float price, String ncy, String productId, String orderId) {
        Map<String, Object> eventParameters3 = new HashMap<String, Object>();
        eventParameters3.put(AFInAppEventParameterName.CURRENCY, ncy);
        eventParameters3.put(AFInAppEventParameterName.REVENUE, price);
        eventParameters3.put(AFInAppEventParameterName.CONTENT_ID, productId);
        eventParameters3.put(AFInAppEventParameterName.ORDER_ID, orderId);
        AppsFlyerLib.getInstance().logEvent(getApplicationContext(), AFInAppEventType.INITIATED_CHECKOUT, eventParameters3);
    }

    //游戏通关
    public static void appsFlyerAchievedLevel(String level) {
        Map<String, Object> eventParameters3 = new HashMap<String, Object>();
        eventParameters3.put(AFInAppEventParameterName.LEVEL, level); // Level the user achieved
        AppsFlyerLib.getInstance().logEvent(getApplicationContext(), AFInAppEventType.LEVEL_ACHIEVED, eventParameters3);
    }

    //首次付费
    public static void appsFlyerFirstPurchase(float price, String ncy, String productId, String orderId) {
        Map<String, Object> eventParameters3 = new HashMap<String, Object>();
        eventParameters3.put(AFInAppEventParameterName.CURRENCY, ncy);
        eventParameters3.put(AFInAppEventParameterName.REVENUE, price);
        eventParameters3.put(AFInAppEventParameterName.CONTENT_ID, productId);
        eventParameters3.put(AFInAppEventParameterName.ORDER_ID, orderId);
        AppsFlyerLib.getInstance().logEvent(getApplicationContext(), "first_purchase", eventParameters3);
    }

    //成就解锁
    public static void appsFlyerAchUnlock(String id, String name) {
        Map<String, Object> eventParameters3 = new HashMap<String, Object>();
        eventParameters3.put(AFInAppEventParameterName.CONTENT_ID, id); // Level the user achieved
        eventParameters3.put(AFInAppEventParameterName.CONTENT, name); // Level the user achieved
        AppsFlyerLib.getInstance().logEvent(getApplicationContext(), AFInAppEventType.ACHIEVEMENT_UNLOCKED, eventParameters3);
    }

    //广告浏览
    public static void appsFlyerADView(String id) {
        Map<String, Object> eventParameters3 = new HashMap<String, Object>();
        eventParameters3.put(AFInAppEventParameterName.AD_REVENUE_AD_TYPE, id); // Level the user achieved
        AppsFlyerLib.getInstance().logEvent(getApplicationContext(), AFInAppEventType.AD_VIEW, eventParameters3);
    }
*/

    //-------------------------------------facebook----------------------------------------//


}

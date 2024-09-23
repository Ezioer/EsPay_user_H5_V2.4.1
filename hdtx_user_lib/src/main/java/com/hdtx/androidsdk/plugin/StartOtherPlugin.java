package com.hdtx.androidsdk.plugin;

import static com.baidu.mobads.action.ActionParam.Key.PURCHASE_MONEY;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.baidu.mobads.action.BaiduAction;
import com.baidu.mobads.action.PrivacyStatus;
import com.bytedance.applog.AppLog;
import com.bytedance.applog.ILogger;
import com.bytedance.applog.InitConfig;
import com.bytedance.applog.game.GameReportHelper;
import com.bytedance.applog.util.UriConstants;
import com.hdtx.androidsdk.data.Constant;
import com.hdtx.androidsdk.http.EAPayInter;
import com.hdtx.androidsdk.util.CommonUtils;
import com.hdtx.androidsdk.util.HDSdkLog;
import com.hdtx.androidsdk.util.OaidHelper;
import com.hdtx.androidsdk.util.SimulatorUtils;
import com.hdtx.androidsdk.util.ThreadPoolManager;
import com.hdtx.androidsdk.util.Tools;
import com.gism.sdk.GismConfig;
import com.gism.sdk.GismEventBuilder;
import com.gism.sdk.GismSDK;
import com.iqiyi.qilin.trans.QiLinTrans;
import com.iqiyi.qilin.trans.TransParam;
import com.iqiyi.qilin.trans.TransType;
import com.kwai.monitor.log.TurboAgent;
import com.kwai.monitor.log.TurboConfig;
import com.qq.gdt.action.ActionType;
import com.qq.gdt.action.GDTAction;
import com.qq.gdt.action.PrivateController;

import org.json.JSONException;
import org.json.JSONObject;

public class StartOtherPlugin {

    /* ================================== 头条SDK ================================== */

    /**
     * 初始化今日头条SDK
     */
    public static void initTTSDK(Context context) {

        if (TextUtils.equals(CommonUtils.readPropertiesValue(context, "use_TT"), "0")) {

            HDSdkLog.d("调用了头条SDK初始化接口");

            Constant.TOUTIAO_SDK = true;

            String aid = CommonUtils.readPropertiesValue(context, "aid");
            String appName = CommonUtils.readPropertiesValue(context, "appName");
            String qn = CommonUtils.readPropertiesValue(context, "qn");

            final InitConfig config = new InitConfig(aid, qn);
            config.setUriConfig(UriConstants.DEFAULT);
            config.setLogger(new ILogger() {
                @Override
                public void log(String s, Throwable throwable) {
                    HDSdkLog.d(s);
                    if (throwable != null) {
                        throwable.printStackTrace();
                    }
                }
            });
            config.setImeiEnable(false);
            config.setAutoTrackEnabled(true);
            config.setLogEnable(true);
            AppLog.setEncryptAndCompress(true);
            config.setEnablePlay(true);
            config.setAutoStart(true);
            config.setAbEnable(true);
            config.setMacEnable(false);
            config.setAndroidIdEnabled(false);
            AppLog.init(context, config);
        }
    }

    /**
     * 开启头条SDK页面停留时长统计
     */
    public static void onTTResume(Activity activity) {
        if (Constant.TOUTIAO_SDK) {
            AppLog.onResume(activity);
        }
    }

    /**
     * 关闭头条SDK页面停留时长统计
     */
    public static void onTTPause(Activity activity) {
        if (Constant.TOUTIAO_SDK) {
            AppLog.onPause(activity);
        }
    }


    /**
     * 头条SDK上报下单
     */
    public static void logTTActionOrder(final String money, String productName) {

        if (Constant.TOUTIAO_SDK) {
            if (money != null) {
                String[] strs = money.split("\\.");
                int payMoney = 0;
                try {
                    payMoney = Integer.parseInt(strs[0]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                GameReportHelper.onEventCheckOut("", productName, "",
                        1, true, "", "", true, payMoney);
            }
        }
    }

    /**
     * 头条SDK上报登陆
     */
    public static void logTTActionLogin(String uid) {
        if (Constant.TOUTIAO_SDK) {
            if (!uid.equals("")) {
                AppLog.setUserUniqueID(uid);
            }
            GameReportHelper.onEventLogin("H5SDK", true);
        }
    }

    public static void logOutTT() {
        if (Constant.TOUTIAO_SDK) {
            AppLog.setUserUniqueID(null);
        }
    }

    /**
     * 头条SDK上报注册
     */
    public static void logTTActionRegister() {
        if (Constant.TOUTIAO_SDK) {
            GameReportHelper.onEventRegister("H5SDK", true);
        }
    }


    /**
     * 头条SDK上报购买
     */
    public static void logTTActionPurchase(final String money, final String productName, final String payType,
                                           final boolean status, final String appId) {

        if (Constant.TOUTIAO_SDK) {
            if (money != null) {

                String[] strs = money.split("\\.");
                int payMoney = 0;
                try {
                    payMoney = Integer.parseInt(strs[0]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                final int mMoney = payMoney;
                ThreadPoolManager.getInstance().addTask(new Runnable() {
                    @Override
                    public void run() {
                        if (Constant.isTTVersion == 1) {
                            int result = EAPayInter.isUploadPay(String.valueOf(Double.valueOf(money) * 100), appId, Constant.ESDK_USERID, "tt");
                            //1上传头条付费日志，0不上传
                            if (result == 1) {
                                HDSdkLog.d("上传头条付费日志");
                                GameReportHelper.onEventPurchase("", productName, "", 1,
                                        payType, "¥", status, mMoney);
                            }
                        } else {
                            GameReportHelper.onEventPurchase("", productName, "", 1,
                                    payType, "¥", status, mMoney);
                        }
                    }
                });
            }
        }
    }


    /* ================================== 联盟OAID ================================== */

    /**
     * 初始化联盟SDK
     */
    public static void initEntry(Context mContext) {

        HDSdkLog.d("调用了联盟SDK初始化接口,版本号=" + Constant.SDK_VERSION);

        Tools.disableAPIDialog();

        try {
//            System.loadLibrary("msaoaidsec");
//            JLibrary.InitEntry(mContext);
            //直接在此处调用即获取oaid，应该会更快
//            getOaid(mContext);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 获取oaid
     */
    public static void getOaid(final Context mContext, String cert) {
        HDSdkLog.d("调用了联盟SDK获取oaid接口");
        if (cert.isEmpty()) {
            return;
        }
        try {
            OaidHelper helper = new OaidHelper(new OaidHelper.AppIdsUpdater() {
                @Override
                public void onIdsValid(final String ids) {
                    if (!Constant.AQY_SDK) {
                        StartOtherPlugin.initAQY(mContext);
                    }
                    if (Constant.BD_SDK) {
                        BaiduAction.setOaid(ids);
                    }
                    HDSdkLog.d("oaid -----> " + ids);
//                            StartOtherPlugin.logGDTActionSetOAID(ids);
                }
            });
//            MiitHelper.getOaid(mContext);
            helper.getDeviceIds(mContext, cert);
            StartOtherPlugin.initKSSDK(mContext);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    //获取oaid证书，如果缓存中有 则直接读取缓存中的 否则服务器获取
    public static void getCert(Context context) {
        String cert = CommonUtils.getCert(context);
        HDSdkLog.c("certnet----->", "cache-->" + cert);
        if (cert.isEmpty()) {
            String temp = EAPayInter.getOaidPerFromNet(context.getApplicationInfo().packageName);
            if (!temp.isEmpty()) {
                getOaid(context, temp);
                CommonUtils.saveCert(context, temp);
            } else {
                StartOtherPlugin.initKSSDK(context);
            }
            HDSdkLog.c("certnet----->", "netvalue-->" + temp);
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

            HDSdkLog.d("调用了是否为模拟器接口");
            SimulatorUtils.checkSimulator(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /* ================================== 汇川SDK ================================== */

    /**
     * 初始化GISM
     */
    public static void initGism(Activity mContext, boolean debug) {

        if (TextUtils.equals(CommonUtils.readPropertiesValue(mContext, "use_GISM"), "0")) {

            HDSdkLog.d("调用了汇川SDK初始化接口");

            Constant.GISM_SDK = true;
            GismSDK.init(GismConfig.newBuilder(mContext.getApplication())
                    .appID(CommonUtils.readPropertiesValue(mContext, "GISM_appid"))
                    .appName(CommonUtils.readPropertiesValue(mContext, "GISM_appName"))
                    .appChannel(CommonUtils.readPropertiesValue(mContext, "qn"))
                    .build());

            if (debug == true) {
                GismSDK.debug();
            }
        }
    }

    /**
     * GISM SDK上报启动游戏
     */
    public static void onLaunchApp() {
        if (Constant.GISM_SDK) {
            GismSDK.onLaunchApp();
        }
    }

    /**
     * GISM SDK上报注册
     */
    public static void logGismActionRegister() {

        if (Constant.GISM_SDK) {
            GismSDK.onEvent(GismEventBuilder.onRegisterEvent()
                    .isRegisterSuccess(true)
                    .registerType("H5SDK")
                    .build());
        }
    }

    /**
     * GISM SDK上报登录
     */
    public static void logGismActionLogin(String uid) {

        if (Constant.GISM_SDK) {
            GismSDK.onEvent(GismEventBuilder.onCustomEvent()
                    .action("login")
                    .putKeyValue("userid", uid)
                    .build());
        }
    }

    /**
     * GISM SDK上报登出
     */
    public static void logGismActionLogout() {

        if (Constant.GISM_SDK) {
            /*GismSDK.onEvent(GismEventBuilder.onCustomEvent()
                    .action("logout")
                    .build());*/
        }
    }


    /**
     * GISM SDK上报下单
     */
    public static void logGismActionOrder(String money, String productName) {

        if (Constant.GISM_SDK) {
            if (money != null) {
                String[] strs = money.split("\\.");
                int payMoney = 0;
                try {
                    payMoney = Integer.parseInt(strs[0]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                GismSDK.onEvent(GismEventBuilder.onCustomEvent()
                        .action("order")
                        .putKeyValue("payAmount", money)
                        .putKeyValue("contentName", productName)
                        .build());
            }
        }
    }

    /**
     * GISM SDK上报付费
     */
    public static void logGismActionPurchase(String money, String productName, String payType,
                                             boolean status) {

        if (Constant.GISM_SDK) {

            if (money != null) {

                String[] strs = money.split("\\.");
                int payMoney = 0;
                try {
                    payMoney = Integer.parseInt(strs[0]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                GismSDK.onEvent(GismEventBuilder.onPayEvent()
                        .isPaySuccess(status)
                        .payAmount(payMoney)
                        .contentName(productName)
                        .payChannelName(payType)
                        .build());
            }
        }
    }


    /**
     * GISM SDK退出游戏回调
     */
    public static void logGismActionExitApp() {

        if (Constant.GISM_SDK) {
            HDSdkLog.d("调用了汇川SDK退出游戏回调接口");
            GismSDK.onExitApp();
        }
    }


    /* ================================== 广点通 ================================== */

    /**
     * 广点通SDK行为数据上报初始化
     *
     * @param mContext 上下文对象
     */
    public static void initGDTAction(Context mContext) {

        if (TextUtils.equals(CommonUtils.readPropertiesValue(mContext, "use_GDT"), "0")) {

            HDSdkLog.d("调用了广点通SDK初始化接口");

            Constant.GDT_SDK = true;
            GDTAction.init(mContext, CommonUtils.readPropertiesValue(mContext, "GDT_setId"),
                    CommonUtils.readPropertiesValue(mContext, "GDT_key"),
                    CommonUtils.readPropertiesValue(mContext, "qn"));
            GDTAction.setPrivateController(new PrivateController() {

                @Override
                public boolean isCanUsePhoneState() {
                    return false;
                }

                @Override
                public String getDevImei() {
                    return Constant.IMEI;
                }

            });
        }
    }

    /**
     * 广点通SDK上报app启动
     */
    public static void logGDTAction() {
        if (Constant.GDT_SDK) {
            HDSdkLog.d("调用了广点通SDK上报app启动接口");
            GDTAction.logAction(ActionType.START_APP);
        }
    }

    /**
     * 广点通SDK赋值oaid
     */
    public static void logGDTActionSetOAID(String oaid) {
        if (Constant.GDT_SDK) {
            try {
//                com.qq.gdt.action.f.a().b(oaid);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 广点通SDK设置用户软ID
     */
    public static void logGDTActionSetID(String uid) {
        if (Constant.GDT_SDK) {
            GDTAction.setUserUniqueId(uid);
        }
    }

    /**
     * 广点通SDK上报下单
     */
    public static void logGDTActionOrder(String money) {

        if (Constant.GDT_SDK) {
            try {
                JSONObject actionParam = new JSONObject();
                actionParam.put("value", Double.valueOf(money) * 100);
                GDTAction.logAction(ActionType.COMPLETE_ORDER, actionParam);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 广点通SDK上报注册
     */
    public static void logGDTActionRegister() {
        if (Constant.GDT_SDK) {
            GDTAction.logAction(ActionType.REGISTER);
        }
    }

    /**
     * 广点通SDK上报购买
     */
    public static void logGDTActionPurchase(String money, final String productName, final boolean status, final String appId) {

        if (Constant.GDT_SDK) {
            if (money != null) {
                String[] strs = money.split("\\.");
                try {
                    final int payMoney = Integer.parseInt(strs[0]);
                    ThreadPoolManager.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {
                            int result = EAPayInter.isUploadPay(String.valueOf(payMoney * 100), appId, Constant.ESDK_USERID, "gdt");
                            //1上传付费日志，0不上传
                            if (result == 1) {
                                HDSdkLog.d("上传广点通付费日志");
                                JSONObject actionParam = new JSONObject();
                                try {
                                    actionParam.put("value", Integer.valueOf(payMoney) * 100);
                                    actionParam.put("name", productName);
                                    actionParam.put("status", status);
                                    GDTAction.logAction(ActionType.PURCHASE, actionParam);
                                } catch (JSONException e) {
                                }
                            }
                        }
                    });
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /* ================================== 快手 ================================== */

    /**
     * 快手SDK初始化
     */
    public static void initKSSDK(Context mContext) {

        if (TextUtils.equals(CommonUtils.readPropertiesValue(mContext, "use_KS"), "0")) {

            HDSdkLog.d("调用了快手SDK初始化接口");
            Constant.KS_SDK = true;
            TurboAgent.init(TurboConfig.TurboConfigBuilder.create(mContext)
                    .setAppId(CommonUtils.readPropertiesValue(mContext, "KS_appid"))
                    .setAppName(CommonUtils.readPropertiesValue(mContext, "KS_appName"))
                    .setAppChannel(CommonUtils.readPropertiesValue(mContext, "qn"))
                    /*.setOAIDProxy(new OAIDProxy() { // 设置获取oaid的接口
                        @Override
                        public String getOAID() {
                            return Constant.OAID;// 返回设备标识oaid
                        }
                    })*/
                    .setEnableDebug(true)
                    .build());
        }
    }

    /**
     * 快手SDK活跃事件，进入app首页时调用
     */
    public static void logKSActionAppActive() {
        if (Constant.KS_SDK) {
            HDSdkLog.d("调用了快手SDK活跃事件接口");
            TurboAgent.onAppActive();
        }
    }

    /**
     * 快手SDK注册事件
     */
    public static void logKSActionRegister() {
        if (Constant.KS_SDK) {
            TurboAgent.onRegister();
        }
    }

    /**
     * 快手次日留存事件
     */
    public static void logStayNextDay() {
        if (Constant.KS_SDK) {
            TurboAgent.onNextDayStay();
        }
    }

    /**
     * 快手SDK付费成功事件
     */
    public static void logKSActionPerchase(String money, final String appId) {
        if (Constant.KS_SDK) {
            if (money != null) {
                String[] strs = money.split("\\.");
                try {
                    final int payMoney = Integer.parseInt(strs[0]);
                    ThreadPoolManager.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {
                            int result = EAPayInter.isUploadPay(String.valueOf(Double.valueOf(payMoney) * 100), appId, Constant.ESDK_USERID, "ks");
                            //1上传头条付费日志，0不上传
                            if (result == 1) {
                                HDSdkLog.d("上传快手付费日志");
                                TurboAgent.onOrderPayed(payMoney);
                                TurboAgent.onPay(payMoney);
                            }
                        }
                    });
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 快手SDK提交订单事件
     */
    public static void logKSActionOrderSubmit(String money) {
        if (Constant.KS_SDK) {
            if (money != null) {
                String[] strs = money.split("\\.");
                int payMoney = 0;
                try {
                    payMoney = Integer.parseInt(strs[0]);
                    TurboAgent.onOrderSubmit(payMoney);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 快手SDK进入游戏界面
     */
    public static void logKSActionPageResume(final Activity activity) {
        if (Constant.KS_SDK) {
            HDSdkLog.d("调用了快手SDK进入游戏界面接口");
            TurboAgent.onPageResume(activity);
        }
    }

    /**
     * 快手SDK退出游戏界面
     */
    public static void logKSActionPagePause(final Activity activity) {
        if (Constant.KS_SDK) {
            HDSdkLog.d("调用了快手SDK退出游戏界面接口");
            TurboAgent.onPagePause(activity);
        }
    }

    /* ================================== 爱奇艺 ================================== */

    /**
     * 爱奇艺sdk初始化
     *
     * @param mContext
     */
    public static void initAQY(Context mContext) {
        if (TextUtils.equals(CommonUtils.readPropertiesValue(mContext, "use_AQY"), "0")) {
            Constant.AQY_SDK = true;
            HDSdkLog.d("调用了爱奇艺sdk初始化" + Constant.OAID);
            QiLinTrans.setDebug(TransParam.LogLevel.LOG_DEBUG, false, "");
            QiLinTrans.init(mContext, CommonUtils.readPropertiesValue(mContext, "aiqiyi_appid"),
                    CommonUtils.readPropertiesValue(mContext, "qn"), Constant.OAID);
        }
    }

    /**
     * 爱奇艺进入游戏界面
     */
    public static void resumeAQY() {
        if (Constant.AQY_SDK) {
            HDSdkLog.d("爱奇艺进入游戏界面");
            QiLinTrans.onResume();
        }
    }

    /**
     * 爱奇艺退出游戏界面
     */
    public static void destoryAQY() {
        if (Constant.AQY_SDK) {
            HDSdkLog.d("爱奇艺退出游戏界面");
            QiLinTrans.onDestroy();
        }
    }

    /**
     * 爱奇艺SDK上报注册
     */
    public static void registerAqyAction() {
        if (Constant.AQY_SDK) {
            QiLinTrans.uploadTrans(TransType.QL_REGISTER);
        }
    }

    /**
     * 爱奇艺SDK上报登录
     */
    public static void loginAqyAction() {
        if (Constant.AQY_SDK) {
            QiLinTrans.uploadTrans(TransType.QL_LOGIN);
        }
    }

    /**
     * 爱奇艺SDK上报登出
     */
    public static void logoutAqyAction() {
        if (Constant.AQY_SDK) {
            QiLinTrans.uploadTrans(TransType.QL_LOGOUT);
        }
    }

    /**
     * 爱奇艺SDK上报创建角色
     */
    public static void createRoleAqyAction(String name) {
        if (Constant.AQY_SDK) {
            try {
                JSONObject actionParam = new JSONObject();
                actionParam.put("role_name", name);
                QiLinTrans.uploadTrans(TransType.QL_CREATE_ROLE, actionParam);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 爱奇艺SDK上报下单
     */
    public static void orderAqyAction(String money) {
        if (Constant.AQY_SDK) {
            try {
                JSONObject actionParam = new JSONObject();
                actionParam.put("money", Double.valueOf(money));
                QiLinTrans.uploadTrans(TransType.QL_PLACE_ORDER, actionParam);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 爱奇艺SDK上报购买
     */
    public static void purchaseAqyAction(String money) {
        if (Constant.AQY_SDK) {
            if (money != null) {

                String[] strs = money.split("\\.");
                int payMoney = 0;
                try {
                    payMoney = Integer.parseInt(strs[0]);

                    try {
                        JSONObject actionParam = new JSONObject();
                        actionParam.put("money", Integer.valueOf(payMoney));
                        QiLinTrans.uploadTrans(TransType.QL_PURCHASE, actionParam);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* ================================== 百度sdk ================================== */

    /**
     * baidu sdk初始化
     *
     * @param mContext
     */
    public static void initBD(Context mContext) {
        if (TextUtils.equals(CommonUtils.readPropertiesValue(mContext, "use_BD"), "0")) {
            HDSdkLog.d("初始化百度sdk");
            Constant.BD_SDK = true;
//            System.loadLibrary("msaoaidsec");
            BaiduAction.setPrintLog(true);
            BaiduAction.enableClip(false);
            try {
                BaiduAction.init(mContext, Long.valueOf(CommonUtils.readPropertiesValue(mContext, "BD_appid")),
                        CommonUtils.readPropertiesValue(mContext, "BD_appSecret"));
            } catch (Exception e) {
            }
            // 设置应用激活的间隔（默认30天）
            try {
                String[] permissions = new String[]{};
                int[] grants = new int[]{0};
                BaiduAction.onRequestPermissionsResult(1024, permissions, grants);
            } catch (Exception e) {
            }
            if (CommonUtils.getIsShowPrivate(mContext) == 1) {
                BaiduAction.setPrivacyStatus(PrivacyStatus.AGREE);
            }
            BaiduAction.setActivateInterval(mContext, 30);
        }
    }

    /**
     * baidu SDK付费成功事件
     */
    public static void logBDActionPerchase(String money, final String appId) {
        if (Constant.BD_SDK) {
            if (money != null) {
                String[] strs = money.split("\\.");
                try {
                    final int payMoney = Integer.parseInt(strs[0]) * 100;
                    final JSONObject actionParam = new JSONObject();
                    actionParam.put(PURCHASE_MONEY, payMoney);

                    ThreadPoolManager.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {
                            int result = EAPayInter.isUploadPay(String.valueOf(payMoney), appId, Constant.ESDK_USERID, "bd");
                            //1上传头条付费日志，0不上传
                            if (result == 1) {
                                HDSdkLog.d("上传百度付费日志");
                                BaiduAction.logAction(com.baidu.mobads.action.ActionType.PURCHASE, actionParam);
                            }
                        }
                    });
                } catch (Exception e) {
                    HDSdkLog.d(e.getMessage());
                }
            }
        }
    }

    /**
     * baidu sdk注册事件
     */
    public static void logBDRegister() {
        if (Constant.BD_SDK) {
            BaiduAction.logAction(com.baidu.mobads.action.ActionType.REGISTER);
        }
    }

    /**
     * baidu sdk登陆事件
     */
    public static void logBDLogin() {
        if (Constant.BD_SDK) {
            BaiduAction.logAction(com.baidu.mobads.action.ActionType.LOGIN);
        }
    }

    /**
     * baidu sdk页面浏览事件
     */
    public static void logBDPage() {
        if (Constant.BD_SDK) {
            BaiduAction.logAction(com.baidu.mobads.action.ActionType.PAGE_VIEW);
        }
    }

    /**
     * baidu SDK下单成功事件
     */
    public static void logBDActionOrder(String money) {
        if (Constant.BD_SDK) {
            if (money != null) {
                String[] strs = money.split("\\.");
                int payMoney = 0;
                try {
                    payMoney = Integer.parseInt(strs[0]) * 100;
                    JSONObject actionParam = new JSONObject();
                    actionParam.put(PURCHASE_MONEY, payMoney);
                    BaiduAction.logAction(com.baidu.mobads.action.ActionType.COMPLETE_ORDER, actionParam);
                } catch (Exception e) {
                }
            }
        }
    }
}
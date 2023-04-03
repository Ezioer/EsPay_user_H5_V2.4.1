package com.easou.androidsdk;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.LogLevel;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.easou.androidsdk.callback.ESdkCallback;
import com.easou.androidsdk.callback.ESdkPayCallback;
import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.data.ESConstant;
import com.easou.androidsdk.http.BaseResponse;
import com.easou.androidsdk.http.EAPayInter;
import com.easou.androidsdk.plugin.StartESUserPlugin;
import com.easou.androidsdk.plugin.StartLogPlugin;
import com.easou.androidsdk.romutils.RomHelper;
import com.easou.androidsdk.romutils.RomUtils;
import com.easou.androidsdk.ui.ESUserWebActivity;
import com.easou.androidsdk.ui.LoadingDialog;
import com.easou.androidsdk.util.AESUtil;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.util.DialogerUtils;
import com.easou.androidsdk.util.ESdkLog;
import com.easou.androidsdk.util.ThreadPoolManager;
import com.easou.androidsdk.util.Tools;
import com.easou.espay_user_lib.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Starter {

    public static ESdkCallback mCallback = null;
    public static Activity mActivity;

    public volatile static Starter mSingleton = null;
    //    private SignInClient oneTapClient;
//    private BeginSignInRequest signInRequest;
//    private BeginSignInRequest signUpRequest;
    private String mProductId = "";
    private String mTradeId = "";
    private ESdkPayCallback mPayCallBack;
    private GoogleSignInClient mGoogleSignInClient;
    private BillingClient billingClient;
    private CallbackManager callbackManager;
    public static final String TAG = "GoogleAndFBLog";
    //    public static final int REQ_ONE_TAP = 10;
//    public static final int REQ_ONE_TAP2 = 11;
    public static final int SIGN_LOGIN = 13;
    //    private static final String AF_DEV_KEY = "CKrrrbztntPYFpSXe86MJb";
    private static String googleID = "364910254975-u909v8v674q8p341kr0k7lg1l7lh92hm.apps.googleusercontent.com";
    private static String ADJUSTKEY = "g51ej45btr7k";
    private AppEventsLogger logger = null;
    private String mESOrder = "";
    private String mNcy = "";
    private double mPrice = 0f;
    private boolean isBindGoogle = false;
    private boolean isBindFacebook = false;
    private JSONObject mPayInfo;

    private Starter() {
    }

    /**
     * 宜搜SDK单例
     */
    public static Starter getInstance() {
        if (mSingleton == null) {
            synchronized (Starter.class) {
                if (mSingleton == null) {
                    mSingleton = new Starter();
                }
            }
        }
        return mSingleton;
    }


    /**
     * 宜搜SDK支付接口
     */
    public void pay(Activity mActivity, JSONObject info, ESdkPayCallback callback) {
        mPayInfo = info;
        mProductId = mPayInfo.optString(ESConstant.PRODUCT_ID);
        mPayCallBack = callback;
        int money = mPayInfo.optInt(ESConstant.MONEY);
        mPrice = Double.valueOf(money) / 100;
        mTradeId = mPayInfo.optString(ESConstant.TRADE_ID);
        initBilling(mActivity);
    }

    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            verResultAndConsume(billingResult, purchases);
        }
    };

    private void verResultAndConsume(BillingResult billingResult, final List<Purchase> purchases) {
        //list为购买交易的集合,查询后继续验证交易和核销订单
        switch (billingResult.getResponseCode()) {
            case BillingClient.BillingResponseCode.OK:
                //通过服务器验证订单
                ThreadPoolManager.getInstance().addTask(new Runnable() {
                    @Override
                    public void run() {
                        //购买商品成功
                        synchronized (Starter.class) {
                            Log.d(TAG, "购买成功，进入验证订单程序........");
                            for (final Purchase purchase : purchases) {
                                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                    Log.d(TAG, "购买成功，验证订单中........");
                                    //购买商品的数量
                                    final int num = purchase.getQuantity();
                                    String appId = CommonUtils.readPropertiesValue(Starter.mActivity, "appId");
                                    final BaseResponse result = EAPayInter.verGooglePlayOrder(purchase.getPurchaseToken(), mESOrder, appId, mPrice * num, num);
                                    if (result != null && result.getCode() == 0) {
                                        //订单确认状态 0 待确认 1 已确认
                                        int acknowledgementState = 0;
                                        //订单核销状态 0 待核销 1 已核销
                                        int consumptionState = 0;
                                        try {
                                            JSONObject custom = new JSONObject(result.getData().toString());
                                            String data = AESUtil.decrypt(custom.optString("content"), Constant.AESKEY);
                                            JSONObject content = new JSONObject(data);
                                          /*  if (content.optBoolean("isFirstPay")) {
                                                //首次付费
                                                StartOtherPlugin.appsFlyerFirstPurchase(Float.valueOf(mPrice) * num, mNcy, mProductId, mESOrder);
                                            }*/
                                            acknowledgementState = content.getInt("ackStatus");
                                            consumptionState = content.getInt("consumptionStatus");
                                        } catch (Exception e) {
                                        }
                                        if (acknowledgementState == 0) {
                                            //服务器验证成功，核销订单
                                            Log.d(TAG, "验证成功，核销订单中........");
//                                            StartOtherPlugin.appsFlyerPurchase(Float.valueOf(mPrice) * num, mNcy, mProductId, mESOrder);
                                            Log.d(TAG, "购买成功日志 fb........" + mPrice + mESOrder);
                                            adjustPay(mPrice, mNcy, mESOrder);
                                            fbPurchased(mPrice, mNcy, mProductId, mESOrder);
                                            consumePurchase(purchase.getPurchaseToken());
                                            //可重复购买的内购商品核销，回调购买给游戏处理
                                            if (mPayCallBack != null) {
                                                mActivity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Log.d(TAG, "验证成功，发放用户权益");
                                                        mPayCallBack.onPaySuccess(num);
                                                    }
                                                });
                                            }
                                        } else {
                                            if (consumptionState == 0) {
                                                Log.d(TAG, "验证成功，该订单未核销，执行核销订单........");
                                                consumePurchase(purchase.getPurchaseToken());
                                            }
                                            Log.d(TAG, "该订单已经验证并已被核销........");
                                        }
                                    } else {
                                        if (mPayCallBack != null) {
                                            mActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    int code = 1002;
                                                    if (result != null && result.getCode() != 9998) {
                                                        //验证失败
                                                        Log.d(TAG, "验证失败，核销订单失败........" + 1002);
                                                        mPayCallBack.onPayFail(code);
                                                    }
                                                    Log.d(TAG, "验证失败，该订单已被验证........" + 1002);
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
                break;
            case BillingClient.BillingResponseCode.USER_CANCELED:
                //取消购买
                if (mPayCallBack != null) {
                    Log.d(TAG, "验证失败，取消购买........" + 1000);
                    mPayCallBack.onPayFail(1000);
                }
                break;
            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                //用户已拥有
                if (mPayCallBack != null) {
                    Log.d(TAG, "验证失败，用户已拥有该产品........" + 1003);
                    mPayCallBack.onPayFail(1003);
                }
                break;
            default:
                //购买失败，具体异常码可以到BillingClient.BillingResponseCode中查看
                if (mPayCallBack != null) {
                    Log.d(TAG, "验证失败，核销订单失败........" + 1001);
                    mPayCallBack.onPayFail(1001);
                }
                break;
        }
    }

    //核销回调
    ConsumeResponseListener consumeResponseListener = new ConsumeResponseListener() {
        @Override
        public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String purchaseToken) {
            //核销完成后回调
            Log.d(TAG, "核销订单成功........");
            final String appId = CommonUtils.readPropertiesValue(Starter.mActivity, "appId");
            ThreadPoolManager.getInstance().addTask(new Runnable() {
                @Override
                public void run() {
                    EAPayInter.verSyncOrderStatus(mESOrder, 1, 1, 1, appId);
                }
            });
        }
    };

    //核销订单
    void consumePurchase(String purchaseToken) {
        if (billingClient != null && billingClient.isReady()) {
            ConsumeParams consumeParams = ConsumeParams.newBuilder()
                    .setPurchaseToken(purchaseToken)
                    .build();
            billingClient.consumeAsync(consumeParams, consumeResponseListener);
        }
    }

    PurchasesResponseListener purchasesResponseListener = new PurchasesResponseListener() {
        @Override
        public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
            if (list != null) {
                List<Purchase> temp = new ArrayList<>();
                for (Purchase purchase : list) {
                    if (!purchase.isAcknowledged()) {
                        temp.add(purchase);
                    }
                }
                verResultAndConsume(billingResult, temp);
            }
        }
    };

    private BillingClientStateListener billingClientStateListener = new BillingClientStateListener() {
        @Override
        public void onBillingServiceDisconnected() {
            // Try to restart the connection on the next request to
            // Google Play by calling the startConnection() method.
            Log.d(TAG, "服务断开，重新连接");
            billingClient.startConnection(this);
        }

        @Override
        public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                // The BillingClient is ready. You can query purchases here.
                //获取商品详情回调
                SkuDetailsResponseListener skuDetailsResponseListener = new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable final List<SkuDetails> list) {
                        //list为可用商品的集合
                        //将要购买商品的商品详情配置到参数中
                        Log.d(TAG, "应用内商品数量：" + list.size());
                        final BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(list.get(0))
                                .build();
                        ThreadPoolManager.getInstance().addTask(new Runnable() {
                            @Override
                            public void run() {
                                mNcy = list.get(0).getPriceCurrencyCode();
                                BaseResponse result = EAPayInter.checkOrder(mTradeId, mProductId, list.get(0).getPrice(), list.get(0).getPriceAmountMicros(), mNcy,
                                        CommonUtils.getCheckOutParams(), mPayInfo);
                                if (result != null && result.getCode() == 0) {
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            BillingResult billingFlow = billingClient.launchBillingFlow(mActivity, billingFlowParams);
                                        }
                                    });
                                    try {
                                        JSONObject custom = new JSONObject(result.getData().toString());
                                        String data = AESUtil.decrypt(custom.optString("content"), Constant.AESKEY);
                                        JSONObject content = new JSONObject(data);
                                        mESOrder = content.optString("orderNo");
//                                        StartOtherPlugin.appsFlyerCheckout(Float.valueOf(mPrice), mNcy, mProductId, mESOrder);
                                        Log.d(TAG, "下单成功日志 fb........" + mPrice + mESOrder);
                                        adjustCheckOut(mPrice);
                                        fbCheckOut(mPrice, mProductId, mNcy, mESOrder);
                                    } catch (Exception e) {
                                    }
                                } else {
                                    Log.d(TAG, "下单失败........" + 1004);
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mPayCallBack.onPayFail(1004);
                                        }
                                    });
                                }
                            }
                        });
                    }
                };

                //查询内购类型的商品
                //productId为产品ID(从谷歌后台获取)
                ArrayList<String> inAppSkuInfo = new ArrayList<>();
                inAppSkuInfo.add(mProductId);
                SkuDetailsParams skuParams = SkuDetailsParams.newBuilder()
                        .setType(BillingClient.SkuType.INAPP)
                        .setSkusList(inAppSkuInfo)
                        .build();
                billingClient.querySkuDetailsAsync(skuParams, skuDetailsResponseListener);
                Log.d(TAG, "查询后台商品");
            }
        }
    };

    private void initBilling(Activity mActivity) {
        if (billingClient == null) {
            billingClient = BillingClient.newBuilder(mActivity)
                    .setListener(purchasesUpdatedListener)
                    .enablePendingPurchases()
                    .build();
            Log.d(TAG, "billingclient 初始化");
        }
        billingClient.startConnection(billingClientStateListener);
        Log.d(TAG, "billingclient 开始连接google play");
    }

    /**
     * 宜搜SDK登陆接口
     */
    public void login(final Activity activity, ESdkCallback mCallback) {
       /* ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                StartOtherPlugin.getCert(activity);
                Looper.loop();
            }
        });
        AppsFlyerLib.getInstance().setCollectIMEI(true);
        AppsFlyerLib.getInstance().setCollectAndroidID(true);*/
        Starter.mCallback = mCallback;
        Starter.mActivity = activity;
        // 获取deviceID
        String imei = Tools.getDeviceImei(activity);
        if (!TextUtils.isEmpty(imei.trim())) {
            Constant.IMEI = imei;
        }
        if (CommonUtils.getIsFirstStart(activity) == 0) {
            adjustActive();
            fbActApp();
            CommonUtils.saveIsFirstStart(activity);
        }
        adjustStart();
        StartESUserPlugin.loginSdk();
    }

    /**
     * 获取SDK用户信息
     */
    public void getUserInfo() {
        StartESUserPlugin.getH5UserInfo();
    }

    /**
     * 显示SDK用户中心页面
     */
    public void showUserCenter() {
        StartESUserPlugin.showSdkView();
    }

    //退出登录
    public void logOut() {
        StartESUserPlugin.changeAccount();
    }

    /**
     * 显示SDK悬浮窗
     */
    public void showFloatView() {
        if (RomUtils.checkIsVivo()) {
            if (mActivity != null && RomHelper.checkPermission(mActivity)) {
                StartESUserPlugin.showFloatView();
            }
        } else {
            StartESUserPlugin.showFloatView();
        }
    }

    /**
     * 隐藏SDK悬浮窗
     */
    public void hideFloatView() {
        StartESUserPlugin.hideFloatView();
    }

    /**
     * 上传游戏登录日志
     *
     * @param playerInfo 游戏角色信息
     */
    @Deprecated
    public void startGameLoginLog(Map<String, String> playerInfo) {
        try {
            StartESUserPlugin.startGameLoginLog(playerInfo);
            //游戏角色数据上传
            StartLogPlugin.gamePlayerDataLog(playerInfo, CommonUtils.readPropertiesValue(Starter.mActivity, "isTurnExt").equals("0"));
            //游戏角色上线日志上传
            Map info = new HashMap();
            info.put("bt", "1");
            info.put("deviceId", Constant.IMEI);
            info.put("userId", Constant.ESDK_USERID);
            ESUserWebActivity.clientToJS(Constant.YSTOJS_GAME_LOGINOROUTLOG, info);
        } catch (Exception e) {
        }
    }

    /**
     * 上传游戏下线日志
     */
    public void startGameLogoutLog() {
        //游戏角色下线日志上传
        Map info = new HashMap();
        info.put("bt", "0");
        info.put("deviceId", Tools.getDeviceImei(Starter.mActivity));
        info.put("userId", CommonUtils.getUserId(Starter.mActivity));
        ESUserWebActivity.clientToJS(Constant.YSTOJS_GAME_LOGINOROUTLOG, info);
    }

    /**
     * 初始化SDK，获取oaid，判断是否为模拟器
     */
    public void initEntry(Context mContext) {
//        StartOtherPlugin.checkSimulator(mContext);
    }

    /**
     * 从Properties文件中读取配置信息
     *
     * @param key：参数名称
     */
    public static String getPropertiesValue(Context _context, String key) {
        return StartESUserPlugin.getPropValue(_context, key);
    }

    public void handleActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        /*if (requestCode == REQ_ONE_TAP) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                String username = credential.getId();
                String password = credential.getPassword();
                if (idToken != null) {
                    // Got an ID token from Google. Use it to authenticate
                    // with your backend.
                    Log.d(TAG, "Got ID token.");
                } else if (password != null) {
                    // Got a saved username and password. Use them to authenticate
                    // with your backend.
                    Log.d(TAG, "Got password.");
                }
            } catch (ApiException e) {
                if (e.getStatusCode() == CommonStatusCodes.CANCELED) {
                    //用户拒绝一键登录
                    Log.d(TAG, "用户拒绝一键登录");
                }
            }
        } else if (requestCode == REQ_ONE_TAP2) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    // Got an ID token from Google. Use it to authenticate
                    // with your backend.
                    Log.d(TAG, "Got ID token.");
                }
            } catch (ApiException e) {
                //用户拒绝一键创建账号，使用常规账号创建
                Log.d(TAG, "用户拒绝一键创建账号，使用常规账号创建");
            }
        } else*/
        if (requestCode == SIGN_LOGIN) {
            Log.d(TAG, "setActivityResultGoogle");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task == null) {
                Log.d(TAG, "task：null");
            }
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                StartESUserPlugin.loginGoogle(account.getIdToken(), account.getId(), isBindGoogle);
//                Toast.makeText(mActivity, "登录成功" + "Id:" + account.getId() + "|Email:" + account.getEmail(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Id:" + account.getId() + "|Email:" + account.getEmail() + "|IdToken:" + account.getIdToken());
            } catch (ApiException e) {
                e.printStackTrace();
                Log.d(TAG, "ApiException:" + e.getMessage());
                Toast.makeText(mActivity, mActivity.getApplication().getResources()
                        .getIdentifier("es_loginerror", "string", mActivity.getApplication().getPackageName()), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 统一为数据初始化接口
     *
     * @param mContext
     */
    public void dataCollectInit(Context mContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            builder.detectFileUriExposure();
            StrictMode.setVmPolicy(builder.build());
        }
        try {
            ApplicationInfo info = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
            googleID = info.metaData.getString("g_clientId");
            CommonUtils.saveKey(mContext, Constant.unuselessdata);
            CommonUtils.saveBase(mContext, Constant.unuselessvalue);
        } catch (PackageManager.NameNotFoundException e) {
        }
        Tools.getAndroidId(mContext);
        Constant.APPID = getPropertiesValue(mContext, "appId");
      /*  if (CommonUtils.readPropertiesValue(mContext, "adjust").equals("0")) {
            Constant.adjust = false;
        } else {
            Constant.adjust = true;*/
        String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;
        AdjustConfig config = new AdjustConfig(mContext, ADJUSTKEY, environment);
        config.setLogLevel(LogLevel.VERBOSE);
        config.setSendInBackground(true);
        Adjust.onCreate(config);
        ((Application) mContext).registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
//        }
      /*  if (CommonUtils.readPropertiesValue(mContext, "facebook").equals("0")) {
            Constant.facebook = false;
        } else {
            Constant.facebook = true;*/
        logger = AppEventsLogger.newLogger(mContext);
        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);
      /*  }
        if (CommonUtils.readPropertiesValue(mContext, "firebase").equals("0")) {
            Constant.firebase = false;
        } else {
            Constant.firebase = true;*/
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
//        }
      /*  AppsFlyerLib.getInstance().waitForCustomerUserId(true);
        AppsFlyerLib.getInstance().init(AF_DEV_KEY, null, mContext);
        AppsFlyerLib.getInstance().start(mContext);
        String cuid = CommonUtils.readPropertiesValue(mContext, Constant.CUID);
        //        AppsFlyerLib.getInstance().setCustomerUserId(cuid);
//        String cuid = AppsFlyerProperties.getInstance().getString(AppsFlyerProperties.APP_USER_ID);
//        AppTimeWatcher.getInstance().registerWatcher((Application) mContext);
        AppsFlyerLib.getInstance().setDebugLog(true);
        AppsFlyerLib.getInstance().setCustomerIdAndLogSession(cuid, mContext);*/
    }

    private Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
            Adjust.onResume();
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Adjust.onPause();
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };

    public void pageResume() {
        ESdkLog.d("进入游戏界面接口");
        if (billingClient != null) {
            //内购商品交易查询
            billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, purchasesResponseListener);
        }
    }

    //初始化facebook登录
    public void initFacebook(boolean isBind) {
        isBindFacebook = isBind;
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        AccessToken accessToken = loginResult.getAccessToken();
                        Log.d(TAG, "Facebook------>>" + "token:" + accessToken.getToken() + "|userid:" + accessToken.getUserId());
                        StartESUserPlugin.loginFacebook(accessToken.getToken(), accessToken.getUserId(), isBindFacebook);
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.d(TAG, "Facebook cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.d(TAG, "Facebook error------>>" + exception.toString());
                    }
                });

        LoginManager.getInstance().logIn(mActivity, Arrays.asList("public_profile", "user_friends"));
    }

    //初始化google登录
    public void initGoogleTapClient() {
        if (mGoogleSignInClient == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions
                    .DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken(googleID)
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(mActivity, gso);
        }
        /*mGoogleSignInClient.silentSignIn()
                .addOnCompleteListener(mActivity,
                        new OnCompleteListener<GoogleSignInAccount>() {
                            @Override
                            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                                if (task != null) {
                                    GoogleSignInAccount account = null;
                                    try {
                                        account = task.getResult(ApiException.class);
                                        String idToken = account.getIdToken();
                                        Log.d(TAG, "用户已登录google账号--->" + idToken);
//                                        StartESUserPlugin.loginGoogle(idToken);
                                    } catch (ApiException e) {
                                        beginLogin();
                                    }
                                } else {
                                    beginLogin();
                                }
                            }
                        });
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(mActivity);
        oneTapClient = Identity.getSignInClient(mActivity);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId("846876477691-gjefh1ll8fdq72pb5htugj4459kls3nr.apps.googleusercontent.com")
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();
        signUpRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId("846876477691-gjefh1ll8fdq72pb5htugj4459kls3nr.apps.googleusercontent.com")
                        // Show all accounts on the device.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();*/
    }

    public void initGoogleLogin(boolean isBind) {
        isBindGoogle = isBind;
        mActivity.startActivityForResult(getGoogleIntent(), SIGN_LOGIN);
        /*oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(mActivity, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        try {
                            mActivity.startIntentSenderForResult(
                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                    null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                        }
                    }
                })
                .addOnFailureListener(mActivity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.d(TAG, "Couldn't find credentials:" + e.getLocalizedMessage());
                        //使用一键创建账号
                        oneTapClient.beginSignIn(signUpRequest)
                                .addOnSuccessListener(mActivity, new OnSuccessListener<BeginSignInResult>() {
                                    @Override
                                    public void onSuccess(BeginSignInResult result) {
                                        try {
                                            mActivity.startIntentSenderForResult(
                                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                                    null, 0, 0, 0);
                                        } catch (IntentSender.SendIntentException e) {
                                            Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                                        }
                                    }
                                })
                                .addOnFailureListener(mActivity, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // No Google Accounts found. Just continue presenting the signed-out UI.
                                        Log.d(TAG, "Couldnt find google account" + e.getLocalizedMessage());
                                    }
                                });
                    }
                });*/
    }

    public Intent getGoogleIntent() {
        Intent signInInten;
        if (mGoogleSignInClient == null) {
            initGoogleTapClient();
        }
        signInInten = mGoogleSignInClient.getSignInIntent();
        return signInInten;
    }

    //退出账号
    public void googleLogout() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(mActivity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    //解除关联
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(mActivity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    /*//appsflyer平台接入接口

    //添加到心愿清单
    public void appsFlyerAddToWishList(float price, String id) {
        StartOtherPlugin.appsFlyerAddToWishList(5.0f, "1321");
    }

    //添加到购物车
    public void appsFlyerAddToCar(float price, String id) {
        StartOtherPlugin.appsFlyerAddToCar(6.0f, "111");
    }

    //广告点击
    public void appsFlyerADClick(String type) {
        //banner, screen,video,dialog
        StartOtherPlugin.appsFlyerADClick(type);
    }

    //更新
    public void appsFlyerUpdate(String oldVersion, String newVersion) {
        StartOtherPlugin.appsFlyerUpdate("1.0", "2.0");
    }

    //分享
    public void appsFlyerShare(String name, String platform) {
        StartOtherPlugin.appsFlyerShare("分享个好玩的游戏", "google");
    }

    //搜索
    public void appsFlyerSearch(String key) {
        StartOtherPlugin.appsFlyerSearch("搜索内容");
    }

    //邀请
    public void appsFlyerInvite(String content) {
        StartOtherPlugin.appsFlyerInvite("邀请你来玩游戏");
    }

    //从通知中打开app
    public void appsFlyerOFPN(String id) {
        StartOtherPlugin.appsFlyerOFPN("111");
    }

    //完成教程
    public void appsFlyerCompTutorial() {
        StartOtherPlugin.appsFlyerCompTutorial();
    }

    //游戏通关
    public void appsFlyerAchievedLevel(String level) {
        StartOtherPlugin.appsFlyerAchievedLevel("7");
    }

    //成就解锁
    public void appsFlyerAchUnlock(String id, String name) {
        StartOtherPlugin.appsFlyerAchUnlock("111", "达到宗师级别");
    }

    //广告浏览
    public void appsFlyerADView(String type) {
        //banner, screen,video,dialog
        StartOtherPlugin.appsFlyerADView(type);
    }*/

    //facebook媒体事件接入

    //购买
    public void fbPurchased(double price, String ncy, String productId, String orderId) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "USD");
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, productId);
        params.putString(AppEventsConstants.EVENT_PARAM_ORDER_ID, orderId);
        if (logger != null) {
//            logger.logEvent(AppEventsConstants.EVENT_NAME_PURCHASED, price, params);
        }
    }

    //下单
    public void fbCheckOut(Double price, String ncy, String productId, String orderId) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "USD");
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, productId);
        params.putString(AppEventsConstants.EVENT_PARAM_ORDER_ID, orderId);
        if (logger != null) {
//            logger.logEvent(AppEventsConstants.EVENT_NAME_INITIATED_CHECKOUT, price, params);
        }
    }

    //添加至购物车
    public void fbAddToCar(String id) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, id);
        if (logger != null) {
            logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, params);
        }
    }

    //添加至心愿单
    public void fbAddToWishList(String id) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, id);
        if (logger != null) {
            logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_WISHLIST, params);
        }
    }

    //广告点击
    public void fbAdClick(String type) {
        //banner, rewarded_video, native
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_AD_TYPE, type);
        if (logger != null) {
            logger.logEvent(AppEventsConstants.EVENT_NAME_AD_CLICK, params);
        }
    }

    //广告浏览
    public void fbAdView(String type) {
        //banner, interstitial, rewarded_video, native
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_AD_TYPE, type);
        if (logger != null) {
            logger.logEvent(AppEventsConstants.EVENT_NAME_AD_IMPRESSION, params);
        }
    }

    //完成关卡
    public void fbAchUnlock(String level) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_LEVEL, level);
        if (logger != null) {
            logger.logEvent(AppEventsConstants.EVENT_NAME_ACHIEVED_LEVEL, params);
        }
    }

    //激活app
    public void fbActApp() {
        if (logger != null) {
//            logger.logEvent(AppEventsConstants.EVENT_NAME_ACTIVATED_APP);
        }
    }

    //完成注册
    public void fbCompRegister() {
        if (logger != null) {
            Bundle bundle = new Bundle();
            bundle.putString("fb_content_id", "1");
//            logger.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION);
        }
    }

    //完成教程
    public void fbCompTutorial() {
        if (logger != null) {
            logger.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_TUTORIAL);
        }
    }

    //搜索
    public void fbSearch(String key) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_SEARCH_STRING, key);
        if (logger != null) {
            logger.logEvent(AppEventsConstants.EVENT_NAME_SEARCHED, params);
        }
    }

    //解锁成就
    public void fbUnlockedAch(String des) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, des);
        if (logger != null) {
            logger.logEvent(AppEventsConstants.EVENT_NAME_UNLOCKED_ACHIEVEMENT, params);
        }
    }

    //---------------------------------------adjust------------------------------------//

    //adjust login事件
    public void adjustLogin(String userId) {
        Adjust.trackEvent(generateEvent("twaj2x", false));
    }

    //adjust 下单事件
    public void adjustCheckOut(Double price) {
        AdjustEvent event = generateEvent("3f7zfy", true);
        event.addCallbackParameter("easou_hk_price", String.valueOf(price));
        event.addPartnerParameter("easou_hk_user_id", Constant.ESDK_USERID);
        Adjust.trackEvent(event);
    }

    //adjust 启动事件
    public void adjustStart() {
        Adjust.trackEvent(generateEvent("sqslba", false));
    }

    //adjust 支付事件
    public void adjustPay(Double price, String ncy, String orderId) {
        AdjustEvent event = generateEvent("6yila5", true);
        event.addCallbackParameter("easou_hk_price", String.valueOf(price));
        event.setRevenue(price, "USD");
        event.addPartnerParameter("easou_hk_user_id", Constant.ESDK_USERID);
        event.setOrderId(orderId);
        Log.d(TAG, "订单id........" + orderId);
        Adjust.trackEvent(event);
    }

    //adjust 注册事件
    public void adjustRegister(String userId) {
        AdjustEvent event = generateEvent("1mmn9g", false);
        event.addPartnerParameter("easou_hk_user_id", userId);
        event.addPartnerParameter("easou_hk_version", Constant.SDK_VERSION);
        event.addPartnerParameter("easou_hk_ver", Constant.SDK_VERSION);
        Adjust.trackEvent(event);
    }

    //adjust 激活事件
    public void adjustActive() {
        Adjust.trackEvent(generateEvent("saqddr", false));
    }

    //adjust 分享事件
    public void adjustShare() {
        Adjust.trackEvent(generateEvent("qhiow4", false));
    }

    //adjust 完成教程事件
    public void adjustCompTutorial() {
        Adjust.trackEvent(generateEvent("9pyu0g", false));
    }

    //adjust 广告点击事件
    public void adjustAdClick() {
        Adjust.trackEvent(generateEvent("hn0bbi", false));
    }

    //adjust 搜索事件
    public void adjustSearch() {
        Adjust.trackEvent(generateEvent("ljl83j", false));
    }

    //adjust 更新事件
    public void adjustUpdate() {
        Adjust.trackEvent(generateEvent("2k9lcf", false));
    }

    //adjust 添加到购物车事件
    public void adjustAddToCar() {
        Adjust.trackEvent(generateEvent("oxg0zu", false));
    }

    //adjust 点击推送消息打开app事件
    public void adjustOFPN() {
        Adjust.trackEvent(generateEvent("xmdded", false));
    }

    //adjust 通关事件
    public void adjustCompGame() {
        Adjust.trackEvent(generateEvent("m62ogs", false));
    }

    //adjust 邀请事件
    public void adjustInvite() {
        Adjust.trackEvent(generateEvent("q8v65g", false));
    }

    private AdjustEvent generateEvent(String code, boolean isAddOrderId) {
        AdjustEvent event = new AdjustEvent(code);
        event.addCallbackParameter("easou_hk_android_id", Constant.ANDROIDID);
        if (isAddOrderId) {
            event.addCallbackParameter("easou_hk_order_id", "easou_hk_" + mESOrder);
        }
        event.addCallbackParameter("easou_hk_device_id", Constant.IMEI);
        event.addCallbackParameter("easou_hk_user_id", Constant.ESDK_USERID);
        event.addCallbackParameter("easou_hk_game_name", "京都大掌櫃");
        if (mActivity != null) {
            event.addCallbackParameter("easou_hk_app_id", getPropertiesValue(mActivity, "appId"));
        }
        event.addCallbackParameter("app_name", Constant.SDK_VERSION);
        if (mActivity != null) {
            event.addCallbackParameter("app_version", mActivity.getApplicationInfo().packageName);
        }
        return event;
    }

    private static ProgressDialog progressDialog = null;

    private void showDialog() {
        try {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(mActivity);
            }
            progressDialog.setMessage("Loading......");
//            progressDialog.setTitle(R.string.es_loading);
         /*   progressDialog.setTitle(mActivity.getApplication().getResources()
                    .getIdentifier("es_loading", "string", getApplication().getPackageName()));*/
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}

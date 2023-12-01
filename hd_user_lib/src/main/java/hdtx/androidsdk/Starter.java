package hdtx.androidsdk;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
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

import hdtx.androidsdk.callback.ActivityLifecycleWrapper;
import hdtx.androidsdk.callback.ESdkCallback;
import hdtx.androidsdk.callback.ESdkPayCallback;
import hdtx.androidsdk.data.Constant;
import hdtx.androidsdk.data.ESConstant;
import hdtx.androidsdk.http.BaseResponse;
import hdtx.androidsdk.http.EAPayInter;
import hdtx.androidsdk.plugin.StartESUserPlugin;
import hdtx.androidsdk.plugin.StartLogPlugin;
import hdtx.androidsdk.romutils.RomHelper;
import hdtx.androidsdk.romutils.RomUtils;
import hdtx.androidsdk.ui.ESPayWebActivity;
import hdtx.androidsdk.ui.ESUserWebActivity;
import hdtx.androidsdk.util.AESUtil;
import hdtx.androidsdk.util.CommonUtils;
import hdtx.androidsdk.util.ESdkLog;
import hdtx.androidsdk.util.ThreadPoolManager;
import hdtx.androidsdk.util.Tools;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class Starter {

    public static ESdkCallback mCallback = null;
    public static Activity mActivity;

    public volatile static Starter mSingleton = null;
    private String mProductId = "";
    private String mTradeId = "";
    private ESdkPayCallback mPayCallBack;
    private GoogleSignInClient mGoogleSignInClient;
    private BillingClient billingClient;
    private CallbackManager callbackManager;
    public static final String TAG = "GoogleAndFBLog";
    public static final int SIGN_LOGIN = 13;
    //测试参数
    private static String googleID = "846876477691-gjefh1ll8fdq72pb5htugj4459kls3nr.apps.googleusercontent.com";
    private static String ADJUSTKEY = "y2vss2p6tedc";
    private String fbAppId = "";
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
        if (mActivity != null) {
            showDialog(mActivity);
        }
        mPayCallBack = callback;
        if (info == null) {
            throw new RuntimeException("Payment parameter is necessary");
        }
        mPayInfo = info;
        mProductId = mPayInfo.optString(ESConstant.PRODUCT_ID);
        mPrice = mPayInfo.optInt(ESConstant.MONEY);
        mTradeId = mPayInfo.optString(ESConstant.TRADE_ID);
        int payType = 0;
        try {
            payType = mPayInfo.optInt("payType");
        } catch (Exception e) {
        }
        Log.i("hdpay", info.toString());
        if (payType == 0) {
            initBilling(mActivity);
        } else {
            xsollaCreateOrder();
        }
    }

    private void xsollaCreateOrder() {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                BaseResponse result = EAPayInter.checkOrder(mTradeId, mProductId, String.valueOf(mPrice), 110l, mNcy,
                        CommonUtils.getCheckOutParams(mActivity.getApplicationInfo().packageName), mPayInfo);
                JSONObject custom = null;
                String payUrl = "";
                if (result != null && result.getCode() == 0) {
                    try {
                        custom = new JSONObject(result.getData().toString());
                        String data = AESUtil.decrypt(custom.optString("content"), Constant.AESKEY);
                        JSONObject content = new JSONObject(data);
                        mESOrder = content.optString("orderNo");
                        String payType = content.optString("isWebView");
                        payUrl = URLDecoder.decode(content.optString("payUrl"));
                        adjustCheckOut(mPrice);
                        fbCheckOut(mPrice, mProductId, mNcy, mESOrder);
                        final String finalPayUrl = payUrl;
                        final String type = payType;
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideDialog();
                                xsollaPay(type, finalPayUrl);
                            }
                        });
                    } catch (Exception e) {
                        payFailAndHideDialog();
                    }
                } else {
                    payFailAndHideDialog();
                }
            }
        });
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
                                    mESOrder = purchase.getAccountIdentifiers().getObfuscatedAccountId();
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
                                            acknowledgementState = content.getInt("ackStatus");
                                            consumptionState = content.getInt("consumptionStatus");
                                        } catch (Exception e) {
                                        }
                                        if (acknowledgementState == 0) {
                                            //服务器验证成功，核销订单
                                            Log.d(TAG, "验证成功，核销订单中........");
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
                                                        mPayCallBack.onPaySuccess();
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
            Log.d(TAG, "核销订单进行中........");
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
                        ThreadPoolManager.getInstance().addTask(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (Starter.class) {
                                    String googleStorePrice = String.valueOf(mPrice);
                                    long priceMicro = 0L;
                                    if (list.size() > 0) {
                                        mNcy = list.get(0).getPriceCurrencyCode();
                                        googleStorePrice = list.get(0).getPrice();
                                        priceMicro = list.get(0).getPriceAmountMicros();
                                    }
                                    BaseResponse result = EAPayInter.checkOrder(mTradeId, mProductId, googleStorePrice, priceMicro, mNcy,
                                            CommonUtils.getCheckOutParams(mActivity.getApplicationInfo().packageName), mPayInfo);
                                    JSONObject custom = null;
                                    String payUrl = "";
                                    if (result != null && result.getCode() == 0) {
                                        try {
                                            custom = new JSONObject(result.getData().toString());
                                            String data = AESUtil.decrypt(custom.optString("content"), Constant.AESKEY);
                                            JSONObject content = new JSONObject(data);
                                            mESOrder = content.optString("orderNo");
                                            String isGooglePay = content.optString("isGooglePay");
                                            Log.d(TAG, "下单成功日志 fb........" + mPrice + mESOrder);
                                            adjustCheckOut(mPrice);
                                            fbCheckOut(mPrice, mProductId, mNcy, mESOrder);
                                            if (isGooglePay.equals("false")) {
                                                String payType = content.optString("isWebView");
                                                payUrl = URLDecoder.decode(content.optString("payUrl"));
                                                final String finalPayUrl = payUrl;
                                                final String type = payType;
                                                mActivity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        hideDialog();
                                                        xsollaPay(type, finalPayUrl);
                                                    }
                                                });
                                            } else {
                                                mActivity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        hideDialog();
                                                        launchGooglePay(list.get(0));
                                                    }
                                                });
                                            }
                                        } catch (Exception e) {
                                        }
                                    } else {
                                        Log.d(TAG, "下单失败........" + 1004);
                                        payFailAndHideDialog();
                                    }
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
        if (requestCode == SIGN_LOGIN) {
            Log.d(TAG, "setActivityResultGoogle");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task == null) {
                Log.d(TAG, "task：null");
            }
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                StartESUserPlugin.loginGoogle(account.getIdToken(), account.getId(), isBindGoogle);
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
        ESdkLog.d("初始化sdk,sdk版本v" + Constant.SDK_VERSION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            builder.detectFileUriExposure();
            StrictMode.setVmPolicy(builder.build());
        }

        try {
            ApplicationInfo info = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
            googleID = info.metaData.getString("g_clientId");
            fbAppId = info.metaData.getString("com.facebook.sdk.ApplicationId");
            if (fbAppId != null && fbAppId.length() > 2) {
                fbAppId = fbAppId.substring(2);
            }
            CommonUtils.saveKey(mContext, Constant.unuselessdata);
            CommonUtils.saveBase(mContext, Constant.unuselessvalue);
        } catch (PackageManager.NameNotFoundException e) {
        }
        Tools.getAndroidId(mContext);
//        Constant.APPID = getPropertiesValue(mContext, "appId");
        initXsolla(mContext);
        initAdjust(mContext);
        initFb(mContext);
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
    }

    private void initFb(Context mContext) {
        logger = AppEventsLogger.newLogger(mContext);
//        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);
    }

    private void initXsolla(Context mContext) {
    /*    LoginConfig config = new LoginConfig.OauthBuilder()
                .setProjectId("")
                .setOauthClientId(0)
                .build();
        XLogin.init(mContext, config);
        XStore.init(0, "jwt");*/
    }

    private void xsollaPay(String type, String payUrl) {
        if (type.equals("false")) {
            //系统浏览器打开
            Uri uri = Uri.parse(payUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            mActivity.startActivity(intent);
        } else {
            //系统webview打开
            Intent intent = new Intent();
            intent.putExtra("url", payUrl);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(mActivity, ESPayWebActivity.class);
            mActivity.startActivity(intent);
        }
    }

    private void launchGooglePay(SkuDetails skuDetails) {
        final BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .setObfuscatedAccountId(mESOrder)
                .build();
        BillingResult billingFlow = billingClient.launchBillingFlow(mActivity, billingFlowParams);
    }

    private void payFailAndHideDialog() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideDialog();
                mPayCallBack.onPayFail(1004);
            }
        });
    }

    private void initAdjust(Context mContext) {
        //测试为沙箱模式，正式版需切换到生产模式
        String environment = AdjustConfig.ENVIRONMENT_SANDBOX;
       /* if (Locale.getDefault().getLanguage().toLowerCase().equals("vi")) {
            ADJUSTKEY = "h12a6xxd1dkw";
        } else if (Locale.getDefault().getLanguage().toLowerCase().equals("es")) {
            ADJUSTKEY = "o8rk94yswvls";
        } else {
            ADJUSTKEY = "wr31h0sikr28";
        }*/
        AdjustConfig config = new AdjustConfig(mContext, ADJUSTKEY, environment);
        config.setLogLevel(LogLevel.VERBOSE);
        config.setSendInBackground(true);
        config.setFbAppId(fbAppId);
        Adjust.onCreate(config);
        ((Application) mContext).registerActivityLifecycleCallbacks(new ActivityLifecycleWrapper());
    }

    public void pageResume() {
        ESdkLog.d("进入游戏界面接口");
        if (billingClient != null) {
            //内购商品交易查询
            billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, purchasesResponseListener);
        }
    }

    //初始化facebook登录
    public void initFacebook(boolean isBind) {
        Log.d(TAG, "进入facebook" + isBind);
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
                        if (exception instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                            }
                        }
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
    }

    public void payCallback(int type) {
        if (mPayCallBack != null) {
            if (type == 0) {
                Log.d(TAG, "web支付成功.......");
                adjustPay(mPrice, mNcy, mESOrder);
                fbPurchased(mPrice, mNcy, mProductId, mESOrder);
                mPayCallBack.onPaySuccess();
            } else if (type == 1) {
                Log.d(TAG, "web支付失败......." + type);
                mPayCallBack.onPayFail(1001);
            } else if (type == 2) {
                Log.d(TAG, "web支付异常......." + type);
                mPayCallBack.onPayFail(1001);
            } else if (type == 3) {
                Log.d(TAG, "用户取消web支付......." + type);
                mPayCallBack.onPayFail(1000);
            }
        }
    }

    public void initGoogleLogin(boolean isBind) {
        isBindGoogle = isBind;
        mActivity.startActivityForResult(getGoogleIntent(), SIGN_LOGIN);
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
    public void fbCompRegister(String id) {
        if (logger != null) {
            Bundle bundle = new Bundle();
            bundle.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, id);
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
        Adjust.trackEvent(generateEvent("tlw0ta", false));
    }

    //adjust 下单事件
    public void adjustCheckOut(Double price) {
        AdjustEvent event = generateEvent("x72bxt", true);
        event.addCallbackParameter("easou_hk_price", String.valueOf(price));
//        event.addPartnerParameter("easou_hk_user_id", Constant.ESDK_USERID);
        Adjust.trackEvent(event);
    }

    //adjust 启动事件
    public void adjustStart() {
        AdjustEvent event = generateEvent("9wf987", false);
//        event.addPartnerParameter("easou_hk_user_id", Constant.APPID);
//        event.addPartnerParameter("easou_hk_user_id", Constant.IMEI + "|" + System.currentTimeMillis());
        Adjust.trackEvent(event);
    }


    //adjust 支付事件
    public void adjustPay(Double price, String ncy, String orderId) {
        AdjustEvent event = generateEvent("6rqmip", true);
        event.addCallbackParameter("easou_hk_price", String.valueOf(price));
        event.setRevenue(price, "USD");
//        event.addPartnerParameter("easou_hk_user_id", Constant.ESDK_USERID);
        event.setOrderId(orderId);
        Log.d(TAG, "订单id........" + orderId);
        Adjust.trackEvent(event);
    }

    //adjust 注册事件
    public void adjustRegister(String userId) {
        AdjustEvent event = generateEvent("dkp32e", false);
//        event.addPartnerParameter("easou_hk_user_id", Constant.IMEI + "|" + System.currentTimeMillis());
        event.addPartnerParameter("easou_hk_user_id", userId);
        Adjust.trackEvent(event);
    }

    //adjust 激活事件
    public void adjustActive() {
        Adjust.trackEvent(generateEvent("hcib0n", false));
    }

    //adjust 分享事件
//    public void adjustShare() {
//        Adjust.trackEvent(generateEvent("qhiow4", false));
//    }

    //adjust 完成教程事件
    public void adjustCompTutorial() {
        Adjust.trackEvent(generateEvent("l1sltf", false));
    }

    //adjust 广告点击事件
    public void adjustAdClick() {
        Adjust.trackEvent(generateEvent("k9d50o", false));
    }

    //adjust 搜索事件
    public void adjustSearch() {
        Adjust.trackEvent(generateEvent("tt8tle", false));
    }

    //adjust 更新事件
//    public void adjustUpdate() {
//        Adjust.trackEvent(generateEvent("2k9lcf", false));
//    }

    //adjust 添加到购物车事件
    public void adjustAddToCar() {
        Adjust.trackEvent(generateEvent("k190re", false));
    }

    //adjust 点击推送消息打开app事件
//    public void adjustOFPN() {
//        Adjust.trackEvent(generateEvent("xmdded", false));
//    }

    //adjust 通关事件
//    public void adjustCompGame() {
//        Adjust.trackEvent(generateEvent("m62ogs", false));
//    }

    //adjust 邀请事件
//    public void adjustInvite() {
//        Adjust.trackEvent(generateEvent("q8v65g", false));
//    }

    private AdjustEvent generateEvent(String code, boolean isAddOrderId) {
        AdjustEvent event = new AdjustEvent(code);
        event.addCallbackParameter("easou_hk_android_id", Constant.ANDROIDID);
        if (isAddOrderId) {
            event.addCallbackParameter("easou_hk_order_id", "easou_hk_" + mESOrder);
        }
        event.addCallbackParameter("easou_hk_device_id", Constant.IMEI);
        event.addCallbackParameter("easou_hk_user_id", Constant.ESDK_USERID);
        event.addCallbackParameter("easou_hk_game_name", "龙珠");
        if (mActivity != null) {
            event.addCallbackParameter("easou_hk_app_id", getPropertiesValue(mActivity, "appId"));
        }
        event.addCallbackParameter("app_name", Constant.SDK_VERSION);
        if (mActivity != null) {
            event.addCallbackParameter("app_version", mActivity.getApplicationInfo().packageName);
        }
        return event;
    }

    private AdjustEvent generatePartnerEvent(AdjustEvent event) {
        event.addPartnerParameter("easou_hk_description", Constant.IMEI + "|" + System.currentTimeMillis());
        return event;
    }

    private ProgressDialog progressDialog = null;

    private void showDialog(Activity mActivity) {
        try {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(mActivity);
            }
            progressDialog.setMessage("Loading......");
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

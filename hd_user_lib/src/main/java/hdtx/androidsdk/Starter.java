package hdtx.androidsdk;

import static hdtx.androidsdk.util.GAdUtils.getAdSize;

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
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.test.espresso.core.internal.deps.guava.collect.ImmutableList;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAdRevenue;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.LogLevel;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.reflect.TypeToken;

import hdtx.androidsdk.callback.ActivityLifecycleWrapper;
import hdtx.androidsdk.callback.AppOpenAdManager;
import hdtx.androidsdk.callback.BannerAdCallback;
import hdtx.androidsdk.callback.ESdkCallback;
import hdtx.androidsdk.callback.ESdkPayCallback;
import hdtx.androidsdk.callback.FBFriendsCallback;
import hdtx.androidsdk.callback.GAdsCallback;
import hdtx.androidsdk.callback.OpenAdCallback;
import hdtx.androidsdk.data.Constant;
import hdtx.androidsdk.data.ESConstant;
import hdtx.androidsdk.data.FBInfo;
import hdtx.androidsdk.data.FBUser;
import hdtx.androidsdk.http.BaseResponse;
import hdtx.androidsdk.http.EAPayInter;
import hdtx.androidsdk.plugin.StartESUserPlugin;
import hdtx.androidsdk.plugin.StartLogPlugin;
import hdtx.androidsdk.romutils.RomHelper;
import hdtx.androidsdk.romutils.RomUtils;
import hdtx.androidsdk.ui.AdDialogFragment;
import hdtx.androidsdk.ui.ESPayWebActivity;
import hdtx.androidsdk.ui.ESUserWebActivity;
import hdtx.androidsdk.util.AESUtil;
import hdtx.androidsdk.util.CommonUtils;
import hdtx.androidsdk.util.ESdkLog;
import hdtx.androidsdk.util.GsonUtil;
import hdtx.androidsdk.util.Md5SignUtils;
import hdtx.androidsdk.util.ThreadPoolManager;
import hdtx.androidsdk.util.Tools;

import org.json.JSONArray;
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
    private FBFriendsCallback mFBFriendsCallBack;
    private GoogleSignInClient mGoogleSignInClient;
    private BillingClient billingClient;
    private CallbackManager callbackManager;
    public static final String TAG = "GoogleAndFBLog";
    public static final int SIGN_LOGIN = 13;
    //测试参数
    private static String googleID = "846876477691-gjefh1ll8fdq72pb5htugj4459kls3nr.apps.googleusercontent.com";
    private static String ADJUSTKEY = "gvm8idmkuha8";
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
        String notifyUrl = mPayInfo.optString(ESConstant.NOTIFY_URL);
     /*   int payType = 0;
        try {
            payType = mPayInfo.optInt("payType");
        } catch (Exception e) {
        }*/
        ESdkLog.c(TAG, info.toString());
        xsollaCreateOrder(notifyUrl);
       /* if (payType == 0) {
            initBilling(mActivity);
        } else {
            xsollaCreateOrder();
        }*/
    }

    private void xsollaCreateOrder(String notifyUrl) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                String appId = CommonUtils.readPropertiesValue(Starter.mActivity, "appId");
                String qn = CommonUtils.readPropertiesValue(Starter.mActivity, "qn");
                String redirectUrl = CommonUtils.readPropertiesValue(Starter.mActivity, "redirectUrl");
                String packName = mActivity.getApplicationInfo().packageName;
                BaseResponse result = EAPayInter.GetOrderToken(appId, packName, Constant.ESDK_USERID, Constant.ESDK_TOKEN, qn, mProductId, notifyUrl, mTradeId);
                JSONObject custom = null;
                if (result != null && result.getCode() == 0) {
                    try {
                        custom = new JSONObject(result.getData().toString());
                        String dataToken = AESUtil.decrypt(custom.optString("content"), Constant.AESKEY);
//                        JSONObject content = new JSONObject(data);
//                        mESOrder = content.optString("orderNo");
//                        String payType = content.optString("isWebView");
//                        payUrl = URLDecoder.decode(content.optString("payUrl"));
                        adjustCheckOut(mPrice);
                        fbCheckOut(mPrice, mProductId, mNcy, mESOrder);
//                        final String finalPayUrl = payUrl;
//                        final String type = payType;
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideDialog();
                                tokenWebView(dataToken, appId, mProductId, mTradeId, qn, notifyUrl, redirectUrl);
//                                xsollaPay(type, finalPayUrl);
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

    private void tokenWebView(String dataToken, String appId, String mProductId, String mTradeId, String qn, String notifyUrl, String redirectUrl) {
        //系统webview打开
        String url = Constant.BASE_URL_PAYWEB + "?" + getParams(dataToken, appId, mProductId, mTradeId, qn, notifyUrl, redirectUrl);
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        mActivity.startActivity(intent);
    }

    private String getParams(String dataToken, String appId, String mProductId, String mTradeId, String qn, String notifyUrl, String redirectUrl) {
        String language = "tw";
        try {
            //语言 简繁体中文都是zh 英文是es
            language = Locale.getDefault().getLanguage().toLowerCase();
            //国家 简体是cn 繁体是tw或hk，英文是us
            String code = Locale.getDefault().getCountry().toLowerCase();
            if (code.equals("tw") || code.equals("hk")) {
                language = "tw";
            } else if (code.equals("cn")) {
                language = "cn";
            } else if (language.equals("es")) {
                language = "en";
            }
        } catch (Exception e) {
            language = "tw";
        }
        String serviceId = mPayInfo.optString(ESConstant.PLAYER_SERVER_ID);
        String serviceName = mPayInfo.optString(ESConstant.SERVER_NAME);
        String playerId = mPayInfo.optString(ESConstant.PLAYER_ID);
        String playerName = mPayInfo.optString(ESConstant.PLAYER_NAME);
        String playerlevel = mPayInfo.optString(ESConstant.PLAYER_LEVEL);
        String productName = mPayInfo.optString(ESConstant.PRODUCT_NAME);
        String key = CommonUtils.readPropertiesValue(Starter.mActivity, "key");
        String params = "token=" + dataToken + "&accountId=" + Constant.ESDK_USERID +
                "&tradeId=" + mTradeId + "&productId=" + mProductId + "&appId=" + appId + "&cpOrderNo=" + mTradeId +
                "&qn=" + qn + "&cpNotifyUrl=" + notifyUrl + "&money=" + mPrice + "&language=" + language + "&deviceId=" + Constant.IMEI +
                "&ip=" + Constant.NET_IP + "&redirectUrl=" + redirectUrl + "&serviceId=" + serviceId + "&playerId=" + playerId + "&serviceName=" + serviceName +
                "&playerName=" + playerName + "&playerLevel=" + playerlevel + "&productName=" + productName;
        Map<String,String> mapValue = new HashMap<>();
        mapValue.put("token",dataToken);
        mapValue.put("accountId",Constant.ESDK_USERID);
        mapValue.put("tradeId",mTradeId);
        mapValue.put("productId",mProductId);
        mapValue.put("appId",appId);
        mapValue.put("cpOrderNo",mTradeId);
        mapValue.put("qn",qn);
        mapValue.put("cpNotifyUrl",notifyUrl);
        mapValue.put("money",String.valueOf(mPrice));
        mapValue.put("language",language);
        mapValue.put("deviceId",Constant.IMEI);
        mapValue.put("ip",Constant.NET_IP );
        mapValue.put("redirectUrl",redirectUrl);
        mapValue.put("serviceId",serviceId);
        mapValue.put("playerId",playerId);
        mapValue.put("serviceName",serviceName);
        mapValue.put("playerName",playerName);
        mapValue.put("playerLevel",playerlevel);
        mapValue.put("productName",productName);
        String sign = Md5SignUtils.sign(mapValue, key);
        return params + "&sign=" + sign;
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
                            ESdkLog.c(TAG, "购买成功，进入验证订单程序........");
                            for (final Purchase purchase : purchases) {
                                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                    ESdkLog.c(TAG, "购买成功，验证订单中........");
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
                                            ESdkLog.c(TAG, "验证成功，核销订单中........");
                                            ESdkLog.c(TAG, "购买成功日志 fb........" + mPrice + mESOrder);
                                            adjustPay(mPrice, mNcy, mESOrder);
                                            fbPurchased(mPrice, mNcy, mProductId, mESOrder);
                                            consumePurchase(purchase.getPurchaseToken());
                                            //可重复购买的内购商品核销，回调购买给游戏处理
                                            if (mPayCallBack != null) {
                                                mActivity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ESdkLog.c(TAG, "验证成功，发放用户权益");
                                                        mPayCallBack.onPaySuccess();
                                                    }
                                                });
                                            }
                                        } else {
                                            if (consumptionState == 0) {
                                                ESdkLog.c(TAG, "验证成功，该订单未核销，执行核销订单........");
                                                consumePurchase(purchase.getPurchaseToken());
                                            }
                                            ESdkLog.c(TAG, "该订单已经验证并已被核销........");
                                        }
                                    } else {
                                        if (mPayCallBack != null) {
                                            mActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    int code = 1002;
                                                    if (result != null && result.getCode() != 9998) {
                                                        //验证失败
                                                        ESdkLog.c(TAG, "验证失败，核销订单失败........" + 1002);
                                                        mPayCallBack.onPayFail(code);
                                                    }
                                                    ESdkLog.c(TAG, "验证失败，该订单已被验证........" + 1002);
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
                    ESdkLog.c(TAG, "验证失败，取消购买........" + 1000);
                    mPayCallBack.onPayFail(1000);
                }
                break;
            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                //用户已拥有
                if (mPayCallBack != null) {
                    ESdkLog.c(TAG, "验证失败，用户已拥有该产品........" + 1003);
                    mPayCallBack.onPayFail(1003);
                }
                break;
            default:
                //购买失败，具体异常码可以到BillingClient.BillingResponseCode中查看
                if (mPayCallBack != null) {
                    ESdkLog.c(TAG, "验证失败，核销订单失败........" + 1001);
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
            ESdkLog.c(TAG, "核销订单成功........");
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
            ESdkLog.c(TAG, "核销订单进行中........");
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
            ESdkLog.c(TAG, "服务断开，重新连接");
            billingClient.startConnection(this);
        }

        @Override
        public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                // The BillingClient is ready. You can query purchases here.
                //获取商品详情回调
                ProductDetailsResponseListener skuDetailsResponseListener = new ProductDetailsResponseListener() {
                    @Override
                    public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> list) {
                        //list为可用商品的集合
                        //将要购买商品的商品详情配置到参数中
                        ESdkLog.c(TAG, "应用内商品数量：" + list.size());
                        ThreadPoolManager.getInstance().addTask(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (Starter.class) {
                                    String googleStorePrice = String.valueOf(mPrice);
                                    long priceMicro = 0L;
                                    if (list.size() > 0) {
                                        mNcy = list.get(0).getOneTimePurchaseOfferDetails().getPriceCurrencyCode();
                                        googleStorePrice = list.get(0).getOneTimePurchaseOfferDetails().getFormattedPrice();
                                        priceMicro = list.get(0).getOneTimePurchaseOfferDetails().getPriceAmountMicros();
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
                                            ESdkLog.c(TAG, "下单成功日志 fb........" + mPrice + mESOrder);
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
                                        ESdkLog.c(TAG, "下单失败........" + 1004);
                                        payFailAndHideDialog();
                                    }
                                }
                            }
                        });
                    }
                };

                //查询内购类型的商品
                //productId为产品ID(从谷歌后台获取)
                QueryProductDetailsParams skuParams = QueryProductDetailsParams.newBuilder()
                        .setProductList(ImmutableList.of(
                                QueryProductDetailsParams.Product.newBuilder()
                                        .setProductId(mProductId)
                                        .setProductType(BillingClient.ProductType.INAPP)
                                        .build()))
                        .build();
                billingClient.queryProductDetailsAsync(skuParams, skuDetailsResponseListener);
                ESdkLog.c(TAG, "查询后台商品");
            }
        }
    };

    private void initBilling(Activity mActivity) {
        if (billingClient == null) {
            billingClient = BillingClient.newBuilder(mActivity)
                    .setListener(purchasesUpdatedListener)
                    .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
                    .build();
            ESdkLog.c(TAG, "billingclient 初始化");
        }
        billingClient.startConnection(billingClientStateListener);
        ESdkLog.c(TAG, "billingclient 开始连接google play");
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
            ESdkLog.c(TAG, "setActivityResultGoogle");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task == null) {
                ESdkLog.c(TAG, "task：null");
            }
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                StartESUserPlugin.loginGoogle(account.getIdToken(), account.getId(), isBindGoogle);
                ESdkLog.c(TAG, "Id:" + account.getId() + "|Email:" + account.getEmail() + "|IdToken:" + account.getIdToken());
            } catch (ApiException e) {
                e.printStackTrace();
                ESdkLog.c(TAG, "ApiException:" + e.getMessage());
                Toast.makeText(mActivity, mActivity.getApplication().getResources()
                        .getIdentifier("es_loginerror", "string", mActivity.getApplication().getPackageName()), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface OnShowAdCompleteListener {
        void onShowAdComplete();

        void onAppOpenAdShow();
    }

    private AppOpenAdManager appOpenAdManager = null;
    private Activity currentActivity;

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
        String isEnableGAd = getPropertiesValue(mContext, "isEnableGAd");
        ((Application) mContext).registerActivityLifecycleCallbacks(new ActivityLifecycleWrapper() {
            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                if (!appOpenAdManager.isShowingAd) {
                    currentActivity = activity;
                }
            }
        });
        initAds(mContext, isEnableGAd);
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                FBInfo info = EAPayInter.getFBInfo(getPropertiesValue(mContext, "partnerId"), getPropertiesValue(mContext, "appId"),
                        getPropertiesValue(mContext, "qn"), getPropertiesValue(mContext, "key"));
                String fbAppId = "";
                if (info == null) {
                    ApplicationInfo applicationInfo = null;
                    try {
                        applicationInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
                        ESdkLog.d("get fb info failed");
                        fbAppId = applicationInfo.metaData.getString("com.facebook.sdk.ApplicationId");
                        if (fbAppId != null && fbAppId.length() > 2) {
                            fbAppId = fbAppId.substring(2);
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                } else {
                    ESdkLog.d("get fb info success" + info.getFbAppId());
                    FacebookSdk.setClientToken(info.getFbToken());
                    FacebookSdk.setApplicationId("fb" + info.getFbAppId());
                    FacebookSdk.setApplicationName(info.getFbName());
                    fbAppId = info.getFbAppId();
                    ESdkLog.d("after set fbappid" + FacebookSdk.getApplicationId());
                }
                initFb(mContext);
                initAdjust(mContext, fbAppId);
            }
        });
        try {
            ApplicationInfo info = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
            googleID = info.metaData.getString("g_clientId");
            CommonUtils.saveKey(mContext, Constant.unuselessdata);
            CommonUtils.saveBase(mContext, Constant.unuselessvalue);
        } catch (PackageManager.NameNotFoundException e) {
        }
        Tools.getAndroidId(mContext);
//        Constant.APPID = getPropertiesValue(mContext, "appId");
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
    }

    private void initFb(Context mContext) {
        logger = AppEventsLogger.newLogger(mContext);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);
    }

    public void setGAdCallback(GAdsCallback gAdsCallback) {
        mGAdCallback = gAdsCallback;
    }

    private boolean noWaitingOpenAd = false;
    private Handler adHandler = new Handler(Looper.myLooper());
    private OpenAdCallback openAdCallback;
    private Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            if (appOpenAdManager != null) {
                appOpenAdManager.stopAutoShow();
            }
            noWaitingOpenAd = true;
        }
    };

    private Runnable enterRunnable = new Runnable() {
        @Override
        public void run() {
            if (appOpenAdManager != null) {
                appOpenAdManager.stopAutoShow();
            }
            openAdCallback.onAdComplete();
        }
    };

    private ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            if (noWaitingOpenAd) {
                removeListener();
            }
            return noWaitingOpenAd;
        }
    };
    private ViewTreeObserver viewTreeObserver;
    private long lastTime = 0L;
    private long interval = 4 * 60 * 60 * 1000;

    public void cancelOpenAd() {
        if (appOpenAdManager != null) {
            appOpenAdManager.stopAutoShow();
        }
    }

    /**
     * 加载开屏广告
     */
    public void loadOpenAd(Activity mActivity, boolean isDiySplash, OpenAdCallback callback) {
        if (appOpenAdManager == null) {
            return;
        }
        openAdCallback = callback;
        if (!isDiySplash) {
            SplashScreen.installSplashScreen(mActivity);
            View rootView = mActivity.findViewById(android.R.id.content);
            viewTreeObserver = rootView.getViewTreeObserver();
            viewTreeObserver.addOnPreDrawListener(preDrawListener);
        }

        appOpenAdManager.showAdIfAvailable(mActivity, new OnShowAdCompleteListener() {
            @Override
            public void onShowAdComplete() {
                if (isDiySplash) {
                    adHandler.removeCallbacks(enterRunnable);
                } else {
                    noWaitingOpenAd = true;
                }
                openAdCallback.onAdComplete();
            }

            @Override
            public void onAppOpenAdShow() {
                if (isDiySplash) {
                    adHandler.removeCallbacks(enterRunnable);
                } else {
                    adHandler.removeCallbacks(timeoutRunnable);
                }
                openAdCallback.onAdShow();
            }
        }, true);
        if (isDiySplash) {
            adHandler.postDelayed(enterRunnable, 5000);
        } else {
            adHandler.postDelayed(timeoutRunnable, 5000);
        }
    }

    private void removeListener() {
        if (viewTreeObserver != null && viewTreeObserver.isAlive()) {
            viewTreeObserver.removeOnPreDrawListener(preDrawListener);
            viewTreeObserver = null;
        }
    }

    /**
     * 初始化广告sdk
     *
     * @param mContext
     * @param gAdsCallback 广告回调
     */
    private GAdsCallback mGAdCallback;

    public void initAds(Context mContext, String isEnableGAd) {
        if (isEnableGAd.equals("0")) {
            String openAdUnitId = "";
            try {
                ApplicationInfo info = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
                openAdUnitId = info.metaData.getString("open_ad_unitId");
            } catch (PackageManager.NameNotFoundException e) {
                Log.i("GoogleAdMobLog", "请在配置文件中添加开屏广告id");
            }
            appOpenAdManager = new AppOpenAdManager(openAdUnitId);
            ThreadPoolManager.getInstance().addTask(new Runnable() {
                @Override
                public void run() {
                    MobileAds.initialize(mContext, initializationStatus -> {
                        Map<String, AdapterStatus> statusMap =
                                initializationStatus.getAdapterStatusMap();
                        for (String adapterClass : statusMap.keySet()) {
                            AdapterStatus status = statusMap.get(adapterClass);
                            Log.d(TAG, String.format("Adapter name: %s, Description: %s, Latency: %d", adapterClass, status.getDescription(), status.getLatency()));
                        }
                        if (appOpenAdManager != null) {
                            appOpenAdManager.loadAd(mContext);
                        }
                    });
                }
            });
            ProcessLifecycleOwner.get().getLifecycle().addObserver(new DefaultLifecycleObserver() {
                @Override
                public void onStart(@NonNull LifecycleOwner owner) {
                    DefaultLifecycleObserver.super.onStart(owner);
                    ESdkLog.c("AppOpenAdManager", "Lifecycle-onStart");
                    if (currentActivity != null) {
                        if (lastTime == 0L || System.currentTimeMillis() - lastTime > interval) {
                            if (appOpenAdManager != null) {
                                ESdkLog.c("AppOpenAdManager", "热启动加载开屏广告");
                                appOpenAdManager.showAdIfAvailable(currentActivity, new OnShowAdCompleteListener() {
                                    @Override
                                    public void onShowAdComplete() {

                                    }

                                    @Override
                                    public void onAppOpenAdShow() {
                                        lastTime = System.currentTimeMillis();
                                    }
                                }, false);
                            }
                        }
                    }
                }
            });
        } else {
            Log.i("GoogleAdMobLog", "请在配置文件client.properties中添加isEnableGAd=0开启google广告");
        }
    }

    /* */
    /**
     * LifecycleObserver method that shows the app open ad when the app moves to foreground.
     *//*
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected void onMoveToForeground() {
        // Show the ad (if available) when the app moves to foreground.
        appOpenAdManager.showAdIfAvailable(currentActivity);
    }

    private void loadAd(@NonNull Activity activity) {
        // We wrap the loadAd to enforce that other classes only interact with MyApplication
        // class.
        appOpenAdManager.loadAd(activity);
    }

    private void showAdIfAvailable(
            @NonNull Activity activity, @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        // We wrap the showAdIfAvailable to enforce that other classes only interact with MyApplication
        // class.
        appOpenAdManager.showAdIfAvailable(activity, onShowAdCompleteListener);
    }*/

    private InterstitialAd mInterstitialAd;

    /**
     * 展示插页式广告
     *
     * @param mActivity
     */
    public void loadInterstitialAd(Activity mActivity) {
        if (!getPropertiesValue(mActivity, "isEnableGAd").equals("0")) {
            return;
        }
        String interstitialAdUnitId = "";
        try {
            ApplicationInfo info = mActivity.getPackageManager().getApplicationInfo(mActivity.getPackageName(), PackageManager.GET_META_DATA);
            interstitialAdUnitId = info.metaData.getString("interstitial_ad_unitId");
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("GoogleAdMobLog", "请在配置文件中添加插页式广告id");
        }
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(mActivity, interstitialAdUnitId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        if (mGAdCallback != null) {
                            mGAdCallback.onAdLoaded();
                        }
                        ESdkLog.c(TAG, "adsource=" + interstitialAd.getResponseInfo().getMediationAdapterClassName());
                        mInterstitialAd.setOnPaidEventListener(new OnPaidEventListener() {
                            @Override
                            public void onPaidEvent(@NonNull AdValue adValue) {
                                adjustADRevenue(adValue.getValueMicros() / 1000000.0, adValue.getCurrencyCode(), mInterstitialAd.getResponseInfo().
                                        getLoadedAdapterResponseInfo().getAdSourceName());
                            }
                        });
                        setFullAdCallback();
                        showFullScreenAd(mActivity);
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                        if (mGAdCallback != null) {
                            mGAdCallback.onAdFailedToLoad();
                        }
                    }
                });
    }

    public void showFullScreenAd(Activity mActivity) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(mActivity);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }


    private void setFullAdCallback() {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.");
                if (mGAdCallback != null) {
                    mGAdCallback.onAdClicked();
                }
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad dismissed fullscreen content.");
                mInterstitialAd = null;
                if (mGAdCallback != null) {
                    mGAdCallback.onAdDismissed();
                }
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.");
                mInterstitialAd = null;
                if (mGAdCallback != null) {
                    mGAdCallback.onAdFailedToShow();
                }
            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.");
                if (mGAdCallback != null) {
                    mGAdCallback.onAdImpression();
                }
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.");
                if (mGAdCallback != null) {
                    mGAdCallback.onAdShowed();
                }
            }
        });
    }

    private RewardedAd rewardedAd;

    /**
     * 加载激励广告
     *
     * @param mActivity
     */
    public void loadRewardAd(Activity mActivity) {
        if (!getPropertiesValue(mActivity, "isEnableGAd").equals("0")) {
            return;
        }
        String rewardAdUnitId = "";
        try {
            ApplicationInfo info = mActivity.getPackageManager().getApplicationInfo(mActivity.getPackageName(), PackageManager.GET_META_DATA);
            rewardAdUnitId = info.metaData.getString("reward_ad_unitId");
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("GoogleAdMobLog", "请在配置文件中添加激励广告id");
        }
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(mActivity, rewardAdUnitId,
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.toString());
                        rewardedAd = null;
                        if (mGAdCallback != null) {
                            mGAdCallback.onAdFailedToLoad();
                        }
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        if (mGAdCallback != null) {
                            mGAdCallback.onAdLoaded();
                        }
                        ESdkLog.c(TAG, "adsource=" + ad.getResponseInfo().getMediationAdapterClassName());
                        rewardedAd.setOnPaidEventListener(new OnPaidEventListener() {
                            @Override
                            public void onPaidEvent(@NonNull AdValue adValue) {
                                adjustADRevenue(adValue.getValueMicros() / 1000000.0, adValue.getCurrencyCode(), mInterstitialAd.getResponseInfo().
                                        getLoadedAdapterResponseInfo().getAdSourceName());
                            }
                        });
                        setRewardFullScreenCallback();
                        showRewardAd(mActivity);
                        Log.d(TAG, "Ad was loaded.");
                    }
                });
    }

    private void setRewardFullScreenCallback() {
        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.");
                if (mGAdCallback != null) {
                    mGAdCallback.onAdClicked();
                }
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad dismissed fullscreen content.");
                rewardedAd = null;
                if (mGAdCallback != null) {
                    mGAdCallback.onAdDismissed();
                }
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.");
                rewardedAd = null;
                if (mGAdCallback != null) {
                    mGAdCallback.onAdFailedToShow();
                }
            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.");
                if (mGAdCallback != null) {
                    mGAdCallback.onAdImpression();
                }
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.");
                if (mGAdCallback != null) {
                    mGAdCallback.onAdShowed();
                }
            }
        });
    }

    public void showRewardAd(Activity mActivity) {
        if (rewardedAd != null) {
            rewardedAd.show(mActivity, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                    if (mGAdCallback != null) {
                        mGAdCallback.onRewardEarned(rewardAmount, rewardType);
                    }
                    Log.d(TAG, "The user earned the reward,reward = " + rewardAmount + "-----type=" + rewardType);
                }
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
        }
    }

    private RewardedInterstitialAd rewardedInterstitialAd = null;

    public void loadRewardInterstitialAd(AppCompatActivity mActivity) {
        if (!getPropertiesValue(mActivity, "isEnableGAd").equals("0")) {
            return;
        }
        String rewardedInterstitialAdUnitId = "";
        try {
            ApplicationInfo info = mActivity.getPackageManager().getApplicationInfo(mActivity.getPackageName(), PackageManager.GET_META_DATA);
            rewardedInterstitialAdUnitId = info.metaData.getString("reward_interstitial_ad_unitId");
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("GoogleAdMobLog", "请在配置文件中添加插页式激励广告id");
        }
        // Use the test ad unit ID to load an ad.
        RewardedInterstitialAd.load(mActivity, rewardedInterstitialAdUnitId,
                new AdRequest.Builder().build(), new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedInterstitialAd ad) {
                        Log.d(TAG, "Ad was loaded.");
                        rewardedInterstitialAd = ad;
                        RewardItem rewardItem = rewardedInterstitialAd.getRewardItem();
                        if (mGAdCallback != null) {
                            mGAdCallback.onAdLoaded();
                        }
                        int rewardAmount = rewardItem.getAmount();
                        String rewardType = rewardItem.getType();
                        introduceVideoAd(rewardAmount, rewardType, mActivity);
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        Log.d(TAG, loadAdError.toString());
                        rewardedInterstitialAd = null;
                        if (mGAdCallback != null) {
                            mGAdCallback.onAdFailedToLoad();
                        }
                    }
                });
    }

    private void introduceVideoAd(int rewardAmount, String rewardType, AppCompatActivity mActivity) {
        AdDialogFragment dialog = AdDialogFragment.newInstance(rewardAmount, rewardType);
        dialog.setAdDialogInteractionListener(
                new AdDialogFragment.AdDialogInteractionListener() {
                    @Override
                    public void onShowAd() {
                        if (mGAdCallback != null) {
                            mGAdCallback.onShowAdDialog();
                        }
                        Log.d(TAG, "The rewarded interstitial ad is starting.");
                        setRewardInterFullAdCallback();
                        showRewardInterstitialAd(mActivity);
                    }

                    @Override
                    public void onCancelAd() {
                        if (mGAdCallback != null) {
                            mGAdCallback.onAdCancel();
                        }
                        Log.d(TAG, "The rewarded interstitial ad was skipped before it starts.");
                    }
                });
        dialog.show(mActivity.getSupportFragmentManager(), "tag");
    }

    private void showRewardInterstitialAd(Activity mActivity) {
        rewardedInterstitialAd.setOnPaidEventListener(new OnPaidEventListener() {
            @Override
            public void onPaidEvent(@NonNull AdValue adValue) {
                adjustADRevenue(adValue.getValueMicros() / 1000000.0, adValue.getCurrencyCode(), mInterstitialAd.getResponseInfo().
                        getLoadedAdapterResponseInfo().getAdSourceName());
            }
        });
        rewardedInterstitialAd.show(mActivity, new OnUserEarnedRewardListener() {
            @Override
            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                // Handle the reward.
                int rewardAmount = rewardItem.getAmount();
                String rewardType = rewardItem.getType();
                if (mGAdCallback != null) {
                    mGAdCallback.onRewardEarned(rewardAmount, rewardType);
                }
                Log.d(TAG, "The user earned the reward,reward = " + rewardAmount + "-----type=" + rewardType);
            }
        });
    }

    private void setRewardInterFullAdCallback() {
        rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.");
                if (mGAdCallback != null) {
                    mGAdCallback.onAdClicked();
                }
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad dismissed fullscreen content.");
                rewardedInterstitialAd = null;
                if (mGAdCallback != null) {
                    mGAdCallback.onAdDismissed();
                }
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.");
                rewardedInterstitialAd = null;
                if (mGAdCallback != null) {
                    mGAdCallback.onAdFailedToShow();
                }
            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.");
                if (mGAdCallback != null) {
                    mGAdCallback.onAdImpression();
                }
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.");
                if (mGAdCallback != null) {
                    mGAdCallback.onAdShowed();
                }
            }
        });
    }

    private void loadBannerAd(Activity mActivity, FrameLayout layout, String bannerId) {
        if (!getPropertiesValue(mActivity, "isEnableGAd").equals("0")) {
            return;
        }
        // Create a new ad view.
        AdView adView = new AdView(mActivity);
        adView.setAdSize(getAdSize(mActivity, layout));
        adView.setAdUnitId(bannerId);
        adView.setAdListener(new BannerAdCallback() {
            @Override
            public void onAdLoaded() {
                if (mGAdCallback != null) {
                    mGAdCallback.onAdLoaded();
                }
                ESdkLog.c(TAG, "adsource=" + adView.getResponseInfo().getMediationAdapterClassName());
                adView.setOnPaidEventListener(new OnPaidEventListener() {
                    @Override
                    public void onPaidEvent(@NonNull AdValue adValue) {
                        adjustADRevenue(adValue.getValueMicros() / 1000000.0, adValue.getCurrencyCode(), mInterstitialAd.getResponseInfo().
                                getLoadedAdapterResponseInfo().getAdSourceName());
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                if (mGAdCallback != null) {
                    mGAdCallback.onAdFailedToLoad();
                }
            }

            @Override
            public void onAdClosed() {
                if (mGAdCallback != null) {
                    mGAdCallback.onAdClosed();
                }
            }

            @Override
            public void onAdClicked() {
                if (mGAdCallback != null) {
                    mGAdCallback.onAdClicked();
                }
            }

            @Override
            public void onAdOpened() {
                if (mGAdCallback != null) {
                    mGAdCallback.onAdOpened();
                }
            }
        });
        // Replace ad container with new ad view.
        layout.removeAllViews();
        layout.addView(adView);

        // Start loading the ad in the background.
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private FrameLayout mAdContainerView = null;

    /**
     * 展示横幅广告
     *
     * @param mActivity
     */
    public void loadBannerAd(Activity mActivity, int gravity) {
        if (mAdContainerView == null) {
            mAdContainerView = new FrameLayout(mActivity);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 300, gravity);
            mAdContainerView.setLayoutParams(params);
            ViewGroup rootView = mActivity.findViewById(android.R.id.content);
            rootView.addView(mAdContainerView);
        }
        String bannerAdUnitId = "";
        try {
            ApplicationInfo info = mActivity.getPackageManager().getApplicationInfo(mActivity.getPackageName(), PackageManager.GET_META_DATA);
            bannerAdUnitId = info.metaData.getString("banner_ad_unitId");
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("GoogleAdMobLog", "请在配置文件中添加banner广告id");
        }
        loadBannerAd(mActivity, mAdContainerView, bannerAdUnitId);
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

    private void launchGooglePay(ProductDetails productDetails) {
        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .build()
                );
        final BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .setObfuscatedAccountId(mESOrder)
                .build();
        BillingResult billingResult = billingClient.launchBillingFlow(mActivity, billingFlowParams);
        ESdkLog.c(TAG, "billingclientcode" + billingResult.getResponseCode());

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

    private void initAdjust(Context mContext, String fbAppId) {
        //测试为沙箱模式，正式版需切换到生产模式
        String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;
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
            billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build(), purchasesResponseListener);
        }
    }

    //初始化facebook登录
    public void initFacebook(boolean isBind, boolean isNeedLoginSdk) {
        ESdkLog.c(TAG, "进入facebook" + isBind);
        isBindFacebook = isBind;
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        ESdkLog.d("after set fbappid login" + FacebookSdk.getApplicationId());
                        AccessToken accessToken = loginResult.getAccessToken();
                        ESdkLog.c(TAG, "Facebook------>>" + "token:" + accessToken.getToken() + "|userid:" + accessToken.getUserId());
                        if (isNeedLoginSdk) {
                            StartESUserPlugin.loginFacebook(accessToken.getToken(), accessToken.getUserId(), isBindFacebook);
                        } else {
                            graphRequest(accessToken);
                        }
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        ESdkLog.c(TAG, "Facebook cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        ESdkLog.c(TAG, "Facebook error------>>" + exception.toString());
                        if (exception instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                            }
                        }
                    }
                });
        LoginManager.getInstance().logIn(mActivity, Arrays.asList("public_profile", "user_friends"));
    }

    private void graphRequest(AccessToken token) {
        //获取好友列表
        String userId = token.getUserId();
        GraphRequest request = GraphRequest.newGraphPathRequest(token, "/" + userId + "/friends", new GraphRequest.Callback() {
            @Override
            public void onCompleted(@NonNull GraphResponse graphResponse) {
                try {
                    ESdkLog.d("fbfriends------->" + graphResponse.getRawResponse());
                    JSONObject jResponse = graphResponse.getJSONObject();
                    JSONObject jError = jResponse.optJSONObject("error");
                    if (jError != null) {
                        ESdkLog.d("fbfriendserror" + jError.optString("message"));
                        if (mFBFriendsCallBack != null) {
                            mFBFriendsCallBack.fail(jError.optInt("code"), jError.optString("message"));
                        }
                        return;
                    }
                    JSONArray jDatas = jResponse.optJSONArray("data");
                    List<FBUser> fbUser = GsonUtil.fromJson(jDatas.toString(), new TypeToken<List<FBUser>>() {
                    }.getType());
                    if (mFBFriendsCallBack != null) {
                        mFBFriendsCallBack.success(fbUser);
                    }
                } catch (Exception e) {
                    ESdkLog.d("fbfrienderror" + e.getMessage());
                    if (mFBFriendsCallBack != null) {
                        mFBFriendsCallBack.fail(101, "unknow error");
                    }
                }
            }
        });
        request.executeAsync();
    }

    public void getFbFriends(FBFriendsCallback callBack) {
        this.mFBFriendsCallBack = callBack;
        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token != null && !token.isExpired()) {
            graphRequest(token);
        } else {
            //需要重新授权登录
            initFacebook(false, false);
        }
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
                ESdkLog.c(TAG, "web支付成功.......");
                adjustPay(mPrice, mNcy, mESOrder);
                fbPurchased(mPrice, mNcy, mProductId, mESOrder);
                mPayCallBack.onPaySuccess();
            } else if (type == 1) {
                ESdkLog.c(TAG, "web支付失败......." + type);
                mPayCallBack.onPayFail(1001);
            } else if (type == 2) {
                ESdkLog.c(TAG, "web支付异常......." + type);
                mPayCallBack.onPayFail(1001);
            } else if (type == 3) {
                ESdkLog.c(TAG, "用户取消web支付......." + type);
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
        Adjust.trackEvent(generateEvent("l53rfd", false));
    }

    //adjust 下单事件
    public void adjustCheckOut(Double price) {
        AdjustEvent event = generateEvent("j995py", true);
        event.addCallbackParameter("easou_hk_price", String.valueOf(price));
//        event.addPartnerParameter("easou_hk_user_id", Constant.ESDK_USERID);
        Adjust.trackEvent(event);
    }

    //adjust 启动事件
    public void adjustStart() {
        AdjustEvent event = generateEvent("dnhuoq", false);
//        event.addPartnerParameter("easou_hk_user_id", Constant.APPID);
//        event.addPartnerParameter("easou_hk_user_id", Constant.IMEI + "|" + System.currentTimeMillis());
        Adjust.trackEvent(event);
    }

    public void adjustADRevenue(Double money, String currencyCode, String network) {
        Log.d(TAG, "adrevenue,money=" + money + "-----currency=" + currencyCode + "-----network=" + network);
        AdjustAdRevenue adRevenue = new AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB);
        adRevenue.setRevenue(money, currencyCode);
        adRevenue.setAdRevenueNetwork(network);
        Adjust.trackAdRevenue(adRevenue);
    }

    //adjust 支付事件
    public void adjustPay(Double price, String ncy, String orderId) {
        AdjustEvent event = generateEvent("4ugnay", true);
        event.addCallbackParameter("easou_hk_price", String.valueOf(price));
        event.setRevenue(price, "USD");
//        event.addPartnerParameter("easou_hk_user_id", Constant.ESDK_USERID);
        event.setOrderId(orderId);
        ESdkLog.c(TAG, "订单id........" + orderId);
        Adjust.trackEvent(event);
    }

    //adjust 注册事件
    public void adjustRegister(String userId) {
        AdjustEvent event = generateEvent("49k5ny", false);
//        event.addPartnerParameter("easou_hk_user_id", Constant.IMEI + "|" + System.currentTimeMillis());
        event.addPartnerParameter("easou_hk_user_id", userId);
        Adjust.trackEvent(event);
    }

    //adjust 激活事件
    public void adjustActive() {
        Adjust.trackEvent(generateEvent("yoq1fj", false));
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
        event.addCallbackParameter("easou_hk_game_name", "龙珠2（賽亞之神）");
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

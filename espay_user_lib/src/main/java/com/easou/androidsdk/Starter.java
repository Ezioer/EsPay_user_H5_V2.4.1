package com.easou.androidsdk;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.baidu.mobads.action.BaiduAction;
import com.easou.androidsdk.callback.AppTimeWatcher;
import com.easou.androidsdk.callback.ESdkCallback;
import com.easou.androidsdk.callback.GameUserDataCallback;
import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.http.EAPayImp;
import com.easou.androidsdk.plugin.StartESPayPlugin;
import com.easou.androidsdk.plugin.StartESUserPlugin;
import com.easou.androidsdk.plugin.StartLogPlugin;
import com.easou.androidsdk.plugin.StartOtherPlugin;
import com.easou.androidsdk.romutils.RomHelper;
import com.easou.androidsdk.romutils.RomUtils;
import com.easou.androidsdk.ui.ESUserWebActivity;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.util.ESdkLog;
import com.easou.androidsdk.util.ThreadPoolManager;
import com.easou.androidsdk.util.Tools;

import java.util.HashMap;
import java.util.Map;


public class Starter {

    public static Map<String, String> map = null;
    public static Handler mHandler = null;
    public static ESdkCallback mCallback = null;
    public static Activity mActivity;

    public volatile static Starter mSingleton = null;

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
     *
     * @param mHandler 回调
     * @param map      参数
     */
    public void pay(Activity mActivity, Map<String, String> map, Handler mHandler) {
        Starter.map = map;
        Starter.mHandler = mHandler;
        StartESPayPlugin.setPayParams(mActivity, map);
    }


    /**
     * 宜搜SDK登陆接口
     */
    public void login(final Activity activity, ESdkCallback mCallback) {
        StartOtherPlugin.getOaid(activity);
        Starter.mCallback = mCallback;
        Starter.mActivity = activity;
        StartOtherPlugin.onLaunchApp();
        StartOtherPlugin.initKSSDK(activity);
        StartOtherPlugin.initTTSDK(activity);
        StartOtherPlugin.initAQY(activity);
        /** 初始化汇川广告GISM SDK */
        StartOtherPlugin.initGism(activity, false);
        /** 广点通SDK初始化 */
        StartOtherPlugin.initGDTAction(activity);
        /** 百度初始化 */
        StartOtherPlugin.initBD(activity);
        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
//                ESdkLog.d("快手sdk激活");
                StartOtherPlugin.logKSActionAppActive();
            }
        }, 3000);
        StartESUserPlugin.loginSdk();
    }

    /**
     * 获取SDK用户信息
     */
    public void getUserInfo() {
        StartESUserPlugin.getH5UserInfo();
    }

    /**
     * 显示SDK实名认证页面
     */
    public void showUserCertView() {
        StartESUserPlugin.getUserCertStatus();
    }

    /**
     * 显示SDK用户中心页面
     */
    public void showUserCenter() {
        StartESUserPlugin.showSdkView();
    }

    /**
     * 显示SDK悬浮窗
     */
    public void showFloatView() {
        //oppo和vivo不给权限都能显示出来，所以每次显示时都得校验是否开启悬浮窗权限
        if (RomUtils.checkIsVivo() || RomUtils.checkIsOppoRom()) {
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
    public void startGameLoginLog(Map<String, String> playerInfo) {
        StartESUserPlugin.startGameLoginLog(playerInfo);
        //游戏角色数据上传
//        StartLogPlugin.gamePlayerDataLog(playerInfo);
        //游戏角色上线日志上传
        Map info = new HashMap();
        info.put("bt", "1");
        info.put("deviceId", Tools.getDeviceImei(Starter.mActivity));
        info.put("userId", CommonUtils.getUserId(Starter.mActivity));
        ESUserWebActivity.clientToJS(Constant.YSTOJS_GAME_LOGINOROUTLOG, info);
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
     * 初始化今日头条SDK
     */
    @Deprecated
    public void initTTSDK(Context context) {
//        StartOtherPlugin.initTTSDK(context);
    }

    /**
     * 初始化爱奇艺SDK
     */
    @Deprecated
    public void initAQY(Context mContext) {
        StartOtherPlugin.initAQY(mContext);
    }

    /**
     * 初始化SDK，获取oaid，判断是否为模拟器
     */
    public void initEntry(Context mContext) {
        StartOtherPlugin.initEntry(mContext);
        StartOtherPlugin.checkSimulator(mContext);
    }

    /**
     * 从Properties文件中读取配置信息
     *
     * @param key：参数名称
     */
    public static String getPropertiesValue(Context _context, String key) {
        return StartESUserPlugin.getPropValue(_context, key);
    }

    /**
     * 初始化GISM SDK
     */
    @Deprecated
    public void initGismSDK(Context context, boolean debug) {
//        StartOtherPlugin.initGism(context, debug);
    }

    /**
     * GISM退出游戏回调
     */
    public void onGismExitApp() {
        StartOtherPlugin.logGismActionExitApp();
    }


    /**
     * 广点通SDK行为数据上报初始化
     *
     * @param mContext 上下文对象
     */
    @Deprecated
    public void initGDTAction(Context mContext) {
        StartOtherPlugin.initGDTAction(mContext);
        AppTimeWatcher.getInstance().registerWatcher((Application) mContext);
    }

    /**
     * 广点通SDK上报app启动
     */
    @Deprecated
    public void logGDTAction() {
        if (Constant.IS_LOGINED) {
            StartOtherPlugin.logGDTAction();
        }
    }

    /**
     * 快手SDK初始化
     */
    @Deprecated
    public void initKSSDK(Context mContext) {
        StartOtherPlugin.initBD(mContext);
//        StartOtherPlugin.initKSSDK(mContext);
    }

    /**
     * 快手SDK活跃事件，进入app首页时调用
     */
    @Deprecated
    public void logKSActionAppActive() {
//        StartOtherPlugin.logKSActionAppActive();
    }

    /**
     * 头条进入页面统计
     */
    @Deprecated
    public void logTTPageResume(Activity context) {
        StartOtherPlugin.onTTResume(context);
    }

    /**
     * 头条离开页面统计
     */
    @Deprecated
    public void logTTPagePause(Activity context) {
        StartOtherPlugin.onTTPause(context);
    }

    /**
     * 快手SDK进入游戏界面
     */
    @Deprecated
    public void logKSActionPageResume(Activity activity) {
        StartOtherPlugin.logKSActionPageResume(activity);
    }

    /**
     * 快手SDK退出游戏界面
     */
    @Deprecated
    public void logKSActionPagePause(Activity activity) {
        StartOtherPlugin.logKSActionPagePause(activity);
    }

    /**
     * 爱奇艺SDK进入游戏界面
     */
    @Deprecated
    public void logAQYActionPageResume() {
        StartOtherPlugin.resumeAQY();
    }

    /**
     * 爱奇艺SDK退出游戏界面
     */
    @Deprecated
    public void logAQYActionPageDestory() {
        StartOtherPlugin.destoryAQY();
    }

    /**
     * baidu进入页面统计
     */
    @Deprecated
    public void logBDPageResume() {
        StartOtherPlugin.logBDPage();
    }

    //上传用户配置文件
    public void saveUserGameData(final String content) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                EAPayImp.uploadTxt(content);
            }
        });
    }

    //下载用户配置文件
    public void getUserGameData(final GameUserDataCallback callback) {
        try {
            ThreadPoolManager.getInstance().addTask(new Runnable() {
                @Override
                public void run() {
                    final String result = EAPayImp.downloadTxt();
                    if (mActivity != null) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccessResult(result);
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            callback.onErrorResult();
        }
    }

    /**
     * 处理百度sdk的权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void handleBDPermissions(int requestCode,
                                    @NonNull String permissions[], @NonNull int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            String temp = permissions[i];
            if (temp.equals(Manifest.permission.READ_PHONE_STATE)) {
                // 授权结果回传
                BaiduAction.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
            }
        }
    }

    /**
     * 统一为数据初始化接口
     *
     * @param mContext
     */
    public void dataCollectInit(Context mContext) {
        ESdkLog.d("初始化媒体接口");
        AppTimeWatcher.getInstance().registerWatcher((Application) mContext);
    }

    public void pageResume(Activity activity) {
        ESdkLog.d("进入游戏界面接口");
        //广点通上报启动
        if (Constant.IS_LOGINED) {
            StartOtherPlugin.logGDTAction();
        }
        //快手sdk进入游戏界面
        StartOtherPlugin.logKSActionPageResume(activity);
        //百度浏览页面
        StartOtherPlugin.logBDPage();
        StartOtherPlugin.onTTResume(activity);
        StartOtherPlugin.resumeAQY();
    }

    public void pagePause(Activity activity) {
        ESdkLog.d("离开游戏界面接口");
        StartOtherPlugin.logKSActionPagePause(activity);
        StartOtherPlugin.onTTPause(activity);
    }

    public void pageDestory() {
        ESdkLog.d("退出游戏");
        StartOtherPlugin.destoryAQY();
    }
}

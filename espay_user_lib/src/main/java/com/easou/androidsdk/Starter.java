package com.easou.androidsdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.baidu.mobads.action.BaiduAction;
import com.easou.androidsdk.callback.AppTimeWatcher;
import com.easou.androidsdk.callback.ESdkCallback;
import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.plugin.StartESPayPlugin;
import com.easou.androidsdk.plugin.StartESUserPlugin;
import com.easou.androidsdk.plugin.StartLogPlugin;
import com.easou.androidsdk.plugin.StartOtherPlugin;
import com.easou.androidsdk.util.ESdkLog;

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
        Starter.mCallback = mCallback;
        Starter.mActivity = activity;
        StartOtherPlugin.onLaunchApp();
        StartOtherPlugin.initKSSDK(activity);
        StartOtherPlugin.initTTSDK(activity);
        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                ESdkLog.d("快手sdk激活");
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
        StartESUserPlugin.showFloatView();
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
    }


    /**
     * 初始化今日头条SDK
     */
    public void initTTSDK(Context context) {
//        StartOtherPlugin.initTTSDK(context);
    }

    /**
     * 初始化爱奇艺SDK
     */
    public void initAQY(Context mContext){
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
    public void initGismSDK(Context context, boolean debug) {
        StartOtherPlugin.initGism(context, debug);
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
    public void initGDTAction(Context mContext) {
        StartOtherPlugin.initGDTAction(mContext);
        AppTimeWatcher.getInstance().registerWatcher((Application)mContext);
    }

    /**
     * 广点通SDK上报app启动
     */
    public void logGDTAction() {
        if (Constant.IS_LOGINED) {
            StartOtherPlugin.logGDTAction();
        }
    }

    /**
     * 快手SDK初始化
     */
    public void initKSSDK(Context mContext) {
        StartOtherPlugin.initBD(mContext);
//        StartOtherPlugin.initKSSDK(mContext);
    }

    /**
     * 快手SDK活跃事件，进入app首页时调用
     */
    public void logKSActionAppActive() {
//        StartOtherPlugin.logKSActionAppActive();
    }

    /**
     * 头条进入页面统计
     */
    public void logTTPageResume(Activity context) {
//        StartOtherPlugin.onTTResume(context);
    }

    /**
     * 头条离开页面统计
     */
    public void logTTPagePause(Activity context) {
//        StartOtherPlugin.onTTPause(context);
    }

    /**
     * 快手SDK进入游戏界面
     */
    public void logKSActionPageResume(Activity activity) {
        StartOtherPlugin.logKSActionPageResume(activity);
    }

    /**
     * 快手SDK退出游戏界面
     */
    public void logKSActionPagePause(Activity activity) {
        StartOtherPlugin.logKSActionPagePause(activity);
    }

    /**
     * 爱奇艺SDK进入游戏界面
     */
    public void logAQYActionPageResume(){
        StartOtherPlugin.resumeAQY();
    }

    /**
     * 爱奇艺SDK退出游戏界面
     */
    public void logAQYActionPageDestory() {
        StartOtherPlugin.destoryAQY();
    }

    /**
     * baidu进入页面统计
     */
    public void logBDPageResume() {
        StartOtherPlugin.logBDPage();
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
        BaiduAction.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}

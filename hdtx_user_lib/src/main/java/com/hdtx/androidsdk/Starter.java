package com.hdtx.androidsdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;

//import com.baidu.mobads.action.BaiduAction;
import com.baidu.mobads.action.BaiduAction;
import com.baidu.mobads.action.PrivacyStatus;
import com.bytedance.hume.readapk.HumeSDK;
import com.hdtx.androidsdk.callback.AppTimeWatcher;
import com.hdtx.androidsdk.callback.HDPrivateCallback;
import com.hdtx.androidsdk.callback.HDSdkCallback;
import com.hdtx.androidsdk.data.Constant;
import com.hdtx.androidsdk.plugin.StartHDUserPlugin;
import com.hdtx.androidsdk.plugin.StartHdPayPlugin;
import com.hdtx.androidsdk.plugin.StartLogPlugin;
import com.hdtx.androidsdk.plugin.StartOtherPlugin;
import com.hdtx.androidsdk.romutils.RomHelper;
import com.hdtx.androidsdk.romutils.RomUtils;
import com.hdtx.androidsdk.ui.HDUserWebActivity;
import com.hdtx.androidsdk.ui.NotiDialog;
import com.hdtx.androidsdk.util.CommonUtils;
import com.hdtx.androidsdk.util.HDSdkLog;
import com.hdtx.androidsdk.util.OaidHelper;
import com.hdtx.androidsdk.util.ThreadPoolManager;
import com.hdtx.androidsdk.util.Tools;
import com.kwai.monitor.payload.TurboHelper;
import com.tencent.vasdolly.helper.ChannelReaderUtil;

import java.util.HashMap;
import java.util.Map;


public class Starter {

    public static Map<String, String> map = null;
    public static Handler mHandler = null;
    public static HDSdkCallback mCallback = null;
    public static Activity mActivity;

    public volatile static Starter mSingleton = null;

    private Starter() {
    }

    /**
     * SDK单例
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
     * SDK支付接口
     *
     * @param mHandler 回调
     * @param map      参数
     */
    public void pay(Activity mActivity, Map<String, String> map, Handler mHandler) {
        Starter.map = map;
        Starter.mHandler = mHandler;
        StartHdPayPlugin.setPayParams(mActivity, map);
    }

    /**
     * SDK登陆接口
     */
    public void login(final Activity activity, HDSdkCallback mCallback) {
        HDSdkLog.d("进入sdk登录流程");
        Starter.mCallback = mCallback;
        Starter.mActivity = activity;
        StartOtherPlugin.onLaunchApp();
        /** 初始化汇川广告GISM SDK */
        StartOtherPlugin.initGism(activity, false);
        /** 广点通SDK初始化 */
        StartOtherPlugin.initGDTAction(activity);
        StartOtherPlugin.initTTSDK(activity);
        if (Constant.isTTVersion == 1) {
            Constant.qnChannel = HumeSDK.getChannel(activity);
        }
        if (CommonUtils.getIsKs(activity)) {
            Constant.qnChannel = TurboHelper.getChannel(activity);
        }
        if (CommonUtils.getIsEnableMedia(activity, "use_GDT")) {
            String qn = ChannelReaderUtil.getChannel(activity.getApplicationContext());
            if (qn != null && !qn.equals("")) {
                Constant.qnChannel = qn;
            }
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                HDSdkLog.d("快手sdk激活");
                StartOtherPlugin.logKSActionAppActive();
                if (!Constant.AQY_SDK) {
                    StartOtherPlugin.initAQY(activity);
                }
            }
        }, 2000);
        StartHDUserPlugin.loginSdk();
    }

    /**
     * 获取SDK用户信息
     */
    public void getUserInfo() {
        StartHDUserPlugin.getH5UserInfo();
    }

    /**
     * 显示SDK实名认证页面
     */
    public void showUserCertView() {
        StartHDUserPlugin.getUserCertStatus();
    }

    /**
     * 显示SDK用户中心页面
     */
    public void showUserCenter() {
        StartHDUserPlugin.showSdkView();
    }

    //退出登录
    public void logOut() {
        StartHDUserPlugin.changeAccount();
    }

    /**
     * 显示SDK悬浮窗
     */
    public void showFloatView() {
        if (RomUtils.checkIsVivo()) {
            if (mActivity != null && RomHelper.checkPermission(mActivity)) {
                StartHDUserPlugin.showFloatView();
            }
        } else {
            StartHDUserPlugin.showFloatView();
        }
    }

    /**
     * 隐藏SDK悬浮窗
     */
    public void hideFloatView() {
        StartHDUserPlugin.hideFloatView();
    }

    /**
     * 上传游戏登录日志
     *
     * @param playerInfo 游戏角色信息
     */
    public void startGameLoginLog(Map<String, String> playerInfo) {
        StartHDUserPlugin.startGameLoginLog(playerInfo);
        //游戏角色数据上传
        StartLogPlugin.gamePlayerDataLog(playerInfo, CommonUtils.readPropertiesValue(Starter.mActivity, "isTurnExt").equals("0"));
        //游戏角色上线日志上传
        Map info = new HashMap();
        info.put("bt", "1");
        info.put("deviceId", Tools.getDeviceImei(Starter.mActivity));
        info.put("userId", CommonUtils.getUserId(Starter.mActivity));
        HDUserWebActivity.clientToJS(Constant.YSTOJS_GAME_LOGINOROUTLOG, info);
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
        HDUserWebActivity.clientToJS(Constant.YSTOJS_GAME_LOGINOROUTLOG, info);
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
        return StartHDUserPlugin.getPropValue(_context, key);
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

    /**
     * 处理百度sdk的权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void handleBDPermissions(int requestCode,
                                    String permissions[], int[] grantResults) {
        if (!Constant.BD_SDK) {
            return;
        }
        boolean isGet = false;
        BaiduAction.onRequestPermissionsResult(requestCode, permissions, grantResults);
      /*  for (int i = 0; i < permissions.length; i++) {
            String temp = permissions[i];
            if (temp.equals(Manifest.permission.READ_PHONE_STATE)) {
                // 授权结果回传
                isGet = true;
                BaiduAction.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
            }
        }*/
    }

    public void showPrivateDialog(final Activity mActivity, final HDPrivateCallback callback) {
        if (CommonUtils.getIsShowPrivate(mActivity) == 0) {
            NotiDialog dialog = new NotiDialog(mActivity);
            dialog.setAgreeListener(new NotiDialog.AgreeListener() {
                @Override
                public void buttonClick(int type) {
                    if (type == 1) {
                        try {
                            System.loadLibrary("msaoaidsec");
                            ThreadPoolManager.getInstance().addTask(new Runnable() {
                                @Override
                                public void run() {
                                    Looper.prepare();
                                    StartOtherPlugin.getCert(mActivity);
                                    Looper.loop();
                                }
                            });
                        } catch (Exception e) {
                        }
                    }
                    //type 0 拒绝 1 同意
                    callback.privateResult(type == 1);
                }
            });
            dialog.show();
        } else {
            callback.privateResult(true);
        }
    }

    /**
     * 统一为数据初始化接口
     *
     * @param mContext
     */
    public void dataCollectInit(final Context mContext) {
        HDSdkLog.d("初始化媒体接口");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            builder.detectFileUriExposure();
            StrictMode.setVmPolicy(builder.build());
        }
        if (getPropertiesValue(mContext, "isTTVersion").equals("0")) {
            Constant.isTTVersion = 1;
        }
        if (CommonUtils.getIsEnableMedia(mContext, "use_BD") && CommonUtils.getCert(mContext).equals("")) {
            StartOtherPlugin.getOaid(mContext, OaidHelper.loadPemFromAssetFile(mContext, "com.hyzjfz.hnclhy.cert.pem"));
        }
        if (CommonUtils.getIsShowPrivate(mContext) == 1) {
            System.loadLibrary("msaoaidsec");
            try {
                ThreadPoolManager.getInstance().addTask(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        StartOtherPlugin.getCert(mContext);
                        Looper.loop();
                    }
                });
            } catch (Exception e) {
            }
        }
        AppTimeWatcher.getInstance().registerWatcher((Application) mContext);
        /** 百度初始化 */
        StartOtherPlugin.initBD(mContext);
    }

    public void pageResume(Activity activity) {
        HDSdkLog.d("进入游戏界面接口");
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
        HDSdkLog.d("离开游戏界面接口");
        StartOtherPlugin.logKSActionPagePause(activity);
        StartOtherPlugin.onTTPause(activity);
    }

    public void pageDestory() {
        HDSdkLog.d("退出游戏");
        StartOtherPlugin.destoryAQY();
    }
}

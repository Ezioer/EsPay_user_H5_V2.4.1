package com.hdtx.androidsdk.callback;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;

import com.hdtx.androidsdk.Starter;
import com.hdtx.androidsdk.HDPlatform;
import com.hdtx.androidsdk.util.CommonUtils;
import com.hdtx.androidsdk.util.HDSdkLog;

public class AppTimeWatcher {
    private AppTimeWatcher() {
    }

    private static AppTimeWatcher mInstance = null;

    public static AppTimeWatcher getInstance() {
        if (mInstance == null) {
            synchronized (AppTimeWatcher.class) {
                if (mInstance == null) {
                    mInstance = new AppTimeWatcher();
                }
            }
        }
        return mInstance;
    }

    private Handler mHandler;

    private static long TIME = 5 * 60 * 1000;
    private long mCurrentTime;
    private long mHasTime = 0;
    private boolean isCancel = false;
    private boolean mBeginWork = false;
    public static boolean isLogOut = false;

    public void onEnterForeground() {
        HDSdkLog.d("app is in foreground");

        if (mHandler == null) {
            mHandler = new Handler();
        }
        isCancel = false;
        mCurrentTime = System.currentTimeMillis();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //每隔5分钟向服务器请求一次
//                ESdkLog.d("计时进行中.......");
                //成年人不统计游玩时长
                if (!isCancel && mBeginWork && CommonUtils.getIsAutoCount(Starter.mActivity).equals("0")) {
//                    ESdkLog.d("发送网络请求");
//                    StartESUserPlugin.postTime();
                }
                if (isLogOut) {
                    Starter.getInstance().showUserCenter();
                    Starter.getInstance().hideFloatView();
                }
//                StartESUserPlugin.postTime();
                mHasTime = 0;
                mCurrentTime = System.currentTimeMillis();
//                mHandler.postDelayed(this, TIME);
            }
        }, 1000);
    }

    public void onEnterBackground() {
        HDSdkLog.d("app is in background");
        mHasTime = System.currentTimeMillis() - mCurrentTime;
        isCancel = true;
//        ESdkLog.d("time:" + mHasTime);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    /**
     * 取消前后台监听并取消handle的消息发送
     */
    public void unRegisterWatcher() {
        HDSdkLog.d("unregister");
        mBeginWork = false;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    public void startTimer() {
        mBeginWork = true;
        if (mHandler == null) {
            HDSdkLog.d("startfromlogin");
            onEnterForeground();
        }
    }

    /**
     * 注册前后台监听
     */
    public void registerWatcher(Application mContext) {
        mContext.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    private Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        private int mActivityCount = 0;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            mActivityCount++;
            HDSdkLog.d("start----->activitycount=" + mActivityCount);
            if (mActivityCount == 1) {
                HDPlatform.isBackground = false;
                onEnterForeground();
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            mActivityCount--;
            HDSdkLog.d("stop----->activitycount=" + mActivityCount);
            if (mActivityCount == 0) {
                HDPlatform.isBackground = true;
                onEnterBackground();
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };
}

package com.easou.androidsdk.callback;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;

import com.easou.androidsdk.plugin.StartESUserPlugin;
import com.easou.androidsdk.util.ESdkLog;

/**
 * created by xiaoqing.zhou
 * on 2020/5/12
 * fun
 */
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

    private static long TIME = 300 * 1000;
    private long mCurrentTime;
    private long mHasTime = 0;
    private boolean isCancel = false;
    private boolean mBeginWork = false;

    public void onEnterForeground() {
        ESdkLog.d("app is in foreground");
        if (mHandler == null) {
            mHandler = new Handler();
        }
        isCancel = false;
        mCurrentTime = System.currentTimeMillis();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //每隔5分钟向服务器请求一次
                ESdkLog.d("计时进行中.......");
                if (!isCancel && mBeginWork) {
                    ESdkLog.d("发送网络请求");
                    StartESUserPlugin.postTime();
                }
                mHasTime = 0;
                mCurrentTime = System.currentTimeMillis();
                mHandler.postDelayed(this, TIME);
            }
        }, TIME - mHasTime);
    }

    public void onEnterBackground() {
        ESdkLog.d("app is in background");
        mHasTime = System.currentTimeMillis() - mCurrentTime;
        isCancel = true;
        ESdkLog.d("time:" + mHasTime);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    /**
     * 取消前后台监听并取消handle的消息发送
     */
    public void unRegisterWatcher() {
        ESdkLog.d("unregister");
        mBeginWork = false;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    public void startTimer() {
        mBeginWork = true;
        if (mHandler == null) {
            ESdkLog.d("startfromlogin");
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
            ESdkLog.d("start----->activitycount=" + mActivityCount);
            if (mActivityCount == 1) {
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
            ESdkLog.d("stop----->activitycount=" + mActivityCount);
            if (mActivityCount == 0) {
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

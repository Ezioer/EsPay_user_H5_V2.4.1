package com.zqhy.hyxm;

import android.app.Application;
import android.content.Context;

import hdtx.androidsdk.Starter;
import hdtx.androidsdk.callback.GAdsCallback;

public class EsApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Starter.getInstance().dataCollectInit(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}

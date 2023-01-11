package com.easou.espay_user_h5;

import android.app.Application;
import android.content.Context;

import com.easou.androidsdk.Starter;

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

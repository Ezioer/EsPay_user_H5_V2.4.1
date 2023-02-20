package com.hdtx.user_h5;

import android.app.Application;
import android.content.Context;

import com.hdtx.androidsdk.Starter;

public class HdApplication extends Application {
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        /** 为方便及准确的接入第三方数据统计服务，现更改为数据上报初始化接口，统一处理，
         * 之前的接口仍保留，但调用了此接口后无需再调用额外接口，不要重复调用*/
        Starter.getInstance().dataCollectInit(this);
        /** 初始化汇川广告GISM SDK */
//        Starter.getInstance().initGismSDK(this, false);

        /** 广点通SDK初始化 */
//        Starter.getInstance().initGDTAction(this);

        /** 快手SDK初始化 */
//        Starter.getInstance().initKSSDK(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        // TODO Auto-generated method stub
        super.attachBaseContext(base);

        /** 初始化SDK，获取oaid */
        Starter.getInstance().initEntry(base);
    }

}

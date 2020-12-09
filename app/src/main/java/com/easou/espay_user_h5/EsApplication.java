package com.easou.espay_user_h5;

import android.app.Application;
import android.content.Context;

import com.easou.androidsdk.Starter;

public class EsApplication extends Application {
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		/** 初始化汇川广告GISM SDK */
		Starter.getInstance().initGismSDK(this, false);

		/** 广点通SDK初始化 */
		Starter.getInstance().initGDTAction(this);

		/** 快手SDK初始化 */
		Starter.getInstance().initKSSDK(this);
	}

	@Override
	protected void attachBaseContext(Context base) {
		// TODO Auto-generated method stub
		super.attachBaseContext(base);
		
		/** 初始化SDK，获取oaid */
		Starter.getInstance().initEntry(base);
	}
	
}

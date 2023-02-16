package com.hdtx.androidsdk.androidsdk.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.hdtx.androidsdk.androidsdk.data.Constant;
import com.hdtx.androidsdk.androidsdk.http.EAPayInter;
import com.hdtx.androidsdk.androidsdk.util.Tools;

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Constant.context = this;
        try {
            EAPayInter.domain = Constant.DOMAIN + Tools.getHostName();
        } catch (Exception e) {
//			e.printStackTrace();
            Log.e("epay", "client.properties文件是否增加到assets中？");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
//		Constant.context = null;
        super.onDestroy();
    }

}

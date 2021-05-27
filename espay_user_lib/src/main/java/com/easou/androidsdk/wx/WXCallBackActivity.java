package com.easou.androidsdk.wx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.util.ESdkLog;
import com.easou.androidsdk.util.NetworkUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.SubscribeMessage;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class WXCallBackActivity extends Activity implements IWXAPIEventHandler {
    private static String TAG = "MicroMsg.WXEntryActivity";

    private IWXAPI api;
    private MyHandler handler;

    private static class MyHandler extends Handler {
        private final WeakReference<WXCallBackActivity> wxEntryActivityWeakReference;

        public MyHandler(WXCallBackActivity wxEntryActivity) {
            wxEntryActivityWeakReference = new WeakReference<WXCallBackActivity>(wxEntryActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            int tag = msg.what;
            switch (tag) {
                case NetworkUtil.GET_TOKEN: {
                    Bundle data = msg.getData();
                    JSONObject json = null;
                    try {
                        json = new JSONObject(data.getString("result"));
                        String openId, accessToken, refreshToken, scope;
                        openId = json.getString("openid");
                        ESdkLog.d("opid" + openId);
                        accessToken = json.getString("access_token");
                        refreshToken = json.getString("refresh_token");
                        scope = json.getString("scope");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, Constant.WXAPPID, false);
        handler = new MyHandler(this);
        try {
            Intent intent = getIntent();
            api.handleIntent(intent, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        ESdkLog.d("wx auth msg----->" + baseReq.toString());
    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp.getType() == ConstantsAPI.COMMAND_SUBSCRIBE_MESSAGE) {
            SubscribeMessage.Resp result = (SubscribeMessage.Resp) baseResp;
            //获取用户的openid
            String openid = result.openId;
        }
        if (baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            SendAuth.Resp authResp = (SendAuth.Resp) baseResp;
            final String code = authResp.code;
            NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/oauth2/access_token?" +
                            "appid=%s&secret=%s&code=%s&grant_type=authorization_code", Constant.WXAPPID,
                    Constant.WXAPPKEY, code), NetworkUtil.GET_TOKEN);
        }
    }
}

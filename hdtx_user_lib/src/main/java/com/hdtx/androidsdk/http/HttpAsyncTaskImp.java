package com.hdtx.androidsdk.http;

import android.content.Context;

import com.hdtx.androidsdk.data.Constant;
import com.hdtx.androidsdk.data.HDConstant;
import com.hdtx.androidsdk.data.ErrorResult;
import com.hdtx.androidsdk.data.FeeType;
import com.hdtx.androidsdk.ui.HDPayCenterActivity;
import com.hdtx.androidsdk.ui.HDToast;
import com.hdtx.androidsdk.ui.UIHelper;
import com.hdtx.androidsdk.util.DialogerUtils;
import com.hdtx.androidsdk.util.HDPayLog;
import com.hdtx.androidsdk.util.Md5SignUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class HttpAsyncTaskImp extends HttpAsyncTask<Void, Void, String[]> {
    private Map<String, String> map;
    private Context mContext;
    private String key;
    private FeeType type;
    private String token;
    public DataFinishListener dataFinishListener;

    public void setDataFinishListener(DataFinishListener dataFinishListener) {
        this.dataFinishListener = dataFinishListener;
    }

    public HttpAsyncTaskImp(Context context, Map<String, String> map, String token, String key, FeeType type) {
        // TODO Auto-generated constructor stub
        this.map = map;
        this.mContext = context;
        this.key = key;
        this.type = type;
        this.token = token;
    }

    @Override
    protected String[] doInBackground(Void... params) {
        // TODO Auto-generated method stub
        String[] result = null;
        switch (type) {
            case WECHAT:
                result = EAPayImp.chargeWinXin(getParam(Constant.WECHAT), token);
                break;
            case XSOLLA:
                result = EAPayImp.chargeXsolla(getParam("XSOLLA"), token);
                break;
            case UNIONPAY:
                result = EAPayImp.cardChargeYinLian(getParam(Constant.UNIONPAY), token);
                break;
            case ALIPAY:
                result = EAPayImp.chargeAlipay(getParam(Constant.ALIPAY), token);
                break;
            case ALIPAYTEST:
                result = EAPayImp.chargeAlipayTest();
                break;
            case WFTWECHAT:
                result = EAPayImp.chargeWFTWECHAT(getParam(Constant.WFTWECHAT), token);
                break;
            case WFTESWECHAT:
                result = EAPayImp.chargeWFTWECHAT(getParam(Constant.WFTESWECHAT), token);
                break;
            case ZWXESWECHAT:
                result = EAPayImp.chargeZWXWECHAT(getParam(Constant.ZWXESWECHAT), token);
                break;
            case YDJFDHPAY:
                result = EAPayImp.chargeYd(getParamWithMobile(), token);
                break;
        }
        return result;
    }

    @Override
    protected void onPostExecute(String[] result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        DialogerUtils.dismiss(mContext);
        JSONObject json = new JSONObject();
        if (result == null) {
            HDPayCenterActivity.onFailedCallBack(ErrorResult.ESPAY_NETWORK_ERROR, "网络异常，请稍后重试");
            return;
        }
        if (result[1] != null
                && result[1].equals(Constant.FLAG_TRADE_RESULT_SUC)) {
            HDPayLog.d("响应数据：" + result.toString());
            switch (type) {
                case ZWXESWECHAT:
                    try {
                        // 解析响应数据
                        json.put("payUrl", result[2]);
                        json.put("monitorUrl", result[3]);
                        json.put("resultUrl", result[4]);
                        json.put("prepay_id", result[5]);

                    } catch (JSONException e) {
                        HDPayLog.d("解析处理失败！" + e);
                        e.printStackTrace();
                    }
                    break;

                case WFTESWECHAT:
                case WFTWECHAT:

                    try {
                        // 解析响应数据
                        json.put("money", result[2]);
                        json.put("outTradeNo", result[3]);
                        json.put("tokenId", result[4]);

                    } catch (JSONException e) {
                        HDPayLog.d("解析处理失败！" + e);
                        e.printStackTrace();
                    }
                    break;
                case WECHAT:
                    try {
                        // 解析响应数据
                        json.put("aid", result[2]);
                        json.put("tid", result[4]);
                        json.put("bn", result[3]);

                    } catch (JSONException e) {
                        HDPayLog.d("解析处理失败！" + e);
                        e.printStackTrace();
                    }
                    break;
                case XSOLLA:
                    try {
                        // 解析响应数据
                        json.put("status",result[1]);
                        json.put("payUrl",result[2]);
                        json.put("resultUrl",result[3]);
                        json.put("monitorUrl",result[4]);
                    } catch (JSONException e) {
                        HDPayLog.d("解析处理失败！" + e);
                        e.printStackTrace();
                    }
                    break;
                case UNIONPAY:
                    try {
                        // 解析响应数据
                        json.put("Version", result[2]);
                        json.put("MerchOrderId", result[4]);
                        json.put("MerchantId", result[3]);
                        json.put("Amount", result[5]);
                        json.put("TradeTime", result[6]);
                        json.put("OrderId", result[7]);
                        json.put("Sign", result[8]);

                    } catch (JSONException e) {
                        HDPayLog.d("解析处理失败！" + e);
                        e.printStackTrace();
                    }
                    break;
                case ALIPAY:
                    try {
                        // 解析响应数据
                        json.put("info", result[0]);

                    } catch (JSONException e) {
                        HDPayLog.d("解析处理失败！" + e);
                        e.printStackTrace();
                    }
                    break;

                case ALIPAYTEST:
                    try {
                        // 解析响应数据
                        json.put("info", result[0]);

                    } catch (JSONException e) {
                        HDPayLog.d("解析处理失败！" + e);
                        e.printStackTrace();
                    }
                    break;
                case YDJFDHPAY:
                    try {
                        json.put("port", result[0]);
                        json.put("sms", result[2]);
                    } catch (JSONException e) {
                        HDPayLog.d("解析处理失败！" + e);
                        e.printStackTrace();
                    }
                    break;
            }
            dataFinishListener.setJson(json);
        } else {
            UIHelper.isClicked = false;
            if (result[0] != null && !result[0].equals("")) {
                HDPayCenterActivity.onFailedCallBack(ErrorResult.ESPAY_NETWORK_ERROR, result[0]);
                HDToast.getInstance().ToastShow(mContext, result[0]);
            }
        }
    }

    private String getParam(String payChannel) {
        String module = Constant.MODULE;
        if (payChannel.equals("XSOLLA")) {
            module = "WEB";
        }
        String sign = Md5SignUtils.sign(map, key);
        String param = "clientIp=" + map.get(Constant.CLIENT_IP)
                + "&deviceId=" + map.get(Constant.DEVICE_ID)
                + "&money=" + map.get(HDConstant.MONEY)
                + "&appId=" + map.get(Constant.APP_ID)
                + "&tradeId=" + map.get(HDConstant.TRADE_ID)
                + "&qn=" + map.get(Constant.QN)
                + "&sign=" + sign
                + "&notifyUrl=" + map.get(HDConstant.NOTIFY_URL)
                + "&redirectUrl=" + map.get(HDConstant.REDIRECT_URL)
                + "&partnerId=" + map.get(Constant.PARTENER_ID)
                + "&tradeMode=" + module
                + "&payChannel=" + map.get(Constant.PAYCHANNEL)
                + "&phoneOs=" + Constant.SDK_PHONEOS
                + "&esVersion=" + Constant.SDK_VERSION;
        return param;
    }

    private String getParamWithMobile() {

        String sign = Md5SignUtils.sign(map, key);
        String param = "clientIp=" + map.get(Constant.CLIENT_IP)
                + "&deviceId=" + map.get(Constant.DEVICE_ID)
                + "&money=" + map.get(HDConstant.MONEY)
                + "&appId=" + map.get(Constant.APP_ID)
                + "&tradeId=" + map.get(HDConstant.TRADE_ID)
                + "&qn=" + map.get(Constant.QN)
                + "&sign=" + sign
                + "&notifyUrl=" + map.get(HDConstant.NOTIFY_URL)
                + "&redirectUrl=" + map.get(HDConstant.REDIRECT_URL)
                + "&partnerId=" + map.get(Constant.PARTENER_ID)
                + "&tradeMode=" + Constant.MODULE
                + "&payChannel=" + map.get(Constant.PAYCHANNEL)
                + "&phoneOs=" + Constant.SDK_PHONEOS
                + "&esVersion=" + Constant.SDK_VERSION
                + "&cardNumber=" + map.get(Constant.CARD_NUM);
        return param;
    }

    public static interface DataFinishListener {
        void setJson(Object object);
    }

}

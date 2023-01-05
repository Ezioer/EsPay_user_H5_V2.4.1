package com.easou.androidsdk.http;

import android.content.Context;

import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.data.ESConstant;
import com.easou.androidsdk.data.FeeType;
import com.easou.androidsdk.ui.ESToast;
import com.easou.androidsdk.util.DialogerUtils;
import com.easou.androidsdk.util.ESPayLog;
import com.easou.androidsdk.util.Md5SignUtils;
import org.json.JSONObject;

import java.util.Map;

public class HttpAsyncTaskImp extends HttpAsyncTask<Void, Void, String[]> {
    private Map<String, String> map;
    private Context mContext;
    private String key;
    public DataFinishListener dataFinishListener;

    public void setDataFinishListener(DataFinishListener dataFinishListener) {
        this.dataFinishListener = dataFinishListener;
    }

    public HttpAsyncTaskImp(Context context, Map<String, String> map, String key) {
        // TODO Auto-generated constructor stub
        this.map = map;
        this.mContext = context;
        this.key = key;
    }

    @Override
    protected String[] doInBackground(Void... params) {
        // TODO Auto-generated method stub
        String[] result = null;
        return result;
    }

    @Override
    protected void onPostExecute(String[] result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        DialogerUtils.dismiss(mContext);
        if (result == null) {
            dataFinishListener.setJson("");
            return;
        }
        if (result[1] != null
                && result[1].equals(Constant.FLAG_TRADE_RESULT_SUC)) {
            ESPayLog.d("宜搜下单响应数据：" + result.toString());
            dataFinishListener.setJson(result[2]);
        } else {
            if (result[0] != null && !result[0].equals("")) {
                dataFinishListener.setJson("");
                ESPayLog.d("宜搜下单失败：" + result[0]);
                ESToast.getInstance().ToastShow(mContext, result[0]);
            }
        }
    }

    private String getParam() {
        String sign = Md5SignUtils.sign(map, key);
        String param = "clientIp=" + map.get(Constant.CLIENT_IP)
                + "&deviceId=" + map.get(Constant.DEVICE_ID)
                + "&money=" + map.get(ESConstant.MONEY)
                + "&appId=" + map.get(Constant.APP_ID)
                + "&tradeId=" + map.get(ESConstant.TRADE_ID)
                + "&qn=" + map.get(Constant.QN)
                + "&sign=" + sign
                + "&notifyUrl=" + map.get(ESConstant.NOTIFY_URL)
                + "&redirectUrl=" + map.get(ESConstant.REDIRECT_URL)
                + "&partnerId=" + map.get(Constant.PARTENER_ID)
                + "&tradeMode=" + Constant.MODULE
                + "&payChannel=" + map.get(Constant.PAYCHANNEL);
        ESPayLog.d("宜搜下单参数：" + param);
        return param;
    }

    public interface DataFinishListener {
        void setJson(String invoice);
    }

}

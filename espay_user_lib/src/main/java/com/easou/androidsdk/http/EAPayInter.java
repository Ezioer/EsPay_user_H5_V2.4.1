package com.easou.androidsdk.http;

import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.util.AESUtil;
import com.easou.androidsdk.util.ESdkLog;
import com.easou.androidsdk.util.RSAUtil;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

public class EAPayInter {

    //海外支付验证交易
    public static BaseResponse verGooglePlayOrder(String token, String orderNo, String appId, float totalAmount, int num) {
        try {
            JSONObject object = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("token", token);
            data.put("orderNo", orderNo);
            data.put("accountId", Constant.ESDK_USERID);
            data.put("appId", appId);
            data.put("totalAmount", String.valueOf(totalAmount));
            data.put("buyNums", num);
            return handleNetOpera(data, object, appId, Constant.GOOGLEVER);
        } catch (Exception e) {
            return null;
        }
    }

    //同步订单状态
    public static BaseResponse verSyncOrderStatus(String orderNo, int orderStatus, int ackStatus, int consumptionStatus, String appId) {
        try {
            JSONObject object = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("orderNo", orderNo);
            data.put("orderStatus", orderStatus);
            data.put("ackStatus", ackStatus);
            data.put("consumptionStatus", consumptionStatus);
            return handleNetOpera(data, object, appId, Constant.CONSUMPTION);
        } catch (Exception e) {
            return null;
        }
    }

    //海外支付下单
    public static BaseResponse checkOrder(String cpOrderNo, String productId, String productPrice,
                                          Long productPriceMicros, String currencyCode, Map<String, String> map) {
        try {
            JSONObject object = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("appId", map.get("appId"));
            data.put("qn", map.get("qn"));
            data.put("cpOrderNo", cpOrderNo);
            data.put("accountId", Constant.ESDK_USERID);
            data.put("cpNotifyUrl", map.get("notifyUrl"));
            data.put("productId", productId);
            data.put("productPrice", productPrice);
            data.put("productPriceMicros", productPriceMicros);
            data.put("currencyCode", currencyCode);
            data.put("payType", 1);
            return handleNetOpera(data, object, map.get("appId"), Constant.CHECKORDER);
        } catch (Exception e) {
            return null;
        }
    }

    private static BaseResponse handleNetOpera(JSONObject data, JSONObject object, String appId, String url) {
        try {
            //AES加密数据
            if (Constant.AESKEY.isEmpty()) {
                String aesKey = AESUtil.getRandomString();
                Constant.AESKEY = aesKey;
            }
            String content = AESUtil.encrypt(data.toString(), Constant.AESKEY);
            //RSA加密AES的密钥
            String key = RSAUtil.encrypt(Constant.AESKEY, RSAUtil.getPublicKey(RSAUtil.publicKey));
            object.put("content", content);
            object.put("key", key);
            object.put("appId", appId);
            BaseResponse result = getBaseResponse(url, object);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getOaidPerFromNet(String applicationId) {
        try {
            ESdkLog.c("certnet----->", applicationId);
            JSONObject object = new JSONObject();
            object.put("name", applicationId);
            BaseResponse result = getBaseResponse(Constant.GETOAIDCERT, object);
            if (result.getCode() == 1 && result.getData() != null) {
                JSONObject custom = new JSONObject(result.getData().toString());
                String cert = custom.getString("cert");
                ESdkLog.c("certnet----->", cert);
                if (cert != null && !cert.isEmpty()) {
                    ESdkLog.d("成功获取证书内容");
                    return cert;
                } else {
                    return "";
                }
            } else {
                return "";
            }
        } catch (Exception e) {
            ESdkLog.c("httperror", e.getMessage() + e.toString());
            return "";
        }
    }

    private static BaseResponse getBaseResponse(String url, JSONObject map) {
        String result = EucHttpClient.httpPost(url, map);
        if (result == null || "".equals(result)) {
            return null;
        }
        BaseResponse bean = new BaseResponse();
        try {
            JSONObject object = new JSONObject(result);
            int code = object.optInt("code");
            String info = object.optString("msg");
            if (object.opt("data") != null) {
                bean.setData(object.opt("data").toString());
            }
            bean.setMsg(info);
            bean.setCode(code);
        } catch (JSONException e) {
            return null;
        }
        return bean;
    }

    public static String getResponse(String url, JSONObject map) {
        String result = EucHttpClient.httpPost(url, map);
        if (result == null || "".equals(result)) {
            return null;
        }
        return result;
    }
}

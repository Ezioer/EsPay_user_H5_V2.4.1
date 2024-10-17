package hdtx.androidsdk.http;

import android.util.Log;

import hdtx.androidsdk.Starter;
import hdtx.androidsdk.data.Constant;
import hdtx.androidsdk.data.FBInfo;
import hdtx.androidsdk.util.AESUtil;
import hdtx.androidsdk.util.CommonUtils;
import hdtx.androidsdk.util.ESdkLog;
import hdtx.androidsdk.util.Md5SignUtils;
import hdtx.androidsdk.util.RSAUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Map;

public class EAPayInter {

    //海外支付验证交易
    public static BaseResponse verGooglePlayOrder(String token, String orderNo, String appId, Double totalAmount, int num) {
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
                                          Long productPriceMicros, String currencyCode, Map<String, String> map, JSONObject info) {
        try {
            JSONObject object = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("appId", map.get("appId"));
            data.put("qn", map.get("qn"));
            data.put("cpOrderNo", cpOrderNo);
            data.put("accountId", Constant.ESDK_USERID);
            String notify = info.optString("notifyUrl");
            String notifyUrl = map.get("notifyUrl");
            if (!notifyUrl.equals("")) {
                notify = notifyUrl;
            }
            data.put("cpNotifyUrl", notify);
            data.put("productId", productId);
            data.put("productPrice", productPrice);
            data.put("productPriceMicros", productPriceMicros);
            data.put("currencyCode", currencyCode);
            data.put("deviceId", Constant.IMEI);
            data.put("area", "");
            data.put("ip", Constant.NET_IP);
            //线上
            data.put("payType", 1);
            //测试
//            data.put("payType", 4);
            data.put("redirectUrl", map.get("redirectUrl"));
            try {
                //语言 简繁体中文都是zh 英文是es
                String language = Locale.getDefault().getLanguage().toLowerCase();
                //国家 简体是cn 繁体是tw或hk，英文是us
                String code = Locale.getDefault().getCountry().toLowerCase();
                if (code.equals("tw") || code.equals("hk")) {
                    language = "tw";
                } else if (code.equals("cn")) {
                    language = "cn";
                } else if (language.equals("es")) {
                    language = "en";
                }
                data.put("language", language);
            } catch (Exception e) {
                data.put("language", "tw");
            }
            //新加的字段
            data.put("tradleId", cpOrderNo);
            data.put("serverId", info.optString("serverId"));
            data.put("serverName", info.optString("serverName"));
            data.put("playerId", info.optString("playerId"));
            data.put("playerName", info.optString("playerName"));
            data.put("playerLevel", info.optString("playerLevel"));
            data.put("money", info.optInt("money"));
            data.put("productName", info.optString("productName"));
            return handleNetOpera(data, object, map.get("appId"), Constant.CHECKORDER);
        } catch (Exception e) {
            return null;
        }
    }

    public static String testPay(String appId, double money, String payChannel, String qn, String tradeMode, String key, String token) {
        try {
            String pay = String.valueOf(money);
            String params = "appId=" + appId + "&money=" + pay + "&payChannel=" + payChannel + "&qn=" + qn + "&tradeMode=" + tradeMode;
            String sign = Md5SignUtils.sign(params, key);
            String url = Constant.TESTPAY + "appId=" + appId + "&money=" + pay + "&payChannel=" + payChannel + "&qn=" + qn + "&tradeMode=" + tradeMode + "&sign=" + sign;
            String result = EsPayNetGetPost.sendGet(url, null, token);
            if (result != null) {
                JSONObject re = new JSONObject(result);
                if (re.optString("status").equals("success")) {
                    return re.optJSONObject("data").optString("payURL");
                } else {
                    return "";
                }
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    private static BaseResponse handleNetOpera(JSONObject data, JSONObject object, String appId, String url) {
        try {
            //AES加密数据
            if (Constant.AESKEY.isEmpty()) {
                String aesKey = AESUtil.getRandomString(CommonUtils.getBase(Starter.mActivity));
                Constant.AESKEY = aesKey;
            }
            String content = AESUtil.encrypt(data.toString(), Constant.AESKEY);
            //RSA加密AES的密钥
            String key = RSAUtil.encrypt(Constant.AESKEY, RSAUtil.getPublicKey(CommonUtils.readPropertiesValue(Starter.mActivity, "publickey")));
            object.put("content", content);
            object.put("key", key);
            object.put("appId", appId);
            Log.d("EAPayCheckOut", object.toString());
          /*  String language = Locale.getDefault().getLanguage().toLowerCase();
            if (language.equals("vi")) {
                url = Constant.BASEURL_VN + url;
            } else if (language.equals("en")) {
                url = Constant.BASEURL_EN + url;
            } else {
                url = Constant.BASEURL_CN + url;
            }*/
            url = Constant.BASEURL_CN + url;
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
        Log.d("EAPayInter", result);
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

    public static FBInfo getFBInfo(String partnerId,String appId,String qn,String key) {
        try {
            JSONObject jBean = new JSONObject();
            JSONObject jHead = new JSONObject();
            JSONObject jBody = new JSONObject();
            jHead.put("flowCode",String.valueOf(System.currentTimeMillis()));
            jHead.put("partnerId",partnerId);
            jHead.put("appId",appId);
            jHead.put("qn",qn);
            jHead.put("source","33");
            jBody.put("appId",appId);
            String sign = Md5SignUtils.sign("appId="+appId,key);
            jHead.put("sign",sign);
            jBean.put("head",jHead);
            jBean.put("body",jBody);
            return getFBResponse(Constant.FBCHANGE,jBean);
        } catch (Exception e) {
            return null;
        }
    }
    private static FBInfo getFBResponse(String url, JSONObject map) {
        String result = EucHttpClient.httpPost(url, map);
        Log.d("EAPayInterfb", result);
        if (result == null || "".equals(result)) {
            return null;
        }
        try {
            JSONObject object = new JSONObject(result);
            JSONObject head = object.optJSONObject("head");
            if (head.optString("ret").equals("0")){
                JSONObject body = object.getJSONObject("body");
                String token = body.optString("fbToken");
                String appId = body.optString("fbAppId");
                String fbName = body.optString("fbName");
                return new FBInfo(appId,fbName,token);
            } else {
                return null;
            }
        } catch (JSONException e) {
            return null;
        }
    }

}

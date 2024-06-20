package com.easou.androidsdk.http;

import com.easou.androidsdk.Starter;
import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.data.PayItem;
import com.easou.androidsdk.util.AESUtil;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.util.ESPayLog;
import com.easou.androidsdk.util.ESdkLog;
import com.easou.androidsdk.util.GsonUtil;
import com.easou.androidsdk.util.Tools;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

public class EAPayInter {

    public static String domain = Constant.DOMAIN + Tools.getHostName();
    private static final String TAG = "EAPayInter";

    public static String queryUserEB(String token, String appId) {

        String url = "/basePay/getUserBalance.e";
        String param = Constant.EASOUTGC + "=" + token + "&appId=" + appId;
        String result = HttpGroupUtils.sendGet(domain + url, param, token);
        String[] result_arr = new String[13];
        try {

            JSONObject jsonObject = new JSONObject(result);
            result_arr[0] = jsonObject.getString("msg");
            result_arr[1] = jsonObject.getString("status");
            result_arr[2] = jsonObject.getString("userEbBalance"); //e币余额

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result_arr[2];
    }


    public static String[] chargeAlipay(String token, String cost, String invoiceId) {
        String url = "/ecenter/ali2!aliCharge.e";
        String params = "money=" + cost + "&ty=android" + "&invoiceId=" + invoiceId;
        String result = EsPayNetGetPost.sendGet(domain + url, params, token);
        String[] result_list = new String[2];
        try {
            JSONObject jsonObject = new JSONObject(result);
            String status = jsonObject.getString("status");
            if (status.equals(Constant.FLAG_TRADE_RESULT_SUC)) {
                JSONObject jsonData = jsonObject.getJSONObject("data");
                result_list[0] = jsonData.getString("url");
                result_list[1] = jsonData.getString("invoice");
            } else {
                result_list = new String[]{"", ""};
            }
        } catch (Exception e) {
        }

        return result_list;
    }


    public static String[] cardCharge(String token, int channel, String card_num, String card_pass, String value, String invoiceId) {
        String url = "/ecenter/card!cardCharge.e";
//		String params = "channelType=12&cardAmt=0.01&cardNumber=6017299923&cardPwd=141965193105388";
        String params = "channelType=" + channel + "&cardAmt=" + value + "&cardNumber=" + card_num + "&cardPwd=" + card_pass + "&invoiceId=" + invoiceId;
        String result = EsPayNetGetPost.sendGet(domain + url, params, token);
        String[] result_arr = new String[2];
        try {
            JSONObject jsonObject = new JSONObject(result);
            String status = jsonObject.getString("status");
            result_arr[0] = status;
            if (status.equals(Constant.FLAG_TRADE_RESULT_SUC)) {
                result = jsonObject.getString("invoice");
                result_arr[1] = result;
            } else {
                result_arr[1] = jsonObject.getString("msg");
            }
        } catch (Exception e) {
        }
        return result_arr;
    }

    /**
     * 银联的充值。
     *
     * @param token
     * @param invoiceId
     * @return
     */
    public static String[] cardChargeYinLian(String token, String count_money, String invoiceId) {
        String url = "/ecenter/uni2!uniCharge2.e";
        //	URL url = new URL(Constant.URL_YINLIAN+"?money="+count_money +"&invoiceId="+invoiceId +
        String params = "money=" + count_money + "&invoiceId=" + invoiceId;
        String result = EsPayNetGetPost.sendGet(domain + url, params, token);
        String[] result_arr = new String[13];
        try {

            JSONObject jsonObject = new JSONObject(result);
            JSONObject data = jsonObject.getJSONObject("data");
            result_arr[0] = jsonObject.getString("msg");
            result_arr[1] = jsonObject.getString("status");

            result_arr[2] = data.getString("Version"); //通讯协议版本号
            result_arr[3] = data.getString("MerchantId"); // 商户代码
            result_arr[4] = data.getString("MerchOrderId"); // 商户订单号
            result_arr[5] = data.getString("Amount"); // 商户订单金额

            result_arr[6] = data.getString("TradeTime");//商户订单时间

            result_arr[7] = data.getString("OrderId"); // 易联订单号
            result_arr[8] = data.getString("Sign"); // 签名

            ESPayLog.d(TAG, "银联解析完毕。");
        } catch (Exception e) {
//			e.printStackTrace();
        }
        return result_arr;


    }

    /**
     * 银联的充值。
     *
     * @param token
     * @param invoiceId
     * @return
     */
    public static String[] cardChargeWeiXin(String token, String count_money, String invoiceId) {
        String url = "/ecenter/wechat!weixinCharge.e";
        //	URL url = new URL(Constant.URL_YINLIAN+"?money="+count_money +"&invoiceId="+invoiceId +
        String params = "money=" + count_money + "&invoiceId=" + invoiceId;
        ESPayLog.d(TAG, "url:" + domain + url);
        ESPayLog.d(TAG, "params:" + params);
        ESPayLog.d(TAG, "token:" + token);
        String result = EsPayNetGetPost.sendGet(domain + url, params, token);
        String[] result_arr = new String[13];
        try {

            JSONObject jsonObject = new JSONObject(result);
            JSONObject data = jsonObject.getJSONObject("data");
            result_arr[0] = jsonObject.getString("msg");
            result_arr[1] = jsonObject.getString("status");

            result_arr[2] = data.getString("aid"); //通讯协议版本号
            result_arr[3] = data.getString("bn"); // 商户代码
            result_arr[4] = data.getString("tid"); // 商户订单号

            ESPayLog.d(TAG, "银联解析完毕。");
        } catch (Exception e) {
//			e.printStackTrace();
        }
        return result_arr;


    }


    public static String[] tradeResult(String token, String invoice) {
        String url = "/ecenter/tradeResult.e";
//		String params = "ti=" + System.currentTimeMillis() + "&invoice=be23cc1df01f441e8ac7dbbf3bf986ad";
        String params = "ti=" + System.currentTimeMillis() + "&invoice=" + invoice;
        String[] result_arr = new String[2];
        String result = EsPayNetGetPost.sendGet(domain + url, params, token);
        try {
            JSONObject jsonObject = new JSONObject(result);
            String status = jsonObject.getString("status");
            String msg = jsonObject.getString("msg");
            if (status.equals("0")) {
                result_arr[0] = Constant.FLAG_TRADE_RESULT_COMMIT;
            } else {
                if (status.equals("1")) {
                    result_arr[0] = Constant.FLAG_TRADE_RESULT_SUC;
                } else {
                    result_arr[0] = Constant.FLAG_TRADE_RESULT_FAIL;
                    result_arr[1] = msg;
                }
            }
        } catch (Exception e) {
        }
        return result_arr;
    }


    public static LinkedList<PayItem> tradeHistory(String token, String appId, int page) {
//		String url = "/ecenter/tradeHistory.e";
        String url = "/basePay/userTradeHistory.e";
        String params = "size=10&page=" + String.valueOf(page) + "&appId=" + appId;
//		String result = EsPayNetGetPost.sendGet(domain + url,params,"TGT-47810-ubAKACcEV5Qm5hsOOxCTSGTYZjnQEHSGewf2Kpbe9EPqCoEA04-sso");
        String result = EsPayNetGetPost.sendGet(domain + url, params, token);

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject data = jsonObject.getJSONObject("data");
            boolean hasNext = data.getBoolean("hasNext");

            if (data != null) {
                JSONArray jArray = data.getJSONArray("list");
                LinkedList<PayItem> list = new LinkedList<PayItem>();
                Gson gson = new Gson();
                PayItem[] users = gson.fromJson(jArray.toString(), PayItem[].class);
                if (hasNext == false) {
                    users[0].setHasNext(false);
                }
                for (int i = 0; i < users.length; i++) {
                    list.add(users[i]);
                }
                return list;
            }

        } catch (Exception e) {
        }
        return null;
    }

    public static int isUploadPay(String userId, String appId) {
        try {
            String url = "https://listener.eayou.com/sa/todayUser.do?accountid=" + userId + "&appid=" + appId + "&t=" + System.currentTimeMillis();
            String result = EsPayNetGetPost.sendGet(url, null, "");
            //数据为null，有可能是请求出错
            if (result == null) {
                return 1;
            }

            //不上传头条付费日志
            if (result.equals("0")) {
                return 0;
            }
            //上传头条付费日志
            return 1;
        } catch (Exception e) {
            ESdkLog.d(e.toString());
            return 1;
        }
    }

    public static int getOnlyDeviceId() {
        try {
            String url = "https://egamec.eayou.com/deviceInfo/getCustomDeviceId";
            JSONObject map = Tools.getOnlyId();
            JSONObject object = new JSONObject();
            object.put("deviceInfo", map);
            object.put("isCustom", 1);
            BaseResponse result = getBaseResponse(url, object);
            if (result.getCode() == 1 && result.getData() != null) {
//                DevicesInfo info = GsonUtil.fromJson(result.getData().toString(), DevicesInfo.class);
                JSONObject custom = new JSONObject(result.getData().toString());
                Constant.CUSTOMDEVICES = custom.getString("customDeviceId");
                if (Constant.CUSTOMDEVICES.isEmpty()) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }
    }

    public static String getOaidPerFromNet(String applicationId) {
        try {
            ESdkLog.c("certnet----->", applicationId);
            String url = "https://egamec.eayou.com/cert/getCertPem";
            JSONObject object = new JSONObject();
            object.put("name", applicationId);
            BaseResponse result = getBaseResponse(url, object);
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
        ESdkLog.d("payresult---->"+result);
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
            if (code != 1) {
                return null;
            }
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
            ESdkLog.c("EAPayCheckOut", object.toString());
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

}

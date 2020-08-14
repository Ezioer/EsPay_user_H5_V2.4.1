package com.easou.androidsdk.http;

import java.util.Map;
import org.json.JSONObject;

import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.http.HttpGroupUtils;
import com.easou.androidsdk.ui.LoadingDialog;
import com.easou.androidsdk.util.ESPayLog;
import com.easou.androidsdk.util.ESdkLog;
import com.easou.androidsdk.util.Tools;

public class EAPayImp {
	
	public static final String domain = Constant.DOMAIN + Tools.getHostName() + Constant.SERVER_URL;
	private static final String TAG = "EAPayImp";
	
	/**
	 * 微信支付
	 * @param param
	 * @return
	 */
	public static String[] chargeWinXin(String param, String token) {
		//TODO
		String result = HttpGroupUtils.sendGet(domain, param, token);
		String[] result_arr = new String[13];
		try {

			JSONObject jsonObject = new JSONObject(result);
			String status = jsonObject.getString("status");
			
			if (status.equals(Constant.FLAG_TRADE_RESULT_SUC)) {
				
				JSONObject data = jsonObject.getJSONObject("data");
				
				result_arr[0] = jsonObject.getString("msg");
				result_arr[1] = status;
				result_arr[2] = data.getString("aid"); //通讯协议版本号
				result_arr[3] = data.getString("bn"); // 商户代码
				result_arr[4] = data.getString("tid"); // 商户订单号
				ESPayLog.d("EAPayInter" , result_arr[0]+"/n" + result_arr[1]+"/n" + result_arr[2]+"/n"
			+ result_arr[3] +"/n"+ result_arr[4]);
				ESPayLog.d(TAG , "微信解析完毕。");
			} else {
				result_arr[0] = jsonObject.getString("msg");
				result_arr[1] = status;
			}
		} catch (Exception e) {
			e.printStackTrace();
			ESPayLog.d(TAG , e.toString());
		}
		return result_arr;
	}
	/**
	 * 微信支付
	 * @param param
	 * @return
	 */
	public static String[] chargeWFTWECHAT(String param, String token) {
		//TODO
		String result = HttpGroupUtils.sendGet(domain, param, token);
		String[] result_arr = new String[13];
		try {

			JSONObject jsonObject = new JSONObject(result);
			String status = jsonObject.getString("status");
			
			if (status.equals(Constant.FLAG_TRADE_RESULT_SUC)) {
				JSONObject data = jsonObject.getJSONObject("data");
				result_arr[0] = jsonObject.getString("msg");
				result_arr[1] = status;
				result_arr[2] = data.getString("money"); //通讯协议版本号
				result_arr[3] = data.getString("outTradeNo"); // 商户代码
				result_arr[4] = data.getString("tokenId"); // 商户订单号
				result_arr[5] = data.getString("code"); // 商户订单号
				ESPayLog.d("EAPayInter" , result_arr[0]+"/n" + result_arr[1]+"/n" + result_arr[2]+"/n"
						+ result_arr[3] +"/n"+ result_arr[4]+"/n"+ result_arr[5]);
				ESPayLog.d(TAG , "微信解析完毕。");
			} else {
				result_arr[0] = jsonObject.getString("msg");
				result_arr[1] = status;
			}
		} catch (Exception e) {
			e.printStackTrace();
			ESPayLog.d(TAG , e.toString());
		}
		return result_arr;
	}
	
	/**
	 * 微信支付
	 * @param param
	 * @return
	 */
	public static String[] chargeZWXWECHAT(String param, String token) {
		//TODO
		String result = HttpGroupUtils.sendGet(domain, param, token);
		String[] result_arr = new String[13];
		try {

			JSONObject jsonObject = new JSONObject(result);
			String status = jsonObject.getString("status");
			
			if (status.equals(Constant.FLAG_TRADE_RESULT_SUC)) {
				JSONObject data = jsonObject.getJSONObject("data");
				result_arr[0] = jsonObject.getString("msg");
				result_arr[1] = status;
				result_arr[2] = data.getString("payUrl"); 
				result_arr[3] = data.getString("monitorUrl"); 
				result_arr[4] = data.getString("resultUrl"); 
				result_arr[5] = data.getString("prepay_id"); 
				ESPayLog.d(TAG , "微信解析完毕。");
			} else {
				result_arr[0] = jsonObject.getString("msg");
				result_arr[1] = status;
			}
		} catch (Exception e) {
			e.printStackTrace();
			ESPayLog.d(TAG , e.toString());
		}
		return result_arr;
	}
	
	/**
	 * 银联的充值。
	 * 
	 * @param token
	 * @param value
	 * @param invoiceId
	 * @return
	 */
	public static String[] cardChargeYinLian(String param, String token) {
		String result = HttpGroupUtils.sendGet(domain, param, token);
		String[] result_arr = new String[13];
		try {

			JSONObject jsonObject = new JSONObject(result);
			String status = jsonObject.getString("status");
			
			if (status.equals(Constant.FLAG_TRADE_RESULT_SUC)) {
				
				JSONObject data = jsonObject.getJSONObject("data");
				result_arr[0] = jsonObject.getString("msg");
				result_arr[1] = status;
				result_arr[2] = data.getString("Version"); //通讯协议版本号
				result_arr[3] = data.getString("MerchantId"); // 商户代码
				result_arr[4] = data.getString("MerchOrderId"); // 商户订单号
				result_arr[5] = data.getString("Amount"); // 商户订单金额
				result_arr[6] = data.getString("TradeTime");//商户订单时间
				result_arr[7] = data.getString("OrderId"); // 易联订单号
				result_arr[8] = data.getString("Sign"); // 签名
				ESPayLog.d(TAG , result_arr[0]+"/n" + result_arr[1]+"/n" + result_arr[2]+"/n"
						+ result_arr[3] +"/n"+ result_arr[4] +"/n"+ result_arr[5]+"/n"
						+ result_arr[6]+"/n" + result_arr[7]+"/n" + result_arr[8]);
				ESPayLog.d(TAG , "银联解析完毕。");
			} else {
				result_arr[0] = jsonObject.getString("msg");
				result_arr[1] = status;
			}
		} catch (Exception e) {
			e.printStackTrace();
			ESPayLog.d(TAG ,e.toString());
		}
		return result_arr;
	}
	/**
	 * 卡类充值请求
	 * @param token 废弃，有用户中心需要使用，传null
	 * @param channel 卡类型
	 * @param card_num 卡号
	 * @param card_pass 卡密
	 * @param value 交易金额
	 * @param invoiceId 
	 * @return
	 */
	public static String[] cardCharge(String params, String token) {
		ESPayLog.d(TAG , "卡类请求参数是：" + params);
		String result = HttpGroupUtils.sendGet(domain, params, token);
		ESPayLog.d(TAG , "请求的数据是：" + result);
		String[] result_arr = new String[2];
		try {
			JSONObject jsonObject = new JSONObject(result);
			String status = jsonObject.getString("status");
			result_arr[1] = status;
			if (status.equals(Constant.FLAG_TRADE_RESULT_SUC)) {
				result = jsonObject.getString("invoice");
				result_arr[0] = result;
			} else {
				result_arr[0] = jsonObject.getString("msg");
			}
		} catch (Exception e) {
			ESPayLog.d(TAG ,e.toString());
		}
		return result_arr;
	}
	/**
	 * 处理支付宝计费
	 * 
	 * @param params
	 * @return
	 */
	public static String[] chargeAlipay(String params, String token) {
		String result = HttpGroupUtils.sendGet(domain, params, token);
		ESPayLog.d(TAG , "aresult:"+result);
		String[] result_list = new String[2];
		try {
			JSONObject jsonObject = new JSONObject(result);
			String status = jsonObject.getString("status");
			if (status.equals(Constant.FLAG_TRADE_RESULT_SUC)) {
				JSONObject jsonData = jsonObject.getJSONObject("data");
				result_list[0] = jsonData.getString("url");
				result_list[1] = status;
			} else {
				result_list[0] = jsonObject.getString("msg");
				result_list[1] = status;
			}
		} catch (Exception e) {
			ESPayLog.d(e.toString());
		}

		return result_list;
	}
	
	/** 查询游戏卡支付结果 */
	public static String[] tradeResult(String token,String invoice) {
		String url = "/ecenter/tradeResult.e";
		String params = "ti=" + System.currentTimeMillis() + "&invoice="+invoice;
		String[] result_arr = new String[2];
		String result = EsPayNetGetPost.sendGet(domain + url,params,token);
		try{
			JSONObject jsonObject = new JSONObject(result);
			String status = jsonObject.getString("status");
			String msg =  jsonObject.getString("msg");
			if(status.equals("0")){
				result_arr[0] = Constant.FLAG_TRADE_RESULT_COMMIT;
			}else{
				if(status.equals("1")){
					result_arr[0] = Constant.FLAG_TRADE_RESULT_SUC;
				}else{
					result_arr[0] = Constant.FLAG_TRADE_RESULT_FAIL;
					result_arr[1] = msg;
				}
			}
			
		}catch(Exception e){
		}
		return result_arr;
	}
	
	
	/**
	 * 请求支付类型
	 * @param token 废弃，有用户中心需要使用，传null
	 * @param appId 
	 * @return
	 */
	public static String getWechatPayment(Map<String, String> map, String token) {
		String params = "clientIp=" + map.get(Constant.CLIENT_IP) 
				+ "&deviceId=" + map.get(Constant.DEVICE_ID) + "&appId=" + map.get(Constant.APP_ID) 
				+ "&qn=" + map.get(Constant.QN) + "&partnerId=" + map.get(Constant.PARTENER_ID) 
				+ "&includeChannels=" + Constant.INCLUDECHANNELS_ALL;
//		String params = "appId=" + map.get(Constant.APP_ID);
		
		String result = HttpGroupUtils.sendGet(Constant.DOMAIN + Tools.getHostName() + Constant.CHANNELCONFIG_URL,
				params, token);
		ESPayLog.d(TAG , "请求的数据是：" + result);
		String[] result_arr = new String[3];
		try{
			JSONObject jsonObject = new JSONObject(result);
			JSONObject data = jsonObject.getJSONObject("extData");
			result_arr[0] = jsonObject.getString("msg");
			result_arr[1] = jsonObject.getString("status");

			if (result_arr[1].equals(Constant.FLAG_TRADE_RESULT_SUC)) {
				result_arr[2] = data.getString("WECHAT");
			} else {
				result_arr[2] = null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			ESPayLog.d(TAG ,e.toString());
			return null;
		}
		LoadingDialog.dismiss();
		return result_arr[2];
	}


	/**
	 * 请求用户当月消费金额总数
	 */
	public static String getMonthTotolPay() {
		String params = "easouId=" + Constant.ESDK_USERID;

		String result = HttpGroupUtils.sendGet(Constant.DOMAIN + Tools.getHostName() + Constant.MONTH_TOTOL_PAY_URL,
				params, null);
		ESPayLog.d(TAG , "请求的数据是：" + result);
		String[] result_arr = new String[3];
		try{
			JSONObject jsonObject = new JSONObject(result);
			JSONObject data = jsonObject.getJSONObject("extData");
			result_arr[0] = jsonObject.getString("msg");
			result_arr[1] = jsonObject.getString("status");

			if (result_arr[1].equals(Constant.FLAG_TRADE_RESULT_SUC)) {
				result_arr[2] = data.getString("amount");
			} else {
				result_arr[2] = "0";
			}
			ESdkLog.d(jsonObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
			ESPayLog.d(TAG ,e.toString());
			return "0";
		}
		LoadingDialog.dismiss();
		return result_arr[2];
	}

	/**
	 * 处理移动积分支付
	 *
	 * @param params
	 * @return
	 */
	public static String[] chargeYd(String params, String token) {
		String result = HttpGroupUtils.sendGet(domain, params, token);
		ESPayLog.d(TAG , "aresult:"+result);
		String[] result_list = new String[3];
		try {
			JSONObject jsonObject = new JSONObject(result);
			String status = jsonObject.getString("status");
			if (status.equals(Constant.FLAG_TRADE_RESULT_SUC)) {
				JSONObject jsonData = jsonObject.getJSONObject("data");
				result_list[0] = jsonData.getString("port");
				result_list[1] =status;
				result_list[2] = jsonData.getString("sms");;
			} else {
				result_list[0] = jsonObject.getString("msg");
				result_list[1] = status;
			}
		} catch (Exception e) {
			ESPayLog.d(e.toString());
		}

		return result_list;
	}
}

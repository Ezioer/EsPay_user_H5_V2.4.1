package com.easou.androidsdk.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.text.TextUtils;

import com.easou.androidsdk.Starter;
import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.sso.AuthBean;
import com.easou.androidsdk.ui.ESUserWebActivity;
import com.easou.androidsdk.ui.FloatView;
import com.easou.androidsdk.util.AES;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.util.ESdkLog;
import com.easou.androidsdk.util.FileHelper;
import com.easou.androidsdk.util.HostRequestUtils;
import com.easou.androidsdk.util.NetworkUtils;
import com.easou.androidsdk.util.Tools;

import java.util.Map;

public class StartESUserPlugin {
	
	/**
	 * 登陆接口
	 */
	public static void loginSdk() {

		StartOtherPlugin.getOaid(Starter.mActivity);

		String channel = CommonUtils.readPropertiesValue(Starter.mActivity, Constant.CHANNEL_MARK);
		if (channel.equals("DHT")){
			Constant.PAY_CHANNEl = 1;
		} else if (channel.equals("YY")){
			Constant.PAY_CHANNEl = 2;
		} else if (channel.equals("ZKX")){
			Constant.PAY_CHANNEl = 3;
		} else if (channel.equals("WZYY")){
			Constant.PAY_CHANNEl = 4;
		} else {
			Constant.PAY_CHANNEl = 3;
		}
	/*	if (TextUtils.equals(CommonUtils.readPropertiesValue(Starter.mActivity, Constant.CHANNEL_MARK),
				Constant.CHANNEL_MARK_DHT)) {
			Constant.USE_DHT = true;
		}*/


		new Thread(new Runnable() {
			
			@Override
			public void run() {

				Looper.prepare();
				
				if (!Constant.IS_LOGINED) {
					startH5Login();
					startRequestHost(Starter.mActivity);
				}
				
				Looper.loop();
			}
		}).start();
	}
	
	/**
	 * 打开H5 SDK页面
	 */
	public static void startH5Login() {

//		StartOtherPlugin.logGDTActionRegister();

		// 获取deviceID
		String imei = Tools.getDeviceImei(Starter.mActivity);
		if (!TextUtils.isEmpty(imei.trim())) {
			Constant.IMEI = imei;
		}
		Constant.NET_IP = Tools.getNetIp();
		
		enterH5View();
	}
	

	/**
	 * 进入H5 SDK页面
	 */
	public static void enterH5View() {
			
		Intent intent = new Intent();
		intent.putExtra("params", getNewParam());
		intent.setClass(Starter.mActivity, ESUserWebActivity.class);
		Starter.mActivity.startActivity(intent);
	}
	

	/**
	 * 获取SDK用户信息
	 */
	public static void getH5UserInfo() {

		ESUserWebActivity.clientToJS(Constant.YSTOJS_GET_USERINFO, null);
	}

	/**
	 * 每隔5分钟去请求服务器更新用户游玩时长
	 */
	public static void postTime(){
		ESUserWebActivity.clientToJS(Constant.YSTOJS_UPLOAD_TIME, null);
	}
	
	/**
	 * 判断用户是否实名认证
	 */
	public static void getUserCertStatus() {

		ESUserWebActivity.clientToJS(Constant.YSTOJS_IS_CERTUSER, null);
		enterH5View();
	}
	
	/**
	 * 打开实名认证页面
	 */
	public static void showUserCert() {
		
		ESUserWebActivity.clientToJS(Constant.YSTOJS_USERCERT, null);
		enterH5View();
	}
	
	/**
	 * 显示SDK页面
	 */
	public static void showSdkView() {
		
		ESUserWebActivity.clientToJS(Constant.YSTOJS_CLICK_FLOATVIEW, null);
		enterH5View();
	}
	
	
	/**
	 * 游戏登录日志
	 */
	public static void startGameLoginLog(Map<String, String> playerInfo) {

		StartLogPlugin.startGameLoginLog(playerInfo);

//		ESUserWebActivity.clientToJS(Constant.YSTOJS_GAME_LOGIN_LOG, playerInfo);
	}

	/**
	 * 获取网页SDK所需参数
	 */
	public static String getNewParam() {

		String encryptKey = CommonUtils.readPropertiesValue(Starter.mActivity, "key");
		try {
			encryptKey = AES.encrypt(encryptKey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String appid = CommonUtils.readPropertiesValue(Starter.mActivity, Constant.APP_ID);
		String token = CommonUtils.getH5Token(Starter.mActivity);
		
		if (TextUtils.isEmpty(token)) {
			try {
//				AuthBean bean = CommonUtils.getAuthBean(Starter.mActivity,appid);
				String bean = CommonUtils.getTokenFromSD(appid);
				if (bean != null) {
//					token = bean.getToken().getToken();
					token =bean;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		String param = "&appId=" + appid
				+ "&qn=" +CommonUtils.readPropertiesValue(Starter.mActivity, Constant.QN)
				+ "&partnerId=" + CommonUtils.readPropertiesValue(Starter.mActivity, Constant.PARTENER_ID)
				+ "&secretKey=" + encryptKey
				+ "&deviceId=" + Constant.IMEI
				+ "&clientIp=" + Constant.NET_IP
				+ "&oaId=" + Constant.OAID
				+ "&phoneOs=" + Constant.SDK_PHONEOS
				+ "&phoneBrand=" + Tools.getDeviceBrand()
				+ "&phoneModel=" + Tools.getSystemModel()
				+ "&phoneVersion=" + Tools.getSystemVersion()
				+ "&userToken=" + token
				+ "&isSimulator=" + Constant.IS_SIMULATOR
				+ "&netMode=" + NetworkUtils.getNetworkState(Starter.mActivity)
				+ "&telecom=" + NetworkUtils.getOperator(Starter.mActivity);

		ESdkLog.d("上传的oaid：" + Constant.OAID);
		System.out.println("param：" + param);

		return param;
	}
	
	/**
	 * 显示悬浮窗
	 */
	public static void showFloatView() {

		if (Constant.IS_LOGINED) {
				FloatView.show(Starter.mActivity);
		} else {
			if (Constant.IS_ENTERED_SDK) {
				// 未登陆显示用户中心
				StartESUserPlugin.enterH5View();
			}
		}
	}
	
	/**
	 * 隐藏悬浮窗
	 */
	public static void hideFloatView() {
		FloatView.close();
	}
	
	/**
	 * 请求host信息
	 */
	public static void startRequestHost(final Activity activity) {
		
		try {
			// 读取存储的host信息
			String jsonData = FileHelper.readFile(Constant.getHostInfoFile(activity));
			if (jsonData == null) {
				jsonData = FileHelper.readFile(Constant.getSDHostInfoFile());
			}
			
			if (jsonData == null || jsonData.equals("")) {
				HostRequestUtils.requestHostInfo(activity, false);
			} else {
				HostRequestUtils.requestHostInfo(activity, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 从Properties文件中读取配置信息
	 * @param key：参数名称
	 */
	public static String getPropValue(Context _context, String key) {
		return CommonUtils.readPropertiesValue(_context, key);
	}
}

package com.easou.androidsdk.util;

import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.http.HttpGroupUtils;

import org.json.JSONObject;

public class HttpLogHelper {
	
	public static void sendHttpRequest(final String url, final String parma) {
		
		 new Thread(new Runnable() {

				@Override
				public void run() {
					
					try {
												
						String result = HttpGroupUtils.sendGet(url, parma, null);
						String title = "";
						
						if (url.equals(Constant.MAIN_URL + Tools.getHostName() + Constant.APP_LOAD_URL)) {
							title = "应用启动";
						} else if (url.equals(Constant.MAIN_URL + Tools.getHostName() + Constant.GAME_LOGIN_URL)) {
							title = "游戏登录";
						} else if (url.equals(Constant.MAIN_URL + Tools.getHostName() + Constant.GAME_ORDER_URL)) {
							title = "游戏订购";
						} else {
							title = "SDK登录";
						}
						
						try {
							JSONObject jsonObject = new JSONObject(result);
							String resultCode = jsonObject.getString("resultCode");
							if (resultCode.equals("1")) {
								ESdkLog.d("上传" + title + "日志成功");
							} else {
								ESdkLog.d("上传" + title + "日志失败");
							}
						} catch (Exception e) {
							ESPayLog.d("上传" + title + "日志失败");
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
		    }).start();
	}
 
	
}

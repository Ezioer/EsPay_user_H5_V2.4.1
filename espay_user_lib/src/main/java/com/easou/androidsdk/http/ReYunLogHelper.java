package com.easou.androidsdk.http;

import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.util.ESPayLog;
import com.easou.androidsdk.util.ESdkLog;
import com.easou.androidsdk.util.ThreadPoolManager;
import com.easou.androidsdk.util.Tools;

import org.json.JSONObject;

public class ReYunLogHelper {

	public static void sendHttpRequest(final String url, final JSONObject parma) {

		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
				try {

					String result = EAPayInter.getResponse(url, parma);
					String title = "";

					if (url.equals(Constant.MAIN_URL + Tools.getHostName() + Constant.REYUN_ADD_PAY_LOG)) {
						title = "热云-游戏内购买";
					} else if (url.equals(Constant.MAIN_URL + Tools.getHostName() + Constant.REYUN_ADD_PLAYER_LOG)) {
						title = "热云-创建游戏角色";
					} else if (url.equals(Constant.MAIN_URL + Tools.getHostName() + Constant.REYUN_ADD_LOGIN_LOG)) {
						title = "热云-游戏登录";
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
		});
	}
}

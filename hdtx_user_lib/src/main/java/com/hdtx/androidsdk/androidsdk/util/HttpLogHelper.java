package com.hdtx.androidsdk.androidsdk.util;

import com.hdtx.androidsdk.androidsdk.data.Constant;
import com.hdtx.androidsdk.androidsdk.http.HttpGroupUtils;

import org.json.JSONObject;

public class HttpLogHelper {

    public static void sendHttpRequest(final String url, final String parma) {

        ThreadPoolManager.getInstance().addTask(new Runnable() {
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
                    } else if (url.equals(Constant.MAIN_URL + Tools.getHostName() + Constant.GAME_PLAYER_LOG)) {
                        title = "游戏角色登录";
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
		 /*new Thread(new Runnable() {

				@Override
				public void run() {
					

				}
		    }).start();*/
    }
}

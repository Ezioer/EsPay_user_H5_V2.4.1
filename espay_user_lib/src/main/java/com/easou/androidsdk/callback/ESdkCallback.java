package com.easou.androidsdk.callback;

import java.util.Map;

public interface ESdkCallback {

	void onLogin(Map<String, String> loginResult);

	void onLogout();
	
	void onRegister(Map<String, String> registerResult);
	
	void onUserInfo(Map<String, String> userInfoResult);
	
	void onUserCert(Map<String, String> userCertResult);
}

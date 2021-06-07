package com.easou.androidsdk.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.easou.androidsdk.ESPlatform;
import com.easou.androidsdk.callback.AppTimeWatcher;
import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.data.ESConstant;
import com.easou.androidsdk.plugin.StartESUserPlugin;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.util.ESdkLog;
import com.easou.androidsdk.util.ReplaceCallBack;
import com.easou.androidsdk.util.ThreadPoolManager;
import com.easou.androidsdk.webviewutils.ImageUtil;
import com.easou.androidsdk.webviewutils.JSAndroid;
import com.easou.androidsdk.webviewutils.PermissionUtil;
import com.easou.androidsdk.webviewutils.ReWebChomeClient;
import com.easou.androidsdk.webviewutils.ReWebViewClient;
import com.easou.espay_user_lib.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ESUserWebActivity extends Activity implements ReWebChomeClient.OpenFileChooserCallBack {

    /**
     * 网页加载提示
     */
    private static ProgressDialog progressDialog = null;
    /**
     * 需要包装网页的控件
     */
    public static WebView mWebView;
    /**
     * 使用的浏览器对象
     */
    private static WebChromeClient mWebChromeClient;
    /**
     * js跳转控制
     */
    private static WebViewClient mWebViewClient;
    private static ESUserWebActivity mActivity;

    private static final int REQUEST_CODE_PICK_IMAGE = 0;
    private static final int REQUEST_CODE_IMAGE_CAPTURE = 1;
    private Intent mSourceIntent;
    public ValueCallback<Uri[]> mUploadMsgForAndroid5;
    private ValueCallback<Uri> mUploadMsg;
    private static final int P_CODE_PERMISSIONS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideBottomUIMenu();
        setContentView(getApplication().getResources().getIdentifier("easou_web_user", "layout",
                getApplication().getPackageName()));

        mActivity = this;
        Constant.IS_ENTERED_SDK = true;

        ESPlatform.init(mActivity);
        initView();
    }

    protected void hideBottomUIMenu() {
        Window _window = getWindow();
        WindowManager.LayoutParams params = _window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        _window.setAttributes(params);
    }

    private void initView() {

        mWebView = (WebView) findViewById(getApplication().getResources().getIdentifier("easou_id_WebView_user", "id",
                getApplication().getPackageName()));

        Intent intent = getIntent();
        final String params = intent.getStringExtra("params");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.setWebContentsDebuggingEnabled(true);
        }
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setJavaScriptEnabled(true);// webview必须设置支持Javascript
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setInitialScale(30);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);// WebView启用Javascript脚本执行
        mWebView.setVerticalScrollBarEnabled(true);// 取消VerticalScrollBar显示
        mWebView.getSettings().setDomStorageEnabled(true);// 设置html5离线缓存可用

        mWebView.addJavascriptInterface(new ESPlatform(), "ESDK");
        mWebView.addJavascriptInterface(new JSAndroid(this), "Android");
        mWebView.setBackgroundColor(0); // 设置背景色
        mWebView.getBackground().setAlpha(0); // 设置填充透明度 范围：0-255

        fixDirPath();
        mWebView.setWebViewClient(new ReWebViewClient());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        mWebChromeClient = new WebChromeClient() {

            @Override
            public void onReceivedTitle(final WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if (title.contains("404") || title.contains("500") || title.contains("Error")
                            || title.contains("找不到网页") || title.contains("网页无法打开")) {
                        ThreadPoolManager.getInstance().addTask(new Runnable() {
                            @Override
                            public void run() {
                                StartESUserPlugin.startRequestHost(mActivity, true, new ReplaceCallBack() {
                                    @Override
                                    public void replaceSuccess() {
                                        view.loadUrl(Constant.SSO_URL + Constant.URL_BACKUP + Constant.SSO_REST + params);
                                    }

                                    @Override
                                    public void replaceFail() {
                                        ViewParent webParentView = (ViewParent) mWebView.getParent();
                                        ((ViewGroup) webParentView).removeAllViews();
                                        showAlert();
                                    }
                                });
                            }
                        });
                    }
                }
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {

                AlertDialog.Builder b2 = new AlertDialog.Builder(mActivity, AlertDialog.THEME_HOLO_LIGHT)
                        .setTitle("温馨提示").setMessage(message)
                        .setPositiveButton("确 定", new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        });

                b2.setCancelable(false);
                b2.create();
                b2.show();

                return true;
            }
        };

        mWebView.requestFocus();
        mWebView.requestFocusFromTouch();

        mWebViewClient = new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideDialog();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showDialog();
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onReceivedError(final WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                ThreadPoolManager.getInstance().addTask(new Runnable() {
                    @Override
                    public void run() {
                        StartESUserPlugin.startRequestHost(mActivity, true, new ReplaceCallBack() {
                            @Override
                            public void replaceSuccess() {
                                view.loadUrl(Constant.SSO_URL + Constant.URL_BACKUP + Constant.SSO_REST + params);
                            }

                            @Override
                            public void replaceFail() {
                                ViewParent webParentView = (ViewParent) mWebView.getParent();
                                ((ViewGroup) webParentView).removeAllViews();
                                showAlert();
                            }
                        });
                    }
                });
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                return super.shouldOverrideUrlLoading(view, url);
            }


        };

        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(new ReWebChomeClient(this));
        String url_backup = CommonUtils.getIsReplaceSso(mActivity);
        if (!TextUtils.isEmpty(CommonUtils.getIsReplaceSso(mActivity))) {
            Constant.URL_BACKUP = url_backup;
        }
        mWebView.loadUrl(Constant.SSO_URL + Constant.URL_BACKUP + Constant.SSO_REST + params);
    }

    public static void clientToJS(int type, final Map<String, String> params) {
        switch (type) {
            case Constant.YSTOJS_GAME_LOGINOROUTLOG:
                final String bt = "bt:" + "'" + params.get("bt") + "'";
                final String deviceId = "deviceId:" + "'" + params.get("deviceId") + "'";
                final String userId = "userId:" + "'" + params.get("userId") + "'";
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:EsSdkShell.esIdentity2LoginOutLog({" + bt + ", " + deviceId + ", " + userId + "})");
                    }
                });
                break;
            case Constant.YSTOJS_GAME_LOGIN_DATA:
                final String pName = "playerName:" + "'" + params.get(ESConstant.PLAYER_NAME) + "'";
                final String pId = "playerId:" + params.get(ESConstant.PLAYER_ID);
                final String pLevel = "playerLevel:" + params.get(ESConstant.PLAYER_LEVEL);
                final String sId = "serverId:" + params.get(ESConstant.PLAYER_SERVER_ID);
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:EsSdkShell.esSetGameLoginData({" + pName + ", " + pId + ", " + pLevel + ", " + sId + "})");
                    }
                });
                break;
            /** 调用服务端上传日志接口 */
            case Constant.YSTOJS_GAME_LOGIN_LOG:

                final String playerName = "playerName:" + "'" + params.get(ESConstant.PLAYER_NAME) + "'";
                final String playerId = "playerId:" + params.get(ESConstant.PLAYER_ID);
                final String playerLevel = "playerLevel:" + params.get(ESConstant.PLAYER_LEVEL);
                final String serverId = "serverId:" + params.get(ESConstant.PLAYER_SERVER_ID);

                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:EsSdkShell.esLogGameLogin({" + playerId + ", " + playerLevel + ", " + playerName + ", " + serverId + "})");
                    }
                });
                break;

            /** 调用服务端游戏下单日志接口 */
            case Constant.YSTOJS_GAME_ORDER_LOG:

                final String amount = "amount:" + params.get(ESConstant.MONEY);
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:EsSdkShell.esLogUserOrder({" + amount + "})");
                    }
                });
                break;

            /** 调用服务端获取用户信息接口 */
            case Constant.YSTOJS_GET_USERINFO:
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:EsSdkOriginal.esSDKGetUserLoginInfo()");
                    }
                });
                break;

            /** 调用服务端浮标点击接口 */
            case Constant.YSTOJS_CLICK_FLOATVIEW:
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:EsSdkShell.esUserClickFubiao()");
                    }
                });
                break;

            /** 调用服务端用户是否已实名认证接口 */
            case Constant.YSTOJS_IS_CERTUSER:
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:EsSdkOriginal.esSsoIsIdentityUser()");
                    }
                });
                break;

            /** 调用服务端实名认证接口 */
            case Constant.YSTOJS_USERCERT:
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:EsSdkShell.esUserClickCert()");
                    }
                });
                break;

            /** 调用服务端设置oaid接口 */
            case Constant.YSTOJS_GET_OAID:
                final String OAID = "oaid:" + "'" + Constant.OAID + "'";
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        ESdkLog.d("重新获取oaid：" + OAID);
                        mWebView.loadUrl("javascript:EsSdkShell.esSetDeviceOaid({" + OAID + "})");
                    }
                });
                break;

            /** 调用服务端获取充值限制信息接口 */
            case Constant.YSTOJS_GET_PAY_LIMIT_INFO:
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:EsSdkOriginal.esSDKGetPayLimitInfo()");
                    }
                });
                break;

            case Constant.YSTOJS_UPLOAD_TIME:
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:EsSdkShell.esUserOnlineTimer()");
                    }
                });
                break;
            default:
                break;
        }
    }

    public void clearData() {

        mWebView.clearFormData();
        mWebView.clearHistory();
    }

    private void showAlert() {

        final AlertDialog.Builder exitDialog = new AlertDialog.Builder(mActivity, AlertDialog.THEME_HOLO_LIGHT);
        exitDialog.setTitle("温馨提示")
                .setMessage("网络连接错误，请检查网络后重启游戏！")
                .setPositiveButton("确 定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        mActivity.finish();
                        System.exit(0);
                    }
                });
        exitDialog.setCancelable(false);
        // 显示
        exitDialog.show();
    }

    private void showDialog() {
        try {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(mActivity, ProgressDialog.THEME_HOLO_LIGHT);
            }
            progressDialog.setMessage("数据加载中，请稍候...");
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /*@Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        if (AppTimeWatcher.isBack && AppTimeWatcher.isFore) {
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            assert manager != null;
            int taskId = getIntent().getIntExtra("taskid", Integer.MIN_VALUE);
            if (taskId > 0) {
                manager.moveTaskToFront(taskId, 0);
            }
            AppTimeWatcher.isFore = false;
            AppTimeWatcher.isBack = false;
        }
        super.onResume();
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            if (mUploadMsg != null) {
                mUploadMsg.onReceiveValue(null);
            }

            if (mUploadMsgForAndroid5 != null) {         // for android 5.0+
                mUploadMsgForAndroid5.onReceiveValue(null);
            }
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_IMAGE_CAPTURE:
            case REQUEST_CODE_PICK_IMAGE: {
                try {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        if (mUploadMsg == null) {
                            return;
                        }

                        String sourcePath = ImageUtil.retrievePath(this, mSourceIntent, data);

                        if (TextUtils.isEmpty(sourcePath) || !new File(sourcePath).exists()) {

                            break;
                        }
                        Uri uri = Uri.fromFile(new File(sourcePath));
                        mUploadMsg.onReceiveValue(uri);

                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (mUploadMsgForAndroid5 == null) {        // for android 5.0+
                            return;
                        }

                        String sourcePath = ImageUtil.retrievePath(this, mSourceIntent, data);

                        if (TextUtils.isEmpty(sourcePath) || !new File(sourcePath).exists()) {

                            break;
                        }
                        Uri uri = Uri.fromFile(new File(sourcePath));
                        mUploadMsgForAndroid5.onReceiveValue(new Uri[]{uri});
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }


    public void showOptions() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setOnCancelListener(new DialogOnCancelListener());
        alertDialog.setTitle("请选择操作");
        String[] options = {"相册", "拍照"};
        alertDialog.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            if (PermissionUtil.isOverMarshmallow()) {
                                if (!PermissionUtil.isPermissionValid(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                    Toast.makeText(mActivity,
                                            "请去\"设置\"中开启本应用的图片媒体访问权限",
                                            Toast.LENGTH_SHORT).show();

                                    restoreUploadMsg();
                                    requestPermissionsAndroidM();
                                    return;
                                }

                            }

                            try {
                                mSourceIntent = ImageUtil.choosePicture();
                                startActivityForResult(mSourceIntent, REQUEST_CODE_PICK_IMAGE);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(mActivity,
                                        "请去\"设置\"中开启本应用的图片媒体访问权限",
                                        Toast.LENGTH_SHORT).show();
                                restoreUploadMsg();
                            }

                        } else {
                            if (PermissionUtil.isOverMarshmallow()) {
                                if (!PermissionUtil.isPermissionValid(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                    Toast.makeText(mActivity,
                                            "请去\"设置\"中开启本应用的图片媒体访问权限",
                                            Toast.LENGTH_SHORT).show();

                                    restoreUploadMsg();
                                    requestPermissionsAndroidM();
                                    return;
                                }

                                if (!PermissionUtil.isPermissionValid(mActivity, Manifest.permission.CAMERA)) {
                                    Toast.makeText(mActivity,
                                            "请去\"设置\"中开启本应用的相机权限",
                                            Toast.LENGTH_SHORT).show();

                                    restoreUploadMsg();
                                    requestPermissionsAndroidM();
                                    return;
                                }
                            }

                            try {
                                mSourceIntent = ImageUtil.takeBigPicture();
                                startActivityForResult(mSourceIntent, REQUEST_CODE_IMAGE_CAPTURE);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(mActivity,
                                        "请去\"设置\"中开启本应用的相机和图片媒体访问权限",
                                        Toast.LENGTH_SHORT).show();

                                restoreUploadMsg();
                            }
                        }
                    }
                }
        ).show();
    }

    private void fixDirPath() {
        String path = ImageUtil.getDirPath();
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType) {
        mUploadMsg = uploadMsg;

        showOptions();
    }


    @Override
    public boolean openFileChooserCallBackAndroid5(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        mUploadMsgForAndroid5 = filePathCallback;

        showOptions();
        return true;
    }


    private class DialogOnCancelListener implements DialogInterface.OnCancelListener {
        @Override
        public void onCancel(DialogInterface dialogInterface) {
            restoreUploadMsg();
        }
    }

    private void restoreUploadMsg() {
        if (mUploadMsg != null) {
            mUploadMsg.onReceiveValue(null);
            mUploadMsg = null;

        } else if (mUploadMsgForAndroid5 != null) {
            mUploadMsgForAndroid5.onReceiveValue(null);
            mUploadMsgForAndroid5 = null;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case P_CODE_PERMISSIONS:
                requestResult(permissions, grantResults);
                restoreUploadMsg();
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void requestPermissionsAndroidM() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> needPermissionList = new ArrayList<>();
            needPermissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            needPermissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            needPermissionList.add(Manifest.permission.CAMERA);

            PermissionUtil.requestPermissions(mActivity, P_CODE_PERMISSIONS, needPermissionList);

        } else {
            return;
        }
    }

    public void requestResult(String[] permissions, int[] grantResults) {
        ArrayList<String> needPermissions = new ArrayList<String>();

        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                if (PermissionUtil.isOverMarshmallow()) {

                    needPermissions.add(permissions[i]);
                }
            }
        }

        if (needPermissions.size() > 0) {
            StringBuilder permissionsMsg = new StringBuilder();

            for (int i = 0; i < needPermissions.size(); i++) {
                String strPermissons = needPermissions.get(i);

                if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(strPermissons)) {
                    permissionsMsg.append("," + getString(R.string.permission_storage));

                } else if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(strPermissons)) {
                    permissionsMsg.append("," + getString(R.string.permission_storage));

                } else if (Manifest.permission.CAMERA.equals(strPermissons)) {
                    permissionsMsg.append("," + getString(R.string.permission_camera));

                }
            }

            String strMessage = "请允许使用\"" + permissionsMsg.substring(1).toString() + "\"权限, 以正常使用APP的所有功能.";

            Toast.makeText(mActivity, strMessage, Toast.LENGTH_SHORT).show();

        } else {
            return;
        }
    }
}

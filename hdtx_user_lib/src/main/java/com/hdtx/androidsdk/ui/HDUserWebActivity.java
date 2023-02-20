package com.hdtx.androidsdk.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.hdtx.androidsdk.HDPlatform;
import com.hdtx.androidsdk.data.Constant;
import com.hdtx.androidsdk.data.HDConstant;
import com.hdtx.androidsdk.util.CommonUtils;
import com.hdtx.androidsdk.util.HDSdkLog;
import com.hdtx.androidsdk.util.ReplaceCallBack;
import com.hdtx.androidsdk.util.ThreadPoolManager;
import com.hdtx.androidsdk.util.Tools;
import com.hdtx.androidsdk.webviewutils.ImageUtil;
import com.hdtx.androidsdk.webviewutils.JSAndroid;
import com.hdtx.androidsdk.webviewutils.PermissionUtil;
import com.hdtx.hdtx_user_lib.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HDUserWebActivity extends Activity {

    /**
     * 网页加载提示
     */
    private static ProgressDialog progressDialog = null;
    /**
     * 需要包装网页的控件
     */
    public static WebView mWebView;
    /**
     * js跳转控制
     */
    private static WebViewClient mWebViewClient;
    private static com.hdtx.androidsdk.ui.HDUserWebActivity mActivity;

    private static final int REQUEST_CODE_PICK_IMAGE = 11;
    private static final int REQUEST_CODE_IMAGE_CAPTURE = 99;
    private static final int P_CODE_PERMISSIONS = 101;
    private Uri mImageUri;
    private Uri mCropUri;
    private String mParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideBottomUIMenu();
        setContentView(getApplication().getResources().getIdentifier("hd_web_user", "layout",
                getApplication().getPackageName()));

        mActivity = this;
        Constant.IS_ENTERED_SDK = true;

        HDPlatform.init(mActivity);
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
        mParams = intent.getStringExtra("params");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.setWebContentsDebuggingEnabled(true);
        }
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setJavaScriptEnabled(true);// webview必须设置支持Javascript
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setBlockNetworkImage(false);
        mWebView.setInitialScale(30);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);// WebView启用Javascript脚本执行
        mWebView.setVerticalScrollBarEnabled(true);// 取消VerticalScrollBar显示
        mWebView.getSettings().setDomStorageEnabled(true);// 设置html5离线缓存可用

        mWebView.addJavascriptInterface(new HDPlatform(), "ESDK");
        mWebView.addJavascriptInterface(new JSAndroid(this), "Android");
        mWebView.setBackgroundColor(0); // 设置背景色
        mWebView.getBackground().setAlpha(0); // 设置填充透明度 范围：0-255

        fixDirPath();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

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
                        com.hdtx.androidsdk.plugin.StartHDUserPlugin.startRequestHost(mActivity, true, new ReplaceCallBack() {
                            @Override
                            public void replaceSuccess() {
                                if (Constant.SSO_URL.startsWith("https")) {
                                    view.loadUrl(Constant.SSO_URL + Constant.URL_BACKUP + Constant.SSO_REST + mParams);
                                } else {
                                    view.loadUrl(Constant.SSO_URL + mParams);
                                }
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

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    view.loadUrl(url);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        };

        Constant.ua = mWebView.getSettings().getUserAgentString();
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(new MyWebChromClient());
        String url_backup = CommonUtils.getIsReplaceSso(mActivity);
        if (!TextUtils.isEmpty(CommonUtils.getIsReplaceSso(mActivity))) {
            Constant.URL_BACKUP = url_backup;
        }
        if (Constant.SSO_URL.startsWith("https")) {
            mWebView.loadUrl(Constant.SSO_URL + Constant.URL_BACKUP + Constant.SSO_REST + mParams);
        } else {
            mWebView.loadUrl(Constant.SSO_URL + mParams);
        }
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
                final String pName = "playerName:" + "'" + params.get(HDConstant.PLAYER_NAME) + "'";
                final String pId = "playerId:" + params.get(HDConstant.PLAYER_ID);
                final String pLevel = "playerLevel:" + params.get(HDConstant.PLAYER_LEVEL);
                final String sId = "serverId:" + params.get(HDConstant.PLAYER_SERVER_ID);
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:EsSdkShell.esSetGameLoginData({" + pName + ", " + pId + ", " + pLevel + ", " + sId + "})");
                    }
                });
                break;
            /** 调用服务端上传日志接口 */
            case Constant.YSTOJS_GAME_LOGIN_LOG:

                final String playerName = "playerName:" + "'" + params.get(HDConstant.PLAYER_NAME) + "'";
                final String playerId = "playerId:" + params.get(HDConstant.PLAYER_ID);
                final String playerLevel = "playerLevel:" + params.get(HDConstant.PLAYER_LEVEL);
                final String serverId = "serverId:" + params.get(HDConstant.PLAYER_SERVER_ID);

                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:EsSdkShell.esLogGameLogin({" + playerId + ", " + playerLevel + ", " + playerName + ", " + serverId + "})");
                    }
                });
                break;

            /** 调用服务端游戏下单日志接口 */
            case Constant.YSTOJS_GAME_ORDER_LOG:

                final String amount = "amount:" + params.get(HDConstant.MONEY);
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

            case Constant.YSTOJS_GAME_INTOFOREGROUND:
                if (mWebView != null) {
                    mWebView.post(new Runnable() {
                        @Override
                        public void run() {
                            mWebView.loadUrl("javascript:EsSdkShell.esUserIntoForeground()");
                        }
                    });
                }
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

            /** 调用服务端切换账号接口 */
            case Constant.YSTOJS_GAME_LOGOUT:
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:EsSdkShell.esUserClickLogOut()");
                    }
                });
                break;

            /** 调用服务端设置oaid接口 */
            case Constant.YSTOJS_GET_OAID:
                final String OAID = "oaid:" + "'" + Constant.OAID + "'";
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        HDSdkLog.d("重新获取oaid：" + OAID);
                        mWebView.loadUrl("javascript:EsSdkShell.esSetDeviceOaid({" + OAID + "})");
                    }
                });
                break;
            case Constant.YSTOJS_GET_CUSTOMDEVICE:

                final String customDeviceId = "customDeviceId:" + "'" + Constant.CUSTOMDEVICES + "'";
                final String customJson = "customJson:" + Tools.getOnlyId().toString();

                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        HDSdkLog.d("获取customid：" + Constant.CUSTOMDEVICES + Tools.getOnlyId().toString());
                        mWebView.loadUrl("javascript:EsSdkShell.esSetCustomId({" + customDeviceId + ", " + customJson + "})");
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
                if (mWebView != null) {
                    mWebView.post(new Runnable() {
                        @Override
                        public void run() {
                            mWebView.loadUrl("javascript:EsSdkShell.esUserOnlineTimer()");
                        }
                    });
                }
                break;
            default:
                break;
        }
    }

    public void clearData() {
        mWebView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWebView.clearFormData();
                mWebView.clearHistory();
            }
        }, 300);

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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
                                Intent mSourceIntent = ImageUtil.choosePicture();
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
                                File photoFile = saveFileName();
                                if (photoFile != null) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        mImageUri = FileProvider.getUriForFile(com.hdtx.androidsdk.ui.HDUserWebActivity.this, com.hdtx.androidsdk.ui.HDUserWebActivity.this.getPackageName() + ".fileprovider", photoFile);
                                    } else {
                                        mImageUri = getDesUri();
                                    }
                                    mCropUri = Uri.fromFile(saveFileName());
                                    Log.d("takephoto", "imageuri-->" + mImageUri);
                                    Intent mSourceIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    mSourceIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                                    mSourceIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                                    startActivityForResult(mSourceIntent, REQUEST_CODE_IMAGE_CAPTURE);
                                }
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

    private String filePath = "";

    private File saveFileName() {
        String folder = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + File.separator;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        String name = format.format(date) + ".jpg";
        File file = null;
        try {
            file = new File(folder + name);
            file.createNewFile();
            filePath = file.getAbsolutePath();
        } catch (Exception e) {

        }
        return file;
    }

    private void fixDirPath() {
        String path = ImageUtil.getDirPath();
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private class DialogOnCancelListener implements DialogInterface.OnCancelListener {
        @Override
        public void onCancel(DialogInterface dialogInterface) {

            restoreUploadMsg();
        }
    }

    private void restoreUploadMsg() {
        if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(null);
            mUploadMessage = null;

        } else if (uploadMessageAboveL != null) {
            uploadMessageAboveL.onReceiveValue(null);
            uploadMessageAboveL = null;
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
            showOptions();
        }
    }


    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;

    private class MyWebChromClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            mUploadMessage = uploadMsg;
            openImageChooserActivity();
        }

        // For Android 3.0+
        public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            openImageChooserActivity();
        }

        // For Android 4.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            mUploadMessage = uploadMsg;
            openImageChooserActivity();
        }

        // For Android >= 5.0
        @Override
        public boolean onShowFileChooser(WebView webView,
                                         ValueCallback<Uri[]> filePathCallback,
                                         WebChromeClient.FileChooserParams fileChooserParams) {
            uploadMessageAboveL = filePathCallback;
            openImageChooserActivity();
            return true;
        }

        @Override
        public void onReceivedTitle(final WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (title.contains("404") || title.contains("500") || title.contains("Error")
                        || title.contains("找不到网页") || title.contains("网页无法打开")) {
                    ThreadPoolManager.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {
                            com.hdtx.androidsdk.plugin.StartHDUserPlugin.startRequestHost(mActivity, true, new ReplaceCallBack() {
                                @Override
                                public void replaceSuccess() {
                                    if (Constant.SSO_URL.startsWith("https")) {
                                        view.loadUrl(Constant.SSO_URL + Constant.URL_BACKUP + Constant.SSO_REST + mParams);
                                    } else {
                                        view.loadUrl(Constant.SSO_URL + mParams);
                                    }
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
    }

    /**
     * 打开本地相册
     */
    private void openImageChooserActivity() {
        Constant.IS_LOGINED = true;
        showOptions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            //从相册选取图片
            if (null == mUploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        } else if (requestCode == REQUEST_CODE_IMAGE_CAPTURE) {
           /* Log.d("takephoto", "resultdata--->" + data);
            if (resultCode == RESULT_OK) {
                //拍照上传先裁剪
                Intent intent1;
                if (Build.VERSION.SDK_INT >= 29) {
                    intent1 = FileUtil.startPhotoZoom(mImageUri, mCropUri, 40);
                } else {
                    intent1 = FileUtil.startPhotoZoom(mImageUri, filePath, 40);
                }
                startActivityForResult(intent1, 10);
            }*/
            if (mImageUri != null && mCropUri != null && resultCode == Activity.RESULT_OK) {
                if (Build.VERSION.SDK_INT >= 30) {
                    uploadMessageAboveL.onReceiveValue(new Uri[]{mCropUri});
                    uploadMessageAboveL = null;
                } else {
                    uploadMessageAboveL.onReceiveValue(new Uri[]{mImageUri});
                    uploadMessageAboveL = null;
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if ((requestCode == REQUEST_CODE_PICK_IMAGE || requestCode == REQUEST_CODE_IMAGE_CAPTURE) && uploadMessageAboveL != null) {
            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK) {
                if (intent != null) {
                    String dataString = intent.getDataString();
                    ClipData clipData = intent.getClipData();
                    if (clipData != null) {
                        results = new Uri[clipData.getItemCount()];
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            results[i] = item.getUri();
                        }
                    }
                    if (dataString != null)
                        results = new Uri[]{Uri.parse(dataString)};
                }
            }
            uploadMessageAboveL.onReceiveValue(results);
            uploadMessageAboveL = null;
        }
    }

    private Uri getDesUri() {
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
        return Uri.fromFile(file);
    }
}

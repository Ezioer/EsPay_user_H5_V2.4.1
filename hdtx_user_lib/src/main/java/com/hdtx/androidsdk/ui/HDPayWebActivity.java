package com.hdtx.androidsdk.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.hdtx.androidsdk.data.Constant;
import com.hdtx.androidsdk.data.ErrorResult;
import com.hdtx.androidsdk.util.HDPayLog;
import com.hdtx.androidsdk.util.Tools;

public class HDPayWebActivity extends Activity implements OnClickListener {
    private static String room_url;
    private static String back_url;
    private static String token;
    /**
     * 网页加载提示
     */
    private static ProgressDialog progressDialog = null;
    /**
     * 用于防止未安装微信客户端的设备重复提示信息
     */
    private static boolean isShowedMsg;

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
    /** 自定义请求头 */
    // private static Map<String, String> map;
    /**
     * 回调通知
     */
    private com.hdtx.androidsdk.ui.HDPayWebActivity mActivity;
    private static int back_num;
    private static String layoutType;
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mActivity = this;
        mContext = this;
        back_num = 0;
        Intent intent = getIntent();

        setContentView(getApplication().getResources().getIdentifier("hd_web", "layout",
                getApplication().getPackageName()));
        mWebView = (WebView) findViewById(getApplication().getResources().getIdentifier("easou_id_WebView", "id",
                getApplication().getPackageName()));

        room_url = intent.getStringExtra("room_url");
        back_url = intent.getStringExtra("back_url");
        token = intent.getStringExtra(Constant.EASOUTGC);

        CookieSyncManager.createInstance(mContext);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookie();
        cookieManager.setCookie(room_url, Constant.EASOUTGC + "=" + token);//cookies是在HttpClient中获得的cookie
        CookieSyncManager.getInstance().sync();

        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setJavaScriptEnabled(true);// webview必须设置支持Javascript
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setInitialScale(30);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);// WebView启用Javascript脚本执行
        mWebView.setVerticalScrollBarEnabled(true);// 取消VerticalScrollBar显示
        mWebView.getSettings().setDomStorageEnabled(true);// 设置html5离线缓存可用

        mWebChromeClient = new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b2 = new AlertDialog.Builder(mActivity, AlertDialog.THEME_HOLO_LIGHT)
                        .setTitle("温馨提示")
                        .setMessage(message)
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
                // TODO Auto-generated method stub

                super.onPageFinished(view, url);

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }

                HDPayLog.d("finishUrl:" + url);
                if (url.startsWith(back_url)) {
                    String status = Tools.getParam(url, "status");
                    HDPayLog.d("status:" + status);
                    if (back_num == 0) {
                        if ("success".equals(status)) {
                            HDPayCenterActivity.onSuccCallBack();
                            mActivity.finish();
                        } else if ("fail".equals(status)) {
                            HDToast.getInstance().ToastShow(mActivity, "支付失败");
                            HDPayCenterActivity.onFailedCallBack(ErrorResult.ESPAY_FEE_ERROR, "支付失败");
                            mActivity.finish();
                        } else {
                            HDPayCenterActivity.onFailedCallBack(ErrorResult.ESPAY_FEE_ERROR, "支付异常");
                            mActivity.finish();
                        }
                        back_num = 1;
                    }
                    if (mWebView != null) {
                        mWebView.stopLoading();
                    }
                    return;
                }

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                super.onPageStarted(view, url, favicon);

                try {
                    if (progressDialog == null) {
                        progressDialog = new ProgressDialog(mActivity, ProgressDialog.THEME_HOLO_LIGHT);
                        progressDialog.setMessage("数据加载中，请稍候...");
                        progressDialog.show();
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }

                HDPayLog.d("pageUrl:" + url);

                if (url.startsWith(back_url)) {
                    String status = Tools.getParam(url, "status");
                    HDPayLog.d("status:" + status);
                    if (back_num == 0) {
                        if ("success".equals(status)) {
                            HDPayCenterActivity.onSuccCallBack();
                            mActivity.finish();
                        } else if ("fail".equals(status)) {
                            HDToast.getInstance().ToastShow(mActivity, "支付失败");
                            HDPayCenterActivity.onFailedCallBack(ErrorResult.ESPAY_FEE_ERROR, "支付失败");
                            mActivity.finish();
                        } else {
                            HDPayCenterActivity.onFailedCallBack(ErrorResult.ESPAY_FEE_ERROR, "支付异常");
                            mActivity.finish();
                        }
                        back_num = 1;
                    }

                    if (mWebView != null) {
                        mWebView.stopLoading();
                    }
                    return;
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.setAcceptCookie(true);
//			    cookieManager.removeSessionCookie();//移除  
                cookieManager.setCookie(url, Constant.EASOUTGC + "=" + token);//cookies是在HttpClient中获得的cookie
                CookieSyncManager.getInstance().sync();

                HDPayLog.d("url:" + url);
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    return super.shouldOverrideUrlLoading(view, url);
                } else {
                    try {
                        if (url.contains("alipays")) {
                            if (checkAliPayInstalled(mContext)) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(intent);
                            } else {
                                return super.shouldOverrideUrlLoading(view, url);
                            }
                        } else {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        if (isShowedMsg == false) {
                            if (url.contains("weixin")) {
                                Toast.makeText(mActivity, "请安装微信客户端", Toast.LENGTH_SHORT).show();
                                isShowedMsg = true;
                                return true;
                            } else if (url.contains("mqqapi")) {
                                Toast.makeText(mActivity, "请安装QQ客户端", Toast.LENGTH_SHORT).show();
                                isShowedMsg = true;
                                return true;
                            }
                        }

                        HDPayCenterActivity.onFailedCallBack(ErrorResult.ESPAY_FEE_ERROR, "支付失败");
                        mActivity.finish();
                    }
                    return true;
                }

            }
        };

        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.setWebViewClient(mWebViewClient);

        mWebView.loadUrl(room_url);
    }

    public static boolean checkAliPayInstalled(Context context) {

        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        return componentName != null;
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == getApplication().getResources().getIdentifier("easou_id_BackBtn", "id",
                getApplication().getPackageName())) {
            HDPayCenterActivity.onFailedCallBack(ErrorResult.ESPAY_CANCEL, "用户取消");
            mActivity.finish();
        }

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        if (mWebView != null) {
            mWebView = null;
        }
        isShowedMsg = false;
        UIHelper.isClicked = false;
        super.onDestroy();
    }

}

package com.hdtx.androidsdk.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobads.action.BaiduAction;
import com.baidu.mobads.action.PrivacyStatus;
import com.hdtx.androidsdk.data.Constant;
import com.hdtx.androidsdk.util.CommonUtils;
import com.hdtx.hdtx_user_lib.R;

/**
 * created by xiaoqing.zhou
 * on 2020/8/3
 * fun
 */
public class NotiDialog extends Dialog {

    public NotiDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    private View mView;
    private Context mContext;
    private WebView mWvReplace;
    private TextView mReplace;
    private TextView content;
    private TextView mKnow;
    private ImageView mBack;
    private boolean isShowBack = false;
//    private String mUserService = "http://www.chenglonghuyu.com/prot_hnqz_user.html";
private String mUserService = "http://www.chenglonghuyu.com/prot_hnqz_user.html";
    private String mPrivate = "http://www.chenglonghuyu.com/prot_hnqz.html";

    @Override
    protected void onStart() {
        super.onStart();
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams windowparams = window.getAttributes();
        windowparams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        windowparams.width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.9);
        window.setAttributes(windowparams);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = LayoutInflater.from(mContext).inflate(R.layout.dialog_userservice, null);
        setContentView(mView);
        initView();
    }

    private void initView() {
        TextView okButton = (TextView) mView.findViewById(R.id.btn_ok);
        TextView cancelButton = (TextView) mView.findViewById(R.id.btn_cancel);
        content = (TextView) mView.findViewById(R.id.tv_content);
        mBack = (ImageView) mView.findViewById(R.id.iv_back);
        mReplace = (TextView) mView.findViewById(R.id.tv_replace);
        mWvReplace = (WebView) mView.findViewById(R.id.wv_replace);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWvReplace.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWvReplace.setWebContentsDebuggingEnabled(true);
        }
        mWvReplace.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    view.loadUrl(url);
                    return true;
                }
                return false;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //super.onReceivedSslError(view, handler, error)
                switch (error.getPrimaryError()) {
                    case SslError.SSL_INVALID: // 校验过程遇到了bug
                    case SslError.SSL_UNTRUSTED: // 证书有问题
                        handler.proceed();
                    default:
                        handler.cancel();
                }
            }
        });
        mWvReplace.getSettings().setJavaScriptEnabled(true);
        mWvReplace.getSettings().setAllowFileAccess(true);
        mWvReplace.getSettings().setDefaultTextEncodingName("utf-8");
        mWvReplace.getSettings().setBlockNetworkImage(false);
        mKnow = (TextView) mView.findViewById(R.id.tv_know);
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(mNoti);
        stringBuilder.setSpan(new TextClick(), 100, 106, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        stringBuilder.setSpan(new TextClick1(), 107, 113, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        content.setMovementMethod(LinkMovementMethod.getInstance());
        content.setText(stringBuilder);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowBack) {
                    isShowBack = false;
                    mBack.setVisibility(View.GONE);
                    content.setVisibility(View.VISIBLE);
                    mReplace.setVisibility(View.GONE);
                    mWvReplace.setVisibility(View.GONE);
                }
            }
        });
        mKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKnow.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
                mReplace.setVisibility(View.GONE);
                mWvReplace.setVisibility(View.GONE);
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    CommonUtils.saveIsShowPrivate(mContext, 1);
                    if (Constant.BD_SDK) {
                        BaiduAction.setPrivacyStatus(PrivacyStatus.AGREE);
                    }
                    dismiss();
                    listener.buttonClick(1);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    isShowBack = false;
                    mKnow.setVisibility(View.VISIBLE);
                    mReplace.setText(mCantUse);
                    mReplace.setVisibility(View.VISIBLE);
                    listener.buttonClick(0);
                    mBack.setVisibility(View.GONE);
                    mWvReplace.setVisibility(View.GONE);
                    content.setVisibility(View.GONE);
                }
            }
        });
    }

    private AgreeListener listener = null;

    public void setAgreeListener(AgreeListener l) {
        listener = l;
    }

    public interface AgreeListener {
        void buttonClick(int type);
    }

    private class TextClick extends ClickableSpan {

        @Override
        public void onClick(@NonNull View widget) {
            isShowBack = true;
            mBack.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
            mReplace.setVisibility(View.GONE);
            mWvReplace.loadUrl(mUserService);
            mWvReplace.setVisibility(View.VISIBLE);
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            ds.setColor(Color.RED);
        }
    }

    private class TextClick1 extends ClickableSpan {

        @Override
        public void onClick(@NonNull View widget) {
            isShowBack = true;
            mBack.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
            mReplace.setVisibility(View.GONE);
            mWvReplace.loadUrl(mPrivate);
            mWvReplace.setVisibility(View.VISIBLE);
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            ds.setColor(Color.RED);
        }
    }

    private String mCantUse = "\n\n\n\n如未确认阅读并同意隐私政策协议，您将无法使用此产品。";
    private String mNoti = "欢迎下载本游戏，我们非常重视个人信息和隐私保护。\n" +
            "为了提供完整的游戏体验，我们会向您申请必要的权限和信息。您可以选择同意或拒绝权限申请，如果拒绝可能会影响游戏体验，请在使用我们的服务时，详细阅读并同意《用户协议》和《隐私政策》。\n" +
            "1、我们可能会申请SIM卡信息：注册/登录账户时，可能需要验证手机号码及验证码 ，使用App服务时，可提供手机号码作为账户登录名；\n" +
            "2、我们可能会申请读写设备上的照片及文件（SD卡）：提供写入外部储存功能；使用场景或目的：允许App写入/下载/保存/修改/删除图片、文件、崩溃日志等信息。\n" +
            "3、我们可能会申请获取设备 IMSI/IMEI 号：提供读取手机设备标识等信息，请您放心该权限无法监听、获取您的任何通话内容与信息；使用场景或目的：读取设备通话状态和识别码，识别手机设备ID，保证运营商网络免流服务，用于完成音视频、信息展示、账号登录、安全保障等主要功能。\n" +
            "我们收集您的信息主要是为了您和其他用户能够更容易和更满意地使用我们的服务。我们的目标是向所有的互联网用户提供安全、刺激、有趣及有教益的上网经历。而这些信息有助于我们实现这一目标。";
}

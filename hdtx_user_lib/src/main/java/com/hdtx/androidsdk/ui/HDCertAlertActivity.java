package com.hdtx.androidsdk.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hdtx.androidsdk.Starter;
import com.hdtx.androidsdk.data.Constant;

public class HDCertAlertActivity extends BaseActivity implements OnClickListener {

    private View mainFrameView;
    public static com.hdtx.androidsdk.ui.HDCertAlertActivity mActivity;

    private ImageButton closeButton;
    private Button apSureButton;
    private TextView tv_title;
    private TextView tv_message;
    private int enter, exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        hideBottomUIMenu();

        enter = getApplication().getResources()
                .getIdentifier("hdtx_anim_dialog_enter", "anim", getApplication().getPackageName());
        exit = getApplication().getResources()
                .getIdentifier("hdtx_anim_dialog_exit", "anim", getApplication().getPackageName());
        overridePendingTransition(enter, exit);

        mActivity = this;
        init();
        setContentView(mainFrameView);
    }

    protected void hideBottomUIMenu() {
        Window _window = getWindow();
        WindowManager.LayoutParams params = _window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        _window.setAttributes(params);
    }

    private void init() {

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        mainFrameView = inflater.inflate(getApplication().getResources().getIdentifier("hd_plugin_dialog_cert",
                "layout", getApplication().getPackageName()), null);

        tv_title = (TextView) mainFrameView.findViewById(getApplication().getResources()
                .getIdentifier("easou_id_pay_dialog_title", "id", getApplication().getPackageName()));
        tv_message = (TextView) mainFrameView.findViewById(getApplication().getResources()
                .getIdentifier("easou_id_pay_dialog_msg", "id", getApplication().getPackageName()));
        closeButton = (ImageButton) mainFrameView.findViewById(getApplication().getResources()
                .getIdentifier("easou_id_pay_dialog_close", "id", getApplication().getPackageName()));
        apSureButton = (Button) mainFrameView.findViewById(getApplication().getResources()
                .getIdentifier("easou_id_pay_dialog_btn", "id", getApplication().getPackageName()));

        try {
            changeView();
        } catch (Exception e) {
        }

        closeButton.setOnClickListener(mActivity);
        apSureButton.setOnClickListener(mActivity);
    }

    private void changeView() {

        if (Constant.PAY_LIMIT_INFO_MAP.get(Constant.SDK_USER_TYPE).equals("1")) {

            closeButton.setVisibility(View.GONE);

            // 未成年人提示框
            tv_title.setText(getApplication().getResources()
                    .getIdentifier("hdpay_wornning_title", "string", getApplication().getPackageName()));
            apSureButton.setText(getApplication().getResources()
                    .getIdentifier("hdpay_wornning_act", "string", getApplication().getPackageName()));

            String message = "下单失败，当前订单超过了您账号的充值限制。\n\n根据国家规定，";
            if (Constant.PAY_LIMIT_INFO_MAP.get(Constant.SDK_C_PAY).equals("0") &&
                    Constant.PAY_LIMIT_INFO_MAP.get(Constant.SDK_S_PAY).equals("0")) {
                message = message + "年龄未满" + Constant.PAY_LIMIT_INFO_MAP.get(Constant.SDK_MAX_AGE)
                        + "周岁的用户不允许进行游戏充值。";
            } else {
                message = message + Constant.PAY_LIMIT_INFO_MAP.get(Constant.SDK_MIN_AGE) +
                        "周岁以上未满" + Constant.PAY_LIMIT_INFO_MAP.get(Constant.SDK_MAX_AGE) +
                        "周岁的用户，单次充值金额不得超过" + Constant.PAY_LIMIT_INFO_MAP.get(Constant.SDK_S_PAY) +
                        "元人民币，当月充值金额累计不得超过" + Constant.PAY_LIMIT_INFO_MAP.get(Constant.SDK_C_PAY) +
                        "元。";
            }
            tv_message.setText(message);
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        overridePendingTransition(enter, exit);
    }

    @Override
    public void onClick(View v) {

        // TODO Auto-generated method stub
        if (v.getId() == getApplication().getResources().getIdentifier("easou_id_pay_dialog_close", "id",
                getApplication().getPackageName())) {
            mActivity.finish();
        } else if (v.getId() == getApplication().getResources().getIdentifier("easou_id_pay_dialog_btn", "id",
                getApplication().getPackageName())) {
            gotoUserCert();
            mActivity.finish();
        }
        overridePendingTransition(enter, exit);
    }

    private void gotoUserCert() {
        try {
            if (Constant.PAY_LIMIT_INFO_MAP.get(Constant.SDK_USER_TYPE).equals("2")) {
                // 用户类型为游客，确认按钮跳转到实名认证界面
                Starter.getInstance().showUserCertView();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        if (mainFrameView != null)
            mainFrameView.clearFocus();
        mainFrameView = null;

        super.finish();
    }

}

package com.hdtx.androidsdk.androidsdk.ui;

import com.hdtx.androidsdk.androidsdk.data.Constant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class ESAccountCenterActivity extends BaseActivity {

    private View mainFrameView;
    private static MHandler mHandler;
    private static Context mContext;
    public static boolean iscount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    @Override
    public void onBackPressed() {
        iscount = false;
        super.onBackPressed();
    }

    private void init() {

        mContext = this;
        mHandler = new MHandler();

        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        int screenHeight = display.getHeight();
        int screenWidth = display.getWidth();
        // 获取状态栏高度
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        // 状态栏高度
        int statusBarHeight = frame.top;

        UIHelper.screen_Width = screenWidth;
        UIHelper.screen_Height = screenHeight - statusBarHeight;
    }

    @Override
    public void finish() {
        if (mainFrameView != null)
            mainFrameView.clearFocus();
        mainFrameView = null;

        super.finish();
    }

    @SuppressLint("HandlerLeak")
    private class MHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.HANDLER_CLOSE_ACCOUNT_CENTER:
                    // 4
                    iscount = false;
                    finish();
                    break;
                case Constant.HANDLER_TOAST_MSG:
                    // 5
                    Toast.makeText(mContext, msg.obj.toString(), Toast.LENGTH_SHORT)
                            .show();
                    break;
                case Constant.HANDLER_GOBACK:
                    // 6
                    Message msg_return = mHandler.obtainMessage();
                    msg_return.what = Constant.HANDLER_LOAD_USERCENTER_VIEW;
                    msg_return.sendToTarget();

                    iscount = false;

                    break;
                case Constant.HANDLER_HIDE_GOBACK_BTN:
                    // 7
                    UIHelper.getBackButton().setVisibility(View.INVISIBLE);
                    break;
                case Constant.HANDLER_SHOW_GOBACK_BTN:
                    // 8
                    UIHelper.getBackButton().setVisibility(View.VISIBLE);
                    break;
                case Constant.HANDLER_SET_TITLE:
                    // 9
                    UIHelper.getTitle().setText(msg.obj.toString());
                    break;
            }
        }
    }

}

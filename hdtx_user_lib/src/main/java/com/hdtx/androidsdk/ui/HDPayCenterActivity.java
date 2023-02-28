package com.hdtx.androidsdk.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.hdtx.androidsdk.Starter;
import com.hdtx.androidsdk.data.Constant;
import com.hdtx.androidsdk.data.HDConstant;
import com.hdtx.androidsdk.data.ErrorResult;
import com.hdtx.androidsdk.data.FeeType;
import com.hdtx.androidsdk.data.PayItem;
import com.hdtx.androidsdk.data.PayResult;
import com.hdtx.androidsdk.http.EAPayImp;
import com.hdtx.androidsdk.http.EAPayInter;
import com.hdtx.androidsdk.http.HttpAsyncTask;
import com.hdtx.androidsdk.http.HttpAsyncTaskImp;
import com.hdtx.androidsdk.plugin.StartLogPlugin;
import com.hdtx.androidsdk.plugin.StartOtherPlugin;
import com.hdtx.androidsdk.util.CommonUtils;
import com.hdtx.androidsdk.util.DialogerUtils;
import com.hdtx.androidsdk.util.HDPayLog;
import com.hdtx.androidsdk.util.ThreadPoolManager;
import com.hdtx.androidsdk.util.Tools;
import com.hdtx.hdtx_user_lib.R;
import com.heepay.plugin.api.HPlugin;
import com.payeco.android.plugin.PayecoPluginLoadingActivity;
import com.ulopay.android.h5_library.manager.CheckOderManager;
import com.ulopay.android.h5_library.manager.WebViewManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("NewApi")
public class HDPayCenterActivity extends BaseActivity {
    /**
     * 银联的返回。
     */
    // private static final String PAYECO_ESPAYACTIVITY_ACTION = "com.easou.sdk.payeco.action";
    private static final String PAYECO_ESPAYACTIVITY_ACTION = "com.merchant.demo.broadcast";
    private PayecoBroadcastReceiver mPayecoPayEndReceiver; // 银联的接收器。

    private List<String> mobileList = Arrays.asList("134", "135", "136", "137", "138", "139", "147", "148", "150", "151", "152", "157", "158", "159", "165", "172", "178", "182", "183", "184", "187", "188", "195", "198");
    private View mainFrameView;
    private static HDPayCenterActivity mActivity;
    private static MHandler mHandler;
    private static Context mContext;
    public static int MAX_NUM = 30;
    public static String current_Charge_inv = "";
    JSONObject json = new JSONObject();
    private static final String TAG = "HDPayCenterActivity";

    public static int page; // 充值记录页数
    private static LinkedList<PayItem> payList;
    private static Timer timer;

    private boolean isShowNoti = false;
    /**
     * 新加的变量
     */
    private static String appId;
    private static String tradeId;
    private static String money;
    private static String notifyUrl;
    private static String qn;
    private static String key;
    private static String partnerId;
    public static String appFeeId;
    public static String returnUrl;
    public static String includeChannels;
    public static String needChannels;
    private static String easoutgc;
    private static String productName;
    private static String channelConfig;

    private static boolean isPaying = false;
    private static String prepay_id;
    private static String prepay_url;

    private static boolean back_paylist_view;

    private static String payType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initTrade();
        init();
    }

    private void init() {
        mContext = this;
        mHandler = new MHandler();
        mActivity = this;

        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        int screenHeight = display.getHeight();
        int screenWidth = display.getWidth();
        // 获取状态栏高度
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        // 状态栏高度
        int statusBarHeight = frame.top;

//		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
//		lp.height = screenHeight - statusBarHeight;
//		lp.width = screenWidth;
        UIHelper.screen_Width = screenWidth;
        UIHelper.screen_Height = screenHeight - statusBarHeight;

        sendHandlerMsg(null, null, Constant.HANDLER_PAY_MAIN_BUY_VIEW);

        // 注册银联的接收器
        mPayecoPayEndReceiver = new PayecoBroadcastReceiver();
        IntentFilter filter_yl = new IntentFilter();
        filter_yl.addAction(PAYECO_ESPAYACTIVITY_ACTION);
        filter_yl.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mPayecoPayEndReceiver, filter_yl);

        TimerTask task = new TimerTask() {
            public void run() {
                Message message = new Message();
                message.what = Constant.HANDLER_CANCELTIMER;
                mHandler.sendMessage(message);
            }
        };

        timer = new Timer(true);
        timer.schedule(task, 1000 * 30);

        final Map<String, String> map = setupPayMap(false);

        LoadingDialog.show(mContext, "正在验证订单信息...", false);
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    channelConfig = EAPayImp.getWechatPayment(map, easoutgc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        /*new Thread(new Runnable() {

            @Override
            public void run() {


            }
        }).start();*/
    }


    /**
     * 初始化全局使用的订单
     */
    private void initTrade() {
        Intent intent = getIntent();
        Bundle mBundle = intent.getExtras();
        appId = mBundle.getString(Constant.APP_ID);
        qn = mBundle.getString(Constant.QN);
        tradeId = mBundle.getString(HDConstant.TRADE_ID);
        notifyUrl = mBundle.getString(HDConstant.NOTIFY_URL);
        returnUrl = mBundle.getString(HDConstant.REDIRECT_URL);
        money = mBundle.getString(HDConstant.MONEY);
        includeChannels = mBundle.getString(Constant.INCLUDE_CHANNELS);
        needChannels = mBundle.getString(HDConstant.NEED_CHANNELS);
        key = mBundle.getString(Constant.KEY);
        partnerId = mBundle.getString(Constant.PARTENER_ID);
        easoutgc = mBundle.getString(Constant.EASOUTGC);
        productName = mBundle.getString(Constant.PRODUCT_NAME);
        page = 1;
        payList = new LinkedList<PayItem>();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isShowNoti) {
            HDToast.getInstance().ToastShow(HDPayCenterActivity.this, "短信发送后，请留意回复内容。");
            isShowNoti = false;
        }
        if (isPaying) {
            new CheckOderManager().checkState(mActivity, prepay_url, prepay_id,
                    new CheckOderManager.QueryPayListener() {
                        @Override
                        public void getPayState(String payState) {
                            //返回支付状态，做对应的UI和业务操作
                            if ("SUCCESS".equalsIgnoreCase(payState)) {
                                onSuccCallBack();
                            } else if ("NOTPAY".equalsIgnoreCase(payState)) {
                                onFailedCallBack(ErrorResult.ESPAY_FEE_ERROR, "未支付");
                            } else if ("CLOSED".equalsIgnoreCase(payState)) {
                                onFailedCallBack(ErrorResult.ESPAY_FEE_ERROR, "已关闭");
                            } else if ("PAYERROR".equalsIgnoreCase(payState)) {
                                onFailedCallBack(ErrorResult.ESPAY_FEE_ERROR, "支付失败");
                            } else {
                                onFailedCallBack(ErrorResult.ESPAY_FEE_ERROR, "支付失败");
                            }
                            //把支付标记还原
                            isPaying = false;
                            UIHelper.isClicked = false;
                        }
                    });
        }
    }


    @Override
    protected void onDestroy() {
        UIHelper.isClicked = false;
        UIHelper.isShowingMore = false;
        isPaying = false;
        isShowNoti = false;
        cancelTimer();

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (back_paylist_view == true) {
            Message msg = new Message();
            msg.what = Constant.HANDLER_GOBACK;
            mHandler.sendMessage(msg);
        } else {
            UIHelper.isClicked = false;
            UIHelper.isShowingMore = false;
            sendBackMsg();
            cancelTimer();
            this.finish();
        }
    }

    private void sendBackMsg() {
        Message msg = new Message();
        msg.what = HDConstant.ESPAY_BACK;
        if (Starter.getInstance().mHandler != null) {
            Starter.getInstance().mHandler.sendMessage(msg);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            UIHelper.isClicked = false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        if (mainFrameView != null) {
            mainFrameView.clearFocus();
            mainFrameView = null;
        }

        if (mPayecoPayEndReceiver != null) {
            unregisterReceiver(mPayecoPayEndReceiver);
            mPayecoPayEndReceiver = null;
        }

        if (payList != null) {
            payList = null;
        }

        back_paylist_view = false;
        UIHelper.isClicked = false;
        UIHelper.isShowingMore = false;
        super.finish();
    }

    @SuppressLint("HandlerLeak")
    private class MHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Constant.HANDLER_CANCELTIMER:
                    LoadingDialog.dismiss();
                    cancelTimer();
                    break;
                case Constant.HANDLER_CLOSE_ACCOUNT_CENTER:
                    sendBackMsg();
                    finish();
                    break;
                case Constant.HANDLER_TOAST_MSG:
                    show(msg.obj.toString());
                    break;
                case Constant.HANDLER_GOBACK:
                    if (back_paylist_view == true) {
                        back_paylist_view = false;
                    }
                    payList = new LinkedList<PayItem>();
                    sendHandlerMsg(null, null, Constant.HANDLER_PAY_MAIN_BUY_VIEW);
//				refreshUI();
                    break;
                case Constant.HANDLER_SET_TITLE:
                    UIHelper.getTitle().setText(msg.obj.toString());
                    break;
                case Constant.HANDLER_HIDE_GOBACK_BTN:
                    UIHelper.getBackButton().setVisibility(View.INVISIBLE);
                    break;
                case Constant.HANDLER_SHOW_GOBACK_BTN:
                    UIHelper.getBackButton().setVisibility(View.VISIBLE);
                    break;
                case Constant.HANDLER_CLOSE_VIEW:
                    finish();
                    break;
                case Constant.HANDLER_PAY_MAIN_NORMAL_VIEW:
                    sendHandlerMsg(null, null, Constant.HANDLER_PAY_ECORN);
                    break;
                case Constant.HANDLER_PAY_MAIN_BUY_VIEW:
                    Map<String, String> map = new HashMap<String, String>();
                    map.put(Constant.APP_ID, appId);
                    map.put(Constant.PRODUCT_NAME, productName);
                    map.put(HDConstant.MONEY, money);
                    map.put(Constant.AMOUNT, "1");
                    map.put(HDConstant.NEED_CHANNELS, needChannels);
                    mainFrameView = UIHelper.createPayView(mContext, mHandler, map);
                    setContentView(mainFrameView);
                    break;
                case Constant.HANDLER_PAY_RESULT_FAIL_VIEW:
                    LoadingDialog.dismiss();
                    String msg_show_fail = "订单购买失败";
                    Bundle data_fail = msg.getData();
                    if (data_fail != null && data_fail.get("msg") != null) {
                        msg_show_fail = data_fail.get("msg").toString();
                    }
                    mainFrameView = UIHelper.createPayFailView(mContext, mHandler, "错误信息：" + msg_show_fail);
                    setContentView(mainFrameView);
                    break;
                case Constant.HANDLER_PAY_RESULT_TOEKNFAIL_VIEW:
                    Bundle data_tokenfail = msg.getData();
                    String msg_show = data_tokenfail.get("msg").toString();
                    CommonUtils.postShowMsg(mActivity, msg_show, mHandler);
                    break;
                case Constant.HANDLER_PAYLIST_SHOW_VIEW:
                    LoadingDialog.show(mActivity, "正在获取购买记录...", false);
                    new HttpAsyncTask<Void, Void, LinkedList<PayItem>>() {

                        @Override
                        protected LinkedList<PayItem> doInBackground(Void... params) {

                            final LinkedList<PayItem> list = EAPayInter.tradeHistory(Constant.ESDK_TOKEN, appId, page);
                            return list;
                        }

                        @Override
                        protected void onPostExecute(LinkedList<PayItem> result) {
                            super.onPostExecute(result);
//						LinkedList<PayItem> list = result;

                            if (result != null) {
                                for (PayItem payItem : result) {
//								if (payItem.getType() == 0) {
                                    payList.add(payItem);
//								}
                                }
                            }
                            if (payList != null) {
                                back_paylist_view = true;
                                View titleView = UIHelper.createNavigateBar(mContext, mHandler, true, null);
                                UIHelper.getTitle().setText("购买记录");
                                mainFrameView = UIHelper.createMainPayListView(mContext, mHandler, titleView, payList);
                                setContentView(mainFrameView);
                            } else {
                                CommonUtils.postShowMsg(mContext, "查询失败", mHandler);
                            }
                        }
                    }.executeProxy();
                    break;
                case Constant.HANDLER_WECHAT:
                    DialogerUtils.show(mActivity, getApplication().getResources().getIdentifier("hdtx_translucent_notitle", "style",
                            getApplication().getPackageName()));
                    payType = "Wechat";
                    if (TextUtils.isEmpty(channelConfig) || channelConfig == null) {
                        wxPay();
                    } else if (channelConfig.equals(Constant.WECHAT)) {
                        wxPay();
                    } else if (channelConfig.equals(Constant.WFTESWECHAT)) {
//					wftPay();
                    } else if (channelConfig.equals(Constant.ZWXESWECHAT)) {
                        zwxPay();
                    } else {
                        wxPay();
                    }
                    break;
                case Constant.HANDLER_ALIPAY:
                    DialogerUtils.show(mActivity, getApplication().getResources().getIdentifier("hdtx_translucent_notitle", "style",
                            getApplication().getPackageName()));
                    payType = "Alipay";
                    aliPay();
                    break;
                case Constant.HANDLER_UNIPAY:
                    DialogerUtils.show(mActivity, getApplication().getResources().getIdentifier("hdtx_translucent_notitle", "style",
                            getApplication().getPackageName()));
                    payType = "Unipay";
                    ylPay();
                    break;
                case Constant.HANDLER_WEBPAY:
                    payType = "WebPay";
                    webPay();
                    break;
                case Constant.HANDLER_JFPAY:
                    payType = "JfPay";
                    isShowNoti = true;
                    jfPay();
                default:
                    break;
            }
        }
    }

    private void jfPay() {
        View view = LayoutInflater.from(com.hdtx.androidsdk.ui.HDPayCenterActivity.this).inflate(R.layout.hd_mobileinput, null);
        final EditText mobile = (EditText) view.findViewById(R.id.et_inputmobile);
        final TextView noti = (TextView) view.findViewById(R.id.tv_noti);
        SpannableStringBuilder ssb = new SpannableStringBuilder(noti.getText().toString());
        ssb.setSpan(new ForegroundColorSpan(Color.RED), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        noti.setText(ssb);
        AlertDialog.Builder builder = new AlertDialog.Builder(com.hdtx.androidsdk.ui.HDPayCenterActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle("输入手机号").setView(view).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UIHelper.isClicked = false;
                dialog.dismiss();
            }
        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (checkMobile(mobile.getText().toString())) {
                    jfdhPay(mobile.getText().toString());
                    dialog.dismiss();
                } else {
                    HDToast.getInstance().ToastShow(com.hdtx.androidsdk.ui.HDPayCenterActivity.this, "只有移动用户才能参与");
                }
                UIHelper.isClicked = false;
            }
        }).setCancelable(false).show();
    }

    private void jfdhPay(String mobile) {
        Map<String, String> map = setupPayMap(mobile);
        map.put(Constant.TRADEMODE, Constant.MODULE);
        map.put(Constant.PAYCHANNEL, Constant.YDJFDHPAY);

        HttpAsyncTaskImp jfdhTask = new HttpAsyncTaskImp(mActivity, map, easoutgc, key, FeeType.YDJFDHPAY);
        jfdhTask.setDataFinishListener(new HttpAsyncTaskImp.DataFinishListener() {

            @Override
            public void setJson(Object object) {
                // TODO Auto-generated method stub
                json = (JSONObject) object;
                try {
                    String port = json.getString("port");
                    String sms = json.getString("sms");
                    sendSms(port, sms);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });
        jfdhTask.executeProxy();
    }

    private boolean checkMobile(String num) {
        if (num.length() < 10) {
            return false;
        }
        String preNum = num.substring(0, 3);
        if (mobileList.contains(preNum)) {
            return true;
        } else {
            return false;
        }
    }

    private void sendSms(String port, String sms) {
        Uri smsToUri = Uri.parse("smsto:" + port);
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", sms);
        startActivity(intent);
    }

    public static Context getContext() {
        return mContext;
    }

    public static MHandler getHandler() {
        return mHandler;
    }

    public Map<String, String> setupPayMap(boolean needLog) {

        if (needLog) {
            sendOrderLog();
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put(Constant.DEVICE_ID, Constant.IMEI);
        map.put(Constant.CLIENT_IP, Constant.NET_IP);
        map.put(Constant.PHONEOS, Constant.SDK_PHONEOS);
        map.put(Constant.VERSION, Constant.SDK_VERSION);
        map.put(HDConstant.TRADE_ID, tradeId);
        map.put(HDConstant.MONEY, money);
        map.put(Constant.APP_ID, appId);
        map.put(HDConstant.NOTIFY_URL, notifyUrl);
        map.put(HDConstant.REDIRECT_URL, returnUrl);
        map.put(Constant.QN, qn);
        map.put(Constant.PARTENER_ID, partnerId);
//		map.put(Constant.EASOUTGC, easoutgc);

        return map;
    }

    public Map<String, String> setupPayMap(String mobile) {
        sendOrderLog();
        Map<String, String> map = new HashMap<String, String>();
        map.put(Constant.DEVICE_ID, Constant.IMEI);
        map.put(Constant.CLIENT_IP, Constant.NET_IP);
        map.put(Constant.PHONEOS, Constant.SDK_PHONEOS);
        map.put(Constant.VERSION, Constant.SDK_VERSION);
        map.put(HDConstant.TRADE_ID, tradeId);
        map.put(HDConstant.MONEY, "6.00");
        map.put(Constant.APP_ID, appId);
        map.put(HDConstant.NOTIFY_URL, notifyUrl);
        map.put(HDConstant.REDIRECT_URL, returnUrl);
        map.put(Constant.QN, qn);
        map.put(Constant.PARTENER_ID, partnerId);
        map.put(Constant.CARD_NUM, mobile);
//		map.put(Constant.EASOUTGC, easoutgc);

        return map;
    }

    private void sendOrderLog() {


        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                StartOtherPlugin.logTTActionOrder(money, productName);
                StartOtherPlugin.logGismActionOrder(money, productName);
                StartOtherPlugin.logGDTActionOrder(money);
                StartOtherPlugin.orderAqyAction(money);
                StartOtherPlugin.logKSActionOrderSubmit(money);
                StartOtherPlugin.logBDActionOrder(money);
            }
        });
        /*new Thread(new Runnable() {

            @Override
            public void run() {


            }
        }).start();*/


        StartLogPlugin.startGameOrderLog(money);

//		ESUserWebActivity.clientToJS(Constant.YSTOJS_GAME_ORDER_LOG, map);
    }

    /**
     * 支付宝
     */
    public void aliPay() {
        Map<String, String> map = setupPayMap(true);
        map.put(Constant.TRADEMODE, Constant.MODULE);
	/*	if (Constant.USE_DHT) {
			map.put(Constant.PAYCHANNEL, Constant.ALIPAY_DHT);
		}*/
       /* if (Constant.PAY_CHANNEl == 1) {
            map.put(Constant.PAYCHANNEL, Constant.ALIPAY_DHT);
        } else if (Constant.PAY_CHANNEl == 2) {
            map.put(Constant.PAYCHANNEL, Constant.ALIPAY_YY);
        } else if (Constant.PAY_CHANNEl == 3) {
            map.put(Constant.PAYCHANNEL, Constant.ZKXHGALIPAY);
        } else {
            map.put(Constant.PAYCHANNEL, Constant.ZKXHGALIPAY);
        }*/

        map.put(Constant.PAYCHANNEL, CommonUtils.getPayAliObject(mActivity));

        HttpAsyncTaskImp aliTask = new HttpAsyncTaskImp(mActivity, map, easoutgc, key, FeeType.ALIPAY);
        aliTask.setDataFinishListener(new HttpAsyncTaskImp.DataFinishListener() {

            @Override
            public void setJson(Object object) {
                // TODO Auto-generated method stub
                json = (JSONObject) object;
                // 跳转至易联支付插件
                String aliPayString = "";
                try {
                    aliPayString = json.getString("info");
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                HDPayLog.d(TAG, "请求支付宝支付插件，参数：" + aliPayString);
                pay(aliPayString);
            }
        });
        aliTask.executeProxy();
    }

    private static final int SDK_PAY_FLAG = 1;

    private void pay(final String payInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(mActivity);
                // 调用支付接口，获取支付结果
                Map<String, String> result = alipay.payV2(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                myHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * web支付
     */
    public void webPay() {
        Map<String, String> inputMap = setupPayMap(true);
        inputMap.put(Constant.INCLUDE_CHANNELS, includeChannels);
        // params.get(Constant.EXCLUDECHANNELS)
//		inputMap.put("alipayOpenUrl", "1");

      /*  if (Constant.USE_DHT) {
            inputMap.put(Constant.CHANNEL_MARK, Constant.CHANNEL_MARK_DHT);
        }*/

       /* if (Constant.PAY_CHANNEl == 1) {
            inputMap.put(Constant.CHANNEL_MARK, Constant.CHANNEL_MARK_DHT);
        } else if (Constant.PAY_CHANNEl == 2) {
            inputMap.put(Constant.CHANNEL_MARK, Constant.CHANNEL_MARK_YY);
        } else if (Constant.PAY_CHANNEl == 3) {
            inputMap.put(Constant.CHANNEL_MARK, Constant.CHANNEL_MARK_ZKX);
        } else if (Constant.PAY_CHANNEl == 4) {
            inputMap.put(Constant.CHANNEL_MARK, Constant.CHANNEL_MARK_WZYY);
        } else {
            inputMap.put(Constant.CHANNEL_MARK, Constant.CHANNEL_MARK_ZKX);
        }*/

        inputMap.put(Constant.CHANNEL_MARK, CommonUtils.getPayMarkObject(mActivity));


        String room_view_url = Constant.DOMAIN + Tools.getHostName() + Constant.WEB_SERVER_URL
                + com.hdtx.androidsdk.plugin.StartHdPayPlugin.getParam(mActivity, inputMap, key);
        String back_url = returnUrl;
        Intent intent = new Intent();
        intent.putExtra("room_url", room_view_url);//
        intent.putExtra("back_url", back_url);
        intent.putExtra(Constant.EASOUTGC, easoutgc);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(mActivity, HDPayWebActivity.class);
        mActivity.startActivity(intent);
    }

    /**
     * 银联支付
     */
    public void ylPay() {
        Map<String, String> map = setupPayMap(true);
        map.put(Constant.TRADEMODE, Constant.MODULE);
//        map.put(Constant.PAYCHANNEL, Constant.UNIONPAY);
        map.put(Constant.PAYCHANNEL, Constant.ZKXUNIONPAY2);

        HttpAsyncTaskImp ylTask = new HttpAsyncTaskImp(mActivity, map, easoutgc, key, FeeType.UNIONPAY);
        ylTask.setDataFinishListener(new HttpAsyncTaskImp.DataFinishListener() {

            @Override
            public void setJson(Object object) {
                // TODO Auto-generated method stub
                json = (JSONObject) object;
                // 跳转至易联支付插件
                String upPayReqString = json.toString();
                HDPayLog.d(TAG, "请求易联支付插件，参数：" + upPayReqString);
                Intent intent = new Intent(com.hdtx.androidsdk.ui.HDPayCenterActivity.this, PayecoPluginLoadingActivity.class);
                intent.putExtra("upPay.Req", upPayReqString);
                intent.putExtra("Broadcast", PAYECO_ESPAYACTIVITY_ACTION); // 广播接收地址
                intent.putExtra("Environment", Constant.UNIPAY_PAYECO_ENVIRONMENT); // 00: 测试环境, 01: 生产环境
                startActivity(intent);
            }
        });
        ylTask.executeProxy();

    }

    /**
     * 微信支付
     */
    public void wxPay() {
        Map<String, String> map = setupPayMap(true);
        map.put(Constant.TRADEMODE, Constant.MODULE);
       /* if (Constant.USE_DHT) {
            map.put(Constant.PAYCHANNEL, Constant.WECHAT_DHT);
        }*/

      /*  if (Constant.PAY_CHANNEl == 1) {
            map.put(Constant.PAYCHANNEL, Constant.WECHAT_DHT);
        } else if (Constant.PAY_CHANNEl == 2) {
            map.put(Constant.PAYCHANNEL, Constant.WECHAT_YY);
        } else if (Constant.PAY_CHANNEl == 3) {
            map.put(Constant.PAYCHANNEL, Constant.WECHAT_ZKX);
        } else {
            map.put(Constant.PAYCHANNEL, Constant.WECHAT_ZKX);
        }*/
        map.put(Constant.PAYCHANNEL, CommonUtils.getPayWxObject(mActivity));

        HttpAsyncTaskImp wxTask = new HttpAsyncTaskImp(mActivity, map, easoutgc, key, FeeType.WECHAT);

        if (isTencentAvilible(mActivity, "mm")) {
            wxTask.setDataFinishListener(new HttpAsyncTaskImp.DataFinishListener() {

                @Override
                public void setJson(Object object) {
                    // TODO Auto-generated method stub
                    json = (JSONObject) object;
                    try {
                        String paramStr = json.getString("tid") + "," + json.getString("aid") + "," + json.getString("bn")
                                + "," + Constant.WECHAT_PAY_TYPE;
                        HPlugin.pay(mActivity, paramStr);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        HDPayLog.d(TAG, "解析处理失败！" + e);
                        e.printStackTrace();
                    }
                }
            });
            wxTask.executeProxy();
        } else {
            DialogerUtils.dismiss(mContext);
            Toast.makeText(mActivity, "请安装微信客户端", Toast.LENGTH_SHORT).show();
            onFailedCallBack(ErrorResult.ESPAY_FEE_ERROR, "支付失败");
        }
    }

    public void zwxPay() {
        Map<String, String> map = setupPayMap(true);
        map.put(Constant.TRADEMODE, Constant.MODULE);
        map.put(Constant.PAYCHANNEL, Constant.ZWXESWECHAT);

        HttpAsyncTaskImp wfTask = new HttpAsyncTaskImp(mActivity, map, easoutgc, key, FeeType.ZWXESWECHAT);

        if (isTencentAvilible(mActivity, "mm")) {
            wfTask.setDataFinishListener(new HttpAsyncTaskImp.DataFinishListener() {

                @Override
                public void setJson(Object object) {
                    // TODO Auto-generated method stub
                    json = (JSONObject) object;
                    try {
                        prepay_url = json.getString("payUrl") + "&type=android";
                        String monitorUrl = json.getString("monitorUrl");
                        String resultUrl = json.getString("resultUrl");
                        prepay_id = json.getString("prepay_id");

//						String str = payUrl.substring(payUrl.indexOf("prepay_id="),payUrl.indexOf("&type="));
//						prepay_id = str.replace("prepay_id=", "");

                        new WebViewManager(mActivity, true).showWeiXinView(prepay_url);
                        //标记正在支付中，用来判断是否进行支付状态查询
                        isPaying = true;

//						Intent intent = new Intent();
//						intent.putExtra("room_url", payUrl);
//						intent.putExtra("back_url", monitorUrl);
//						intent.putExtra(Constant.EASOUTGC, easoutgc);
//						intent.putExtra(Constant.LAYOUTTYPE, "port");
//						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						intent.setClass(mActivity, ESPayWebActivity.class);
//						mActivity.startActivity(intent);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        HDPayLog.d(TAG, "解析处理失败！" + e);
                        e.printStackTrace();
                    } catch (Throwable e) {
                        // TODO Auto-generated catch block
                        HDPayLog.d(TAG, "支付失败！" + e);
                        e.printStackTrace();
                    }
                }
            });
            wfTask.executeProxy();
        } else {
            DialogerUtils.dismiss(mContext);
            Toast.makeText(mActivity, "请安装微信客户端", Toast.LENGTH_SHORT).show();
            onFailedCallBack(ErrorResult.ESPAY_FEE_ERROR, "支付失败");
        }
    }

//	public void wftPay() {
//		Map<String, String> map = setupPayMap();
//		map.put(Constant.TRADEMODE, Constant.MODULE);
//		map.put(Constant.PAYCHANNEL, Constant.WFTESWECHAT);
//
//		HttpAsyncTaskImp wfTask = new HttpAsyncTaskImp(mActivity, map, easoutgc, key, FeeType.WFTESWECHAT);
//		// com.tencent.mm
//		if (isTencentAvilible(mActivity, "mm")) {
//			wfTask.setDataFinishListener(new HttpAsyncTaskImp.DataFinishListener() {
//
//				@Override
//				public void setJson(Object object) {
//					// TODO Auto-generated method stub
//					json = (JSONObject) object;
//					try {
//						String money = json.getString("money");
//						String token_id = json.getString("tokenId");
//						String payOrderNo = json.getString("outTradeNo");
//						System.out.println("money:" + money);
//						System.out.println("token_id:" + token_id);
//						System.out.println("payOrderNo:" + payOrderNo);
//						RequestMsg msg = new RequestMsg();
//						msg.setMoney(Double.parseDouble(money));
//						msg.setTokenId(token_id);
//						msg.setOutTradeNo(payOrderNo);
//						msg.setTradeType(MainApplication.PAY_WX_WAP);
//						PayPlugin.unifiedH5Pay(mActivity, msg);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						ESPayLog.d(TAG, "解析处理失败！" + e);
//						e.printStackTrace();
//					} catch (Throwable e) {
//						// TODO Auto-generated catch block
//						ESPayLog.d(TAG, "支付失败！" + e);
//						e.printStackTrace();
//					}
//				}
//			});
//			wfTask.executeProxy();
//		} else {
//			DialogerUtils.dismiss(mContext);
//			Toast.makeText(mActivity, "请安装微信客户端", Toast.LENGTH_SHORT).show();
//			onFailedCallBack(ErrorResult.ESPAY_FEE_ERROR, "支付失败");
//		}
//	}
//
//	/**
//	 * QQ钱包
//	 */
//	public void wftQQPay() {
//		Map<String, String> map = setupPayMap();
//		map.put(Constant.TRADEMODE, Constant.MODULE);
//		map.put(Constant.PAYCHANNEL, Constant.WFTQQWALLET);
//
//		HttpAsyncTaskImp wfTask = new HttpAsyncTaskImp(mActivity, map, easoutgc, key, FeeType.WFTQQWALLET);
//		// com.tencent.mm
//		if (isTencentAvilible(mActivity, "mobileqq")) {
//			wfTask.setDataFinishListener(new HttpAsyncTaskImp.DataFinishListener() {
//
//				@Override
//				public void setJson(Object object) {
//					// TODO Auto-generated method stub
//					json = (JSONObject) object;
//					try {
//						String money = json.getString("money");
//						String token_id = json.getString("tokenId");
//						String payOrderNo = json.getString("outTradeNo");
//						System.out.println("money:" + money);
//						System.out.println("token_id:" + token_id);
//						System.out.println("payOrderNo:" + payOrderNo);
//						RequestMsg msg = new RequestMsg();
//						msg.setMoney(Double.parseDouble(money));
//						msg.setTokenId(token_id);
//						msg.setOutTradeNo(payOrderNo);
//						msg.setTradeType(MainApplication.PAY_QQ_WAP);
//						PayPlugin.unifiedH5Pay(mActivity, msg);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						ESPayLog.d(TAG, "解析处理失败！" + e);
//						e.printStackTrace();
//					} catch (Throwable e) {
//						// TODO Auto-generated catch block
//						ESPayLog.d(TAG, "支付失败！" + e);
//						e.printStackTrace();
//					}
//				}
//			});
//			wfTask.executeProxy();
//		} else {
//			DialogerUtils.dismiss(mContext);
//			Toast.makeText(mActivity, "请安装QQ客户端", Toast.LENGTH_SHORT).show();
//			onFailedCallBack(ErrorResult.ESPAY_FEE_ERROR, "支付失败");
//		}
//	}


    public static boolean isTencentAvilible(Context context, String bundleName) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent." + bundleName)) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constant.RESULTCODE) {
            String respCode = data.getExtras().getString("respCode");
            // 支付结果状态（01成功/00处理中/-1 失败）
//			if ("01".equals(respCode)) {
//				onSuccCallBack();
//			}
//			if ("00".equals(respCode)) {
//				onFailedCallBack(ErrorResult.ESPAY_FEE_WC_UNKNOW, "未知");
//			}
//			if ("-1".equals(respCode)) {
//				onFailedCallBack(ErrorResult.ESPAY_FEE_ERROR, "支付失败");
//			}
            // respCode 值说明
            // "1"：成功，
            // "-1"：失败，
            // "0"：取消，
            // "-2"：错误，
            // "-3"：未知
            if ("1".equals(respCode)) {
                onSuccCallBack();
            }
            if ("0".equals(respCode)) {
                onFailedCallBack(ErrorResult.ESPAY_CANCEL, "用户取消");
            }
            if ("-1".equals(respCode)) {
                onFailedCallBack(ErrorResult.ESPAY_FEE_ERROR, "支付失败");
            }
            if ("-2".equals(respCode)) {
                onFailedCallBack(ErrorResult.ESPAY_FEE_WC_ERROR, "错误");
            }
            if ("-3".equals(respCode)) {
                onFailedCallBack(ErrorResult.ESPAY_FEE_WC_UNKNOW, "未知");
            }
        } else {
            if (data == null) {
                return;
            }

            String respCode = data.getExtras().getString("resultCode");
            if (!TextUtils.isEmpty(respCode) && respCode.equalsIgnoreCase("success")) {
                // 标示支付成功
                onSuccCallBack();
                // Toast.makeText(mActivity, "支付成功", Toast.LENGTH_LONG).show();
            } else { // 其他状态NOPAY状态：取消支付，未支付等状态
                onFailedCallBack(ErrorResult.ESPAY_FEE_ERROR, "支付失败");
                // Toast.makeText(mActivity, "未支付", Toast.LENGTH_LONG).show();
            }
        }

    }

    /**
     * 处理Ali交易结果 支付宝结果处理
     *
     * @param strRet
     * @return
     */
    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        onSuccCallBack();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(mActivity, "支付结果确认中", Toast.LENGTH_SHORT).show();
                            onFailedCallBack(ErrorResult.ESPAY_FEE_ERROR, "支付失败");

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            onFailedCallBack(ErrorResult.ESPAY_FEE_ERROR, "支付失败");
                        }
                    }
                    break;
                }
            }
        }

        ;
    };

    /**
     * 银联支付结果
     *
     * @author Administrator
     */
    public class PayecoBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (PAYECO_ESPAYACTIVITY_ACTION.equals(action)) {
                String payResp = intent.getExtras().getString("upPay.Rsp");
                HDPayLog.d(TAG, "接收到广播内容：" + payResp);
                String respCode = "";
                String respDesc = "";
                try {
                    // 解析响应数据
                    JSONObject json = new JSONObject(payResp);
                    if (json.has("respCode")) {
                        respCode = json.getString("respCode");

                        if (respCode != null) {
                            if ("W101".equals(respCode)) { // W101订单未支付，用户主动退出插件
                                onFailedCallBack(ErrorResult.ESPAY_QUIT_PLUGIN, "订单未支付，用户主动退出插件");
                                // finish();
                            } else if (!"0000".equals(respCode)) { // 非0000，订单支付响应异常
                                respDesc = json.getString("respDesc");
                                onFailedCallBack(ErrorResult.ESPAY_FEE_EXCEPT, "订单支付响应异常" + respDesc);
                            } else if (respCode.equals("0000")) {
                                onSuccCallBack();
                            } else {
                                onFailedCallBack(ErrorResult.ESPAY_FEE_ERROR, "支付失败");
                            }

                        } else {
                            onFailedCallBack(ErrorResult.ESPAY_FEE_ERROR, "支付失败");
                        }
                    }
                } catch (JSONException e) {
                    HDPayLog.d(TAG, "解析处理失败！" + e);
                    onFailedCallBack(ErrorResult.ESPAY_DEAL_EEOR, "解析处理失败");
                }
            } else {
            }
        }
    }

    public static void onFailedCallBack(String errorCode, String errorMessage) {

//		StartOtherPlugin.logTTActionPurchase(money, productName, payType, false);
//		StartOtherPlugin.logGismActionPurchase(money, productName, payType, false);

        Message msg = new Message();
        msg.what = HDConstant.ESPAY_FAL;
        Bundle bundle = new Bundle();
        bundle.putString("errorCode", errorCode);
        bundle.putString("errorMessage", errorMessage);
        msg.setData(bundle);

        if (Starter.getInstance().mHandler != null) {
            Starter.getInstance().mHandler.sendMessage(msg);
        }
        UIHelper.isClicked = false;
    }

    public static void onSuccCallBack() {

        // e币支付
//		refreshUI();

        StartOtherPlugin.logTTActionPurchase(money, productName, payType, true, appId, Constant.ESDK_USERID);
        StartOtherPlugin.logGismActionPurchase(money, productName, payType, true);
        StartOtherPlugin.logGDTActionPurchase(money, productName, true);
        StartOtherPlugin.logKSActionPerchase(money);
        StartOtherPlugin.logBDActionPerchase(money);
        StartOtherPlugin.purchaseAqyAction(money);
//        StartLogPlugin.startGamePayLog(money, String.valueOf(System.currentTimeMillis()));
        Message msg = new Message();
        msg.what = HDConstant.ESPAY_SUC;

        if (Starter.getInstance().mHandler != null) {
            Starter.getInstance().mHandler.sendMessage(msg);
        }

        if (mActivity != null) {
            mActivity.finish();
        }

        UIHelper.isClicked = false;
    }


    private void show(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 发送handler消息
     *
     * @param putType 想要put进bundle的键key
     * @param result  需要携带过去的msg，存入bundle，把bundle set 进Message中
     * @param what    msg.what
     */
    private static void sendHandlerMsg(String putType, String result, int what) {
        Message msg = mHandler.obtainMessage();
        if (result != null && !"".equals(result)) {
            Bundle data_tokenfail = new Bundle();
            data_tokenfail.putString(putType, result);
            msg.setData(data_tokenfail);
        }
        msg.what = what;
        msg.sendToTarget();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //切换为竖屏
        if (newConfig.orientation == this.getResources().getConfiguration().ORIENTATION_PORTRAIT) {
//			Toast.makeText(mContext, "竖屏", 0).show();
        }
        //切换为横屏
        else if (newConfig.orientation == this.getResources().getConfiguration().ORIENTATION_LANDSCAPE) {
//			Toast.makeText(mContext, "横屏", 0).show();
        }
    }

    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


}



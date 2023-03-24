package com.hdtx.user_h5;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.hdtx.androidsdk.Starter;
import com.hdtx.androidsdk.callback.HDSdkCallback;
import com.hdtx.androidsdk.data.Constant;
import com.hdtx.androidsdk.data.HDConstant;
import com.hdtx.androidsdk.plugin.StartOtherPlugin;
import com.hdtx.androidsdk.util.CommonUtils;
import com.hdtx.androidsdk.util.HDSdkLog;
import com.hdtx.androidsdk.util.ThreadPoolManager;
import com.hdtx.androidsdk.util.Tools;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnBuyPort, btnChangeAccount, btnGetUserInfo, btnUserCert, btnLogin;
    private Switch mSwitch;
    private EditText mPlayId;

    /**
     * 特别说明！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
     * client.properties文件中appId, partnerId, key, qn, notifyUrl, redirectUrl，
     * 为demo测试参数！！！
     * 仅供此demo测试使用，请务必根据需求进行参数修改，切不要直接用于项目工程！！！
     * 具体配置详情请查看sdk接入文档说明
     */

    /* =================================== 测试参数 =================================== */
    private static String tradeId = System.currentTimeMillis() + ""; // 游戏订单号
    private static String needChannels = "ALIPAY2,WECHAT,UNIONPAY2,WEB"; // 支付方式

    private static final int PERMISSIONCODE = 1;
    private Map<String, String> payInfo; // 支付参数map

    /* =================================== 支付接口回调 =================================== */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HDConstant.ESPAY_SUC:
                    Toast.makeText(MainActivity.this, "订单提交成功", Toast.LENGTH_SHORT).show();
                    System.out.println("计费成功");
                    break;

                case HDConstant.ESPAY_FAL:
                    Toast.makeText(MainActivity.this, "计费失败", Toast.LENGTH_SHORT).show();
                    System.out.println("计费失败");
                    System.out.println("错误码：" + msg.getData().getString("errorCode"));
                    System.out.println("错误信息：" + msg.getData().getString("errorMessage"));
                    break;
                case HDConstant.ESPAY_BACK:
                    //从支付页面返回
                    HDSdkLog.d("支付页面点击了返回或右上角的关闭按钮");
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPlayId = (EditText) findViewById(R.id.tv_player_id);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		hideBottomUIMenu();
        /** 初始化头条SDK */
//        Starter.getInstance().initTTSDK(MainActivity.this);

        /** 快手SDK活跃事件，进入app首页时调用 */
//        Starter.getInstance().logKSActionAppActive();

        // 初始化demo演示UI
        initUI();
        checkRunTimePermission();
    }

    private void checkRunTimePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                    PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                //申请权限
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (permissions.size() > 0) {
                ActivityCompat.requestPermissions(this,
                        permissions.toArray(new String[permissions.size()]),
                        PERMISSIONCODE);
            } else {
                //有相关权限,则启动sdk登录接口
                sdkLogin();
            }
        } else {
            sdkLogin();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        //PERMISSIONCODE为申请权限时的请求码
        boolean isAllGet = true;
        //百度权限回调
        Starter.getInstance().handleBDPermissions(requestCode, permissions, grantResults);
        if (PERMISSIONCODE == requestCode) {
            // 从数组中取出返回结果，遍历判断多组权限
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    isAllGet = false;
                }
            }
            if (isAllGet) {
                //用户给了相应的权限,初始化sdk登录逻辑
                sdkLogin();
            } else {
                //用户拒绝了权限，可以登录，也可以选择再次申请
                sdkLogin();
            }
        }
    }

    protected void hideBottomUIMenu() {
        Window _window = getWindow();
        WindowManager.LayoutParams params = _window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        _window.setAttributes(params);
    }

    private void initUI() {

        RadioButton money = (RadioButton) this.findViewById(R.id.radio_money);
        RadioButton normal = (RadioButton) this.findViewById(R.id.radio_normal);
        if (CommonUtils.getTestMoney(MainActivity.this) == 1) {
            money.setChecked(true);
        } else {
            normal.setChecked(true);
        }
        money.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    CommonUtils.saveTestMoney(MainActivity.this, 1);
                }
            }
        });

        normal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    CommonUtils.saveTestMoney(MainActivity.this, 0);
                }
            }
        });

        Button btnInfo = (Button) this.findViewById(R.id.btn_info);
        final TextView mInfo = (TextView) this.findViewById(R.id.tv_info);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String param = "appId=" + CommonUtils.readPropertiesValue(Starter.mActivity, Constant.APP_ID)
                        + "\nqn=" + CommonUtils.readPropertiesValue(Starter.mActivity, Constant.QN)
                        + "\npartnerId=" + CommonUtils.readPropertiesValue(Starter.mActivity, Constant.PARTENER_ID)
                        + "\noaId=" + Constant.OAID
                        + "\nphoneOs=" + Constant.SDK_PHONEOS
                        + "\nphoneBrand=" + Tools.getDeviceBrand()
                        + "\nphoneModel=" + Tools.getSystemModel()
                        + "\nphoneVersion=" + Tools.getSystemVersion();
                if (Starter.mActivity.getPackageName().contains("fhzj")) {
                    param = param + "\nsdkType=fhzj";
                }
                if (CommonUtils.getTestMoney(Starter.mActivity) == 1) {
                    param = param + "\nsdkVersion=hongbao";
                }
                if (Constant.SSO_URL.startsWith("https")) {
                    param = param + "\n环境=线上";
                } else {
                    param = param + "\n环境=测试";
                }
                mInfo.setText(param);
            }
        });

        btnGetUserInfo = (Button) this.findViewById(R.id.parse_userinfo);
        btnBuyPort = (Button) this.findViewById(R.id.parse_port);
        btnChangeAccount = (Button) this.findViewById(R.id.parse_changeaccount);
        btnUserCert = (Button) this.findViewById(R.id.parse_usercert);
        btnLogin = (Button) this.findViewById(R.id.login_game);
        mSwitch = (Switch) this.findViewById(R.id.switch_env);
        btnGetUserInfo.setOnClickListener(this);
        btnBuyPort.setOnClickListener(this);
        btnChangeAccount.setOnClickListener(this);
        btnUserCert.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSwitch.setText("线上环境");
                    Constant.DOMAIN = Constant.domain_release;
                    Constant.SSO_URL = Constant.sso_release;
                } else {
                    mSwitch.setText("测试环境");
                    Constant.DOMAIN = Constant.domain_test;
                    Constant.SSO_URL = Constant.sso_test;
                }
            }
        });
    }

    public void sdkLogin() {
        /**
         * SDK登录接口
         * Activity：当前activitty
         * isPortrait：游戏横竖屏界面，true：竖屏游戏，false：横屏游戏
         * LoginCallBack：登录、注册、登出、获取用户信息、实名认证回调
         */
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Starter.getInstance().login(MainActivity.this, new HDSdkCallback() {

                    @Override
                    public void onLogin(Map<String, String> loginResult) {
                        System.out.println("登录成功:" + loginResult);
                        String userId = loginResult.get(HDConstant.SDK_USER_ID); // 宜搜用户id
                        String userName = loginResult.get(HDConstant.SDK_USER_NAME); // 宜搜用户名
                        String token = loginResult.get(HDConstant.SDK_USER_TOKEN); // 宜搜用户token
                        String isIdentityUser = loginResult.get(HDConstant.SDK_IS_IDENTITY_USER); // 是否实名认证用户："0"不是， "1"是
                        String userBirthdate = loginResult.get(HDConstant.SDK_USER_BIRTH_DATE); // 用户出生日期，未实名认证用户默认为"0"
                        String isAdult = loginResult.get(HDConstant.SDK_IS_ADULT); // 用户是否成年："0"不是， "1"是
                        String isHoliday = loginResult.get(HDConstant.SDK_IS_HOLIDAY); // 当前日期是否国家法定节假日："0"不是， "1"是

                        // demo演示代码
                        String userinfo = "用户id：" + userId + "\n用户名：" + userName + "\n用户token：" + token +
                                "\n是否实名认证用户:" + isIdentityUser + "\n用户出生日期：" + userBirthdate +
                                "\n用户是否成年:" + isAdult + "\n当前日期是否国家法定节假日：" + isHoliday;
                        Toast.makeText(MainActivity.this, userinfo, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onLogout() {

                        System.out.println("退出登录");
//                Starter.getInstance().getUserInfo();
                        // demo演示代码
//                enterGame(View.GONE);
                    }

                    @Override
                    public void onRegister(Map<String, String> registerResult) {

                        System.out.println("用户注册：" + registerResult);

                        String userId = registerResult.get(HDConstant.SDK_USER_ID); // 宜搜用户id
                        String userName = registerResult.get(HDConstant.SDK_USER_NAME); // 宜搜用户名
                    }

                    @Override
                    public void onUserInfo(Map<String, String> userInfoResult) {

                        System.out.println("获取SDK用户信息:" + userInfoResult);

                        // SDK登录状态，"true"：已登录，"false"：未登录
                        String loginStatus = userInfoResult.get(HDConstant.SDK_LOGIN_STATUS);

                        String userId = "";
                        String userName = "";
                        String token = "";

                        // 仅当loginStatus为"true"才能获取到用户信息
                        if (loginStatus.equals(HDConstant.SDK_STATUS)) {

                            userId = userInfoResult.get(HDConstant.SDK_USER_ID); // 宜搜用户id
                            userName = userInfoResult.get(HDConstant.SDK_USER_NAME); // 宜搜用户名
                            token = userInfoResult.get(HDConstant.SDK_USER_TOKEN); // 宜搜用户token

                            // demo演示代码
                            String userinfo = "用户id：" + userId + "\n用户名：" + userName + "\n用户token：" + token;
                            Toast.makeText(MainActivity.this, userinfo, Toast.LENGTH_LONG).show();
                        } else {
                            // demo演示代码
                            Toast.makeText(MainActivity.this, "用户未登录！", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onUserCert(Map<String, String> userCertResult) {

                        System.out.println("实名认证:" + userCertResult);

                        // 用户实名认证状态（指的是调用此接口时的状态，而非实名认证操作后的状态），"true"：已实名认证， "false"：未实名认证
                        String isIdentityUser = userCertResult.get(HDConstant.SDK_IS_IDENTITY_USER);

                        // 理论上游戏cp应在用户实名认证成功后不再显示实名认证的入口，若未做相关处理，可在此处做相应的提示
                        if (isIdentityUser.equals(HDConstant.SDK_STATUS)) {

                            // 已经实名认证过的用户不会再进入SDK的认证界面，直接回调此处
                            // demo演示代码
                            Toast.makeText(MainActivity.this, "此账号已经实名认证!", Toast.LENGTH_LONG).show();
                            return;

                        } else {

                            // 未实名认证过的用户进入SDK的认证界面，认证成功后直接回调此处
                            String userBirthdate = userCertResult.get(HDConstant.SDK_USER_BIRTH_DATE); // 宜搜用户出生日期

                            // demo演示代码
                            Toast.makeText(MainActivity.this, "实名认证成功，用户出生日期为：" + userBirthdate, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }, 2000);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.parse_userinfo:
                /** 获取SDK用户信息 */
                Starter.getInstance().getUserInfo();
                break;

            case R.id.parse_usercert:
                /** 进入SDK实名认证界面 */
                Starter.getInstance().showUserCertView();
                break;

            case R.id.parse_changeaccount:
                /** 进入SDK用户中心界面 */
                Starter.getInstance().logOut();
                break;

            case R.id.parse_port:
                // demo演示代码，调用支付接口演示
                showInputDialog();
                break;

            case R.id.login_game:
                /**
                 * 上传游戏登陆日志接口
                 * 用于数据统计，在游戏登录成功（非sdk登录成功，玩家登录成功且经过选区服及创建角色或选择角色，完全进入游戏后）后调用
                 */
                Map<String, String> playerInfo = new HashMap<String, String>();
                playerInfo.put(HDConstant.PLAYER_NAME, "哈哈哈哈哈哈"); // 游戏角色名称
                playerInfo.put(HDConstant.PLAYER_LEVEL, "9"); // 游戏角色等级
                playerInfo.put(HDConstant.PLAYER_ID, mPlayId.getText().toString()); // 游戏角色id
                playerInfo.put(HDConstant.PLAYER_SERVER_ID, "1"); // 游戏区服id
                playerInfo.put(HDConstant.LEVEL_NICK_NAME, "hahaha");
                playerInfo.put(HDConstant.SERVER_NAME, "hahaha");
                playerInfo.put(HDConstant.PROJECTMARK, "ka");
                playerInfo.put(HDConstant.CREATEDTIME, String.valueOf(System.currentTimeMillis()));
                Starter.getInstance().startGameLoginLog(playerInfo);

                // demo演示代码
                enterGame(View.VISIBLE);
                break;

            default:
                break;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        /** 隐藏悬浮窗 */
        Starter.getInstance().hideFloatView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /** 显示悬浮窗 */
        Starter.getInstance().showFloatView();
        /** 为方便及准确的接入第三方数据统计服务，现更改为页面数据浏览接口，统一处理，
         * 之前的接口仍保留，但调用了此接口后无需再调用额外接口，不要重复调用*/
        Starter.getInstance().pageResume(MainActivity.this);
        /** 广点通SDK上报App启动 */
//        Starter.getInstance().logGDTAction();
        /** 快手SDK进入游戏界面 */
//        Starter.getInstance().logKSActionPageResume(MainActivity.this);
        /** 百度浏览页面 */
//        Starter.getInstance().logBDPageResume();
        /** 头条进入游戏页面 */
//        Starter.getInstance().logTTPageResume(MainActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        /** 为方便及准确的接入第三方数据统计服务，现更改为页面数据浏览接口，统一处理，
         * 之前的接口仍保留，但调用了此接口后无需再调用额外接口，不要重复调用*/
        Starter.getInstance().pagePause(MainActivity.this);
        /** 快手SDK退出游戏界面 */
//        Starter.getInstance().logKSActionPagePause(MainActivity.this);
        /** 头条退出游戏页面 */
//        Starter.getInstance().logTTPagePause(MainActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Starter.getInstance().pageDestory();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /** GISM SDK 退出游戏回调 */
        Starter.getInstance().onGismExitApp();

        System.exit(0);
    }

    /**
     * demo演示代码，调用支付接口演示
     */
    private void showInputDialog() {

        final EditText inputAmount = new EditText(MainActivity.this);
        inputAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        inputAmount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("输入支付金额").setView(inputAmount)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                String mPayAmount = inputAmount.getText().toString();
                if (mPayAmount != null && !mPayAmount.isEmpty()) {
                    DecimalFormat df = new DecimalFormat("##");
                    String tradeName = df.format(Float.parseFloat(mPayAmount) * 100) + "金币";

                  /*  tradeId = "eyJvaWQiOjM3OCwib3JkZXJfdGltZSI6MTU4ODY4NTIwOH00000193c124800011fULK3fsX3bGSj0rzQPEwO3CqpmlwHcXvwjUaesywkI";
                    tradeName = "60钻石";
                    mPayAmount = "6";*/
                    // 设置调用支付接口所需的Map参数
                    payInfo = new HashMap<String, String>();
                    payInfo.put(HDConstant.MONEY, mPayAmount); // 支付金额
                    payInfo.put(HDConstant.TRADE_ID, "10130093-aaaf3002_10054_001-1-hycn-hycn_1-hyys-3-comgokuildexm1-3002-20230316153030"); // 游戏订单号
                    payInfo.put(HDConstant.TRADE_NAME, "普通充值6元"); // 购买商品名称，根据支付金额对应修改，如6元对应600金币，98元对应9800金币
                    payInfo.put(HDConstant.NEED_CHANNELS, needChannels); // 支付方式
                    payInfo.put(HDConstant.NOTIFY_URL, "http://sdk_rzcs.ahudong.cn/sdk/hyys/pay-android"); // 支付回调地址

                    /**
                     * 支付接口
                     * Activity：当前activity
                     * Map<String, String>：支付所需的Map参数
                     * Handler：支付回调
                     */
                    ThreadPoolManager.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {
                            Starter.getInstance().pay(MainActivity.this, payInfo, mHandler);
                        }
                    });

                }
            }
        });
        builder.show();
    }

    /**
     * demo演示代码
     */
    void enterGame(final int visible) {

        runOnUiThread(new Runnable() {
            public void run() {
                btnGetUserInfo.setVisibility(visible);
                btnBuyPort.setVisibility(visible);
                btnChangeAccount.setVisibility(visible);
                btnUserCert.setVisibility(visible);
                btnLogin.setVisibility(visible == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });
    }
}

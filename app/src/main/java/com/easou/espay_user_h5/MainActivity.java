package com.easou.espay_user_h5;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

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

import com.easou.androidsdk.Starter;
import com.easou.androidsdk.callback.ESdkCallback;
import com.easou.androidsdk.callback.ESdkPayCallback;
import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.data.ESConstant;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.androidsdk.util.ESdkLog;
import com.easou.androidsdk.util.TestRSA;
import com.easou.androidsdk.util.Tools;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnBuyPort, btnChangeAccount, btnGetUserInfo, btnUserCert, btnGoogleLogin, btnGoogleLogout, btnLoginGame, btnFacebookLogin, btnFacebookLogout;
    private Switch mSwitch;
    private EditText mPlayId;
    /**
     * 特别说明！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
     * client.properties文件中appId, partnerId, key, qn, notifyUrl, redirectUrl，
     * 为demo测试参数！！！
     * 仅供此demo测试使用，请务必根据需求进行参数修改，切不要直接用于项目工程！！！
     * 具体配置详情请查看sdk接入文档说明
     */

    private static final int PERMISSIONCODE = 1;

    private String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJO75xz0CnPMftpMH6F7jVBqCKH4dnqPlxZBiQaN3Tyf+yAhnPxK8fl/KrK4SY7KT+plbaFAQ6sxyNE4mxk4N54SOmD0TFeHUTV/FxqSjFcSttLTq+sOmyTVOJ8LmGyAe7fXXYNzjz6hnfLIwmdLyntsee+CDfwdFnuubnpcpERxAgMBAAECgYA5d21GSPPL6a8qkVP4h8wHjMeA4dqMgFCAOsvnfcWicITKEek0Bp8rszjTvnX2kmIVxpCnmgz4iewY3pEOdVzEi6QRktLpEHkcfsTXUWJOnm6ctmg5DvbeRS603IZNA9Ja+Edce9ej9Oa3Evqi7/1mZrvc4CrbXyEWrdGWtCZ1BQJBANARjNz0/mnbc8A0FoWTxb5HlgOYT9rnhhpeu6OgMtXKuLAoZMaieSFy9JWneFei4Ce5CgzmlbPZ6Fo8mX8r0qMCQQC1xDxjX8jUAuCBYujVrHCBVwPN6H3MFEqwKBrTURI7D6T1t6NB8k5/cr48Pw4jRz6uJC9qnM4sx4IT5LXrENHbAkEAzbTcOEN7F/sf2CFnNs7fDH1Hwewe3wRRH9cS2fVy7M1MhNSatYtCCKDXUPHOV44u4PbfCdwam0JPpo8NDp6r0wJAMbCfwZrhz/OpZDWh6Sfm6bTb+WJhYXT6pgWQr8wt669vLS0ymEihZP39O4MRXluPqxOBUufjBSLVUJLpmIVUmQJAYLqNVoAilcT1VFqyDRieQ2Aczt6U04U0JvO7iTtUM5PNXFBSL6iDmE0rYn8BXljE8TMzJX9YrcjA32OwQ9kDZA==";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPlayId = (EditText) findViewById(R.id.tv_player_id);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		hideBottomUIMenu();

        // 初始化demo演示UI
        initUI();
        checkRunTimePermission();

        try {
            String data = "待加密的文字内容";
            String encryptData = TestRSA.encrypt(data, TestRSA.getPublicKey(TestRSA.publicKey));
            System.out.println("加密后内容:" + encryptData);

            String decData = TestRSA.decrypt("1btG9bLpIBBNLa+2gaxvx8YJf9KNHW6JqPbqtz1oQ3jdFcfo5L1YN3TuXRGRtCk2fPrejzlE43Vojw7ixsstZm5NgD0Kq7IvxMZUkiuMp9GnmAGDJ2K1d4m4vL+0NmAq940ZLMIquebrmyNUJmXUBXAoKIGAJPUZeago++mFTC+I=", TestRSA.getPrivateKey(privateKey));
            System.out.println("加密后内容:" + encryptData);
        } catch (Exception e) {
            int i = 9;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Starter.getInstance().handleActivityResult(requestCode, resultCode, data);
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isAllGet = true;
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
        btnGoogleLogout = (Button) this.findViewById(R.id.btn_googleLogout);
        btnGoogleLogin = (Button) this.findViewById(R.id.btn_googleLogin);
        btnFacebookLogout = (Button) this.findViewById(R.id.btn_facebookLogout);
        btnFacebookLogin = (Button) this.findViewById(R.id.btn_facebookLogin);
        btnChangeAccount = (Button) this.findViewById(R.id.parse_changeaccount);
        btnUserCert = (Button) this.findViewById(R.id.parse_usercert);
        btnLoginGame = (Button) this.findViewById(R.id.login_game);
        mSwitch = (Switch) this.findViewById(R.id.switch_env);
        btnGetUserInfo.setOnClickListener(this);
        btnBuyPort.setOnClickListener(this);
        btnGoogleLogin.setOnClickListener(this);
        btnGoogleLogout.setOnClickListener(this);
        btnFacebookLogin.setOnClickListener(this);
        btnFacebookLogout.setOnClickListener(this);
        btnChangeAccount.setOnClickListener(this);
        btnUserCert.setOnClickListener(this);
        btnLoginGame.setOnClickListener(this);

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
        Starter.getInstance().login(MainActivity.this, new ESdkCallback() {

            @Override
            public void onLogin(Map<String, String> loginResult) {
                System.out.println("登录成功:" + loginResult);
                String userId = loginResult.get(ESConstant.SDK_USER_ID); // 宜搜用户id
                String userName = loginResult.get(ESConstant.SDK_USER_NAME); // 宜搜用户名
                String token = loginResult.get(ESConstant.SDK_USER_TOKEN); // 宜搜用户token
                String isIdentityUser = loginResult.get(ESConstant.SDK_IS_IDENTITY_USER); // 是否实名认证用户："0"不是， "1"是
                String userBirthdate = loginResult.get(ESConstant.SDK_USER_BIRTH_DATE); // 用户出生日期，未实名认证用户默认为"0"
                String isAdult = loginResult.get(ESConstant.SDK_IS_ADULT); // 用户是否成年："0"不是， "1"是
                String isHoliday = loginResult.get(ESConstant.SDK_IS_HOLIDAY); // 当前日期是否国家法定节假日："0"不是， "1"是

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

                String userId = registerResult.get(ESConstant.SDK_USER_ID); // 宜搜用户id
                String userName = registerResult.get(ESConstant.SDK_USER_NAME); // 宜搜用户名
            }

            @Override
            public void onUserInfo(Map<String, String> userInfoResult) {

                System.out.println("获取SDK用户信息:" + userInfoResult);

                // SDK登录状态，"true"：已登录，"false"：未登录
                String loginStatus = userInfoResult.get(ESConstant.SDK_LOGIN_STATUS);

                String userId = "";
                String userName = "";
                String token = "";

                // 仅当loginStatus为"true"才能获取到用户信息
                if (loginStatus.equals(ESConstant.SDK_STATUS)) {

                    userId = userInfoResult.get(ESConstant.SDK_USER_ID); // 宜搜用户id
                    userName = userInfoResult.get(ESConstant.SDK_USER_NAME); // 宜搜用户名
                    token = userInfoResult.get(ESConstant.SDK_USER_TOKEN); // 宜搜用户token

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
                String isIdentityUser = userCertResult.get(ESConstant.SDK_IS_IDENTITY_USER);

                // 理论上游戏cp应在用户实名认证成功后不再显示实名认证的入口，若未做相关处理，可在此处做相应的提示
                if (isIdentityUser.equals(ESConstant.SDK_STATUS)) {

                    // 已经实名认证过的用户不会再进入SDK的认证界面，直接回调此处
                    // demo演示代码
                    Toast.makeText(MainActivity.this, "此账号已经实名认证!", Toast.LENGTH_LONG).show();
                    return;

                } else {

                    // 未实名认证过的用户进入SDK的认证界面，认证成功后直接回调此处
                    String userBirthdate = userCertResult.get(ESConstant.SDK_USER_BIRTH_DATE); // 宜搜用户出生日期

                    // demo演示代码
                    Toast.makeText(MainActivity.this, "实名认证成功，用户出生日期为：" + userBirthdate, Toast.LENGTH_LONG).show();
                }
            }
        });
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

            case R.id.btn_googleLogout:
                Starter.getInstance().googleLogout();
                break;

            case R.id.btn_googleLogin:
                sdkLogin();
                break;

            case R.id.btn_facebookLogin:
                Starter.getInstance().initFacebook();
                break;

            case R.id.btn_facebookLogout:
                break;

            case R.id.parse_port:
                // demo演示代码，调用支付接口演示
                /**
                 * 支付接口
                 * Activity：当前activity
                 */
                Starter.getInstance().pay(MainActivity.this, "esgame_04", new ESdkPayCallback() {

                    @Override
                    public void onPaySuccess() {
                        Toast.makeText(MainActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPayFail(int code) {
                        //1000用户取消支付
                        //1001支付失败
                        //1002服务器验证交易失败，等验证成功后会继续回调支付成功接口
                        //1003已拥有该商品
                        Toast.makeText(MainActivity.this, "支付失败" + code, Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            case R.id.login_game:
                /**
                 * 上传游戏登陆日志接口
                 * 用于数据统计，在游戏登录成功（非sdk登录成功，玩家登录成功且经过选区服及创建角色或选择角色，完全进入游戏后）后调用
                 */
                Map<String, String> playerInfo = new HashMap<String, String>();
                playerInfo.put(ESConstant.PLAYER_NAME, "哈哈哈哈哈哈"); // 游戏角色名称
                playerInfo.put(ESConstant.PLAYER_LEVEL, "9"); // 游戏角色等级
                playerInfo.put(ESConstant.PLAYER_ID, mPlayId.getText().toString()); // 游戏角色id
                playerInfo.put(ESConstant.PLAYER_SERVER_ID, "1"); // 游戏区服id
                playerInfo.put(ESConstant.LEVEL_NICK_NAME, "hahaha");
                playerInfo.put(ESConstant.SERVER_NAME, "hahaha");
                playerInfo.put(ESConstant.PROJECTMARK, "ka");
                playerInfo.put(ESConstant.CREATEDTIME, String.valueOf(System.currentTimeMillis()));
                Starter.getInstance().startGameLoginLog(playerInfo);
                // demo演示代码
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
        Starter.getInstance().pageResume(MainActivity.this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }
}

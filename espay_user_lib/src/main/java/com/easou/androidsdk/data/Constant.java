package com.easou.androidsdk.data;

import android.content.Context;
import android.os.Environment;

import com.easou.androidsdk.util.CommonUtils;

import java.io.File;
import java.util.Map;

public class Constant {

    public static Context context;

    /**
     * SDK版本号，更新版本记得改版本号
     */
    public static final String SDK_VERSION = "2.5.0";
    public static final String SDK_PHONEOS = "Android";

    public static final String API_SOURCE = "30";
    public static final String API_VERSION = "5";

    /**
     * H5 SDK 用户id
     */
    public static String ESDK_USERID;
    /**
     * H5 SDK 用户token
     */
    public static String ESDK_TOKEN;

    /**
     * 记录是否登录
     */
    public static boolean IS_LOGINED;
    /**
     * 记录是否进入用户中心webview
     */
    public static boolean IS_ENTERED_SDK;
    /**
     * 记录设备号
     */
    public static String IMEI = "0";
    /**
     * 记录外网ip
     */
    public static String NET_IP = "";
    /**
     * 记录oaid
     */
    public static String OAID = "0";
    /**
     * 记录是否为模拟器 0为真机，1为模拟器
     */
    public static int IS_SIMULATOR = 0;
    /**
     * 记录是否启用头条SDK
     */
    public static boolean TOUTIAO_SDK;
    /**
     * 记录是否启用汇川（UC）GISM SDK
     */
    public static boolean GISM_SDK;
    /**
     * 记录是否启用广点通SDK
     */
    public static boolean GDT_SDK;
    /**
     * 记录是否启用快手SDK
     */
    public static boolean KS_SDK;
    public static boolean BD_SDK;
    /**
     * 记录是否启用爱奇艺SDK
     */
    public static boolean AQY_SDK;
    /**
     * 保存支付限制信息
     */
    public static Map<String, String> PAY_LIMIT_INFO_MAP;
    /**
     * 收款方 0 默认宜搜，1为大华通，2为易游,3为ZKX，4为WZ支付
     */
    public static int PAY_CHANNEl = 0;

    /**
     * URL 信息
     */
    public static String HOST_NAME = "";
    public static String HOST_NAME_DEFAULT = "mtianshitong.com";

//        public static String DOMAIN = "http://lab.pay.appeasou.com";
    public static String DOMAIN = "https://service.pay.";

    public static final String[] DOMAIN_HOST = {"domain.game.eayou.com",
            "domain.game.love778.com",
            "domain.game.74mo.com"};

    /**
     * 热云日志URL
     */
    public static final String MAIN_URL = "https://reyun.game.";

    /**
     * 上传日志URL
     */
    public static final String APP_LOAD_URL = "/androidGameLog/addStepLog.e";
    public static final String GAME_LOGIN_URL = "/androidGameLog/addGameLoginLog.e";
    public static final String GAME_ORDER_URL = "/androidGameLog/addOrderLog.e";
    public static final String SDK_LOGIN_URL = "/androidGameLog/addLoginLog.e";

    /**
     * H5 SDK url
     */
//    public static String SSO_URL = "http://lab.h5.appeasou.com/static/sdk/2.0.0/es_sdk2_original.html?1=1&sdkSource=Android-SDK&payHostName=http://lab.pay.appeasou.com&ssoHostName=http://lab.sso.mtianshitong.com";
    public static String SSO_URL = "https://h5.pay.mtianshitong.com/static/sdk/2.0.0/es_sdk2_original.html?1=1&sdkSource=Android-SDK&";

    public static String sso_release = "https://h5.pay.mtianshitong.com/static/sdk/2.0.0/es_sdk2_original.html?1=1&sdkSource=Android-SDK&";
    public static String sso_test = "http://lab.pay.appeasou.com:7500/static/sdk/2.0.0/es_sdk2_original.html?1=1&sdkSource=Android-SDK&payHostName=http://lab.pay.appeasou.com&ssoHostName=http://lab.sso.mtianshitong.com";

    public static String domain_release = "https://service.pay.";
    public static String domain_test = "http://lab.pay.appeasou.com";
    /**
     * 支付 url
     */
    public static final String SERVER_URL = "/basePay/charge.e";
    public static final String CHANNELCONFIG_URL = "/basePay/channelConfig.e";
    public static final String WEB_SERVER_URL = "/basePay/chargePage.e?";
    public static final String UNIPAY_PAYECO_ENVIRONMENT = "01";
    public static final String INCLUDECHANNELS_ALL = "WFTQQWALLET,ALIPAY2,UNIONPAY2,CARD_PHONE,CARD_GAME,CARD_QQCARD,WECHAT,WFTESWECHAT,ZWXESWECHAT";

    /**
     * 查询用户当月总消费金额接口
     */
    public static final String MONTH_TOTOL_PAY_URL = "/basePay/monthPayResult.e";

    /**
     * @notice 网络连接失败，请检查网络
     */
    public static final String NETWORK_ERROR = "网络连接失败，请检查网络";

    /**
     * 标记信息
     */
    public static final String FLAG_TRADE_RESULT_SUC = "success";
    public static final String FLAG_TRADE_RESULT_FAIL = "fail";
    public static final String FLAG_TRADE_RESULT_COMMIT = "commit";

    /**
     * SharedPerferences Key 信息
     */
    public static final String KEY_NEED_SHOW_DIALOG = "needShowDialog";
    public static final String KEY_NEED_NOTICE_BIND_PHONE = "needNotice";

    /**
     * 与Handler相关的常量
     */
    public static final int HANDLER_CLOSE_ACCOUNT_CENTER = 1;
    public static final int HANDLER_TOAST_MSG = 2;
    /**
     * 返回
     */
    public static final int HANDLER_GOBACK = 3;
    public static final int HANDLER_HIDE_GOBACK_BTN = 4;
    public static final int HANDLER_SHOW_GOBACK_BTN = 5;
    public static final int HANDLER_SET_TITLE = 6;
    public static final int HANDLER_CLOSE_VIEW = 7;
    public static final int HANDLER_LOAD_USERCENTER_VIEW = 8;
    public static final int HANDLER_PAYLIST_SHOW_VIEW = 9;
    public static final int HANDLER_PAY_MAIN_NORMAL_VIEW = 10;
    /**
     * 宜支付收银台界面
     */
    public static final int HANDLER_PAY_MAIN_BUY_VIEW = 11;

    public static final int HANDLER_PAY_RESULT_SUCCESS_VIEW = 12;
    public static final int HANDLER_PAY_RESULT_FAIL_VIEW = 13;
    public static final int HANDLER_PAY_RESULT_UNFINISH_VIEW = 14;
    public static final int HANDLER_PAY_RESULT_TOEKNFAIL_VIEW = 15;

    public static final int HANDLER_CANCELTIMER = 16;

    public static final int HANDLER_PAY_ECORN = 17;

    public static final int HANDLER_ALIPAY = 18;
    public static final int HANDLER_WECHAT = 19;
    public static final int HANDLER_UNIPAY = 20;
    public static final int HANDLER_WEBPAY = 21;
    public static final int HANDLER_JFPAY = 40;

    public static final int YSTOJS_GAME_LOGIN_LOG = 22;
    public static final int YSTOJS_GAME_ORDER_LOG = 23;
    public static final int YSTOJS_GET_USERINFO = 24;
    public static final int YSTOJS_CLICK_FLOATVIEW = 25;
    public static final int YSTOJS_IS_CERTUSER = 26;
    public static final int YSTOJS_USERCERT = 27;
    public static final int YSTOJS_GET_OAID = 28;
    public static final int YSTOJS_GET_PAY_LIMIT_INFO = 29;
    public static final int YSTOJS_UPLOAD_TIME = 30;
    public static final int YSTOJS_GAME_LOGIN_DATA = 31;
    public static final int YSTOJS_GAME_LOGINOROUTLOG = 32;

    public static final String WECHAT = "WECHAT";
    public static final String WECHAT_DHT = "WECHAT_DHT";
    public static final String WECHAT_YY = "WECHAT_YY";
    //正式展科鑫
    public static final String WECHAT_ZKX = "WECHAT_ZKX";
    //正式赛恒达通
//    public static final String WECHAT_ZKX = "WECHAT_SHDT";
    //正式中智时代
//	public static final String WECHAT_ZKX = "WECHAT_ZZSD";
    //正式聚合互娱
//	public static final String WECHAT_ZKX = "WECHAT_JHHY";
    //正式北京海默
//	public static final String WECHAT_ZKX = "WECHAT_BJHM";

    //    public static final String WECHAT_ZKX_GZH = "WECHAT_ZKX_GZH";
    //正式展科鑫
    public static final String ZKXHGALIPAY = "ZKXHGALIPAY";
    //正式赛恒达通
//    public static final String ZKXHGALIPAY = "SHDTALIPAY";
    //正式中智时代
//	public static final String ZKXHGALIPAY="ZZSDALIPAY";
    //正式聚合互娱
//	public static final String ZKXHGALIPAY="JHHYALIPAY";
    //正式北京海默
//	public static final String ZKXHGALIPAY="BJHMALIPAY";

    //易联2.0展科鑫
    public static final String ZKXUNIONPAY2 = "ZKXUNIONPAY2";


    public static final String UNIONPAY = "UNIONPAY2";
    public static final String ALIPAY = "ALIPAY2";
    public static final String ALIPAY_DHT = "YXDHTALIPAY";
    public static final String YDJFDHPAY = "YDJFDHPAY";
    public static final String ALIPAY_YY = "YYXZALIPAY";
    public static final String WFTWECHAT = "WFTWECHAT";
    public static final String ZWXESWECHAT = "ZWXESWECHAT";
    public static final String WFTESWECHAT = "WFTESWECHAT";
    public static final String MODULE = "MODULE";
    public static final String TRADEMODE = "tradeMode";
    public static final String PAYCHANNEL = "payChannel";
    public static final String EASOUTGC = "EASOUTGC";
    public static final String INCLUDE_CHANNELS = "includeChannels";
    public static final String PRODUCT_NAME = "productName";
    public static final String AMOUNT = "amount";
    public static final String WECHAT_PAY_TYPE = "30";

    public static final int RESULTCODE = 4128;    //汇元网支付回调状态值

    /**
     * Intent 参数的Key
     */
    public static final String DEVICE_ID = "deviceId";
    public static final String CLIENT_IP = "clientIp";
    public static final String APP_ID = "appId";
    public static final String PARTENER_ID = "partnerId";
    public static final String CARD_NUM = "cardNumber";
    public static final String KEY = "key";
    public static final String QN = "qn";
    public static final String VERSION = "esVersion";
    public static final String PHONEOS = "phoneOs";

    public static final String SDK_SHOWSTATUS = "status";


    public static final String ES_DEVICE_ID = "EsDeviceID";
    public static final String ES_DEV_ID = "devID";
    public static final String ES_H5_TOKEN = "H5Token";
    public static final String ES_TOKEN = "token";

    public static final String SDK_PAY_STATUS = "payStatus";
    public static final String SDK_USER_TYPE = "userType";
    public static final String SDK_MIN_AGE = "minAge";
    public static final String SDK_MAX_AGE = "maxAge";
    public static final String SDK_S_PAY = "sPay"; // 单次最大能支付金额
    public static final String SDK_C_PAY = "cPay"; // 单月最大能支付总额度

    public static final String CHANNEL_MARK = "channelMark";
    public static final String CHANNEL_MARK_DHT = "DHT";
    public static final String CHANNEL_MARK_YY = "YY";
    public static final String CHANNEL_MARK_WZYY = "WZYY";
    //正式展科鑫
    public static final String CHANNEL_MARK_ZKX = "HYWZKX";
    //test
//    public static final String CHANNEL_MARK_ZKX= "WECHAT_ZM";
    //正式赛恒达通
//    public static final String CHANNEL_MARK_ZKX = "HYWSHDT";
    //正式中智时代
//    public static final String CHANNEL_MARK_ZKX= "HYWZZSD";
    //正式聚合互娱
//    public static final String CHANNEL_MARK_ZKX= "HYWJHHY";
    //正式北京海默
//    public static final String CHANNEL_MARK_ZKX= "HYWBJHM";

    //支付宝展科鑫网页版
//    public static final String CHANNEL_MARK_ZFBZKX = "ZFBZKX";


    public static final String WXAPPID = "wx5303fe302200dc98";
    public static final String WXAPPKEY = "6c3a59ea1e2c8582c9968a9bb992fd42";

    public static class SdcardPath {
        /**
         * Easou根目录
         */
        public static final String SAVE_ROOTPATH = Environment
                .getExternalStorageDirectory() + "/.EasouSDK";
        /**
         * 图片缓存目录
         */
        public static final String IMAGE_SAVEPATH = SAVE_ROOTPATH + "/images";
        /**
         * 缓存目录
         */
        public static final String CACHE_SAVEPATH = SAVE_ROOTPATH + "/cache";
        /**
         * 应用更新目录
         */
        public static final String UPDATE_APK_SAVEPATH = SAVE_ROOTPATH + "/update";
        /**
         * 文件缓存目录
         */
        public static final String DOWNLOAD_TMP_SAVEPATH = SAVE_ROOTPATH + "/tmp";
        /**
         * 日志
         */
        public static final String LOG_SAVEPATH = SAVE_ROOTPATH + "/log";

    }

    /**
     * 获取存储的host文件
     */
    public static final File getHostInfoFile(Context context) {

        if (null == context) {
            return null;
        }
        return new File(context.getFilesDir(), ".host");
    }

    /**
     * 获取保存在SD卡上的host文件
     *
     * @return 如果SD卡不存在则返回null
     */
    public static final File getSDHostInfoFile() {
        if (CommonUtils.hasSdcard()) {
            return new File(SdcardPath.CACHE_SAVEPATH, ".host");
        }
        return null;
    }

    /**
     * 获取保存在SD卡上的用户登录信息的文件
     *
     * @return 如果SD卡不存在则返回null
     */
    public static final File getSDLoginInfoFile() {
        if (CommonUtils.hasSdcard()) {
            return new File(SdcardPath.CACHE_SAVEPATH, ".login");
        }
        return null;
    }

    public static final File getSLoginInfoFile(String appid) {
        if (CommonUtils.hasSdcard()) {
            return new File(SdcardPath.CACHE_SAVEPATH + "/" + appid + ".txt");
        }
        return null;
    }

    /**
     * 获取存储用户登录信息的文件
     */
    public static final File getLoginInfoFile(Context context) {

        if (null == context) {
            return null;
        }
        return new File(context.getFilesDir(), ".login");
    }
}

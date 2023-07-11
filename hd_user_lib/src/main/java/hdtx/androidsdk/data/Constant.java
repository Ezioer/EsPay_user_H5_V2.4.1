package hdtx.androidsdk.data;

import android.content.Context;
import android.os.Environment;


import java.io.File;
import java.util.Map;

import hdtx.androidsdk.util.CommonUtils;

public class Constant {

    public static Context context;

    /**
     * SDK版本号，更新版本记得改版本号
     */
    public static final String SDK_VERSION = "1.2.1";
    public static final String SDK_PHONEOS = "Android";

    /**
     * H5 SDK 用户id
     */
    public static String ESDK_USERID = "";
    /**
     * H5 SDK 用户token
     */
    public static String ESDK_TOKEN = "";

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
    public static String ANDROIDID = "";
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
     * 保存支付限制信息
     */
    public static Map<String, String> PAY_LIMIT_INFO_MAP;
    /**
     * URL 信息
     */
    public static String HOST_NAME = "";
    public static String HOST_NAME_DEFAULT = "mtianshitong.com";

    public static String DOMAIN = "https://service.pay.";

    public static final String[] DOMAIN_HOST = {"domain.game.mtianshitong.com",
            "domain.game.eayou.com",
            "domain.game.szzkxkj.com"};


    //记录游戏角色信息
    public static String playerId = "";
    public static String playerLevel = "";
    public static String playerName = "";
    public static String serverId = "";
    public static String ua = "";
    public static int isTurnExt = 0;
    public static int isTurnExtUser = 0;
//    public static boolean facebook = true;
//    public static boolean adjust = true;
//    public static boolean firebase = true;
    /**
     * 热云日志URL
     */
    public static final String MAIN_URL = "https://reyun.game.";
//    public static final String MAIN_URL = "http://lab.reyun.tjqzqkj.com";

    /**
     * 上传日志URL
     */
    public static final String APP_LOAD_URL = "/androidGameLog/addStepLog.e";
    public static final String GAME_LOGIN_URL = "/androidGameLog/addGameLoginLog.e";
    public static final String GAME_ORDER_URL = "/androidGameLog/addOrderLog.e";
    public static final String SDK_LOGIN_URL = "/androidGameLog/addLoginLog.e";

    //转端热云日志
    //创建角色
    public static final String REYUN_ADD_PLAYER_LOG = "/turnExt/addPlayerLog.e";
    //支付
    public static final String REYUN_ADD_PAY_LOG = "/turnExt/addPayLog.e";
    //游戏登录
    public static final String REYUN_ADD_LOGIN_LOG = "/turnExt/addGameLoginLog.e";

    //游戏角色数据上传日志
    public static final String GAME_PLAYER_LOG = "/gameLog/addPlayerLog.e";

    /**
     * H5 SDK url
     */
    public static String URL_BACKUP = "mtianshitong.com";
    //        public static String SSO_URL = "http://lab.h5.tjqzqkj.com/static/sdk/3.0.0/es_sdk3_original.html?1=1&sdkSource=Android-SDK&payHostName=http://lab.pay.appeasou.com&ssoHostName=http://lab.sso.mtianshitong.com";
    public static String SSO_URL = "https://login.easou-hk.com/static/sdk/2.0.0/es_sdk2_original.html?1=1&sdkSource=Android-SDK&";
    //    public static String SSO_URL = "https://h5.pay.mtianshitong.com/static/sdk/2.0.0/es_sdk2_original.html?1=1&sdkSource=Android-SDK&";
    public static String SSO_REST = "/static/sdk/2.0.0/es_sdk2_original.html?1=1&sdkSource=Android-SDK&";

    /**
     * 支付 url
     */
    //google验证交易
    public static final String GOOGLEVER = "https://pay.easou-hk.com/play/checkSign";
    //pay.easou-hk.com
    //海外下单
    public static final String CHECKORDER = "https://pay.easou-hk.com/play/createOrder";
    //同步核销状态
    public static final String CONSUMPTION = "https://pay.easou-hk.com/play/consumption";
    //获取oaid证书
    public static final String GETOAIDCERT = "https://egamec.eayou.com/cert/getCertPem";
    /**
     * @notice 网络连接失败，请检查网络
     */
    public static final String NETWORK_ERROR = "网络连接失败，请检查网络";

    /**
     * 标记信息
     */
    public static final String FLAG_TRADE_RESULT_SUC = "success";

    /**
     * SharedPerferences Key 信息
     */
    public static final String KEY_NEED_SHOW_DIALOG = "needShowDialog";
    public static final String KEY_NEED_NOTICE_BIND_PHONE = "needNotice";

    /**
     * 与Handler相关的常量
     */
    public static final int HANDLER_CLOSE_ACCOUNT_CENTER = 1;
    /**
     * 返回
     */
    public static final int HANDLER_GOBACK = 3;
    public static final int HANDLER_LOAD_USERCENTER_VIEW = 8;
    public static final int HANDLER_PAYLIST_SHOW_VIEW = 9;

    public static final int HANDLER_ALIPAY = 18;
    public static final int HANDLER_WECHAT = 19;
    public static final int HANDLER_UNIPAY = 20;
    public static final int HANDLER_WEBPAY = 21;

    public static final int YSTOJS_GAME_LOGIN_LOG = 22;
    public static final int YSTOJS_GAME_ORDER_LOG = 23;
    public static final int YSTOJS_GET_USERINFO = 24;
    public static final int YSTOJS_CLICK_FLOATVIEW = 25;
    public static final int YSTOJS_IS_CERTUSER = 26;
    public static final int YSTOJS_USERCERT = 27;
    public static final int YSTOJS_GET_OAID = 28;
    public static final int YSTOJS_GET_CUSTOMDEVICE = 34;
    public static final int YSTOJS_GET_PAY_LIMIT_INFO = 29;
    public static final int YSTOJS_UPLOAD_TIME = 30;
    public static final int YSTOJS_GAME_LOGIN_DATA = 31;
    public static final int YSTOJS_GAME_LOGINOROUTLOG = 32;
    public static final int YSTOJS_GAME_INTOFOREGROUND = 33;
    public static final int YSTOJS_GAME_LOGOUT = 35;
    public static final int YSTOJS_GAME_LOGINGOOGLE = 100;
    public static final int YSTOJS_GAME_LOGINFACEBOOK = 101;
    public static String AESKEY = "";
    public static final String unuselessdata = "ezGW6SrVAFezVftc";
    public static final String unuselessvalue = "abcdefghijklmnopqrstuvwxyz0123456789";

    public static final String MODULE = "MODULE";
    public static final String PAYCHANNEL = "payChannel";
    public static final String EASOUTGC = "EASOUTGC";
    public static final String PRODUCT_NAME = "productName";
    public static final String AMOUNT = "amount";

    /**
     * Intent 参数的Key
     */
    public static final String DEVICE_ID = "deviceId";
    public static final String CLIENT_IP = "clientIp";
    public static final String APP_ID = "appId";
    public static String APPID = "";
    public static final String PARTENER_ID = "partnerId";
    public static final String KEY = "key";
    public static final String QN = "qn";
    public static final String CUID = "cuid";
    public static final String VERSION = "esVersion";
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

    public static String CUSTOMDEVICES = "";

    public static class SdcardPath {
        /**
         * 根目录
         */
        public static final String SAVE_ROOTPATH = Environment
                .getExternalStorageDirectory() + "/.hdSDK";
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

        public static final String DEVICEIDPATH = Constant.SdcardPath.CACHE_SAVEPATH + "/" + "deviceid.txt";

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

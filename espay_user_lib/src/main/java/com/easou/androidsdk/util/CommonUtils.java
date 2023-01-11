package com.easou.androidsdk.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.collection.ArrayMap;

import com.easou.androidsdk.Starter;
import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.sso.AuthBean;
import com.easou.androidsdk.ui.UIHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    public static AuthBean getAuthBean(Context context, String appid) {
        String gsonStr = FileHelper
                .readFile(Constant.getLoginInfoFile(context));
        if (null == gsonStr || "".equals(gsonStr)) {
            gsonStr = FileHelper.readFile(Constant.getSLoginInfoFile(appid));
            if (null == gsonStr || "".equals(gsonStr))
                return null;
        }
        AuthBean bean = GsonUtil.fromJson(gsonStr, AuthBean.class);
        return bean;
    }

    public static String getTokenFromSD(String appid) {
        try {
            String gsonStr = FileHelper.readFile(Constant.getSLoginInfoFile(appid));
            if (null == gsonStr || "".equals(gsonStr))
                return null;
            return gsonStr;
        } catch (Exception e) {
            ESdkLog.d(e.toString());
        }
        return "";
    }

    public static void saveH5TokenToCard(String token, String appid) {
        try {
            FileHelper.writeFile(new File(Constant.SdcardPath.CACHE_SAVEPATH + "/" + appid + ".txt"), token);
        } catch (Exception e) {
            ESdkLog.d(e.toString());
        }
    }

    /**
     * 保存设备号到sd卡上，避免用户卸载应用后设备号改变
     *
     * @param deviceId
     */
    public static void saveDeviceId2SdCard(String deviceId) {
        try {
            File file = new File(Constant.SdcardPath.DEVICEIDPATH);
            //如果已经保存了deviceid 则返回 确保deviceid唯一
            if (file.exists()) {
                return;
            }
            FileHelper.writeFile(file, deviceId);
        } catch (Exception e) {

        }
    }

    //获取保存在硬盘上的deviceid
    public static String getDeviceIdFromSd() {
        try {
            File file = new File(Constant.SdcardPath.DEVICEIDPATH);
            if (file.exists()) {
                return FileHelper.readFile(file);
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public static String getEsDeviceID(Context mContext) {
        String sdcardid = getDeviceIdFromSd();
        //如果硬盘中存储的deviceid有效，则直接返回，否则需要进一步判断缓存中的deviceid
        if (sdcardid != null && !sdcardid.equals("null") & !TextUtils.isEmpty(sdcardid)) {
            return sdcardid;
        } else {
            SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_DEVICE_ID, 0);
            String esDevID = settings.getString(Constant.ES_DEV_ID, "").toString();

            if (TextUtils.isEmpty(esDevID)) {
                String devID = Tools.getDeviceBrand() + Tools.getSystemModel() +
                        Tools.getSystemVersion() + System.currentTimeMillis();
                esDevID = Md5SignUtils.sign(devID, readPropertiesValue(mContext, Constant.KEY));
                saveEsDeviceID(mContext, esDevID);
                saveDeviceId2SdCard(esDevID);
            }

            return esDevID;
        }
    }

    public static void saveEsDeviceID(Context mContext, String devID) {

        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_DEVICE_ID, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constant.ES_DEV_ID, devID);
        editor.commit();
    }

    public static String getH5Token(Context mContext) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        String token = settings.getString(Constant.ES_TOKEN, "").toString();
        if (token.isEmpty() || null == settings || null == token) {
            settings = mContext.getSharedPreferences(mContext.getPackageName() + Constant.ES_H5_TOKEN, 0);
            token = settings.getString(Constant.ES_TOKEN, "").toString();
        }
        return token;
    }

    public static void saveH5Token(Context mContext, String token) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constant.ES_TOKEN, token);
        editor.commit();
    }

    public static String getUserId(Context mContext) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        String userid = settings.getString("esuserid", "");
        return userid;
    }

    public static String getIsReplaceSso(Context mContext) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        String sso = settings.getString("replacesso", "");
        return sso;
    }

    public static void saveReplaceSso(Context mContext, String sso) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("replacesso", sso);
        editor.commit();
    }

    public static int getIsFirstStart(Context mContext) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        int sso = settings.getInt("isfirststart", 0);
        return sso;
    }

    public static void saveIsFirstStart(Context mContext) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("isfirststart", 1);
        editor.commit();
    }

    public static void saveIsAutoCount(Context mContext, String isCount) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("isautocount", isCount);
        editor.commit();
    }

    //1为成年人，不进行自动计时
    public static String getIsAutoCount(Context mContext) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        String userid = settings.getString("isautocount", "0");
        return userid;
    }

    public static void saveCert(Context mContext, String cert) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("oaidcert", cert);
        editor.commit();
    }

    public static String getCert(Context mContext) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        String cert = settings.getString("oaidcert", "");
        return cert;
    }

    public static void saveUserId(Context mContext, String id) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("esuserid", id);
        editor.commit();
    }

    /**
     * 检测某个应用是否安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 判断当前是否有网络
     *
     * @param context
     * @return
     */
    public static boolean isNetworkUseable(Context context) {
        // ConnectivityManager cm = (ConnectivityManager) context
        // .getSystemService(Context.CONNECTIVITY_SERVICE);
        // if (cm == null)
        // return false;
        // NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if (networkInfo != null && networkInfo.isAvailable() &&
        // networkInfo.isConnectedOrConnecting()) {
        // return true;
        // }
        // return false;

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable()) {
            return false;
        }
        return true;
    }

    /**
     * 是否挂载了sdcard
     *
     * @return
     */
    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取VersionName
     *
     * @return
     */
    public static String getVersionName(Context context) {
        PackageInfo pkg;
        try {
            pkg = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return pkg.versionName;
        } catch (NameNotFoundException e) {
            return "";
        }
    }

    /**
     * 获取VersionCode
     *
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageInfo pkg;
        try {
            pkg = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return pkg.versionCode;
        } catch (NameNotFoundException e) {
            return 0;
        }
    }

    /**
     * 获取渠道号Channel Id（存在Mainfest文件中）
     *
     * @return
     */
    public static String getMetaDataItem(Context context, String key) {
        ApplicationInfo info;
        try {
            info = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            String msg = info.metaData.getString(key);
            return msg;
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 根据字段名得到QN参数
     *
     * @param context
     * @return
     */
    public String getQN(Context context) {
        // TODO: 从Mainfest文件中读取QN
        ApplicationInfo info;
        try {
            info = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            String msg = info.metaData.getString("UMENG_CHANNEL");
            return msg;
        } catch (Exception e) {
            return "none";
        }
    }

    /**
     * 显示提示信息
     *
     * @param context
     * @param msg
     * @param handler
     */
    public static void postShowMsg(final Context context, final String msg,
                                   Handler handler) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 显示自定义位置的Toast
     *
     * @param context
     * @param msg
     * @param handler
     * @param gravity
     */
    public static void postShowMsg(final Context context, final String msg,
                                   Handler handler, final int gravity) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast t = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                t.setView(UIHelper.createToastView(context, msg));
                t.setGravity(gravity, 0, 50);
                t.show();
            }
        });
    }

    /**
     * 得到Preference对象
     *
     * @param context
     * @return
     */
    public static SharedPreferences getSettings(Context context) {
        SharedPreferences preference = context.getSharedPreferences(
                Constant.KEY_NEED_NOTICE_BIND_PHONE, Context.MODE_PRIVATE);
        return preference;
    }


    /**
     * 从URL中得到ticket
     *
     * @param url
     * @return
     */
    public static String getTicket(String url) {
        int start = url.indexOf("ticket=") + "ticket=".length();
        String queryString = url.substring(start);
        int end = queryString.indexOf("&");
        if (end == -1) {
            return queryString;
        } else {
            return queryString.substring(0, end);
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // Easou Pay !
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 从Properties文件中读取配置信息
     *
     * @param key：参数名称
     */
    public static String readPropertiesValue(Context _context, String key) {
        Properties prop = new Properties();
        InputStream is = null;
        String str = "ZKX";
        try {
            is = _context.getAssets().open("client.properties");
            prop.load(is);
            str = prop.getProperty(key);
            if (null == str) {
                str = "ZKX";
            }
        } catch (IOException e) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            return str;
        }
    }


    /**
     * 获取待签名字符串
     */
    public static String getStringForSign(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        TreeMap<String, String> treeMap = new TreeMap<String, String>(map);
        if (treeMap != null) {
            for (Map.Entry<String, String> entity : treeMap.entrySet()) {
                if (entity.getKey() != null && entity.getValue() != null) {
                    sb.append(entity.getKey()).append("=")
                            .append(String.valueOf(entity.getValue()))
                            .append("&");
                }
            }
        }
        if (sb.length() > 0) {// 删除最后的&符
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }


    /**
     *
     */
    public static String encoder(String value)
            throws UnsupportedEncodingException {
        // 转中文
        String enUft = URLEncoder.encode(value, "UTF-8");
        java.net.URLDecoder urlDecoder = new java.net.URLDecoder();
        String str = urlDecoder.decode(enUft, "UTF-8");
        return str;
    }

    public static int uniLength(String value) {
        int valueLength = 0;
        // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (int i = 0; i < value.length(); i++) {
            // 获取一个字符
            char c = value.charAt(i);
            // 判断是否为中文字符
            if (isChinese(c)) {
                // 中文字符长度为2
                valueLength += 2;
            } else {
                // 其他字符长度为1
                valueLength += 1;
            }
        }
        // 进位取整
        return valueLength;
    }

    public static boolean isHalfChar(String str) {
        if (str == null) {
            return false;
        } else {
            String regex = "[\u0000-\u00FF]+";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(str);
            boolean validate = m.matches();
            return validate;
        }
    }

    private static final boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 从资源文件中读取图片
     *
     * @param context
     * @param fileName
     * @return
     */
    public static Bitmap getBitmap(Context context, String fileName) {
        AssetManager am = context.getAssets();
        Bitmap bitmap = null;
        InputStream is = null;
        try {
            is = am.open(fileName);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return bitmap;
    }

    /**
     * 得到点9图
     *
     * @param fileName
     * @return
     */
    public static NinePatchDrawable getNinePatchDrawable(Context context,
                                                         String fileName) {
        AssetManager am = context.getAssets();

        InputStream is = null;
        try {
            if (fileName.contains(".9")) {
                is = am.open(fileName);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                byte[] chunk = bitmap.getNinePatchChunk();
                NinePatchDrawable patchy = new NinePatchDrawable(bitmap, chunk,
                        new Rect(), null);
                return patchy;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 获取9path图形
     *
     * @param context
     * @param fileName
     * @return
     */
    public static Drawable get9Path(Context context, String fileName) {
        AssetManager am = context.getAssets();
        InputStream is = null;
        try {
            is = am.open(fileName);
            String srcName = fileName;
            Drawable d = NinePatchDrawable.createFromStream(is, srcName);
            return d;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    is = null;
                }
            }
        }
        return null;
    }
    // 来自StartESPayCenter
    //
    // /**
    // * 得到带有Cookie信息的url
    // *
    // * @return
    // */
    // public String buildUrl(Context context, TradeInfo info) {
    // String[] list = getToken(context);
    // if (list == null) {
    // list = new String[] { "", "" };
    // }
    // String visitUrl = "";
    // try {
    // // String titleUrl = Constant.DOMAIN + Tools.getHostName() + Constant.URL_PAY_TRADE + "EASOUTGC=" + list[0]
    // // + "&U=" + list[1] + "&redirectUrl=";
    // String titleUrl = Constant.DOMAIN + Tools.getHostName() + Constant.URL_PAY_TRADE
    // +
    // "EASOUTGC=TGT-47992-vFnlrTjf2cBE55rdydqohoaWlJIDFWFdah7ZfnD7j9yOLlZc2S-sso"
    // + "&redirectUrl=";
    //
    // String bodyUrl = Constant.DOMAIN + Tools.getHostName() + Constant.URL_PAY + "appId="
    // + URLEncoder.encode(info.getAppId(), "UTF-8")
    // + "&partnerId="
    // + URLEncoder.encode(info.getPartenerId(), "UTF-8")
    // + "&tradeId="
    // + URLEncoder.encode(info.getTradeId(), "UTF-8")
    // + "&tradeName="
    // + URLEncoder.encode(info.getTradeName(), "UTF-8")
    // + "&tradeDesc="
    // + URLEncoder.encode(info.getTradeDesc(), "UTF-8")
    // + "&reqFee=" + URLEncoder.encode(info.getReqFee(), "UTF-8")
    // + "&notifyUrl="
    // + URLEncoder.encode(info.getNotifyUrl(), "UTF-8")
    // + "&redirectUrl="
    // + URLEncoder.encode(info.getRedirectUrl(), "UTF-8")
    // + "&sign=" + URLEncoder.encode(getSign(info), "UTF-8")
    // + "&separable="
    // + URLEncoder.encode(info.getSeparable(), "UTF-8")
    // + "&payerId="
    // + URLEncoder.encode(info.getPayerId(), "UTF-8") + "&qn="
    // + URLEncoder.encode(info.getQN(), "UTF-8") + "&extInfo="
    // + URLEncoder.encode(info.getExectInfo(), "UTF-8");
    //
    // String bode = URLEncoder.encode(bodyUrl, "UTF-8");
    // visitUrl = titleUrl + bode;
    // } catch (UnsupportedEncodingException e) {
    // Lg.e(e.toString());
    // }
    // return visitUrl;
    // }

    // /**
    // * 取得签名内容
    // *
    // * @param info
    // * @return
    // */
    // private static String getSign(TradeInfo info) {
    // Map<String, String> map = new HashMap<String, String>();
    // map.put("appId", info.getAppId());
    // map.put("partnerId", info.getPartenerId());
    // map.put("tradeId", info.getTradeId());
    // map.put("tradeName", info.getTradeName());
    // map.put("tradeDesc", info.getTradeDesc());
    // map.put("reqFee", info.getReqFee());
    // map.put("notifyUrl", info.getNotifyUrl());
    // map.put("separable", info.getSeparable());
    // map.put("payerId", info.getPayerId());
    // map.put("qn", info.getQN());
    // // map.put("extInfo",extInfo);
    // map.put("redirectUrl", info.getRedirectUrl());
    //
    // String temp = CommonUtils.getStringForSign(map);
    // return CommonUtils.md5(temp);
    // }
    //
    // /**
    // * 获取Token信息
    // *
    // * @return
    // */
    // private static String[] getToken(Context context) {
    // String gsonStr = FileHelper
    // .readFile(Constant.getLoginInfoFile(context));
    // if (null == gsonStr || "".equals(gsonStr)) {
    // gsonStr = FileHelper.readFile(Constant.getSDLoginInfoFile());
    // if (null == gsonStr || "".equals(gsonStr))
    // return null;
    // }
    // AuthBean bean = GsonUtil.fromJson(gsonStr, AuthBean.class);
    // if (bean != null && bean.getToken() != null
    // && !"".equals(bean.getToken().getToken())) {
    // String[] strings = new String[] { bean.getToken().getToken(),
    // bean.getU().getU() };
    // return strings;
    // }
    // return null;
    // }

    /**
     * 根据字段名得到QN参数
     *
     * @param type
     * @param context
     * @return
     */
    public String getQN(String type, Context context) {
        ApplicationInfo info;
        try {
            info = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            String msg = info.metaData.getString("UMENG_CHANNEL");
            return msg;
        } catch (Exception e) {
            return "none";
        }
    }

    /**
     * 取得配置文件的输入流
     *
     * @return
     */
    public static InputStream getPropertiesFileInputStream(Context _context) {
        try {
            return _context.getAssets().open("client.properties");
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        return null;
        // return
        // StartESPayCenter.class.getResourceAsStream("/client/client.properties");
    }

    /**
     * 得到宜支付的App-Agent
     *
     * @return
     */
    public static Map<String, String> getPaySDKWebViewAgent() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("App-Agent", "AndroidSDKEasouPay");
        return map;
    }

    /**
     * 得到用户中心的App-Agent
     *
     * @return
     */
    public static Map<String, String> getAccountSDKWebViewAgent() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("App-Agent", "AndroidSDKEasouAccount");
        return map;
    }

    /**
     * 产生一个随机的字符串
     *
     * @return
     */
    public static String getRandomString(int length) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static boolean isNotNullOrEmpty(String string) {
        if (string == null) {
            return false;
        }
        if (string.isEmpty() || string.length() == 0) {
            return false;
        }
        return true;
    }

    public static int getAge(String birthDay) {
        try {
            String year = birthDay.substring(0, 4);
            Calendar date = Calendar.getInstance();
            String currentYear = String.valueOf(date.get(Calendar.YEAR));
            return Integer.valueOf(currentYear) - Integer.valueOf(year);
        } catch (Exception e) {
            return 0;
        }
    }

    //国家货币代码和符号
    private void initCurrency(Map map) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //用map存储，便于匹配
            for (Currency availableCurrency : Currency.getAvailableCurrencies()) {
                //currentcyCode为key,货币符号为value
                //对于没有特定符号的货币，symbol与currencyCode相同。
                map.put(availableCurrency.getCurrencyCode(), availableCurrency.getSymbol());
            }
        }
    }

    //将美元符号替换未对应国家货币的符号
    private String replacePrice(String priceStr, String currencyCode, String currencySymbol) {
        if (priceStr.startsWith("$")) {
            if (currencySymbol != null) {
                if (currencySymbol.equals(currencyCode)) {
                    //没有货币符号的情况，把货币码拼接到前面
                    priceStr = currencySymbol + priceStr;
                } else {
                    if (!priceStr.startsWith(currencySymbol)) {
                        priceStr = priceStr.replace("$", currencySymbol);
                    }
                }
            }
        }
        return priceStr;
    }

    public static Map getCheckOutParams() {
        String appId = CommonUtils.readPropertiesValue(Starter.mActivity, "appId");
        String qn = CommonUtils.readPropertiesValue(Starter.mActivity, "qn");
        String notifyUrl = CommonUtils.readPropertiesValue(Starter.mActivity, "notifyUrl");
        Map<String, String> map = new HashMap();
        map.put("appId", appId);
        map.put("qn", qn);
        map.put("notifyUrl", notifyUrl);
        return map;
    }

    public static String getMoneyFromStr(String str) {
        int k = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
                k = i;
                break;
            }
        }
        return str.substring(k);
    }
}

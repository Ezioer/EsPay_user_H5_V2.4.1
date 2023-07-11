package hdtx.androidsdk.util;

import hdtx.androidsdk.data.Constant;
import hdtx.androidsdk.http.HttpGroupUtils;

import android.app.Activity;

public class HostRequestUtils {

    public static void requestHostInfo(Activity activity, boolean hasLocalHostInfo,
                                       boolean isReplaceSso, final ReplaceCallBack callBack) {

        String result = "";

        if (hasLocalHostInfo) {
            String hInfo = getLocalHostInfo(activity);
            String[] tempInfo = hInfo.split(",");
            if (tempInfo.length == 4) {
                result = getResult(tempInfo);
            }
        }

        if (result.equals("")) {

            result = getResult(Constant.DOMAIN_HOST);
        }

        if (result != null && !result.equals("")) {

            String[] tempInfo = result.split(",");

            if (tempInfo.length == 4) {

                if (tempInfo[3].contains(".com") || tempInfo[3].contains(".cn")) {

                    Constant.HOST_NAME = tempInfo[3];
                    if (isReplaceSso) {
                        //是否需要替换个人中心的域名
                        Constant.URL_BACKUP = tempInfo[3];
                        //更换成功后记录有效的域名，下次直接使用新域名
                        CommonUtils.saveReplaceSso(activity, tempInfo[3]);
                        if (callBack != null) {
                            //替换域名后需要重新加载个人中心页面
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.replaceSuccess();
                                }
                            });
                        }
                    }
                    String gsonStr = GsonUtil.toJson(result);
                    FileHelper.writeFile(Constant.getHostInfoFile(activity), gsonStr);
                    FileHelper.writeFile(Constant.getSDHostInfoFile(), gsonStr);
                } else {
                    if (callBack != null) {
                        //替换失败需要给出页面提示
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callBack.replaceFail();
                            }
                        });
                    }
                }

            } else {
                if (callBack != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callBack.replaceFail();
                        }
                    });
                }
            }
        } else {
            if (callBack != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callBack.replaceFail();
                    }
                });
            }
        }
    }

    private static String getResult(String[] hostInfo) {

        String result = HttpGroupUtils.sendGet(getFullHost(hostInfo[0]), null, null);
        String[] tempInfo1 = result.split(",");
        if (tempInfo1.length != 4) {

            result = "";
            result = HttpGroupUtils.sendGet(getFullHost(hostInfo[1]), null, null);

            String[] tempInfo2 = result.split(",");
            if (tempInfo2.length != 4) {

                result = "";
                result = HttpGroupUtils.sendGet(getFullHost(hostInfo[2]), null, null);
            }
        }

        return result;
    }

    public static String getLocalHostInfo(Activity activity) {
        String gsonStr = FileHelper.readFile(Constant.getHostInfoFile(activity));
        if (null == gsonStr || "".equals(gsonStr)) {
            gsonStr = FileHelper.readFile(Constant.getSDHostInfoFile());
            if (null == gsonStr || "".equals(gsonStr))
                return null;
        }
        String hInfo = GsonUtil.fromJson(gsonStr, String.class);
        return hInfo;
    }

    public static String getFullHost(String hostStr) {
        String host = "http://" + hostStr + "/domain.conf";
        return host;
    }
}

package com.easou.androidsdk.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.easou.androidsdk.data.Constant;
import com.snail.antifake.jni.EmulatorDetectUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;


/***
 * \判断是否为模拟器
 */
public class SimulatorUtils {
//    public static final int RESULT_MAYBE_EMULATOR = 0;//可能是模拟器
//    public static final int RESULT_EMULATOR = 1;//模拟器
//    public static final int RESULT_UNKNOWN = 2;//可能是真机
//
//    public static final int RESULT_SUSPECT_CHECK = 3; //可疑行为数，被判定为模拟器

    /***
     * 判断是否为模拟器
     * @param context 上下文对象
     */
    public static void checkSimulator(final Context context) {
        if (context == null)
            throw new IllegalArgumentException("context must not be null");


        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
//                    // 先读取文件
//                    File file = new File(context.getFilesDir()+ File.separator + suspectCountFile);
//                    if (file.exists() && file.isFile()) { //文件存在，那么读取一下内容
//                        String content = FileHelper.readFile(file);
//                        try {
//                            if (!TextUtils.isEmpty(content)) {
//                                int suspectCount = Integer.parseInt(content);
//                                if (suspectCount >= RESULT_SUSPECT_CHECK) {  // 有值判断,	且返回
//                                    updateSimulatorStatus();
//                                }
//                                return;
//                            }
//                        } catch (NumberFormatException e) {
//
//                        }
//                    }
//                    FileHelper.initFile(file);
                    long start = System.currentTimeMillis();
//                    int suspectCount = checkSuspect(context); // 可疑数量
                    boolean isEmulator = EmulatorDetectUtil.isEmulator();
                    if (isEmulator){
                        updateSimulatorStatus();
                    }
                    long end = System.currentTimeMillis();

                    ESdkLog.d("是否被认为是模拟器：" + isEmulator + "，时间" + (end - start));
//                    ESdkLog.d("可疑行为数量" + suspectCount + "，时间" + (end - start));
//                    if (suspectCount >= RESULT_SUSPECT_CHECK) {
//                        updateSimulatorStatus();
//                    }
//                    FileHelper.writeFile(file, suspectCount + ""); //保存记录


                } catch (Exception e) {
                    if (TextUtils.isEmpty(e.getMessage())) {
                        ESdkLog.d("获取是否为模拟器，抛异常了，信息:" + e.getMessage());
                    }
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
     * 更新状态
     */
    private static void updateSimulatorStatus() {
        Constant.IS_SIMULATOR = 1;
        ESdkLog.d("此设备被认为是模拟器！");
    }


//    /**
//     * 检测可疑行为
//     * <p>
//     * * @param context
//     * * @return
//     */
//    private static int checkSuspect(Context context) {
//        if (isPad(context)) { // 平板
//            return checkPadSuspectCount(context);
//        } else { //手机
//            return checkPhoneSuspectCount(context);
//        }
//    }
//
//
//    /**
//     * 判断当前设备是手机还是平板
//     *
//     * @param context
//     * @return 平板返回 True，手机返回 False
//     */
//    private static boolean isPad(Context context) {
//        return (context.getResources().getConfiguration().screenLayout
//                & Configuration.SCREENLAYOUT_SIZE_MASK)
//                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
//    }
//
//    /**
//     * 判断平板，平板可能没有SIM卡功能、光传感器、没有基带、没有打电话功能
//     *
//     * @param context
//     * @return
//     */
//    private static int checkPadSuspectCount(Context context) {
//        int suspectCount = 0;
//        try {
//            int checkFeaturesByHardware = checkFeaturesByHardware();
//            if (checkFeaturesByHardware != RESULT_UNKNOWN) {  // 可能是模拟器 硬件信息，x86
//                suspectCount++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            String systemApp = exec("pm list packages -s");
//            if (!TextUtils.isEmpty(systemApp)) {
//                String[] result = systemApp.split("package:");
//                double countSize = result.length;
//                if (countSize > 0) {
//                    if (countSize <= 80) {
//                        suspectCount++;
//                    }
//                    double androidSize = 0;
//                    for (String name : result) {
//                        if (name.indexOf(".android") != -1) {
//                            androidSize++;
//                        }
//                    }
//                    if ((androidSize / countSize) >= 0.8) {
//                        suspectCount += 2;
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if (suspectCount >= 2) {
//            return RESULT_SUSPECT_CHECK;
//        }
//
//        try {
//            if (!supportCamera(context)) { // 不支持拍照，一定不是真机
//                suspectCount++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        try {
//            if (!supportBluetooth(context)) {  // 没有蓝牙，可疑？
//                suspectCount++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            if (checkFeaturesByFlavor() != RESULT_UNKNOWN) {
//                suspectCount++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        try {
//            String manufacturer = getProperty("ro.product.manufacturer");
//            if (checkFeaturesByManufacturer(manufacturer) != RESULT_UNKNOWN) {
//                suspectCount++;
//            }
//        } catch (Exception e) {
//        }
//        try {
//            if (checkFeaturesByModel() != RESULT_UNKNOWN) {
//                suspectCount++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        try {
//            if (checkFeaturesByBoard() != RESULT_UNKNOWN) {
//                suspectCount++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            if (checkFeaturesByPlatform() != RESULT_UNKNOWN) {
//                suspectCount++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return suspectCount;
//    }
//
//    private static int checkPhoneSuspectCount(Context context) {
//        int suspectCount = 0;
//        try {
//            if (!supportCallIntent(context)) { // 不支持打电话，一定不是真机
//                suspectCount++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            if (!supportCamera(context)) { // 不支持拍照，一定不是真机
//                suspectCount++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            int checkFeaturesByHardware = checkFeaturesByHardware();
//            if (checkFeaturesByHardware != RESULT_UNKNOWN) {  // 可能是模拟器 硬件信息，x86
//                suspectCount++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            if (getSensorCount(context) <= 11) { //传感器小于 11个
//                suspectCount++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        try {
//            if (!hasLightSensor(context)) {  // 没有光传感器，可疑？
//                suspectCount++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            if (!supportBluetooth(context)) {  // 没有蓝牙，可疑？
//                suspectCount++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            if (!supportCameraFlash(context)) { //没有闪光灯， 可疑？
//                suspectCount++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        try {
//            if (checkFeaturesByFlavor() != RESULT_UNKNOWN) {
//                suspectCount++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        try {
//            if (checkFeaturesByModel() != RESULT_UNKNOWN) {
//                suspectCount++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            if (checkFeaturesByManufacturer() != RESULT_UNKNOWN) {
//                suspectCount++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            if (checkFeaturesByBoard() != RESULT_UNKNOWN) {
//                suspectCount++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            if (checkFeaturesByPlatform() != RESULT_UNKNOWN) {
//                suspectCount++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            if (checkFeaturesByBaseBand() != RESULT_UNKNOWN) {
//                suspectCount++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            if (checkFeaturesByCgroup() != RESULT_UNKNOWN) {
//                suspectCount++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return suspectCount;
//    }
//
//
//    /**
//     * 特征参数-硬件名称
//     *
//     * @return 0，1，2
//     */
//    private static int checkFeaturesByHardware() {
//        String hardware = getProperty("ro.hardware");
//        if (TextUtils.isEmpty(hardware)) { //表示可能是模拟器
//            return RESULT_MAYBE_EMULATOR;
//        }
//        int result;
//        String tempValue = hardware.toLowerCase();
//        switch (tempValue) {
//            case "ttvm"://天天模拟器
//            case "nox"://夜神模拟器
//            case "cancro"://网易MUMU模拟器
//            case "intel"://逍遥模拟器
//            case "vbox":
//            case "vbox86"://腾讯手游助手
//            case "android_x86"://雷电模拟器
//                result = RESULT_EMULATOR; // 表示模拟器
//                break;
//            default:
//                result = RESULT_UNKNOWN; // 表示可能是真机
//                break;
//        }
//
//        return result;
//    }
//
//    /***
//     * 获取传感器个数
//     * @param context 上下文对象
//     * @return
//     */
//    private static int getSensorCount(Context context) {
//        int result = 0;
//        try {
//            SensorManager sensorManager = (SensorManager)
//                    context.getSystemService(Context.SENSOR_SERVICE);
//            if (sensorManager != null) {
//                List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
//                if (sensors != null) {
//                    result = sensors.size();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace(System.out);
//        }
//
//        Log.e("TAG", "传感器个数");
//        return result;
//    }
//
//    /**
//     * 读取信息
//     *
//     * @param propName
//     * @return
//     */
//    private static String getProperty(String propName) {
//        String value = null;
//        Object roSecureObj;
//        try {
//            roSecureObj = Class.forName("android.os.SystemProperties")
//                    .getMethod("get", String.class)
//                    .invoke(null, propName);
//            if (roSecureObj != null) value = (String) roSecureObj;
//        } catch (Exception e) {
//            value = null;
//        } finally {
//            return value;
//        }
//    }
//
//    private static String exec(String command) {
//        BufferedOutputStream bufferedOutputStream = null;
//        BufferedInputStream bufferedInputStream = null;
//        Process process = null;
//        try {
//            process = Runtime.getRuntime().exec("sh");
//            bufferedOutputStream = new BufferedOutputStream(process.getOutputStream());
//
//            bufferedInputStream = new BufferedInputStream(process.getInputStream());
//            bufferedOutputStream.write(command.getBytes());
//            bufferedOutputStream.write('\n');
//            bufferedOutputStream.flush();
//            bufferedOutputStream.close();
//
//            process.waitFor();
//
//            String outputStr = getStrFromBufferInputSteam(bufferedInputStream);
//            return outputStr;
//        } catch (Exception e) {
//            return null;
//        } finally {
//            if (bufferedOutputStream != null) {
//                try {
//                    bufferedOutputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (bufferedInputStream != null) {
//                try {
//                    bufferedInputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (process != null) {
//                process.destroy();
//            }
//        }
//    }
//
//    private static String getStrFromBufferInputSteam(BufferedInputStream bufferedInputStream) {
//        if (null == bufferedInputStream) {
//            return "";
//        }
//        int BUFFER_SIZE = 512;
//        byte[] buffer = new byte[BUFFER_SIZE];
//        StringBuilder result = new StringBuilder();
//        try {
//            while (true) {
//                int read = bufferedInputStream.read(buffer);
//                if (read > 0) {
//                    result.append(new String(buffer, 0, read));
//                }
//                if (read < BUFFER_SIZE) {
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result.toString();
//    }
//
//    /**
//     * 获取已安装第三方应用数量
//     */
//    private int getUserAppNumber() {
//        String userApps = exec("pm list package -3");
//        return getUserAppNum(userApps);
//    }
//
//    private int getUserAppNum(String userApps) {
//        if (TextUtils.isEmpty(userApps)) return 0;
//        String[] result = userApps.split("package:");
//        return result.length;
//    }
//
//    private static int checkFingerPrint() {
//        String flavor = getProperty("ro.bootimage.build.fingerprint");
//        if (TextUtils.isEmpty(flavor)) return 0;
//
//        return RESULT_EMULATOR;
//    }
//
//    /**
//     * 特征参数-渠道
//     *
//     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
//     */
//    private static int checkFeaturesByFlavor() {
//        String flavor = getProperty("ro.build.flavor");
//        if (TextUtils.isEmpty(flavor)) return 0;
//        int result;
//        String tempValue = flavor.toLowerCase();
//        if (tempValue.contains("vbox")) result = RESULT_EMULATOR;
//        else if (tempValue.contains("sdk_gphone")) result = RESULT_EMULATOR;
//        else result = RESULT_UNKNOWN;
//        return result;
//    }
//
//    /**
//     * 特征参数-设备型号
//     *
//     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
//     */
//    private static int checkFeaturesByModel() {
//        String model = getProperty("ro.product.model");
//        if (TextUtils.isEmpty(model)) return RESULT_MAYBE_EMULATOR;
//        int result;
//        String tempValue = model.toLowerCase();
//        if (tempValue.contains("google_sdk")) result = RESULT_EMULATOR;
//        else if (tempValue.contains("emulator")) result = RESULT_EMULATOR;
//        else if (tempValue.contains("android sdk built for x86")) result = RESULT_EMULATOR;
//        else result = RESULT_UNKNOWN;
//        return result;
//    }
//
//    /**
//     * 特征参数-硬件制造商
//     *
//     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
//     */
//    private static int checkFeaturesByManufacturer() {
//        String manufacturer = getProperty("ro.product.manufacturer");
//        return checkFeaturesByManufacturer(manufacturer);
//    }
//
//    /**
//     * 特征参数-硬件制造商
//     *
//     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
//     */
//    private static int checkFeaturesByManufacturer(String manufacturer) {
//        if (TextUtils.isEmpty(manufacturer)) return RESULT_MAYBE_EMULATOR;
//        int result;
//        String tempValue = manufacturer.toLowerCase();
//        if (tempValue.contains("genymotion")) result = RESULT_EMULATOR;
//        else if (tempValue.contains("netease")) result = RESULT_EMULATOR;//网易MUMU模拟器
//        else result = RESULT_UNKNOWN;
//        return result;
//    }
//
//    /**
//     * 特征参数-主板名称
//     *
//     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
//     */
//    private static int checkFeaturesByBoard() {
//        String board = getProperty("ro.product.board");
//        if (TextUtils.isEmpty(board)) return RESULT_MAYBE_EMULATOR;
//        int result;
//        String tempValue = board.toLowerCase();
//        if (tempValue.contains("android")) result = RESULT_EMULATOR;
//        else if (tempValue.contains("goldfish")) result = RESULT_EMULATOR;
//        else result = RESULT_UNKNOWN;
//        return result;
//    }
//
//    /**
//     * 特征参数-主板平台
//     *
//     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
//     */
//    private static int checkFeaturesByPlatform() {
//        String platform = getProperty("ro.board.platform");
//        if (TextUtils.isEmpty(platform)) return RESULT_MAYBE_EMULATOR;
//        int result;
//        String tempValue = platform.toLowerCase();
//        if (tempValue.contains("android")) result = RESULT_EMULATOR;
//        else result = RESULT_UNKNOWN;
//        return result;
//    }
//
//    /**
//     * 特征参数-基带信息
//     *
//     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
//     */
//    private static int checkFeaturesByBaseBand() {
//        String baseBandVersion = getProperty("gsm.version.baseband");
//        if (TextUtils.isEmpty(baseBandVersion)) return RESULT_MAYBE_EMULATOR;
//        int result;
//        if (baseBandVersion.contains("1.0.0.0")) result = RESULT_EMULATOR;
//        else result = RESULT_UNKNOWN;
//        return result;
//    }
//
//
//    /**
//     * 是否支持相机
//     */
//    private static boolean supportCamera(Context context) {
//        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
//    }
//
//    /**
//     * 是否支持闪光灯
//     */
//    private static boolean supportCameraFlash(Context context) {
//        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
//    }
//
//
//    /**
//     * 是否支持蓝牙
//     */
//    private static boolean supportBluetooth(Context context) {
//        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
//    }
//
//    /**
//     * 是否前置摄像头
//     */
//    private boolean supportCameraFront(Context context) {
//        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
//    }
//
//    /**
//     * 判断是否存在光传感器来判断是否为模拟器
//     * 部分真机也不存在温度和压力传感器。其余传感器模拟器也存在。
//     *
//     * @return false为模拟器
//     */
//    private static boolean hasLightSensor(Context context) {
//        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
//        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); //光线传感器
//        if (null == sensor) return false;
//        else return true;
//    }
//
//    /**
//     * 特征参数-进程组信息
//     */
//    private static int checkFeaturesByCgroup() {
//        String filter = exec("cat /proc/self/cgroup");
//        if (null == filter) return RESULT_MAYBE_EMULATOR;
//        return RESULT_UNKNOWN;
//    }
//
//    /***
//     * 通过判断是否有拨号功能，但是《夜神》《蓝蝶》 模拟器有此功能
//     * @param context
//     * @return
//     */
//    private static boolean supportCallIntent(Context context) {
//        try {
//            String url = "tel:" + "10086";
//            Intent intent = new Intent();
//            intent.setData(Uri.parse(url));
//            intent.setAction(Intent.ACTION_DIAL);
//            // 隐示意图跳转到打电话界面 Intent （检查是否存在）
//            boolean canResolverIntent = intent.resolveActivity(context.getPackageManager()) != null;
//            return canResolverIntent;
//        } catch (Exception e) {
//            return true;
//        }
//    }

}

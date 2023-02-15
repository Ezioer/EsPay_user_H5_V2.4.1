package com.easou.androidsdk.romutils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.util.CommonUtils;
import com.easou.espay_user_lib.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class RomHelper {

	private static final String TAG = "Helper";
	public static AlertDialog normalDialog;

	/**
	 * 检测悬浮窗权限
	 */
	public static boolean checkFloatWindowPermission(Context context) {
		if (checkPermission(context)) {
			return true;
		} else {
            if (CommonUtils.getSettings(context).getBoolean(
                    Constant.KEY_NEED_SHOW_DIALOG, true)) {
                if (RomHelper.normalDialog == null) {
                    showNormalDialog(context);
                }
            }
            return false;
        }
    }

    public static boolean checkPermission(Context context) {
        //6.0 版本之后由于 google 增加了对悬浮窗权限的管理，所以方式就统一了
        if (Build.VERSION.SDK_INT < 23) {
            if (RomUtils.checkIsMiuiRom()) {
                return miuiPermissionCheck(context);
            } else if (RomUtils.checkIsMeizuRom()) {
                return meizuPermissionCheck(context);
            } else if (RomUtils.checkIsHuaweiRom()) {
                return huaweiPermissionCheck(context);
            } else if (RomUtils.checkIs360Rom()) {
                return qikuPermissionCheck(context);
            } else if (RomUtils.checkIsOppoRom()) {
                return oppoROMPermissionCheck(context);
            }
        }
		return commonROMPermissionCheck(context);
	}

	private static boolean huaweiPermissionCheck(Context context) {
		return HuaweiUtils.checkFloatWindowPermission(context);
	}

	private static boolean miuiPermissionCheck(Context context) {
		return MiuiUtils.checkFloatWindowPermission(context);
	}

	private static boolean meizuPermissionCheck(Context context) {
		return MeizuUtils.checkFloatWindowPermission(context);
	}

	private static boolean qikuPermissionCheck(Context context) {
		return QikuUtils.checkFloatWindowPermission(context);
	}

	private static boolean oppoROMPermissionCheck(Context context) {
		return OppoUtils.checkFloatWindowPermission(context);
	}

	private static boolean commonROMPermissionCheck(Context context) {
		// 最新发现魅族6.0的系统这种方式不好用，天杀的，只有你是奇葩，没办法，单独适配一下
		if (RomUtils.checkIsMeizuRom()) {
			return meizuPermissionCheck(context);
		} else {
			Boolean result = true;
			if (Build.VERSION.SDK_INT >= 23) {
				try {
					Class clazz = Settings.class;
					Method canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
					result = (Boolean) canDrawOverlays.invoke(null, context);
				} catch (Exception e) {
					Log.e(TAG, Log.getStackTraceString(e));
				}
			}
			return result;
		}
	}

	private static void applyPermission(Context context) {
        if (Build.VERSION.SDK_INT < 23) {
            if (RomUtils.checkIsMiuiRom()) {
                miuiROMPermissionApply(context);
            } else if (RomUtils.checkIsMeizuRom()) {
                meizuROMPermissionApply(context);
            } else if (RomUtils.checkIsHuaweiRom()) {
                huaweiROMPermissionApply(context);
            } else if (RomUtils.checkIs360Rom()) {
                ROM360PermissionApply(context);
            } else if (RomUtils.checkIsOppoRom()) {
                oppoROMPermissionApply(context);
            }
        } else {
        	 commonROMPermissionApply(context);
        }
    }

	private static void ROM360PermissionApply(final Context context) {
		QikuUtils.applyPermission(context);
	}

	private static void huaweiROMPermissionApply(final Context context) {
		HuaweiUtils.applyPermission(context);
	}

	private static void meizuROMPermissionApply(final Context context) {
		MeizuUtils.applyPermission(context);
	}

	private static void miuiROMPermissionApply(final Context context) {
		MiuiUtils.applyMiuiPermission(context);
	}
	
	private static void oppoROMPermissionApply(final Context context) {
		OppoUtils.applyOppoPermission(context);
	}

	/**
	 * 通用 rom 权限申请
	 */
	private static void commonROMPermissionApply(final Context context) {
		// 这里也一样，魅族系统需要单独适配
		if (RomUtils.checkIsMeizuRom()) {
			meizuROMPermissionApply(context);
		} else {
			if (Build.VERSION.SDK_INT >= 23) {
				try {
					commonROMPermissionApplyInternal(context);
				} catch (Exception e) {
                    Toast.makeText(context, context.getResources()
                            .getIdentifier("es_settingfloat", "string", context.getPackageName()), Toast.LENGTH_LONG).show();
                    Log.e(TAG, Log.getStackTraceString(e));
                }
			}
		}
	}

	public static void commonROMPermissionApplyInternal(Context context) throws NoSuchFieldException, IllegalAccessException {
        Class clazz = Settings.class;
        Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");

        Intent intent = new Intent(field.get(null).toString());
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

	public static void
	showNormalDialog(final Context context) {
        normalDialog = new AlertDialog.Builder(context)
                .setTitle(context.getResources()
                        .getIdentifier("es_notice", "string", context.getPackageName()))
                .setMessage(context.getResources()
                        .getIdentifier("es_allowfloat", "string", context.getPackageName()))
                .setPositiveButton(context.getResources()
                        .getIdentifier("es_go", "string", context.getPackageName()), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        applyPermission(context);
                    }
                })
                .setNeutralButton(context.getResources()
                        .getIdentifier("es_nomorenotice", "string", context.getPackageName()), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showConfirmDialog(context);
                    }
                })
                .setNegativeButton(context.getResources()
                        .getIdentifier("es_cancel", "string", context.getPackageName()), null)
				.create();
		normalDialog.show();
		normalDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
		normalDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
		normalDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(Color.GRAY);
	}

	public static void showConfirmDialog(final Context context) {
        normalDialog = new AlertDialog.Builder(context)
                .setTitle(context.getResources()
                        .getIdentifier("es_notice", "string", context.getPackageName()))
                .setMessage(context.getResources()
                        .getIdentifier("es_areusure", "string", context.getPackageName()))
                .setPositiveButton(context.getResources()
                        .getIdentifier("es_go", "string", context.getPackageName()), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        applyPermission(context);
                    }
                })
                .setNeutralButton(context.getResources()
                        .getIdentifier("es_nomorenotice", "string", context.getPackageName()), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CommonUtils.getSettings(context).edit().putBoolean(Constant.KEY_NEED_SHOW_DIALOG, false).commit();
                    }
                })
				.create();
		// 显示
		normalDialog.show();
		normalDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
		normalDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(Color.GRAY);
	}
}

package hdtx.androidsdk.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * 单位的转换，如px -> dp
 *
 * @author ： Heavy Yang
 */
public class UnitUtils {

    /**
     * 将px装换成dp
     *
     * @param c
     * @param px
     * @return
     */
    public static int px2dp(Context c, float px) {
        float scale = c.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * dp转px
     *
     * @param context
     * @param dp
     * @return
     */
    public static int dp2px(Context c, float dp) {
        final float scale = c.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * 将sp值转换为px值
     *
     * @param spValue
     * @param fontScale （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * px转sp
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int[] screenDisplay(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        return new int[]{width, height};
    }

    /**
     * 获取sp的像素密度
     *
     * @param context
     * @return
     */
    public static int getSpUnit(Context context) {
        return (int) context.getResources().getDisplayMetrics().scaledDensity;
    }


}

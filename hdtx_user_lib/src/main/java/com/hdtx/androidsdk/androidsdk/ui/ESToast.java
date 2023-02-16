package com.hdtx.androidsdk.androidsdk.ui;

import com.hdtx.androidsdk.androidsdk.util.CommonUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class ESToast {
    private static ESToast toastUtils;
    private static final int TEXT_COLOR_GRAY = 0xff9b9797;

    public ESToast() {
        // TODO Auto-generated constructor stub
    }

    public static ESToast getInstance() {
        if (toastUtils == null) {
            toastUtils = new ESToast();
        }
        return toastUtils;
    }

    public void ToastShow(Context context, String tvString) {
        View layout = LayoutInflater.from(context).inflate(context.getResources().getIdentifier("easou_toast_dialog", "layout", context.getPackageName()), null);
        TextView text = (TextView) layout.findViewById(context.getResources().getIdentifier("easou_id_apToastText", "id", context.getPackageName()));
        text.setText(tvString);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    /**
     * 创建一个Toast用的提示view
     */
    public static View createToastView(Context context, String msg) {
        LinearLayout layout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        // layout.setBackgroundResource(R.drawable.es_welcome_back_toast_bg);
//			Bitmap toastLayoutImg = CommonUtils.getBitmap(context,
//					"es_welcome_back_toast_bg.png");
//			layout.setBackgroundDrawable(new BitmapDrawable(context.getResources(),
//					toastLayoutImg));
        layout.setBackgroundResource(context.getResources().getIdentifier("es_welcome_back_toast_bg", "drawable",
                context.getPackageName()));
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        layout.setLayoutParams(layoutParams);

        ImageView image = new ImageView(context);
        Bitmap luncherImg = CommonUtils.getBitmap(context, "logo.png");
        image.setBackgroundDrawable(new BitmapDrawable(context.getResources(),
                luncherImg));
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        imageParams.setMargins(14, 14, 14, 14);
        layout.addView(image, imageParams);

        TextView tostText = new TextView(context);
        tostText.setText(msg);
        tostText.setTextColor(TEXT_COLOR_GRAY);
        tostText.setTextSize(16);
        LinearLayout.LayoutParams toastTextParams = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        toastTextParams.gravity = Gravity.CENTER_VERTICAL;
        toastTextParams.rightMargin = 50;
        layout.addView(tostText, toastTextParams);
        return layout;
    }
}

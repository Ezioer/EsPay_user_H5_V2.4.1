package hdtx.androidsdk.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author
 */
public class LoadingDialog {

    //	private static Context context;
    private static Dialog dialog;
    // private static ImageView bar;
//	private static TextView textView;
//	private static LayoutParams textViewParams;
//	private static String msg;
//	private static RotateAnimation rotate1, rotate2, rotate3, rotate4, rotate5,
//			rotate6;

    private LoadingDialog() {
    }

    /**
     * 得到整个dialog的布局Layout
     *
     * @param context
     * @param string
     * @return 默认的View
     */
    private static LinearLayout createView(Context context, String string) {
        return createView(context, string, false);
    }


    /**
     * 得到整个dialog的布局Layout
     *
     * @param context
     * @param string
     * @param isNormal true时显示普通的进度条，false时显示特殊的bar
     * @return
     */
    private static LinearLayout createView(Context context, String string,
                                           boolean isNormal) {
        /** init data */
//		context = context;
        String msg = string;
        /** init layout */
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
//		layout.setBackgroundResource(R.drawable.es_pay_bg);
        layout.setGravity(Gravity.CENTER);
//		Bitmap layoutBitmap = CommonUtils.getBitmap(context, "es_pay_bg.png");
//		layout.setBackgroundDrawable(new BitmapDrawable(context.getResources(),layoutBitmap));
        layout.setBackgroundResource(context.getResources().getIdentifier("es_pay_bg", "drawable",
                context.getPackageName()));
//		LayoutParams layoutParams = new LayoutParams(layoutBitmap.getWidth(),
//				layoutBitmap.getHeight());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(layoutParams);
        LinearLayout centerlayout = new LinearLayout(context);
        centerlayout.setOrientation(LinearLayout.VERTICAL);
        TextView textView = new TextView(context);
        textView.setText(msg);
        textView.setTextSize(14);
        textView.setTextColor(Color.BLACK);
        LayoutParams textViewParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        textViewParams.setMargins(0, 21, 0, 6);
        textViewParams.gravity = Gravity.CENTER;
        centerlayout.addView(textView, textViewParams);

        // init ProgressBar
        View bar = createProgress(context, isNormal);
        LayoutParams barParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        barParams.gravity = Gravity.CENTER;
        barParams.setMargins(0, 21, 0, 6);
        centerlayout.addView(bar, barParams);
        layout.addView(centerlayout);
        // init TextView

        return layout;
    }


    /**
     * @param context
     * @param isNormal
     * @return
     */
    private static View createProgress(Context context, boolean isNormal) {
        if (!isNormal) {
            FrameLayout layout = new FrameLayout(context);
            android.widget.FrameLayout.LayoutParams layoutParams = new android.widget.FrameLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layout.setLayoutParams(layoutParams);

            // 定义旋转动画
            RotateAnimation rotate1 = new RotateAnimation(0f, 270f, 36.5f, 36.5f);
            rotate1.setDuration(1000);
            rotate1.setRepeatCount(10000);

            RotateAnimation rotate2 = new RotateAnimation(0f, -270f, 36.5f, 36.5f);
            rotate2.setDuration(1000);
            rotate2.setRepeatCount(10000);

            RotateAnimation rotate3 = new RotateAnimation(0f, 180f, 36.5f, 36.5f);
            rotate3.setDuration(1000);
            rotate3.setRepeatCount(10000);

            RotateAnimation rotate4 = new RotateAnimation(0f, -180f, 36.5f, 36.5f);
            rotate4.setDuration(1000);
            rotate4.setRepeatCount(10000);

            RotateAnimation rotate5 = new RotateAnimation(0f, -180f, 36.5f, 36.5f);
            rotate5.setDuration(1000);
            rotate5.setRepeatCount(10000);

            RotateAnimation rotate6 = new RotateAnimation(0f, 180f, 36.5f, 36.5f);
            rotate6.setDuration(1000);
            rotate6.setRepeatCount(10000);

            rotate1.setRepeatMode(Animation.REVERSE);
            rotate2.setRepeatMode(Animation.REVERSE);
            rotate3.setRepeatMode(Animation.REVERSE);
            rotate4.setRepeatMode(Animation.REVERSE);
            rotate5.setRepeatMode(Animation.REVERSE);
            rotate6.setRepeatMode(Animation.REVERSE);
            // 初始化要旋转的图片
            ImageView innerImg1 = new ImageView(context);
//			innerImg1.setBackgroundResource(R.drawable.es_inner1_dark);
//			innerImg1.setBackgroundDrawable(new BitmapDrawable(CommonUtils.getBitmap(context, "es_inner1_dark.png")));
            innerImg1.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
                    context.getResources().getIdentifier("es_inner1_dark", "drawable", context.getPackageName())));
            innerImg1.setAnimation(rotate1);

            ImageView innerImg2 = new ImageView(context);
            innerImg2.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
                    context.getResources().getIdentifier("es_inner1_light", "drawable", context.getPackageName())));
//			innerImg2.setBackgroundDrawable(new BitmapDrawable(CommonUtils.getBitmap(context, "es_inner1_light.png")));
//			setBackgroundResource(R.drawable.es_inner1_light);
            innerImg2.setAnimation(rotate2);

            ImageView innerImg3 = new ImageView(context);
            innerImg3.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
                    context.getResources().getIdentifier("es_inner2_dark", "drawable", context.getPackageName())));
//			innerImg3.setBackgroundDrawable(new BitmapDrawable(CommonUtils.getBitmap(context, "es_inner2_dark.png")));
//			setBackgroundResource(R.drawable.es_inner2_dark);
            innerImg3.setAnimation(rotate3);

            ImageView innerImg4 = new ImageView(context);
            innerImg4.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
                    context.getResources().getIdentifier("es_inner2_light", "drawable", context.getPackageName())));
//			innerImg4.setBackgroundDrawable(new BitmapDrawable(CommonUtils.getBitmap(context, "es_inner2_light.png")));
//			setBackgroundResource(R.drawable.es_inner2_light);
            innerImg4.setAnimation(rotate4);

            ImageView innerImg5 = new ImageView(context);
            innerImg5.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
                    context.getResources().getIdentifier("es_inner3_dark", "drawable", context.getPackageName())));
//			innerImg5.setBackgroundDrawable(new BitmapDrawable(CommonUtils.getBitmap(context, "es_inner3_dark.png")));
//			setBackgroundResource(R.drawable.es_inner3_dark);
            innerImg5.setAnimation(rotate5);

            ImageView innerImg6 = new ImageView(context);
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                    context.getResources().getIdentifier("es_inner3_light", "drawable", context.getPackageName()));
//			Bitmap bitmap = CommonUtils.getBitmap(context, "es_inner3_light.png");
            innerImg6.setImageBitmap(bitmap);
//			innerImg6.setBackgroundDrawable(new BitmapDrawable(bitmap));
//			setBackgroundResource(R.drawable.es_inner3_light);
            innerImg6.setAnimation(rotate6);

            LinearLayout.LayoutParams imageLp = new LinearLayout.LayoutParams(73, 73);
//			LinearLayout.LayoutParams imageLp = new LinearLayout.LayoutParams(bitmap.getWidth(),bitmap.getHeight());

            layout.addView(innerImg1, imageLp);
            layout.addView(innerImg2, imageLp);
            layout.addView(innerImg3, imageLp);
            layout.addView(innerImg4, imageLp);
            layout.addView(innerImg5, imageLp);
            layout.addView(innerImg6, imageLp);

            return layout;
        }
        return new ProgressBar(context);
    }


    /**
     * 显示安全加载对话框
     *
     * @param context
     * @param string
     * @param isNormal
     */
    public static void show(Context context, String string, boolean isNormal) {
//		if (dialog != null && dialog.isShowing()) {
//			return;
//		}
        dismiss();

        /** create Layout */
        LinearLayout layout = createView(context, string, isNormal);
        /** init dialog */
        dialog = new Dialog(context, android.R.style.Theme_Panel);
        dialog.setContentView(layout);

        dialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                if (listener != null) {
                    listener.onDismiss();
                }
            }
        });
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.65f;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();

//		rotate1.start();
//		rotate2.start();
//		rotate3.start();
//		rotate4.start();
//		rotate5.start();
//		rotate6.start();
    }

    /**
     * 隐藏对话框
     */
    public static void dismiss() {
        if (dialog != null) {
            if (dialog.isShowing())
                dialog.dismiss();
            dialog = null;
        }
    }

    /**
     * 释放dialog资源
     */
    public static void release() {
        dismiss();
    }

    public interface IDismissListener {
        void onDismiss();
    }

    private static IDismissListener listener;


    public IDismissListener getListener() {
        return listener;
    }

    public static void setListener(IDismissListener l) {
        listener = l;
    }
}

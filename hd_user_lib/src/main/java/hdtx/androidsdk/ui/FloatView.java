package hdtx.androidsdk.ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;

import hdtx.androidsdk.plugin.StartESUserPlugin;

public class FloatView extends View {

    private DisplayMetrics displayMetrics;
    private Drawable drawable;
    private Drawable fhzjDrawable;
    private int firstX, firstY, lastX, lastY;
    private View floatViewLayout;
    private ImageView imageview;

    private int mPreviousPosition_x;
    private int mPreviousPosition_y;
    private boolean isViewadded = false;
    private static boolean isClosed = false;

    private OnTouchListener mTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            try {
                final int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN) {
                    mPreviousPosition_x = mWMParams.x;
                    mPreviousPosition_y = mWMParams.y;
                    firstX = (int) event.getRawX();
                    firstY = (int) event.getRawY();
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                } else if (action == MotionEvent.ACTION_MOVE) {
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();

                    int totalDeltaX = lastX - firstX;
                    int totalDeltaY = lastY - firstY;

                    if (Math.abs(totalDeltaX) >= 10 || Math.abs(totalDeltaY) >= 10) {
                        mWMParams.x = mPreviousPosition_x + totalDeltaX;
                        mWMParams.y = mPreviousPosition_y + totalDeltaY;
//						imageview.setImageDrawable(drawable);
                        mWManager.updateViewLayout(floatViewLayout, mWMParams);
                    }
                } else if (action == MotionEvent.ACTION_UP) {
                    if (isClick(firstX, firstY, lastX, lastY)) {

                        StartESUserPlugin.showSdkView();

                    } else {
                        if (mWMParams.x > 0) {
//							imageview.setImageDrawable(drawable);
                            mWMParams.x = displayMetrics.widthPixels / 2;
                            mWManager.updateViewLayout(floatViewLayout, mWMParams);
                        } else {
//							imageview.setImageDrawable(drawable);
                            mWMParams.x = -displayMetrics.widthPixels / 2;
                            mWManager.updateViewLayout(floatViewLayout, mWMParams);
                        }
                    }
                    firstX = firstY = lastX = lastY = 0;
                }
                return true;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
        }
    };
    private WindowManager mWManager;

    private WindowManager.LayoutParams mWMParams = null;

    private FloatView(Activity activity) {
        super(activity);

        mWManager = (WindowManager) activity.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        displayMetrics = new DisplayMetrics();
        ;
        if (Build.VERSION.SDK_INT > 17) {
            mWManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        } else {
            mWManager.getDefaultDisplay().getMetrics(displayMetrics);
        }

        floatViewLayout = LayoutInflater.from(activity).inflate(
                activity.getResources().getIdentifier("hd_floatview", "layout", activity.getPackageName()),
                null);
        floatViewLayout.setBackgroundColor(Color.TRANSPARENT);
        imageview = (ImageView) floatViewLayout
                .findViewById(activity.getResources().getIdentifier("floatimage", "id", activity.getPackageName()));

        floatViewLayout.setOnTouchListener(mTouchListener);

        drawable = activity.getResources().getDrawable(activity.getApplication().getResources()
                .getIdentifier("es_floaticon", "drawable", activity.getApplication().getPackageName()));
        fhzjDrawable = activity.getResources().getDrawable(activity.getApplication().getResources()
                .getIdentifier("es_floaticon_fhzj", "drawable", activity.getApplication().getPackageName()));
        if (activity.getPackageName().contains("fhzj")) {
            //绯红之境改用新图标
            imageview.setImageDrawable(fhzjDrawable);
        } else {
            imageview.setImageDrawable(drawable);
        }

        mWMParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= 26) {//8.0新特性
            mWMParams.type = 2038;
        } else {
            mWMParams.type = 2003;
        }
        mWMParams.flags = 40;
        mWMParams.width = LayoutParams.WRAP_CONTENT;
        mWMParams.height = LayoutParams.WRAP_CONTENT;

        mWMParams.format = -3;
        if (getOrientation(activity) == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            mWMParams.x = displayMetrics.widthPixels / 2;
        else
            mWMParams.x = displayMetrics.heightPixels / 2;
//		imageview.setAlpha(128);
    }

    private static int getOrientation(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int orientation = display.getOrientation();
        return orientation;
    }

    private boolean isClick(int firstX, int firstY, int lastX, int lastY) {
        float xDifference = Math.abs(firstX - lastX);
        float yDifference = Math.abs(firstY - lastY);
        double difference = Math.sqrt(Math.pow(xDifference, 2) + Math.pow(yDifference, 2));
        if (difference - 10 > 0) {
            Log.i("distance", "distance " + (difference - 10));
            return false;
        }
        return true;
    }


    private volatile static FloatView floatView = null;

    public static FloatView getInstance(Activity activity) {
        if (floatView == null) {
            synchronized (FloatView.class) {
                if (floatView == null) {
                    floatView = new FloatView(activity);
                }
            }
        }
        return floatView;

    }

    public static void show(Activity activity) {

        try {
            if (floatView == null) {
                floatView = getInstance(activity);
                isClosed = false;
            } else {
                isClosed = false;
            }

            floatView.showFloatview();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showFloatview() {
        try {
            if (!isClosed) {
                if (!isViewadded) {
                    mWManager.addView(floatViewLayout, mWMParams);
                    isViewadded = true;
                }
            }
        } catch (Exception e) {
            mWManager.updateViewLayout(floatViewLayout, mWMParams);
            isViewadded = true;
            e.printStackTrace();
        }
    }

    public static void close() {
        if (floatView != null) {
            floatView.closeFloatview();
        } else {
            isClosed = true;
        }
    }

    public void closeFloatview() {
        if (isViewadded) {
            mWManager.removeView(floatViewLayout);
        }
        isViewadded = false;
    }
}
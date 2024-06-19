package hdtx.androidsdk.util;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdSize;

public class GAdUtils {
    public static AdSize getAdSize(Activity activity, FrameLayout adContainerView) {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = adContainerView.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }
}

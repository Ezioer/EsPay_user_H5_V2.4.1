package hdtx.androidsdk.callback;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.Date;

import hdtx.androidsdk.Starter;
import hdtx.androidsdk.util.ESdkLog;
import hdtx.androidsdk.util.ThreadPoolManager;

public class AppOpenAdManager {
    private static final String LOG_TAG = "AppOpenAdManager";
    private static String AD_UNIT_ID = "";

    private AppOpenAd appOpenAd = null;
    private boolean isLoadingAd = false;
    public boolean isShowingAd = false;
    private long loadTime = 0;
    private Activity mTempActivity = null;
    private Starter.OnShowAdCompleteListener mTempCallback = null;
    private boolean showReady = false;
//    private Handler adHandler = new Handler(Looper.myLooper());

    /**
     * Constructor.
     */
    public AppOpenAdManager(String unitId) {
        AD_UNIT_ID = unitId;
    }

    /**
     * Request an ad.
     */
    public void loadAd(Context context) {
        // We will implement this below.
        if (isLoadingAd || isAdAvailable()) {
            return;
        }

        isLoadingAd = true;
        AdRequest request = new AdRequest.Builder().build();
        AppOpenAd.load(
                context, AD_UNIT_ID, request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(AppOpenAd ad) {
                        // Called when an app open ad has loaded.
                        ESdkLog.c(LOG_TAG, "Ad was loaded.");
                        appOpenAd = ad;
                        isLoadingAd = false;
                        loadTime = (new Date()).getTime();
                        if (showReady) {
                            ESdkLog.c(LOG_TAG, "Ready to show ad");
                            showReady = false;
                            if (mTempActivity != null) {
                                showAdIfAvailable(mTempActivity, mTempCallback, false);
                            }
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        // Called when an app open ad has failed to load.
                        ESdkLog.c(LOG_TAG, loadAdError.getMessage());
                        isLoadingAd = false;
                    }
                });
    }

    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    /**
     * Check if ad exists and can be shown.
     */
    private boolean isAdAvailable() {
        return appOpenAd != null && (loadTime !=0L && wasLoadTimeLessThanNHoursAgo(4));
    }

    /**
     * Show the ad if one isn't already showing.
     */
    public void showAdIfAvailable(@NonNull final Activity activity) {
        showAdIfAvailable(
                activity,
                new Starter.OnShowAdCompleteListener() {
                    @Override
                    public void onShowAdComplete() {
                        // Empty because the user will go back to the activity that shows the ad.
                    }

                    @Override
                    public void onAppOpenAdShow() {

                    }
                }, false);
    }

    /**
     * Shows the ad if one isn't already showing.
     */
    public void showAdIfAvailable(
            @NonNull final Activity activity,
            @NonNull Starter.OnShowAdCompleteListener onShowAdCompleteListener, boolean show) {
        // If the app open ad is already showing, do not show the ad again.
        if (isShowingAd) {
            ESdkLog.c(LOG_TAG, "The app open ad is already showing.");
            return;
        }

        // If the app open ad is not available yet, invoke the callback then load the ad.
        if (!isAdAvailable()) {
            if (show) {
                ESdkLog.c(LOG_TAG, "Show when ad ready");
                this.showReady = true;
                mTempActivity = activity;
                mTempCallback = onShowAdCompleteListener;
            } else {
                onShowAdCompleteListener.onShowAdComplete();
            }
            loadAd(activity);
            ESdkLog.c(LOG_TAG, "The app open ad is not ready yet.");
            return;
        }

        appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent();
                ESdkLog.c(LOG_TAG, "Ad dismissed fullscreen content.");
                appOpenAd = null;
                isShowingAd = false;
                mTempActivity = null;
                mTempCallback = null;
//                adHandler.removeCallbacksAndMessages(null);
//                adHandler = null;
                onShowAdCompleteListener.onShowAdComplete();
                loadAd(activity);
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                ESdkLog.c(LOG_TAG, adError.getMessage());
                appOpenAd = null;
                isShowingAd = false;
                mTempActivity = null;
                mTempCallback = null;
                onShowAdCompleteListener.onShowAdComplete();
                loadAd(activity);
            }

            @Override
            public void onAdShowedFullScreenContent() {
                ESdkLog.c(LOG_TAG, "Ad showed fullscreen content.");
               /* if (adHandler != null) {
                    adHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            appOpenAd.getFullScreenContentCallback().onAdDismissedFullScreenContent();
                        }
                    }, 4000);
                }*/
            }
        });
        isShowingAd = true;
        appOpenAd.setImmersiveMode(true);
        appOpenAd.show(activity);
        ESdkLog.c(LOG_TAG, "Ad is showing");
        onShowAdCompleteListener.onAppOpenAdShow();
    }

    public void stopAutoShow() {
        mTempActivity = null;
        mTempCallback = null;
        showReady = false;
    }
}

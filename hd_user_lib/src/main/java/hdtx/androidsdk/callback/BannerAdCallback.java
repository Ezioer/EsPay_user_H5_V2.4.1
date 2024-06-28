package hdtx.androidsdk.callback;

import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.LoadAdError;

public class BannerAdCallback extends AdListener {
    private static final String TAG = "BannerAdCallback";
    @Override
    public void onAdClicked() {
        // Code to be executed when the user clicks on an ad.
        Log.e(TAG, "Banner Ad click");
    }

    @Override
    public void onAdClosed() {
        // Code to be executed when the user is about to return
        // to the app after tapping on an ad.
        Log.e(TAG, "Banner Ad close");
    }

    @Override
    public void onAdFailedToLoad(LoadAdError adError) {
        // Code to be executed when an ad request fails.
        Log.e(TAG, "Banner Ad failed ");
    }

    @Override
    public void onAdImpression() {
        // Code to be executed when an impression is recorded
        // for an ad.
        Log.e(TAG, "Banner Ad impression");
    }

    @Override
    public void onAdOpened() {
        // Code to be executed when an ad opens an overlay that
        // covers the screen.
        Log.e(TAG, "Banner Ad open");
    }
}

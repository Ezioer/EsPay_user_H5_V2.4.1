package hdtx.androidsdk.callback;

import android.util.Log;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;

public class GAdsCallback extends FullScreenContentCallback  {
    private static final String TAG = "GAdsCallback";
    @Override
    public void onAdClicked() {
        // Called when a click is recorded for an ad.
        Log.d(TAG, "Ad was clicked.");
    }

    @Override
    public void onAdImpression() {
        // Called when an impression is recorded for an ad.
        Log.d(TAG, "Ad recorded an impression.");
    }

    @Override
    public void onAdShowedFullScreenContent() {
        // Called when ad is shown.
        Log.d(TAG, "Ad showed fullscreen content.");
    }
}

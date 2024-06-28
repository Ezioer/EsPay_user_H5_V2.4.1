package hdtx.androidsdk.callback;

import android.util.Log;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;

public interface GAdsCallback {
   //广告加载成功
   void onAdLoaded();
   //广告加载失败
   void onAdFailedToLoad();
   //广告被点击
   void onAdClicked();
   //广告被隐藏
   void onAdDismissed();
   //广告展示
   void onAdShowed();
   //广告被记录为展示成功
   void onAdImpression();
   //广告展示失败
   void onAdFailedToShow();
   //横幅广告打开覆盖屏幕的叠加层时调用此方法
   void onAdOpened();
   //用户在查看广告的目标网址后返回应用时，系统会调用 onAdClosed() 方法。应用可以使用此方法恢复暂停的活动，或执行任何其他必要的操作，以做好互动准备
   void onAdClosed();
   //激励广告的奖励
   void onRewardEarned(int rewardAmount,String rewardType);
}

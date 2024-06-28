package hdtx.androidsdk.callback;

public interface OpenAdCallback {
   //广告展示
   void onAdShow();
   //用户在查看广告的目标网址后返回应用时，系统会调用 onAdClosed() 方法。应用可以使用此方法恢复暂停的活动，或执行任何其他必要的操作，以做好互动准备
   void onAdComplete();
}

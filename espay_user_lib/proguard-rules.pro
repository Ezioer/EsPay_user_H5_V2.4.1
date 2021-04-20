
-dontoptimize
-optimizations !field/removal/writeonly,!field/marking/private,!class/merging/*,!code/allocation/variable
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-optimizationpasses 2
-dontusemixedcaseclassnames
-verbose
-dontwarn com.payeco.android.plugin.**,com.**,com.google.gson.**,com.heepay.plugin.**,com.baidu.location.**,com.switfpass.pay.**,org.apache.http.entity.mime.**
-ignorewarnings

-keep public class * extends android.app.Activity

-keep public class * extends android.app.Application

-keep public class * extends android.app.Service

-keep public class * extends android.content.BroadcastReceiver

-keep public class * extends android.content.ContentProvider

-keep public class * extends android.app.backup.BackupAgentHelper

-keep public class * extends android.preference.Preference

-keep public class com.android.vending.licensing.ILicensingService

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-keepclasseswithmembers class * {
    public <init>(android.content.Context,android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context,android.util.AttributeSet,int);
}

-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

-keep class * extends android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keep class com.easou.androidsdk.util.LogcatHelper {
    <fields>;
    <methods>;
}

-keep class com.easou.androidsdk.Starter {
    <fields>;
    <methods>;
}


#-keep class com.easou.androidsdk.callback.ESdkCallback{
#    <fields>;
#    <methods>;
#}

-keep class com.easou.androidsdk.data.ESConstant {
    <fields>;
    <methods>;
}

-keep class com.easou.androidsdk.data.PayItem{
    <fields>;
    <methods>;
}


-keep class com.easou.androidsdk.util.FileHelper{
    <fields>;
    <methods>;
}

-keep class com.easou.androidsdk.sso.AuthBean {
    <fields>;
    <methods>;
}

-keep class com.easou.androidsdk.sso.EucToken {
    <fields>;
    <methods>;
}

-keep class com.easou.androidsdk.sso.JUser {
    <fields>;
    <methods>;
}

-keep class com.easou.androidsdk.sso.EucUCookie{
    <fields>;
    <methods>;
}


-keepclasseswithmembernames class * {
native <methods>;
}

-keep class com.easou.androidsdk.callback.** {*;}

## Android architecture components: Lifecycle
# LifecycleObserver's empty constructor is considered to be unused by proguard
-keepclassmembers class * implements android.arch.lifecycle.LifecycleObserver {
    <init>(...);
}
# ViewModel's empty constructor is considered to be unused by proguard
-keepclassmembers class * extends android.arch.lifecycle.ViewModel {
    <init>(...);
}
# keep Lifecycle State and Event enums values
-keepclassmembers class android.arch.lifecycle.Lifecycle$State { *; }
-keepclassmembers class android.arch.lifecycle.Lifecycle$Event { *; }
# keep methods annotated with @OnLifecycleEvent even if they seem to be unused
# (Mostly for LiveData.LifecycleBoundObserver.onStateChange(), but who knows)
-keepclassmembers class * {
    @android.arch.lifecycle.OnLifecycleEvent *;
}

-dontwarn com.gism.**
-keep class com.gism.** {*;}
-keep class com.kwai.monitor.** {*;}

-dontwarn com.qq.gdt.action.**
-keep class com.qq.gdt.action.** {*;}

-keepclasseswithmembers class * {
native <methods>;
}

-keep class com.ss.android.common.**{*;}

-keep class com.google.gson.** {*;}

-keep class com.bun.miitmdid.core.** {*;}
-keep class org.apache.**{*;}
-keep class com.heepay.plugin.** {*;}
-keep class com.snail.antifake.** {*;}
-keep class com.payeco.android.plugin.** {*;}
-dontwarn com.payeco.android.plugin.**
-keepclassmembers class com.payeco.android.plugin {
  public *;
}

-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepparameternames
-keep class com.bytedance.applog.AppLog { public *; }
-keep public interface com.bytedance.applog.IDataObserver { *; }
-keep public interface com.bytedance.applog.IAppParam { *; }
-keep public interface com.bytedance.applog.IExtraParams { *; }
-keep public interface com.bytedance.applog.IPicker { *; }
-keep public interface com.bytedance.applog.IOaidObserver { *; }
-keep class com.bytedance.applog.IOaidObserver$Oaid { *; }
-keep class com.bytedance.applog.GameReportHelper { public *; }
-keep class com.bytedance.applog.WhalerGameHelper { *; }
-keep class com.bytedance.applog.WhalerGameHelper$Result { *; }
-keep class com.bytedance.applog.InitConfig { public *; }
-keep class com.bytedance.applog.util.UriConfig { public *; }
-keep class com.bytedance.applog.tracker.Tracker { public *; }
-keep class com.bytedance.applog.picker.Picker { public *; }
-keep class com.bytedance.applog.tracker.WebViewJsUtil { *; }
-keep interface com.bytedance.base_bdtracker.bt { public *; }
-keep class com.bytedance.base_bdtracker.bt$a { public *; }
-keep class com.bytedance.base_bdtracker.bt$a$a { public *; }
-dontwarn com.tencent.smtt.sdk.WebView
-dontwarn com.tencent.smtt.sdk.WebChromeClient

-dontwarn androidx.annotation.Nullable
-dontwarn androidx.annotation.NonNull
-dontwarn com.google.android.gms.ads.identifier.AdvertisingIdClient
-dontwarn com.google.android.gms.ads.identifier.AdvertisingIdClient$Info
-dontwarn androidx.appcompat.app.AlertDialog
-dontwarn androidx.appcompat.view.menu.ListMenuItemView
-dontwarn androidx.recyclerview.widget.RecyclerView
-dontwarn androidx.swiperefreshlayout.widget.SwipeRefreshLayout
-dontwarn androidx.viewpager.widget.ViewPager
-dontwarn androidx.recyclerview.widget.RecyclerView
-dontwarn androidx.annotation.RequiresApi
-dontwarn androidx.fragment.app.FragmentActivity
-dontwarn androidx.fragment.app.Fragment
-dontwarn androidx.annotation.AnyThread
-dontwarn androidx.annotation.WorkerThread

-keepattributes *Annotation*
-keepattributes *JavascriptInterface*

# Also keep - Enumerations. Keep the special static methods that are required in
# enumeration classes.
-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep names - Native method names. Keep all native class/method names.
-keepclasseswithmembers,allowshrinking class * {
    native <methods>;
}

-keepclassmembers class com.easou.androidsdk.ESPlatform {
   public *;
}
-dontwarn com.iqiyi.qilin.trans.**
-keep class com.iqiyi.qilin.trans.** {*;}

-dontwarn com.baidu.mobads.action.**
-keep class com.baidu.mobads.action.** {*;}

-keep class com.bytedance.applog.picker.DomSender { public *; }

-keep class com.bytedance.dr.VivoIdentifier {*;}
-keep class com.bytedance.dr.VivoIdentifier$* {*;}

# 这个 ProGuard 文件被指定为 consumerProguardFiles。
# 如此一来，AAR 包的使用者在其应用进行 ProGuard 混淆时，将自动附加下列规则，
# 省去了接入 JAR 时手动在 ProGuard 规则文件中加入支付宝 SDK 规则的步骤。

-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-keep class com.alipay.sdk.app.H5PayCallback {
    <fields>;
    <methods>;
}
-keep class com.alipay.android.phone.mrpc.core.** { *; }
-keep class com.alipay.apmobilesecuritysdk.** { *; }
-keep class com.alipay.mobile.framework.service.annotation.** { *; }
-keep class com.alipay.mobilesecuritysdk.face.** { *; }
-keep class com.alipay.tscenter.biz.rpc.** { *; }
-keep class org.json.alipay.** { *; }
-keep class com.alipay.tscenter.** { *; }
-keep class com.ta.utdid2.** { *;}
-keep class com.ut.device.** { *;}

-dontwarn com.ta.utdid2.**
-dontwarn com.ut.device.**

-dontwarn com.alipay.mobilesecuritysdk.**
-dontwarn com.alipay.security.**

-dontwarn android.net.SSLCertificateSocketFactory

-keep class com.tencent.mm.opensdk.** {
    *;
}

-keep class com.tencent.wxop.** {
    *;
}

-keep class com.tencent.mm.sdk.** {
    *;
}
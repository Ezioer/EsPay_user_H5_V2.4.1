package hdtx.androidsdk.callback;

public interface ESdkPayCallback {

    void onPaySuccess();

    //1004宜搜下单失败
    void onPayFail(int code);

}

package com.easou.androidsdk.callback;

public interface ESdkPayCallback {

    void onPaySuccess();

    //1000用户取消支付
    //1001支付失败
    //1002服务器验证交易失败，等验证成功后会继续回调支付成功接口
    //1003已拥有该商品
    void onPayFail(int code);

}

package com.hdtx.androidsdk.data;

import java.io.Serializable;

/**
 * 订单信息，应存储在接入方的服务器中，有服务器生成订单信息后传递给客户端，再由客户端传递给SDK
 *
 * @author Erica
 */
public final class PayItem implements Serializable {

    private static final long serialVersionUID = 2813530717861439277L;
    private String createDatetime;//交易时间
    private String invoice;//订单号
    private String paidFee;//eb数量（正数是用户e币充值数，负数是用户消费e币数）
    private String tradeName;//交易名称
    private String channelName;
    private String paidFeeMoney;

    public String getPaidFeeMoney() {
        return paidFeeMoney;
    }

    public void setPaidFeeMoney(String paidFeeMoney) {
        this.paidFeeMoney = paidFeeMoney;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    private int type;//-1=支出 1=充值
    private boolean hasNext = true;


    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public String getPaidFee() {
        return paidFee;
    }

    public void setPaidFee(String paidFee) {
        this.paidFee = paidFee;
    }

    public String getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(String createDatetime) {
        this.createDatetime = createDatetime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


}

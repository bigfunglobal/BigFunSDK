package com.bigfun.tm;

public interface NetConstant {
    String BMARTPAY_BASE_URL = "https://pay.bmartpay.com/api/";
    String BIGFUN_BASE_URL = "https://bigfun.xiaoxiangwan.com/api/";

    /**
     * 登录
     */
    String LOGIN = BIGFUN_BASE_URL + "sdkuser/blogin";

    /**
     * 发送验证码
     */
    String SEND_SMS = BIGFUN_BASE_URL + "sdkuser/sendsms";

    /**
     * 充值下单
     */
    String RECHARGE_ORDER = BIGFUN_BASE_URL + "sdkpayment/order";

    /**
     * 获取渠道配置
     */
    String GET_CHANNEL_CONFIG = BIGFUN_BASE_URL + "sdkapp/getgameconfig";

    /**
     * 获取可用的充值通道
     */
    String GET_RECHARGE_CHANNEL = BIGFUN_BASE_URL + "sdkpayment/QueryPaymentPassageByChannelCode";

    /**
     * 获取可用的提现通道
     */
    String GET_WITHDRAW_CHANNEL = BIGFUN_BASE_URL + "sdkpayment/QueryPayoutPassageByChannelCode";

    /**
     * 下单
     */
    String PAY_URL = BMARTPAY_BASE_URL + "sdkpayment/sorder";
}

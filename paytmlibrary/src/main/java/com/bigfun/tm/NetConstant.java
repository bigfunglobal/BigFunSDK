package com.bigfun.tm;

public interface NetConstant {
    String BASE_URL = "https://bigfun.xiaoxiangwan.com/api/";

//    String BASE_URL = "http://47.241.62.219:8463/api/";
    /**
     * 登录
     */
    String LOGIN = BASE_URL + "sdkuser/blogin";

    /**
     * 发送验证码
     */
    String SEND_SMS = BASE_URL + "sdkuser/sendsms";

    /**
     * 充值下单
     */
    String RECHARGE_ORDER = BASE_URL + "sdkpayment/order";

    /**
     * 提现下单
     */
    String WITHDRAW_ORDER = BASE_URL + "sdkpayment/trans";

    /**
     * 获取渠道配置
     */
    String GET_CHANNEL_CONFIG = BASE_URL + "sdkapp/getgameconfig";

    /**
     * 获取可用的充值通道
     */
    String GET_RECHARGE_CHANNEL = BASE_URL + "sdkpayment/QueryPaymentPassageByChannelCode";

    /**
     * 获取可用的提现通道
     */
    String GET_WITHDRAW_CHANNEL = BASE_URL + "sdkpayment/QueryPayoutPassageByChannelCode";

    /**
     * 充值
     */
    String PAY_URL = BASE_URL + "sdkpayment/sorder";
}

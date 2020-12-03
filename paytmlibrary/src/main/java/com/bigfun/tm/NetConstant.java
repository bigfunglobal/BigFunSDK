package com.bigfun.tm;

public interface NetConstant {
    String BMARTPAY_BASE_URL = "https://pay.bmartpay.com/api/";
    String BIGFUN_BASE_URL = "https://bigfun.xiaoxiangwan.com/api/";

    /**
     * 登录
     */
    String LOGIN = BIGFUN_BASE_URL + "sdkuser/blogin";

    /**
     * 下单
     */
    String PAY_URL = BMARTPAY_BASE_URL + "sdkpayment/sorder";
}

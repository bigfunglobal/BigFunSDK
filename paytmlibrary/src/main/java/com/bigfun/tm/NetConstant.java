package com.bigfun.tm;

public interface NetConstant {
    String BMARTPAY_BASE_URL = "http://pay.bmartpay.com/api/";
    //    String BMARTPAY_BASE_URL = "http://47.241.62.219:8463/api/";
    String BIGFUN_BASE_URL = "http://bigfun.xiaoxiangwan.com/api/";
    String PLATFORM_BASE_URL = "http://platformapi.xiaoxiangwan.com/api/";

    /**
     * 登录
     */
    String LOGIN = BIGFUN_BASE_URL + "sdkuser/blogin";

    /**
     * 下单
     */
    String PAY_URL = BMARTPAY_BASE_URL + "sdkpayment/sorder";

    /**
     * 上报
     */
    String REPORT_URL = PLATFORM_BASE_URL + "exportClickhouse/exportGameUserAction";
}

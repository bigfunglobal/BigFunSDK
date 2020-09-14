package com.bigfun.tm

const val BASE_URL = "https://bigfun.xiaoxiangwan.com/api/"

//const val BASE_URL = "http://192.168.188.248:8463/api/"
//const val BASE_URL = "http://192.168.188.93:8463/api/"

/**
 * 登录
 */
const val LOGIN = "${BASE_URL}sdkuser/blogin"

/**
 * 发送验证码
 */
const val SEND_SMS = "${BASE_URL}sdkuser/sendsms"

/**
 * 充值下单
 */
const val RECHARGE_ORDER = "${BASE_URL}sdkpayment/order"

/**
 * 提现下单
 */
const val WITHDRAW_ORDER = "${BASE_URL}sdkpayment/trans"

/**
 * 获取渠道配置
 */
const val GET_CHANNEL_CONFIG = "${BASE_URL}sdkapp/getgameconfig"
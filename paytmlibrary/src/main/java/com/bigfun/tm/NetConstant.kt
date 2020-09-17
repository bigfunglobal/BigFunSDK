package com.bigfun.tm

const val BASE_URL = "https://bigfun.xiaoxiangwan.com/api/"

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

/**
 * 获取可用的充值通道
 */
const val GET_RECHARGE_CHANNEL = "${BASE_URL}sdkpayment/QueryPaymentPassageByChannelCode"

/**
 * 获取可用的提现通道
 */
const val GET_WITHDRAW_CHANNEL = "${BASE_URL}sdkpayment/QueryPayoutPassageByChannelCode"

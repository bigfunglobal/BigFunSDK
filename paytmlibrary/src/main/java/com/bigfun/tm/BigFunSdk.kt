package com.bigfun.tm

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.support.annotation.Keep
import android.util.Log
import android.widget.Toast
import com.bigfun.tm.encrypt.MD5Utils
import com.bigfun.tm.login.Callback
import java.util.*
import kotlin.concurrent.thread

@Keep
class BigFunSdk private constructor() {

    private val treeMap = TreeMap<String, Any?>(String::compareTo)
    private val sb = StringBuilder()
    private var mPhone: String = ""

    @Keep
    companion object {

        internal var mContext: Context? = null
        internal var mChannel = ""
        internal var mKey = ""

        @JvmStatic
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            BigFunSdk()
        }

        @Keep
        fun init(
            context: Context,
            channel: String,
            key: String
        ) {
            mContext = context
            mChannel = channel
            mKey = key
            Log.d(PAY_TAG, "init: init success")
        }
    }

    /**
     * 登录
     */
    @Keep
    internal fun login(
        params: Map<String, Any>,
        listener: ResponseListener
    ) {
        thread {
            check()
            if (!params.containsKey("loginType")) {
                throw IllegalArgumentException("$PAY_TAG 缺少参数")
            }
            if (params["loginType"] == 2) {
                if (!params.containsKey("mobile") || !params.containsKey(
                        "code"
                    ) || params["code"] == null
                ) {
                    throw IllegalArgumentException("$PAY_TAG 缺少参数")
                }
                if (params["code"].toString() != HttpUtils.instance.smsCode || mPhone != params["mobile"]) {
                    listener.onFail("请输入正确的验证码")
                    return@thread
                }
            }
            val map = mutableMapOf<String, Any>()
            map.putAll(params)
            map["deviceType"] = "Android"
            map["deviceModel"] = Build.MODEL
            map["deviceBrand"] = Build.BRAND
            map["aaid"] = AdvertisingIdClient.getAdId(mContext!!)
            map["androidId"] =
                Settings.System.getString(mContext!!.contentResolver, Settings.Secure.ANDROID_ID)
            map["ip"] = getIp(mContext!!)
            map["channelCode"] = mChannel
            map.apply {
                if (!containsKey("email")) {
                    put("email", "")
                }
                if (!containsKey("sex")) {
                    put("sex", 0)
                }
                if (!containsKey("age")) {
                    put("age", 0)
                }
                if (!containsKey("nickName")) {
                    put("nickName", "unknow")
                }
                if (!containsKey("headImg")) {
                    put("headImg", "unknow")
                }
                if (!containsKey("mobile")) {
                    put("mobile", "0")
                }
                if (!containsKey("gameUserId")) {
                    put("gameUserId", 0)
                }
                if (!containsKey("authCode")) {
                    put("authCode", "")
                }
            }
            treeMap.clear()
            sb.clear()
            treeMap.apply {
                put("loginType", map["loginType"])
                put("channelCode", mChannel)
                put("gameUserId", map["gameUserId"])
                put("mobile", map["mobile"])
                put("androidId", map["androidId"])
                put("authCode", map["authCode"])
            }
            for ((key, value) in treeMap) {
                sb.append("$key=$value&")
            }
            sb.append("key=$mKey")
            val sign = MD5Utils.getMD5Standard(sb.toString()).toLowerCase()
            map["sign"] = sign
            HttpUtils.instance.login(LOGIN, map, listener)
            Log.d("TAG", "login: $map")
        }
    }

    /**
     * 发送短信
     */
    @Keep
    fun sendSms(params: Map<String, Any>, listener: ResponseListener) {
        check()
        if (!params.containsKey("mobile")) {
            throw IllegalArgumentException("$PAY_TAG 缺少参数")
        }
        mPhone = params["mobile"].toString()
        if (mPhone.length != 12 && mPhone.length != 10) {
            Toast.makeText(
                mContext!!,
                "Please fill in the correct phone number",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val phone = if (mPhone.startsWith("91")) {
            mPhone
        } else {
            "91$mPhone"
        }
        val map = mutableMapOf<String, Any>()
        treeMap.clear()
        sb.clear()
        treeMap.apply {
            put("mobile", phone)
            put("channelCode", mChannel)
        }
        for ((key, value) in treeMap) {
            sb.append("$key=$value&")
        }
        sb.append("key=$mKey")
        val sign = MD5Utils.getMD5Standard(sb.toString()).toLowerCase()
        map.putAll(params)
        map["codeType"] = 2
        map["channelCode"] = mChannel
        map["sign"] = sign
        HttpUtils.instance.sendSms(SEND_SMS, map, listener)
    }

    /**
     * 充值下单
     */
    @Keep
    fun rechargeOrder(
        params: Map<String, Any>,
        activity: Activity,
        listener: ResponseListener
    ) {
        check()
        if (!params.containsKey("outUserId") || !params.containsKey("outOrderNo") || !params.containsKey(
                "commodityId"
            )
        ) {
            throw IllegalArgumentException("$PAY_TAG 缺少参数")
        }
        val map = mutableMapOf<String, Any>()
        treeMap.clear()
        sb.clear()
        treeMap.apply {
            put("channelCode", mChannel)
            put("outUserId", params["outUserId"])
            put("outOrderNo", params["outOrderNo"])
            put("commodityId", params["commodityId"])
        }
        for ((key, value) in treeMap) {
            sb.append("$key=$value&")
        }
        sb.append("key=$mKey")
        val sign = MD5Utils.getMD5Standard(sb.toString()).toLowerCase()
        map.putAll(params)
        map["sign"] = sign
        map["channelCode"] = mChannel
        HttpUtils.instance.paymentOrder(RECHARGE_ORDER, map, activity, 100, listener)
    }

    /**
     * 获取渠道配置
     */
    @Keep
    fun <T> getChannelConfig(
        callBack: Callback<T>
    ) {
        check()
        treeMap.clear()
        treeMap.apply {
            put("channelCode", mChannel)
        }
        sb.clear()
        for ((key, value) in treeMap) {
            sb.append("$key=$value&")
        }
        sb.append("key=$mKey")
        val sign = MD5Utils.getMD5Standard(sb.toString()).toLowerCase()
        val map = mutableMapOf<String, Any>()
        map["sign"] = sign
        map["ip"] = getIp(mContext!!)
        map["gameUserId"] = "0"
        map["channelCode"] = mChannel
        HttpUtils.instance.getChannelConfig(GET_CHANNEL_CONFIG, map, callBack)
    }

    /**
     * 游客登录
     */
    fun guestLogin(listener: ResponseListener) {
        val map = mutableMapOf("loginType" to 1)
        login(map, listener)
    }

    /**
     * 手机号登录
     */
    fun phoneLogin(params: Map<String, Any>, listener: ResponseListener) {
        val map = mutableMapOf<String, Any>()
        map["loginType"] = 2
        map.putAll(params)
        login(map, listener)
    }

    /**
     * 检查是否初始化
     */
    private fun check() {
        if (mChannel.isEmpty() || mContext == null) {
            throw IllegalArgumentException("$PAY_TAG not init,please init sdk")
        }
    }

    /**
     * 获取Paytm支付结果
     */
    @Keep
    fun getPayResult(requestCode: Int, data: Intent?): Boolean {
        if (requestCode == 100 && data != null) {
            return data.getStringExtra("nativeSdkForMerchantMessage").isEmpty()
        }
        return false
    }
}
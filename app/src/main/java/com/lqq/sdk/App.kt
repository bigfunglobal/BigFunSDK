package com.lqq.sdk

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.bigfun.tm.BigFunSDK
import com.bigfun.tm.chat.BigFunChat

class App : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        BigFunSDK.setDebug(true)
        BigFunSDK.getInstance().init(
            applicationContext,
            "bmartpay_test",
            "kofreedemo-5jzrjmrf3"
        )
        BigFunChat.init(applicationContext)
    }
}
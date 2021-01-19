package com.lqq.sdk

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import android.util.Log
import com.bigfun.tm.BigFunSDK

private const val TAG = "App"

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
            "bmartpay_test"
        ) { channelCode, source ->
            Log.d(TAG, "onCreate: $channelCode--$source")
        }
    }
}
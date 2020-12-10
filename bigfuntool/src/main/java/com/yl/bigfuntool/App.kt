package com.yl.bigfuntool

import android.app.Application
import android.support.multidex.MultiDex
import com.facebook.FacebookSdk

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();
    }
}
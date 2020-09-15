package com.lqq.sdk;

import android.app.Application;

import com.bigfun.tm.BigFunSdk;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BigFunSdk.init(getApplicationContext(), "", "");
    }
}

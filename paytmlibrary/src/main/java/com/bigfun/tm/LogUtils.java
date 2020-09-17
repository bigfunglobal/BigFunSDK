package com.bigfun.tm;

import android.util.Log;

public class LogUtils {
    private static final String TAG = "BigFunSDK>>>>";

    private LogUtils() {

    }

    public static void log(String message) {
        if (!BigFunSDK.isDebug) {
            return;
        }
        Log.d(TAG, "" + message);
    }
}

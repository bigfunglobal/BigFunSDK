package com.bigfun.tm.login;

import android.app.Activity;
import android.support.annotation.Keep;

import com.facebook.CallbackManager;

@Keep
public class LoginSDK {

    private static LoginSDK instance;
    private Activity activity;
    private LoginModel loginModel;

    private LoginSDK(Activity activity) {
        this.activity = activity;
        loginModel = new LoginModel(activity);
    }

    @Keep
    public static LoginSDK getInstance(Activity activity) {
        if (instance == null) {
            synchronized (LoginSDK.class) {
                if (instance == null) {
                    instance = new LoginSDK(activity);
                }
            }
        }
        return instance;
    }

    /**
     * facebook登录
     *
     * @param callbackManager
     * @param listener
     */
    @Keep
    public void facebookLogin(CallbackManager callbackManager, IFBLoginListener listener) {
        loginModel.facebookLogin(callbackManager, listener);
    }
}

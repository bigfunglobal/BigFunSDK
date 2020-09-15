package com.bigfun.tm.login

import android.app.Activity
import android.support.annotation.Keep
import com.facebook.CallbackManager

@Keep
class LoginSdk private constructor(private val activity: Activity) {

    private val loginModel by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        LoginModel(activity)
    }

    @Keep
    companion object {

        @Volatile
        private var instance: LoginSdk? = null

        @JvmStatic
        fun getInstance(activity: Activity): LoginSdk {
            val i = instance
            if (i != null) {
                return i
            }

            return synchronized(this) {
                val i2 = instance
                if (i2 != null) {
                    i2
                } else {
                    val create = LoginSdk(activity)
                    instance = create
                    create
                }
            }
        }
    }

    /**
     * facebook登录
     */
    @Keep
    fun fbLogin(callbackManager: CallbackManager, listener: IFBLoginListener) {
        loginModel.facebookLogin(callbackManager, listener)
    }

    /**
     * google登录
     */
    @Keep
    private fun googleLogin(webClientId: String, requestCode: Int) {
//        loginModel.googleLogin(webClientId, requestCode)
    }
}
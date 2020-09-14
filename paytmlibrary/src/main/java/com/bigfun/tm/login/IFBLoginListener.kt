package com.bigfun.tm.login

import android.support.annotation.Keep
import com.facebook.FacebookException
import org.json.JSONObject

@Keep
interface IFBLoginListener {
    /**
     * 取消
     */
    @Keep
    fun onCancel()

    /**
     * 错误
     */
    @Keep
    fun onError(error: FacebookException)

    /**
     * 完成
     * @param jsonObject
     */
    @Keep
    fun onComplete(jsonObject: JSONObject?)
}
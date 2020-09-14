package com.bigfun.tm

import android.support.annotation.Keep


@Keep
interface ResponseListener {

    @Keep
    fun onFail(msg: String)

    @Keep
    fun onSuccess()
}
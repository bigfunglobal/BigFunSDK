package com.bigfun.tm.login

import android.support.annotation.Keep

@Keep
interface Callback<T> {
    @Keep
    fun onResult(result: T)

    @Keep
    fun onFail(msg: String)
}
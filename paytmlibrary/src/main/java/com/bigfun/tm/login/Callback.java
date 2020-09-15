package com.bigfun.tm.login;

import android.support.annotation.Keep;

@Keep
public interface Callback<T> {
    @Keep
    void onResult(T result);

    @Keep
    void onFail(String msg);
}

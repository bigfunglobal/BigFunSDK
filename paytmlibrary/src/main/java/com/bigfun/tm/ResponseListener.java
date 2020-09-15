package com.bigfun.tm;

import android.support.annotation.Keep;

@Keep
public interface ResponseListener {
    void onSuccess();

    void onFail(String msg);
}

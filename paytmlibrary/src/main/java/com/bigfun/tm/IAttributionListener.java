package com.bigfun.tm;

import android.support.annotation.Keep;

@Keep
public interface IAttributionListener {
    void attribution(String channelCode, String source);
}

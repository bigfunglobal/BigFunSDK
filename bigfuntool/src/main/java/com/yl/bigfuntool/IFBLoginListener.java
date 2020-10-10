package com.yl.bigfuntool;

import android.support.annotation.Keep;

import com.facebook.FacebookException;

import org.json.JSONObject;

@Keep
public interface IFBLoginListener {
    /**
     * 取消
     */
    @Keep
    void onCancel();

    /**
     * 错误
     */
    @Keep
    void onError(FacebookException error);

    /**
     * 完成
     *
     * @param jsonObject
     */
    @Keep
    void onComplete(JSONObject jsonObject);
}

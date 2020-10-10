package com.bigfun.tm.login;

import android.app.Activity;
import android.os.Bundle;

import com.bigfun.tm.BigFunSDK;
import com.bigfun.tm.LogUtils;
import com.bigfun.tm.ResponseListener;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginModel {
    private Activity mActivity;

    public LoginModel(Activity mActivity) {
        this.mActivity = mActivity;
    }

    /**
     * facebook登录
     *
     * @param callbackManager
     * @param listener
     */
    public void facebookLogin(CallbackManager callbackManager, IFBLoginListener listener) {
        List<String> permissionList = new ArrayList<>();
        permissionList.add("public_profile");
        permissionList.add("email");
        LoginManager.getInstance().logInWithReadPermissions(mActivity, permissionList);
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getFacebookInfo(loginResult.getAccessToken(), listener);
            }

            @Override
            public void onCancel() {
                listener.onCancel();
            }

            @Override
            public void onError(FacebookException error) {
                listener.onError(error);
            }
        });
    }

    /**
     * facebook登录成功后获取用户信息
     *
     * @param accessToken
     * @param listener
     */
    private void getFacebookInfo(AccessToken accessToken, IFBLoginListener listener) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, (object, response) -> {
            listener.onComplete(object);
            login(object);
        });
        Bundle bundle = new Bundle();
        bundle.putString("fields",
                "id,name,link,gender,birthday,email,picture,locale,updated_time,timezone,age_range,first_name,last_name");
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();
    }

    private void login(JSONObject jsonObject) {
        Map<String, Object> map = new HashMap<>();
        map.put("loginType", 3);
        map.put("authCode", jsonObject.optString("id"));
        map.put("email", jsonObject.optString("email"));
        map.put("nickName", jsonObject.optString("name"));
        JSONObject pictureJson = jsonObject.optJSONObject("picture");
        JSONObject dataJson = pictureJson.optJSONObject("data");
        map.put("headImg", dataJson.optString("url"));
        BigFunSDK.getInstance().login(map, new ResponseListener() {
            @Override
            public void onSuccess() {
                LogUtils.log("facebook call login success");
            }

            @Override
            public void onFail(String msg) {
                LogUtils.log("facebook call login fail $msg");
            }
        });
    }
}

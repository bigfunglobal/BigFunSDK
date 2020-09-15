package com.bigfun.tm.login

import android.app.Activity
import android.os.Bundle
import com.bigfun.tm.BigFunSDK
import com.bigfun.tm.ResponseListener
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import org.json.JSONObject
import java.util.ArrayList

internal class LoginModel(private val activity: Activity) {

    /**
     * google登录
     */
//    fun googleLogin(webClientId: String, requestCode: Int) {
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(webClientId)
//            .requestEmail()
//            .build()
//        val client = GoogleSignIn.getClient(activity, gso)
//        val signInIntent = client.signInIntent
//        activity.startActivityForResult(signInIntent, requestCode)
//    }

    /**
     * fb登录
     */
    fun facebookLogin(callbackManager: CallbackManager, listener: IFBLoginListener) {
        val permissionList: MutableList<String> = ArrayList()
        permissionList.add("public_profile")
        permissionList.add("email")
        LoginManager.getInstance().logInWithReadPermissions(activity, permissionList)
        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                getFacebookInfo(loginResult.accessToken, listener)
            }

            override fun onCancel() {
                listener.onCancel()
            }

            override fun onError(error: FacebookException) {
                listener.onError(error)
            }
        })
    }

    /**
     * 登录成功获取用户信息
     */
    private fun getFacebookInfo(accessToken: AccessToken, listener: IFBLoginListener) {
        val request = GraphRequest.newMeRequest(accessToken) { `object`, _ ->
            if (`object` != null) {
                listener.onComplete(`object`)
                login(`object`)
            } else {
                listener.onError(FacebookException("login fail"))
            }
        }
        val parameters = Bundle()
        parameters.putString(
            "fields",
            "id,name,link,gender,birthday,email,picture,locale,updated_time,timezone,age_range,first_name,last_name"
        )
        request.parameters = parameters
        request.executeAsync()
    }

    /**
     *
     */
    private fun login(jsonObject: JSONObject) {
        val map = mutableMapOf<String, Any>()
        map["loginType"] = 3
        jsonObject.apply {
            map["authCode"] = optString("id")
            map["email"] = optString("email")
            map["nickName"] = optString("name")
            optString("picture").apply {
                map["headImg"] = optString("url")
            }
        }
        BigFunSDK.getInstance().login(map, object : Callback<String> {
            override fun onFail(msg: String) {

            }

            override fun onResult(result: String?) {
            }
        })
    }
}
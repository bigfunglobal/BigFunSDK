package com.lqq.sdk

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.bigfun.tm.BigFunSDK
import com.bigfun.tm.CustomDialog
import com.bigfun.tm.ResponseListener
import com.bigfun.tm.login.Callback
import com.bigfun.tm.login.IFBLoginListener
import com.bigfun.tm.login.LoginSDK
import com.facebook.CallbackManager
import com.facebook.FacebookException
import kotlinx.android.synthetic.main.activity_second.*
import org.json.JSONObject
import java.util.HashMap


private const val TAG = "SecondActivity"
const val TOKEN =
    "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiYXBpdXNlciIsImFjY291bnQiOiI0Mjc1MiIsImlzcyI6ImpveWNoZWFwIiwiYXVkIjoiMDk4ZjZiY2Q0NjIxZDM3M2NhZGU0ZTgzMjYyN2I0ZjYiLCJleHBpcmVkVGltZSI6MTYwMDk0NjQyMDgzMywiZXhwIjoxNjAwOTQ2NDIwLCJuYmYiOjE1OTkyMTg0MjB9.pGnxoGkghbZBhKxqx029ftIj9RyUehsmvonbm9W7RZ8"

class SecondActivity : AppCompatActivity() {

    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        BigFunSDK.init(
            applicationContext,
            "test_fb",
            "EUXLRCIKPVWBYEJW"
        )

        btn_order.setOnClickListener {
            BigFunSDK.getInstance().rechargeOrder(
                mapOf(
                    "outUserId" to "0",
                    "outOrderNo" to "774",
                    "commodityId" to "10RUPEE"
                ),
                this, object : ResponseListener {
                    override fun onFail(msg: String) {
                        Log.d(TAG, "onFail: $msg")
                    }

                    override fun onSuccess() {
                        Log.d(TAG, "onResult: ")
                    }
                }
            )
        }

        btn_builder.setOnClickListener {
            val dialog = CustomDialog(this)
            dialog.show()
        }

        btn_login.setOnClickListener {
            BigFunSDK.getInstance().guestLogin(
                object : ResponseListener {
                    override fun onSuccess() {

                    }

                    override fun onFail(msg: String?) {
                    }
                })
        }

        btn_phone_login.setOnClickListener {
            BigFunSDK.getInstance().phoneLogin(mutableMapOf<String, Any>(
                "mobile" to et_phone.text.toString(),
            ),
                object : ResponseListener {
                    override fun onSuccess() {
                        Log.d(TAG, "onSuccess: ")
                    }

                    override fun onFail(msg: String?) {
                        Log.d(TAG, "onFail: $msg")
                    }
                })
        }

        btn_channel_config.setOnClickListener {
            BigFunSDK.getInstance().getChannelConfig(
                object : Callback<String> {
                    override fun onResult(result: String) {
                        Log.d(TAG, "onResult: $result")
                    }

                    override fun onFail(msg: String) {
                        Log.d(TAG, "onFail: $msg")
                    }
                })
        }

        btn_send_sms.setOnClickListener {
            val map = mutableMapOf<String,Any>()
            map.put("authCode","google用户信息对应的id")
            map.put("email","邮箱")
            map.put("sex","性别")
            map.put("age","年龄")
            map.put("nickName","昵称")
            map.put("headImg","头像")
            BigFunSDK.getInstance().googleLogin(map,object :ResponseListener{
                override fun onSuccess() {

                }

                override fun onFail(msg: String?) {

                }
            })
        }

        btn_fb_login.setOnClickListener {
            LoginSDK.getInstance(this).facebookLogin(callbackManager, object : IFBLoginListener {
                override fun onCancel() {
                    Log.d(TAG, "onCancel: ")
                }

                override fun onError(error: FacebookException?) {
                    Log.d(TAG, "onError: ${error?.message}")
                }

                override fun onComplete(jsonObject: JSONObject?) {
                    Log.d(TAG, "onComplete: ")
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && data != null) {
            Log.d(
                "pay",
                "onActivityResult: ${data.getStringExtra("response")}--${data.getStringExtra("nativeSdkForMerchantMessage")}"
            )
            Log.d(
                TAG,
                "onActivityResult: ${data.getStringExtra("nativeSdkForMerchantMessage") == null}"
            )
        }
    }
}
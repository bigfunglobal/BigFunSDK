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
            val map = mutableMapOf<String, Any>(
                "gameUserId" to 1
            )
            BigFunSDK.getInstance().guestLogin(map,
                object : ResponseListener {
                    override fun onSuccess() {
                        Log.d(TAG, "onSuccess: ")
                    }

                    override fun onFail(msg: String?) {
                        Log.d(TAG, "onFail: $msg")
                    }
                })
        }

        btn_phone_login.setOnClickListener {
            BigFunSDK.getInstance().loginWithCode(mutableMapOf<String, Any>(
                "mobile" to et_phone.text.toString(),
                "code" to et_code.text.toString()
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
            BigFunSDK.getInstance().sendSms(mutableMapOf<String, Any>(
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

        btn_is_login.setOnClickListener {
            Log.d(TAG, "onCreate: ${BigFunSDK.getInstance().isLogin}")
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
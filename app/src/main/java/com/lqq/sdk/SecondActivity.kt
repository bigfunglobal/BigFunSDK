package com.lqq.sdk

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.ArrayMap
import android.util.Log
import android.widget.Toast
import com.bigfun.tm.BigFunSDK
import com.bigfun.tm.CustomDialog
import com.bigfun.tm.ResponseListener
import com.bigfun.tm.chat.BigFunChat
import com.bigfun.tm.login.Callback
import com.facebook.CallbackManager
import kotlinx.android.synthetic.main.activity_second.*


private const val TAG = "SecondActivity"

class SecondActivity : AppCompatActivity() {

    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

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
                        Log.d(TAG, "onSuccess: 下单成功")
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
                "mobile" to "b21f033b1c1d4a10b639c8fb0ff5520a",
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

        btn_order2.setOnClickListener {
            BigFunSDK.getInstance().payOrder(
                mutableMapOf<String, Any>("orderId" to "202101041111569337298197"),
                this,
                object : ResponseListener {
                    override fun onSuccess() {
                        Log.d(TAG, "onSuccess: ")
                        runOnUiThread {
                            Toast.makeText(applicationContext, "下单成功", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFail(msg: String?) {
                        Log.d(TAG, "onFail: $msg")
                        runOnUiThread {
                            Toast.makeText(applicationContext, "下单失败--$msg", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                })
        }

        btn_is_login.setOnClickListener {
            Log.d(TAG, "onCreate: ${BigFunSDK.getInstance().isLogin}")
        }

        btn_chat.setOnClickListener {
            val params = ArrayMap<String, String>()
            params["name"] = "Test"
            params["email"] = "test@gmail.com"
            params["phone"] = "7400000000"
//            params["gameUserId"] = "110110110"
            params["appId"] = "120120120120"
            params["appName"] = "Test Rummy"
            BigFunChat.getInstance().chat(this, params)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: ${BigFunSDK.getInstance().getPayResult(requestCode, data)}")
        if (requestCode == 100 && data != null) {
            Log.d(
                TAG,
                "onActivityResult: ${data.getStringExtra("response")}--${data.getStringExtra("nativeSdkForMerchantMessage")}"
            )
        }
    }
}
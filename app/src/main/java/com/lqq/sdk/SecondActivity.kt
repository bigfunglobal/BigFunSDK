package com.lqq.sdk

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.bigfun.tm.CustomDialog
import com.bigfun.tm.BigFunSdk
import com.bigfun.tm.ResponseListener
import com.bigfun.tm.login.Callback
import kotlinx.android.synthetic.main.activity_second.*


private const val TAG = "SecondActivity"
const val TOKEN =
    "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiYXBpdXNlciIsImFjY291bnQiOiI0Mjc1MiIsImlzcyI6ImpveWNoZWFwIiwiYXVkIjoiMDk4ZjZiY2Q0NjIxZDM3M2NhZGU0ZTgzMjYyN2I0ZjYiLCJleHBpcmVkVGltZSI6MTYwMDk0NjQyMDgzMywiZXhwIjoxNjAwOTQ2NDIwLCJuYmYiOjE1OTkyMTg0MjB9.pGnxoGkghbZBhKxqx029ftIj9RyUehsmvonbm9W7RZ8"

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        BigFunSdk.init(
            applicationContext,
            "test_fb",
            "EUXLRCIKPVWBYEJW"
        )

        btn_order.setOnClickListener {
            BigFunSdk.instance.rechargeOrder(
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
            BigFunSdk.instance.guestLogin(
                object : ResponseListener {
                    override fun onFail(msg: String) {
                        Log.d(TAG, "onFail: $msg")
                        runOnUiThread {
                            tv.text = "登录失败$msg"
                        }
                    }

                    override fun onSuccess() {
                        Log.d(TAG, "onResult: ")
                        runOnUiThread {
                            tv.text = "登录成功"
                        }
                    }
                })
        }

        btn_phone_login.setOnClickListener {
            BigFunSdk.instance.phoneLogin(mutableMapOf(
                "mobile" to et_phone.text.toString(),
                "code" to et_code.text.toString()
            ),
                object : ResponseListener {
                    override fun onFail(msg: String) {
                        Log.d(TAG, "onFail: $msg")
                        runOnUiThread {
                            tv.text = "登录失败--$msg"
                        }
                    }

                    override fun onSuccess() {
                        Log.d(TAG, "onSuccess: ")
                        runOnUiThread {
                            tv.text = "登录成功"
                        }
                    }

                })
        }

        btn_channel_config.setOnClickListener {
            BigFunSdk.instance.getChannelConfig(
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
            BigFunSdk.instance.sendSms(mutableMapOf(
                "mobile" to "917406202796"
            ), object : ResponseListener {
                override fun onFail(msg: String) {
                    Log.d(
                        TAG,
                        "onFail: $msg"
                    )
                    runOnUiThread {
                        tv.text = "获取验证码失败$msg"
                    }
                }

                override fun onSuccess() {
                    runOnUiThread {
                        tv.text = "获取验证码成功"
                    }
                }
            })
        }

        btn_with_order.setOnClickListener {
            //            PaytmSdk.instance.withdrawOrder(mutableMapOf(
//                "outOrderNo" to "545634564564",
//                "outUserId" to "0",
//                "userId" to "444",
//                "channelCode" to "test007",
//                "payType" to 3,
//                "payAccount" to 3,
//                "payAmount" to 200
//            ), object : ResponseListener {
//                override fun onFail(e: Exception) {
//                    Log.d(TAG, "onFail: ${e.message}")
//                }
//
//                override fun onResult(response: Response) {
//                    Log.d(TAG, "onResult: ${response.body?.string()}--${response.isSuccessful}")
//                }
//            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
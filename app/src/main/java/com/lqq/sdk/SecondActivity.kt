package com.lqq.sdk

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.bigfun.tm.BigFunSDK
import com.bigfun.tm.CustomDialog
import com.bigfun.tm.ResponseListener
import com.bigfun.tm.encrypt.DesUtils
import com.bigfun.tm.encrypt.RSAEncrypt
import com.bigfun.tm.login.Callback
import com.facebook.CallbackManager
import kotlinx.android.synthetic.main.activity_second.*

private const val TAG = "SecondActivity"
const val TOKEN =
    "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiYXBpdXNlciIsImFjY291bnQiOiI0Mjc1MiIsImlzcyI6ImpveWNoZWFwIiwiYXVkIjoiMDk4ZjZiY2Q0NjIxZDM3M2NhZGU0ZTgzMjYyN2I0ZjYiLCJleHBpcmVkVGltZSI6MTYwMDk0NjQyMDgzMywiZXhwIjoxNjAwOTQ2NDIwLCJuYmYiOjE1OTkyMTg0MjB9.pGnxoGkghbZBhKxqx029ftIj9RyUehsmvonbm9W7RZ8"
const val PASS = "7C5049081A3CD0FA228DC9D2608880E4EA4FED82B3C702D411E1C7C674D6E1F1"

class SecondActivity : AppCompatActivity() {

    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        btn_order.setOnClickListener {

        }

        btn_builder.setOnClickListener {
            val dialog = CustomDialog(this)
            dialog.show()
        }

        btn_login.setOnClickListener {

        }

        btn_phone_login.setOnClickListener {

        }

        btn_channel_config.setOnClickListener {

        }

        btn_send_sms.setOnClickListener {

        }

        btn_is_login.setOnClickListener {

        }

        btn_pay_order.setOnClickListener {
            BigFunSDK.getInstance().payOrder(mutableMapOf<String, Any>(
                "orderId" to "202011251928053535876977"
            ), this, object : ResponseListener {
                override fun onSuccess() {
                    runOnUiThread {
                        tv.text = "下单成功"
                    }
                    Log.d(TAG, "onSuccess: 预下单")
                }

                override fun onFail(msg: String?) {
                    runOnUiThread {
                        tv.text = "下单失败$msg"
                    }
                    Log.d(TAG, "onFail: 预下单--$msg")
                }

            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: ${BigFunSDK.getInstance().getPayResult(requestCode, data)}")
    }
}
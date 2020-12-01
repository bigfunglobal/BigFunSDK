package com.lqq.sdk

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.bigfun.tm.BigFunSDK
import com.bigfun.tm.ResponseListener
import kotlinx.android.synthetic.main.activity_second.*

private const val TAG = "SecondActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        btn_order.setOnClickListener {
            BigFunSDK.getInstance().payOrder(mutableMapOf<String, Any>(
                "orderId" to "202011251943212051965178"
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
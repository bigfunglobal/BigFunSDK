package com.bigfun.tm

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.bigfun.tm.model.PaymentOrderBean
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager

private const val TAG = "PayUtils"

class PayUtils private constructor() {
    companion object {
        val instance by lazy {
            PayUtils()
        }
    }

    fun pay(
        bean: PaymentOrderBean.DataBean,
        activity: Activity,
        requestCode: Int,
        email: String?,
        phone: String?
    ) {
        if (bean.paymentChannel.toInt() == 1) {
            paytm(bean, activity, requestCode)
        } else if (bean.paymentChannel.toInt() == 0) {
            if (bean.openType.toInt() == 1) {
                activity.runOnUiThread {
                    val intent = Intent(activity, PayActivity::class.java)
                    intent.putExtra(EXTRA_KEY_PAY_URL, bean.jumpUrl)
                    activity.startActivity(intent)
                }
            } else {
                activity.runOnUiThread {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(bean.jumpUrl))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    activity.startActivity(intent)
                }
            }
        } else {
            if (email != null && phone != null && email.isNotEmpty() && phone.isNotEmpty()) {
                SPUtils.instance.put(BigFunSDK.mContext, KEY_EMAIL, email)
                SPUtils.instance.put(BigFunSDK.mContext, KEY_PHONE, phone)
                activity.runOnUiThread {
                    val intent = Intent(activity, PayActivity::class.java)
                    var url = bean.jumpUrl.replace("email=null", "email=$email")
                    url = url.replace("mobile=null", "mobile=$phone")
                    intent.putExtra(EXTRA_KEY_PAY_URL, url)
                    activity.startActivity(intent)
                }
            } else {
                val saveEmail =
                    SPUtils.instance.get(BigFunSDK.mContext, KEY_EMAIL, "") as String
                val savePhone =
                    SPUtils.instance.get(BigFunSDK.mContext, KEY_PHONE, "") as String
                if (saveEmail.isNotEmpty() && savePhone.isNotEmpty()) {
                    activity.runOnUiThread {
                        val intent = Intent(activity, PayActivity::class.java)
                        var url = bean.jumpUrl.replace("email=null", "email=$saveEmail")
                        url = url.replace("mobile=null", "mobile=$savePhone")
                        intent.putExtra(EXTRA_KEY_PAY_URL, url)
                        activity.startActivity(intent)
                    }
                } else {
                    activity.runOnUiThread {
                        val dialog = CustomDialog(activity)
                        dialog.setOnClickListener(object : CustomDialog.IOnClickListener {
                            override fun ok(email: String, phone: String) {
                                SPUtils.instance.put(BigFunSDK.mContext, KEY_EMAIL, email)
                                SPUtils.instance.put(BigFunSDK.mContext, KEY_PHONE, phone)
                                val intent = Intent(activity, PayActivity::class.java)
                                var url = bean.jumpUrl.replace("email=null", "email=$email")
                                url = url.replace("mobile=null", "mobile=$phone")
                                intent.putExtra(EXTRA_KEY_PAY_URL, url)
                                activity.startActivity(intent)
                            }
                        })
                        dialog.show()
                    }
                }
            }
        }
    }

    /**
     * 调用Paytm支付
     */
    private fun paytm(bean: PaymentOrderBean.DataBean, activity: Activity, requestCode: Int) {
        val arr = bean.jumpUrl.split("?")[1].split("&")
        val map = mutableMapOf<String, String>()
        arr.forEach {
            val split = it.split("=")
            map[split[0]] = split[1]
        }
        val paytmOrder = PaytmOrder(
            map["orderId"],
            map["mid"],
            map["txnToken"],
            bean.outPayAmount.toString(),
            "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=${map["orderId"]}"
        )
        val transactionManager =
            TransactionManager(paytmOrder, object : PaytmPaymentTransactionCallback {
                override fun onTransactionResponse(bundle: Bundle?) {
                    Log.d(TAG, "onTransactionResponse: $bundle")
                }

                override fun networkNotAvailable() {
                    Log.d(TAG, "networkNotAvailable: ")
                }

                override fun clientAuthenticationFailed(s: String?) {
                    Log.d(TAG, "clientAuthenticationFailed: $s")
                }

                override fun someUIErrorOccurred(s: String?) {
                    Log.d(TAG, "someUIErrorOccurred: $s")
                }

                override fun onErrorLoadingWebPage(i: Int, s1: String?, s2: String?) {
                    Log.d(TAG, "onErrorLoadingWebPage: $i--$s1--$s2")
                }

                override fun onBackPressedCancelTransaction() {
                    Log.d(TAG, "onBackPressedCancelTransaction: ")
                }

                override fun onTransactionCancel(s: String?, bundle: Bundle?) {
                    Log.d(TAG, "onTransactionCancel: $s--$bundle")
                }
            })
        transactionManager.setShowPaymentUrl("https://securegw.paytm.in/theia/api/v1/showPaymentPage")
        transactionManager.startTransaction(activity, requestCode)
    }
}
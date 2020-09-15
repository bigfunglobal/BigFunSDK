package com.bigfun.tm

import android.app.Activity
import com.google.gson.Gson
import com.bigfun.tm.encrypt.EncryptUtil
import com.bigfun.tm.model.LoginBean
import com.bigfun.tm.model.PaymentOrderBean
import com.bigfun.tm.model.SendSmsBean
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit


internal class HttpUtils private constructor() {

    private val mediaType = MediaType.get("application/json; charset=utf-8")
    var smsCode = ""
    private val token by lazy {
        SPUtils.instance.get(BigFunSDK.mContext, "accessToken", "") as String
    }

    companion object {
        private const val TIME_OUT = 10L

        @JvmStatic
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { HttpUtils() }
    }

    private val gson by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        Gson()
    }

    private val okHttpClient by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        OkHttpClient.Builder()
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .build()
    }

    /**
     * post请求
     * @param url 请求地址
     * @param params 请求参数
     * @param listener 请求回调
     */
//    internal fun <T> post(
//        url: String,
//        params: Map<String, Any>,
//        listener: ResponseListener<T>
//    ) {
//        if (url.isEmpty()) throw IllegalArgumentException("url.length() == 0")
//        if (params.isEmpty()) throw IllegalArgumentException("params.size == 0")
//        val json = EncryptUtil.encryptData(gson.toJson(params))
//        val request = Request.Builder()
//            .url(url)
//            .addHeader(
//                "accessToken",
//                SPUtils.instance.get(PaytmSdk.mContext!!, KEY_TOKEN, "") as String
//            )
//            .post(RequestBody.create(mediaType, json))
//            .build()
//        okHttpClient.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                listener.onFail("${e.message}")
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                try {
//                    if (response.isSuccessful) {
//                        if (response.code() == 200) {
//                            if (response.body() != null) {
//                                val callBackType: Type? = listener.javaClass.genericSuperclass
//                                if (callBackType != null) {
//                                    // 获取泛型类型数组
//                                    val array: Array<Type> =
//                                        (callBackType as ParameterizedType).actualTypeArguments
//                                    val bean: Any =
//                                        gson.fromJson(response.body()!!.string(), array[0])
//                                    listener.onResult(bean as T)
//                                    if (bean is LoginBean) {
//                                        SPUtils.instance.put(
//                                            PaytmSdk.mContext!!,
//                                            KEY_TOKEN,
//                                            bean.data.accessToken
//                                        )
//                                    } else if (bean is SendSmsBean) {
//                                        smsCode = bean.data
//                                    }
//                                } else {
//                                    listener.onFail("请传入泛型参数")
//                                }
//                            } else {
//                                listener.onFail("${response.code()}--${response.message()}")
//                            }
//                        } else {
//                            listener.onFail("${response.code()}--${response.message()}")
//                        }
//                    } else {
//                        listener.onFail("${response.code()}--${response.message()}")
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        })
//    }

    /**
     * 登录
     */
    fun login(
        url: String,
        params: Map<String, Any>,
        listener: ResponseListener
    ) {
        if (url.isEmpty()) throw IllegalArgumentException("url.length() == 0")
        if (params.isEmpty()) throw IllegalArgumentException("params.size == 0")
        val json = EncryptUtil.encryptData(gson.toJson(params))
        val request = Request.Builder()
            .url(url)
            .addHeader(
                "accessToken",
                SPUtils.instance.get(BigFunSDK.mContext, KEY_TOKEN, "") as String
            )
            .post(RequestBody.create(mediaType, json))
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                listener.onFail("${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                val loginBean =
                                    gson.fromJson(response.body()!!.string(), LoginBean::class.java)
                                if (loginBean.code.toInt() == 0) {
                                    SPUtils.instance.put(
                                        BigFunSDK.mContext,
                                        KEY_TOKEN,
                                        loginBean.data.accessToken
                                    )
                                    listener.onSuccess()
                                } else {
                                    listener.onFail(loginBean.msg)
                                }
                            } else {
                                listener.onFail(response.message())
                            }
                        } else {
                            listener.onFail(response.message())
                        }
                    } else {
                        listener.onFail("${response.code()}--${response.message()}")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        })
    }

    /**
     * 发送验证码
     */
    fun sendSms(
        url: String,
        params: Map<String, Any>,
        listener: ResponseListener
    ) {
        if (url.isEmpty()) throw IllegalArgumentException("url.length() == 0")
        if (params.isEmpty()) throw IllegalArgumentException("params.size == 0")
        val json = EncryptUtil.encryptData(gson.toJson(params))
        val request = Request.Builder()
            .url(url)
            .addHeader(
                "accessToken",
                SPUtils.instance.get(BigFunSDK.mContext, KEY_TOKEN, "") as String
            )
            .post(RequestBody.create(mediaType, json))
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                listener.onFail("${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                val bean =
                                    gson.fromJson(
                                        response.body()!!.string(),
                                        SendSmsBean::class.java
                                    )
                                if (bean.code.toInt() == 0) {
                                    smsCode = bean.data
                                    listener.onSuccess()
                                } else {
                                    listener.onFail(bean.msg)
                                }
                            } else {
                                listener.onFail(response.message())
                            }
                        } else {
                            listener.onFail(response.message())
                        }
                    } else {
                        listener.onFail("${response.code()}--${response.message()}")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        })
    }

    /**
     * get请求
     * @param url 请求地址
     * @param params 请求参数
     * @param listener 请求回调
     */
    internal fun <T> get(
        url: String,
        params: Map<String, Any>,
        listener: ResponseListener
    ) {
        try {
            if (url.isEmpty()) throw IllegalArgumentException("url.length() == 0")
            val requestUrl = StringBuffer(url)
            var isFirst = true
            for ((key, value) in params) {
                if (isFirst) {
                    isFirst = false
                    requestUrl.append("?")
                } else {
                    requestUrl.append("&")
                }
                requestUrl.append("$key=$value")
            }
            val request = Request.Builder()
                .url(requestUrl.toString())
                .addHeader("accessToken", token)
                .get()
                .build()
            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    listener.onFail("${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 充值下单
     */
    fun paymentOrder(
        url: String,
        params: Map<String, Any>,
        activity: Activity,
        requestCode: Int,
        listener: ResponseListener
    ) {
        if (url.isEmpty()) throw IllegalArgumentException("url.length() == 0")
        if (params.isEmpty()) throw IllegalArgumentException("params.size == 0")
        val json = EncryptUtil.encryptData(gson.toJson(params))
        val request = Request.Builder()
            .url(url)
            .addHeader(
                "accessToken",
                SPUtils.instance.get(BigFunSDK.mContext, KEY_TOKEN, "") as String
            )
            .post(RequestBody.create(mediaType, json))
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                listener.onFail("${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            if (response.code() == 200) {
                                val bean =
                                    gson.fromJson(
                                        response.body()!!.string(),
                                        PaymentOrderBean::class.java
                                    )
                                if (bean.code.toInt() == 0) {
                                    if (bean.data != null) {
                                        listener.onSuccess()
                                        PayUtils.instance.pay(
                                            bean.data,
                                            activity,
                                            requestCode,
                                            params["email"] as String?,
                                            params["mobile"] as String?
                                        )
                                    } else {
                                        listener.onFail(bean.msg)
                                    }
                                } else {
                                    listener.onFail(bean.msg)
                                }
                            } else {
                                listener.onFail(response.message())
                            }
                        } else {
                            listener.onFail(response.message())
                        }
                    } else {
                        listener.onFail(response.message())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    /**
     * 获取渠道配置
     */
    fun <T> getChannelConfig(
        url: String,
        params: Map<String, Any>,
        callback: com.bigfun.tm.login.Callback<T>
    ) {
        if (url.isEmpty()) throw IllegalArgumentException("url.length() == 0")
        if (params.isEmpty()) throw IllegalArgumentException("params.size == 0")
        val json = EncryptUtil.encryptData(gson.toJson(params))
        val request = Request.Builder()
            .url(url)
            .addHeader(
                "accessToken",
                SPUtils.instance.get(BigFunSDK.mContext, KEY_TOKEN, "") as String
            )
            .post(RequestBody.create(mediaType, json))
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFail("${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            callback.onResult(response.body()!!.string() as T)
                        } else {
                            callback.onFail(response.message())
                        }
                    } else {
                        callback.onFail(response.message())
                    }
                } else {
                    callback.onFail(response.message())
                }
            }
        })
    }
}
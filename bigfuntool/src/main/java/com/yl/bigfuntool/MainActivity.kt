package com.yl.bigfuntool

import android.content.*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.bigfun.tm.BigFunSDK
import com.bigfun.tm.ResponseListener
import com.facebook.CallbackManager
import com.facebook.FacebookException
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject

private const val TAG = "MainActivity"
const val GOOGLE_SIGN_IN_CODE = 100

class MainActivity : AppCompatActivity() {
    //e0268ed4-39b2-4eef-aab1-66d11eec324e
    private val callbackManager = CallbackManager.Factory.create()
    private lateinit var cm: ClipboardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        setContentView(R.layout.activity_main)
        initListener()
    }

    /**
     * 点击监听
     */
    private fun initListener() {
        btn_init.setOnClickListener {
            init()
        }

        btn_fb_login.setOnClickListener {
            fbLogin()
        }

        btn_google_login.setOnClickListener {
            googleLogin()
        }
        btn_phone_login.setOnClickListener {
            phoneLogin()
        }

        btn_guest_login.setOnClickListener {
            guestLogin()
        }
        btn_decrypt_key.setOnClickListener {
            decryptKey()
        }
        btn_encrypt_key.setOnClickListener {
            encryptKey()
        }
    }

    /**
     * sdk初始化
     */
    private fun init() {
        val channel = et_channel.text.toString().trim()
        val key = et_key.text.toString().trim()
        if (channel.isEmpty() || key.isEmpty()) {
            Toast.makeText(applicationContext, "请输入渠道号或者Key", Toast.LENGTH_SHORT).show()
            return
        }
        BigFunSDK.getInstance().init(applicationContext, channel)
        tv_result.text = "sdk初始化成功"
    }

    /**
     * Facebook登录
     */
    private fun fbLogin() {
        LoginModel(this).facebookLogin(callbackManager, object : IFBLoginListener {
            override fun onCancel() {
                Log.d(TAG, "onCancel: facebook登录取消")
                tv_result.text = "Facebook登录取消"
            }

            override fun onError(error: FacebookException?) {
                Log.d(TAG, "onError: ${error?.message}")
                tv_result.text = "Facebook登录错误--${error?.message}"
            }

            override fun onComplete(jsonObject: JSONObject?) {
                jsonObject?.apply {
                    val id = optString("id")
                    val name = optString("name")
                    val jo = optJSONObject("picture")
                    val data = jo?.optJSONObject("data")
                    val headImg = data?.optString("url")
                    val email = optString("email")
                    val map = mutableMapOf<String, Any?>(
                        "authCode" to id, "email" to email,
                        "nickName" to name, "headImg" to headImg
                    )
                    tv_result.text = "Facebook登录成功用户信息--$map"
                    val gameUserId = et_game_user_id.text.toString().trim()
                    if (gameUserId.isEmpty()) {
                        Toast.makeText(applicationContext, "请输入gameUserId", Toast.LENGTH_SHORT)
                            .show()
                        return
                    }
                    map["gameUserId"] = gameUserId
                    BigFunSDK.getInstance().fbLogin(map, object : ResponseListener {
                        override fun onSuccess() {
                            runOnUiThread {
                                tv_result.text = "fb登录成功"
                            }
                        }

                        override fun onFail(msg: String?) {
                            runOnUiThread {
                                tv_result.text = "fb登录失败-$msg"
                            }
                        }
                    })
                }
            }
        })
    }

    /**
     * Google登录
     */
    private fun googleLogin() {
        LoginModel(this).googleLogin(getString(R.string.default_web_client_id), GOOGLE_SIGN_IN_CODE)
    }

    /**
     * 手机号登录
     */
    private fun phoneLogin() {
        val phone = et_phone.text.toString().trim()
        if (phone.isEmpty()) {
            Toast.makeText(applicationContext, "请输入手机号", Toast.LENGTH_SHORT).show()
            return
        }
        if (phone.length != 10 && phone.length != 12) {
            Toast.makeText(applicationContext, "手机号位数错误", Toast.LENGTH_SHORT).show()
            return
        }
        val gameUserId = et_game_user_id.text.toString().trim()
        if (gameUserId.isEmpty()) {
            Toast.makeText(applicationContext, "请输入gameUserId", Toast.LENGTH_SHORT).show()
            return
        }
        val map = mutableMapOf<String, Any?>(
            "gameUserId" to gameUserId,
            "mobile" to phone
        )
        BigFunSDK.getInstance().phoneLogin(map, object : ResponseListener {
            override fun onSuccess() {
                runOnUiThread {
                    tv_result.text = "手机号登录成功"
                }
            }

            override fun onFail(msg: String?) {
                runOnUiThread {
                    tv_result.text = "手机号登录失败-$msg"
                }
            }
        })
    }

    /**
     * 游客登录
     */
    private fun guestLogin() {
        val gameUserId = et_game_user_id.text.toString().trim()
        if (gameUserId.isEmpty()) {
            Toast.makeText(applicationContext, "请输入gameUserId", Toast.LENGTH_SHORT).show()
            return
        }
        val map = mutableMapOf<String, Any?>(
            "gameUserId" to gameUserId
        )
        BigFunSDK.getInstance().guestLogin(map, object : ResponseListener {
            override fun onSuccess() {
                runOnUiThread {
                    tv_result.text = "游客登录成功"
                }
            }

            override fun onFail(msg: String?) {
                runOnUiThread {
                    tv_result.text = "游客登录失败--$msg"
                }
            }
        })
    }

    /**
     * 加密渠道号
     */
    private fun encryptKey() {
        val channel = et_channel.text.toString().trim()
        val key = et_key.text.toString().trim()
        if (channel.isEmpty() || key.isEmpty()) {
            Toast.makeText(applicationContext, "渠道号或者Key不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        val encryptChannel = DesUtils.encode(key, "channelCode:$channel")
        tv_result.text = "加密后的渠道--$encryptChannel"
        val clipData = ClipData.newPlainText("Label", encryptChannel)
        cm.setPrimaryClip(clipData)
    }

    /**
     * 解密渠道号
     */
    private fun decryptKey() {
        val key = et_key.text.toString().trim()
        if (key.isEmpty()) {
            Toast.makeText(applicationContext, "Key不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        if (cm.hasPrimaryClip()) {
            if (cm.primaryClipDescription!!.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                val clipData = cm.primaryClip
                val item = clipData!!.getItemAt(0)
                if (item.text != null && !TextUtils.isEmpty(item.text.toString())) {
                    val result = DesUtils.decode(key, item.text.toString())
                    Log.d(TAG, "decryptKey: $result")
                    if (result.startsWith("channelCode:")) {
                        val resultArr = result.split(":".toRegex()).toTypedArray()
                        tv_result.text = "解密出来的渠道号--${resultArr[1]}"
                    } else {
                        Toast.makeText(applicationContext, "剪切板内容格式不是渠道合适", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(applicationContext, "剪切板无内容", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "剪切板无内容", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(applicationContext, "剪切板无内容", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN_CODE) {
            if (data != null) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val result = task.getResult(ApiException::class.java)
                    val id = result.id
                    val name = result.displayName
                    val headImg = result.photoUrl.toString()
                    val token = result.idToken
                    val email = result.email
                    val map = mutableMapOf<String, Any?>(
                        "authCode" to id,
                        "email" to email, "nickName" to name, "headImg" to headImg
                    )
                    Log.d(TAG, "onActivityResult: $map")
                    tv_result.text = "Google登录成功用户信息--$map"
                    val gameUserId = et_game_user_id.text.toString().trim()
                    if (gameUserId.isEmpty()) {
                        Toast.makeText(applicationContext, "gameUserId不能为空", Toast.LENGTH_SHORT)
                            .show()
                        return
                    }
                    map["gameUserId"] = gameUserId
                    BigFunSDK.getInstance().googleLogin(map, object : ResponseListener {
                        override fun onSuccess() {
                            runOnUiThread {
                                tv_result.text = "google登录成功"
                            }
                        }

                        override fun onFail(msg: String?) {
                            runOnUiThread {
                                tv_result.text = "google登录失败--$msg"
                            }
                        }
                    })
                } catch (e: ApiException) {
                    Log.d(TAG, "onActivityResult: error" + e.message)
                    e.printStackTrace()
                } catch (e: JSONException) {
                    Log.d(TAG, "onActivityResult: error" + e.message)
                    e.printStackTrace()
                }
            }
        }
    }

    fun logSentFriendRequestEvent() {
        AppEventsLogger.newLogger(applicationContext).logEvent("sentFriendRequest")
    }
}

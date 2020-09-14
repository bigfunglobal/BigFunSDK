package com.lqq.sdk

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.facebook.CallbackManager
import com.facebook.FacebookException
import com.three.login.LoginModel
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private val callbackManager by lazy {
        CallbackManager.Factory.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_google_login.setOnClickListener {
            val loginModel = LoginModel(this)
        }

        btn_google_login.setOnClickListener {
            val loginModel = LoginModel(this)
            loginModel.facebookLogin(callbackManager, object : LoginModel.IFBLoginListener {
                override fun onCancel() {
                    //用户取消
                }

                override fun onComplete(jsonObject: JSONObject?, token: String?) {
                    //获取用户信息成功
                    val id = jsonObject?.optString("id")
                    val name = jsonObject?.optString("name")
                    val dataObject = jsonObject?.optJSONObject("picture")
                    var headImg = ""
                    dataObject?.apply {
                        optJSONObject("data")?.apply {
                            headImg = optString("url")
                        }
                    }
                }


                override fun onError(error: FacebookException) {
                    //登录失败
                }
            })
        }

        btn_aaid.setOnClickListener {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            data?.apply {
//                val task = GoogleSignIn.getSignedInAccountFromIntent(this)
//                Log.d(TAG, "onActivityResult: ${task.isSuccessful}")
            }
        }
    }
}
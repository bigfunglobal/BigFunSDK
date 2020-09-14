package com.bigfun.tm

import android.app.Dialog
import android.content.Context
import android.support.annotation.Keep
import android.widget.Toast
import kotlinx.android.synthetic.main.dialog_custom.*
import java.util.regex.Matcher
import java.util.regex.Pattern

@Keep
class CustomDialog : Dialog {
    private lateinit var mListener: IOnClickListener

    constructor(context: Context) : this(context, 0)

    constructor(context: Context, themeResId: Int) : super(context, themeResId) {
        initView(context)
    }

    private fun initView(context: Context) {
        val view = layoutInflater.inflate(R.layout.dialog_custom, null)
        setCanceledOnTouchOutside(false)
        setContentView(view)

        btn_certain.setOnClickListener {
            if (et_phone.text.isEmpty()) {
                Toast.makeText(context, "please enter phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (et_phone.text.toString().length != 10) {
                Toast.makeText(
                    context,
                    "Please fill in the correct phone number",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (et_email.text.isEmpty()) {
                Toast.makeText(context, "please input your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!checkEmail(et_email.text.toString())) {
                Toast.makeText(
                    context,
                    "Please fill in the correct email address",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            mListener.ok(et_email.text.toString(), et_phone.text.toString())
            dismiss()
        }
    }

    @Keep
    interface IOnClickListener {
        fun ok(email: String, phone: String)
    }

    fun setOnClickListener(listener: IOnClickListener) {
        mListener = listener
    }

    private fun checkEmail(email: String): Boolean {
        val regex =
            "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$"
        val pattern: Pattern = Pattern.compile(regex)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }
}
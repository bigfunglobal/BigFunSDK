package com.bigfun.tm

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.widget.LinearLayout
import com.just.agentweb.AgentWeb
import kotlinx.android.synthetic.main.activity_pay.*

class PayActivity : AppCompatActivity() {

    private var mAgentWeb: AgentWeb? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)

        val url = intent.getStringExtra(EXTRA_KEY_PAY_URL)

        mAgentWeb = AgentWeb.with(this)
            .setAgentWebParent(fl, LinearLayout.LayoutParams(-1, -1))
            .useDefaultIndicator()
            .createAgentWeb()
            .ready()
            .go(url)

        iv_back.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        mAgentWeb?.webLifeCycle?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mAgentWeb?.webLifeCycle?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mAgentWeb?.webLifeCycle?.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        mAgentWeb?.apply {
            if (handleKeyEvent(keyCode, event)) {
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
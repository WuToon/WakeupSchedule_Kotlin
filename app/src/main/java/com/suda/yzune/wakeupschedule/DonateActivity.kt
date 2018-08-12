package com.suda.yzune.wakeupschedule

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.suda.yzune.wakeupschedule.bean.CourseOldBean
import com.suda.yzune.wakeupschedule.bean.DonateBean
import com.suda.yzune.wakeupschedule.utils.DonateUtils
import com.suda.yzune.wakeupschedule.utils.MyRetrofitUtils
import com.suda.yzune.wakeupschedule.utils.SizeUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_donate.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DonateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ViewUtils.fullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donate)
        ViewUtils.resizeStatusBar(this, v_status)
        initEvent()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initData() {
        MyRetrofitUtils.instance.getService().getDonateList().enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                displayError()
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response!!.body() != null) {
                    val gson = Gson()
                    val list = gson.fromJson<List<DonateBean>>(response.body()!!.string(), object : TypeToken<List<DonateBean>>() {
                    }.type)
                    displayList(list)
                } else {
                    displayError()
                }
            }

        })
    }

    private fun displayError() {
        val textView = TextView(this)
        val textParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        textParams.setMargins(0, 0, 0, SizeUtils.dp2px(applicationContext, 8f))
        textView.layoutParams = textParams
        textView.text = "加载失败:(\n\n点击此处重试"
        textView.setOnClickListener {
            ll_middle.removeAllViews()
            initData()
        }
        textView.textSize = 12f
        textView.gravity = Gravity.CENTER
        ll_middle.addView(textView)
    }

    private fun displayList(list: List<DonateBean>) {
        for (item in list) {
            val textView = TextView(this)
            val textParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            textParams.setMargins(0, 0, 0, SizeUtils.dp2px(applicationContext, 8f))
            textView.layoutParams = textParams
            textView.text = item.name
            textView.textSize = 12f
            when (item.id % 3) {
                0 -> ll_right.addView(textView)
                1 -> ll_left.addView(textView)
                2 -> ll_middle.addView(textView)
            }
        }
    }

    private fun initEvent() {
        ib_back.setOnClickListener {
            finish()
        }

        tv_donate.setOnClickListener {
            if (DonateUtils.isAppInstalled(applicationContext, "com.eg.android.AlipayGphone")) {
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                val qrCodeUrl = Uri.parse("alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=HTTPS://QR.ALIPAY.COM/FKX09148M0LN2VUUZENO9B?_s=web-other")
                intent.data = qrCodeUrl
                intent.setClassName("com.eg.android.AlipayGphone", "com.alipay.mobile.quinox.LauncherActivity")
                startActivity(intent)
                Toasty.success(applicationContext, "非常感谢(*^▽^*)").show()
            } else {
                Toasty.info(applicationContext, "没有检测到支付宝客户端o(╥﹏╥)o").show()
            }
        }
    }
}

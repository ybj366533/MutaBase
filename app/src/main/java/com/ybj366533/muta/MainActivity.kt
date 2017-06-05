package com.ybj366533.muta

import android.app.Activity
import android.os.Bundle
import com.ybj366533.muta_base.util.LogUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_show.setOnClickListener{
            LogUtil.e("lalal")
        }
    }
}

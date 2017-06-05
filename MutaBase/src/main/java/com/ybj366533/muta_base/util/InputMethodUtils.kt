package com.ybj366533.muta_base.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Created by YBJ on 2017/6/5.
 * 显示键盘d工具类
 */
object InputMethodUtils {

    /** 显示软键盘  */
    fun showInputMethod(view: View) {

        val imm = view.context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    /** 显示软键盘  */
    fun showInputMethod(context: Context) {

        val imm = context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    /** 多少时间后显示软键盘  */
    fun showInputMethod(view: View?, delayMillis: Long) {

        if (view == null)
            return
        // 显示输入法
        view.postDelayed({ InputMethodUtils.showInputMethod(view) }, delayMillis)
    }
}

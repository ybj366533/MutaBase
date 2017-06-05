package com.ybj366533.muta_base.base_popup

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow

/**
 * Created by YBJ on 2017/6/5.
 *
 *
 * 与basePopupWindow强引用(或者说与PopupController强引用)
 */

class PopupWindowProxy : PopupWindow {

    private val isFixAndroidN = Build.VERSION.SDK_INT == 24
    private val isOverAndroidN = Build.VERSION.SDK_INT > 24


    private lateinit var mController: PopupController

    constructor(context: Context, mController: PopupController) : super(context) {
        this.mController = mController
    }

    constructor(context: Context, attrs: AttributeSet, mController: PopupController) : super(context, attrs) {
        this.mController = mController
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, mController: PopupController) : super(context, attrs, defStyleAttr) {
        this.mController = mController
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int, mController: PopupController) : super(context, attrs, defStyleAttr, defStyleRes) {
        this.mController = mController
    }

    constructor(mController: PopupController) {
        this.mController = mController
    }

    constructor(contentView: View, mController: PopupController) : super(contentView) {
        this.mController = mController
    }

    constructor(width: Int, height: Int, mController: PopupController) : super(width, height) {
        this.mController = mController
    }

    constructor(contentView: View, width: Int, height: Int, mController: PopupController) : super(contentView, width, height) {
        this.mController = mController
    }

    constructor(contentView: View, width: Int, height: Int, focusable: Boolean, mController: PopupController) : super(contentView, width, height, focusable) {
        this.mController = mController
    }


    /**
     * fix showAsDropDown when android api ver is over N
     *
     *
     * https://code.google.com/p/android/issues/detail?id=221001

     * @param anchor
     * *
     * @param xoff
     * *
     * @param yoff
     * *
     * @param gravity
     */
    override fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int, gravity: Int) {
        if (isFixAndroidN && anchor != null) {
            val a = IntArray(2)
            anchor.getLocationInWindow(a)
            val activity = anchor.context as Activity
            super.showAtLocation(activity.window.decorView, Gravity.NO_GRAVITY, 0, a[1] + anchor.height)
        } else {
            if (isOverAndroidN) {
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            super.showAsDropDown(anchor, xoff, yoff, gravity)
        }
    }

    override fun dismiss() {
        if (mController == null) return

        val performDismiss = mController!!.onBeforeDismiss()
        if (!performDismiss) return
        val dismissAtOnce = mController!!.callDismissAtOnce()
        if (dismissAtOnce) {
            callSuperDismiss()
        }
    }

    internal fun callSuperDismiss() {
        super.dismiss()
    }

}

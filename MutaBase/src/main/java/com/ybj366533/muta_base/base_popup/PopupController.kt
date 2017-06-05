package com.ybj366533.muta_base.base_popup

/**
 * Created by YBJ on 2017/6/5.
 *
 */

interface PopupController {


    fun onBeforeDismiss(): Boolean

    fun callDismissAtOnce(): Boolean

}

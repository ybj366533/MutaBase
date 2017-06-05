package com.ybj366533.muta_base.util

import android.os.Handler
import android.os.Looper


import com.ybj366533.muta_base.util.task.ThreadPool

import android.os.SystemClock.sleep

/**
 * 线程工具类
 * Created by wittytutu on 17-2-23.
 */

object ThreadUtil {
    internal val mHandle: Handler

    init {
        mHandle = Handler(Looper.getMainLooper())
    }

    /**
     * 运行在主线程

     * @param runnable
     */
    fun runOnUiThread(runnable: Runnable) {
        if (isMainThread) {
            runnable.run()
        } else {
            mHandle.post(runnable)
        }
    }

    /**
     * 运行在主线程

     * @param runnable
     * *
     * @param delayMillis
     */
    fun runOnUiThread(runnable: Runnable, delayMillis: Long) {
        mHandle.postDelayed(runnable, delayMillis)
    }

    /**
     * 运行在子线程

     * @param runnable
     */
    fun runOnBackgroundThread(runnable: Runnable) {
        if (isMainThread) {
            runOnBackgroundThread(runnable, 0)
        } else {
            runnable.run()
        }
    }

    /**
     * 运行在子线程

     * @param runnable
     * *
     * @param delayMillis
     */
    fun runOnBackgroundThread(runnable: Runnable, delayMillis: Long) {
        ThreadPool.cachedThread().execute {
            if (delayMillis > 0) {
                sleep(delayMillis)
            }
            runnable.run()
        }
    }

    /**
     * 当前线程是主线程

     * @return
     */
    // Thread.currentThread() == Looper.getMainLooper().getThread();
    val isMainThread: Boolean
        get() {
            var state = false
            if (Looper.myLooper() == Looper.getMainLooper()) {
                state = true
            }
            return state
        }
}

/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ybj366533.muta_base.util

import android.util.Log

/**
 * Log工具，类似android.util.Log。
 * tag自动产生，格式: tagPrefix:className.methodName(L:lineNumber),
 * customTagPrefix为空时只输出：className.methodName(L:lineNumber)。
 * Author: wyouflf
 * Date: 13-7-24
 * Time: 下午12:23
 */
object LogUtil {

    var tagPrefix = "MUTA"
    var isDebug = true
    //private static  String space = "\r\n-------------------------------------------\r\n";
    private val space = "\r\n"

    private fun generateTag(): String {
        //String tag = getTrace(3);
        //tag = TextUtils.isEmpty(tagPrefix) ? tag : tagPrefix + ":" + tag;
        //return tag;
        return tagPrefix
    }

    private val trace: String
        get() = getTrace(3)

    private fun getTrace(index: Int): String {
        val caller = Throwable().stackTrace[index]
        var tag = "%s.%s(%s:%d)"
        val callerClazzName = caller.className
        //callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.methodName, caller.fileName, caller.lineNumber)
        return tag
    }

    fun d(content: String) {
        if (!isDebug) return
        val tag = generateTag()

        Log.d(tag, trace + space + content)
    }

    fun d(content: String, tr: Throwable) {
        if (!isDebug) return
        val tag = generateTag()

        Log.d(tag, trace + space + content, tr)
    }

    fun e(content: String) {
        if (!isDebug) return
        val tag = generateTag()

        Log.e(tag, trace + space + content)
    }

    fun e(content: String, tr: Throwable) {
        if (!isDebug) return
        val tag = generateTag()

        Log.e(tag, trace + space + content, tr)
    }

    fun i(content: String) {
        if (!isDebug) return
        val tag = generateTag()

        Log.i(tag, trace + space + content)
    }

    fun i(content: String, tr: Throwable) {
        if (!isDebug) return
        val tag = generateTag()

        Log.i(tag, trace + space + content, tr)
    }

    fun v(content: String) {
        if (!isDebug) return
        val tag = generateTag()

        Log.v(tag, trace + space + content)
    }

    fun v(content: String, tr: Throwable) {
        if (!isDebug) return
        val tag = generateTag()

        Log.v(tag, trace + space + content, tr)
    }

    fun w(content: String) {
        if (!isDebug) return
        val tag = generateTag()

        Log.w(tag, trace + space + content)
    }

    fun w(content: String, tr: Throwable) {
        if (!isDebug) return
        val tag = generateTag()

        Log.w(tag, trace + space + content, tr)
    }

    fun w(tr: Throwable) {
        if (!isDebug) return
        val tag = generateTag()

        //Log.w(tag, getTrace() + space +tr);
        Log.w(tag, trace + space, tr)
    }


    fun wtf(content: String) {
        if (!isDebug) return
        val tag = generateTag()

        Log.wtf(tag, trace + space + content)
    }

    fun wtf(content: String, tr: Throwable) {
        if (!isDebug) return
        val tag = generateTag()

        Log.wtf(tag, trace + space + content, tr)
    }

    fun wtf(tr: Throwable) {
        if (!isDebug) return
        val tag = generateTag()

        //Log.wtf(tag, getTrace() + space +tr);
        Log.wtf(tag, trace + space, tr)
    }

}

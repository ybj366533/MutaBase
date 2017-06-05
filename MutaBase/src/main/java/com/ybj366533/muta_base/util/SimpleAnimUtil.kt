package com.ybj366533.muta_base.util

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation

/**
 * Created by YBJ on 2017/6/5.
 * -*
 */

object SimpleAnimUtil {


    /**
     * 生成TranslateAnimation

     * @param durationMillis 动画显示时间
     * *
     * @param start          初始位置
     */
    fun getTranslateAnimation(start: Int, end: Int, durationMillis: Int): Animation {
        val translateAnimation = TranslateAnimation(0f, 0f, start.toFloat(), end.toFloat())
        translateAnimation.duration = durationMillis.toLong()
        translateAnimation.isFillEnabled = true
        translateAnimation.fillAfter = true
        return translateAnimation
    }


    /**
     * 生成ScaleAnimation

     * time=300
     */
    fun getScaleAnimation(fromX: Float,
                          toX: Float,
                          fromY: Float,
                          toY: Float,
                          pivotXType: Int,
                          pivotXValue: Float,
                          pivotYType: Int,
                          pivotYValue: Float): Animation {
        val scaleAnimation = ScaleAnimation(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType,
                pivotYValue
        )
        scaleAnimation.duration = 300
        scaleAnimation.isFillEnabled = true
        scaleAnimation.fillAfter = true
        return scaleAnimation
    }


    /**
     * 生成自定义ScaleAnimation
     */
    val defaultScaleAnimation: Animation
        get() {
            val scaleAnimation = ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            )
            scaleAnimation.duration = 300
            scaleAnimation.interpolator = AccelerateInterpolator()
            scaleAnimation.isFillEnabled = true
            scaleAnimation.fillAfter = true
            return scaleAnimation
        }


    /**
     * 生成默认的AlphaAnimation
     */
    val defaultAlphaAnimation: Animation
        get() {
            val alphaAnimation = AlphaAnimation(0.0f, 1.0f)
            alphaAnimation.duration = 300
            alphaAnimation.interpolator = AccelerateInterpolator()
            alphaAnimation.isFillEnabled = true
            alphaAnimation.fillAfter = true
            return alphaAnimation
        }


    /**
     * 从下方滑动上来
     */
    fun getDefaultSlideFromBottomAnimationSet(mAnimaView: View?): AnimatorSet {

        var set: AnimatorSet? = AnimatorSet()
        if (mAnimaView != null) {
            set?.playTogether(
                    ObjectAnimator.ofFloat(mAnimaView, "translationY", 250.0f, 0.0f).setDuration(400),
                    ObjectAnimator.ofFloat(mAnimaView, "alpha", 0.4f, 1.0f).setDuration((250 * 3 / 2).toLong())
            )
        }
        return set!!
    }
}

package com.ybj366533.muta_base.base_popup

import android.animation.Animator
import android.animation.AnimatorSet
import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.widget.AdapterView
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.PopupWindow

import com.ybj366533.muta_base.R
import com.ybj366533.muta_base.util.InputMethodUtils
import com.ybj366533.muta_base.util.SimpleAnimUtil


/**
 * Created by YBJ on 2017/6/5.
 *
 * 抽象通用popupwindow的父类
 */
abstract class BasePopupWindow : BasePopup, PopupWindow.OnDismissListener, PopupController {

    //元素定义
    private lateinit var mPopupWindow: PopupWindowProxy

    //popup视图
    /**
     * 获取popupwindow的根布局

     * @return
     */
    private lateinit var popupWindowView: View
    private lateinit var mContext: Activity
    protected lateinit var mAnimaView: View
    protected lateinit var mDismissView: View

    //是否自动弹出输入框(default:false)
    private var autoShowInputMethod = false
    private lateinit var onDismissListener: OnDismissListener
    private lateinit var onBeforeShowCallback: OnBeforeShowCallback

    //anima
    private lateinit var mShowAnimation: Animation
    private lateinit var mShowAnimator: Animator
    private lateinit var mExitAnimation: Animation
    private lateinit var mExitAnimator: Animator

    private  var isExitAnimaPlaying = false
    /**
     * popupwindow是否需要淡入淡出
     */
    var needPopupFade = true
        set(needPopupFadeAnima) {
            field = needPopupFadeAnima
            setPopupAnimaStyle(if (needPopupFadeAnima) R.style.PopupAnimaFade else 0)
        }

    //option
    /**
     * 设置参考点，一般情况下，参考对象指的不是指定的view，而是它的windoToken，可以看作为整个screen

     * @param popupGravity
     */
    var popupGravity = Gravity.NO_GRAVITY
    /**
     * 设定x位置的偏移量(中心点在popup的左上角)
     *
     *

     * @param offsetX
     */
    var offsetX: Int = 0
    /**
     * 设定y位置的偏移量(中心点在popup的左上角)

     * @param offsetY
     */
    var offsetY: Int = 0
    /**
     * 这个值是在创建view时进行测量的，并不能当作一个完全准确的值

     * @return
     */
    var popupViewWidth: Int = 0
        private set
    /**
     * 这个值是在创建view时进行测量的，并不能当作一个完全准确的值

     * @return
     */
    var popupViewHeight: Int = 0
        private set
    //锚点view的location
    private var mAnchorViewLocation: IntArray? = null
    //是否参考锚点
    /**
     * 是否参考锚点view，如果是true，则会显示到跟指定view的x,y一样的位置(如果空间足够的话)

     * @param relativeToAnchorView
     */
    var isRelativeToAnchorView: Boolean = false
        set(relativeToAnchorView) {
            isShowAtDown = true
            field = relativeToAnchorView
        }
    //是否自动适配popup的位置
    var isAutoLocatePopup: Boolean = false
        set(autoLocatePopup) {
            isShowAtDown = true
            field = autoLocatePopup
        }
    //showasdropdown
    /**
     * 决定使用showAtLocation还是showAsDropDown
     * decide showAtLocation/showAsDropDown

     * @param showAtDown
     */
    var isShowAtDown: Boolean = false
    //点击popup外部是否消失
    /**
     * 点击外部是否消失
     *
     *
     * dismiss popup when touch ouside from popup

     * @param dismissWhenTouchOuside true for dismiss
     */
    //指定透明背景，back键相关
    var isDismissWhenTouchOuside: Boolean = false
        set(dismissWhenTouchOuside) {
            field = dismissWhenTouchOuside
            if (dismissWhenTouchOuside) {
                mPopupWindow.isFocusable = true
                mPopupWindow.isOutsideTouchable = true
                mPopupWindow.setBackgroundDrawable(ColorDrawable())
            } else {
                mPopupWindow.isFocusable = false
                mPopupWindow.isOutsideTouchable = false
                mPopupWindow.setBackgroundDrawable(null)
            }

        }

    private var popupLayoutid: Int = 0

    constructor(context: Activity) {
        initView(context, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    constructor(context: Activity, w: Int, h: Int) {
        initView(context, w, h)
    }

    private fun initView(context: Activity, w: Int, h: Int) {
        mContext = context

        popupWindowView = onCreatePopupView()
        mAnimaView = initAnimaView()
        checkPopupAnimaView()

        //默认占满全屏
        mPopupWindow = PopupWindowProxy(popupWindowView, w, h, this)
        mPopupWindow.setOnDismissListener(this)
        isDismissWhenTouchOuside = true

        preMeasurePopupView(w, h)

        //默认是渐入动画
        needPopupFade = Build.VERSION.SDK_INT <= 22

        //=============================================================为外层的view添加点击事件，并设置点击消失
        mDismissView = clickToDismissView
        if (mDismissView !is AdapterView<*>) {
            mDismissView.setOnClickListener { dismiss() }
        }
        if (mAnimaView !is AdapterView<*>) {
            mAnimaView.setOnClickListener { }
        }
        //=============================================================元素获取
        mShowAnimation = initShowAnimation()
        mShowAnimator = initShowAnimator()!!
        mExitAnimation = initExitAnimation()!!
        mExitAnimator = initExitAnimator()!!

        mAnchorViewLocation = IntArray(2)
    }

    private fun checkPopupAnimaView() {
        //处理popupview与animaview相同的情况
        //当popupView与animaView相同的时候，处理位置信息会出问题，因此这里需要对mAnimaView再包裹一层
        if (popupWindowView === mAnimaView) {
            try {
                popupWindowView = FrameLayout(context)
                if (popupLayoutid == 0) {
                    (popupWindowView as FrameLayout).addView(mAnimaView)
                } else {
                    mAnimaView = View.inflate(context, popupLayoutid, popupWindowView as FrameLayout?)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun preMeasurePopupView(w: Int, h: Int) {
        if (popupWindowView != null) {
            //修复可能出现的android 4.2的measure空指针问题
            popupWindowView.measure(w, h)
            popupViewWidth = popupWindowView.measuredWidth
            popupViewHeight = popupWindowView.measuredHeight
            popupWindowView.isFocusableInTouchMode = true
        }
    }


    //------------------------------------------抽象-----------------------------------------------

    /**
     * PopupWindow展示出来后，需要执行动画的View.一般为蒙层之上的View
     */
    protected abstract fun initShowAnimation(): Animation

    /**
     * 设置一个点击后触发dismiss PopupWindow的View，一般为蒙层
     */
    abstract val clickToDismissView: View

    /**
     * 设置展示动画View的属性动画
     */
    protected fun initShowAnimator(): Animator? {
        return null
    }

    /**
     * 设置一个拥有输入功能的View，一般为EditTextView
     */
    val inputView: EditText?
        get() = null

    /**
     * 设置PopupWindow销毁时的退出动画
     */
    protected fun initExitAnimation(): Animation? {
        return null
    }

    /**
     * 设置PopupWindow销毁时的退出属性动画
     */
    protected fun initExitAnimator(): Animator? {
        return null
    }

    /**
     * 设置popup的动画style
     */
    fun setPopupAnimaStyle(animaStyleRes: Int) {
        mPopupWindow.animationStyle = animaStyleRes
    }

    //------------------------------------------showPopup-----------------------------------------------

    /**
     * 调用此方法时，PopupWindow将会显示在DecorView
     */
    fun showPopupWindow() {
        if (checkPerformShow(null)) {
            tryToShowPopup(null)
        }
    }

    /**
     * 调用此方法时，PopupWindow左上角将会与anchorview左上角对齐

     * @param anchorViewResid
     */
    fun showPopupWindow(anchorViewResid: Int) {
        val v = mContext.findViewById(anchorViewResid)
        showPopupWindow(v)
    }

    /**
     * 调用此方法时，PopupWindow左上角将会与anchorview左上角对齐

     * @param v
     */
    fun showPopupWindow(v: View) {
        if (checkPerformShow(v)) {
            isRelativeToAnchorView = true
            tryToShowPopup(v)
        }
    }

    //------------------------------------------Methods-----------------------------------------------
    private fun tryToShowPopup(v: View?) {
        try {
            val offset: IntArray
            //传递了view
            if (v != null) {
                offset = calcuateOffset(v)
                if (isShowAtDown) {
                    mPopupWindow.showAsDropDown(v, offset[0], offset[1])
                } else {
                    mPopupWindow.showAtLocation(v, popupGravity, offset[0], offset[1])
                }
            } else {
                //什么都没传递，取顶级view的id
                mPopupWindow.showAtLocation(mContext!!.findViewById(android.R.id.content), popupGravity, offsetX, offsetY)
            }
            if (mShowAnimation != null && mAnimaView != null) {
                mAnimaView.clearAnimation()
                mAnimaView.startAnimation(mShowAnimation)
            }
            if (mShowAnimation == null && mShowAnimator != null && mAnimaView != null) {
                mShowAnimator.start()
            }
            //自动弹出键盘
            if (autoShowInputMethod && inputView != null) {
                inputView!!.requestFocus()
                InputMethodUtils.showInputMethod(inputView, 150)
            }
        } catch (e: Exception) {
            Log.e(TAG, "show error")
            e.printStackTrace()
        }

    }


    /**
     * 暂时还不是很稳定，需要进一步测试优化

     * @param anchorView
     * *
     * @return
     */
    private fun calcuateOffset(anchorView: View): IntArray {
        val offset = intArrayOf(0, 0)
        anchorView.getLocationOnScreen(mAnchorViewLocation)
        //当参考了anchorView，那么意味着必定使用showAsDropDown，此时popup初始显示位置在anchorView的底部
        //因此需要先将popupview与anchorView的左上角对齐
        if (isRelativeToAnchorView) {
            offset[0] = offset[0] + offsetX
            offset[1] = -anchorView.height + offsetY
        }

        if (this.isAutoLocatePopup) {
            val onTop = screenHeight - mAnchorViewLocation!![1] + offset[1] < popupViewHeight
            if (onTop) {
                offset[1] = offset[1] - popupViewHeight + offsetY
                showOnTop(popupWindowView)
            } else {
                showOnDown(popupWindowView)
            }
        }
        return offset

    }

    /**
     * PopupWindow是否需要自适应输入法，为输入法弹出让出区域

     * @param needAdjust <br></br>
     * *                   ture for "SOFT_INPUT_ADJUST_RESIZE" mode<br></br>
     * *                   false for "SOFT_INPUT_ADJUST_NOTHING" mode
     */
    fun setAdjustInputMethod(needAdjust: Boolean) {
        if (needAdjust) {
            mPopupWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        } else {
            mPopupWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        }
    }

    /**
     * 当PopupWindow展示的时候，这个参数决定了是否自动弹出输入法
     * 如果使用这个方法，您必须保证通过 **getInputView()****得到一个EditTextView
     ** */
    fun setAutoShowInputMethod(autoShow: Boolean) {
        this.autoShowInputMethod = autoShow
        if (autoShow) {
            setAdjustInputMethod(true)
        } else {
            setAdjustInputMethod(false)
        }
    }

    /**
     * 这个参数决定点击返回键是否可以取消掉PopupWindow
     */
    fun setBackPressEnable(backPressEnable: Boolean) {
        if (backPressEnable) {
            mPopupWindow.setBackgroundDrawable(ColorDrawable())
        } else {
            mPopupWindow.setBackgroundDrawable(null)
        }
    }

    /**
     * 这个方法封装了LayoutInflater.from(context).inflate，方便您设置PopupWindow所用的xml

     * @param resId reference of layout
     * *
     * @return root View of the layout
     */
    fun createPopupById(resId: Int): View? {
        if (resId != 0) {
            popupLayoutid = resId
            return LayoutInflater.from(mContext).inflate(resId, null)
        } else {
            return null
        }
    }

    protected fun findViewById(id: Int): View? {
        if (popupWindowView != null && id != 0) {
            return popupWindowView.findViewById(id)
        }
        return null
    }

    /**
     * 是否允许popupwindow覆盖屏幕（包含状态栏）
     */
    fun setPopupWindowFullScreen(needFullScreen: Boolean) {
        fitPopupWindowOverStatusBar(needFullScreen)
    }

    /**
     * 这个方法用于简化您为View设置OnClickListener事件，多个View将会使用同一个点击事件
     */
    protected fun setViewClickListener(listener: View.OnClickListener?, vararg views: View) {
        for (view in views) {
            if (listener != null) {
                view.setOnClickListener(listener)
            }
        }
    }

    private fun fitPopupWindowOverStatusBar(needFullScreen: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                val mLayoutInScreen = PopupWindow::class.java.getDeclaredField("mLayoutInScreen")
                mLayoutInScreen.isAccessible = true
                mLayoutInScreen.set(mPopupWindow, needFullScreen)
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

        }
    }
    //------------------------------------------Getter/Setter-----------------------------------------------

    /**
     * PopupWindow是否处于展示状态
     */
    val isShowing: Boolean
        get() = mPopupWindow.isShowing

    var showAnimation: Animation?
        get() = mShowAnimation
        set(showAnimation) {
            if (mShowAnimation != null && mAnimaView != null) {
                mAnimaView.clearAnimation()
                mShowAnimation.cancel()
            }
            if (showAnimation != null && showAnimation !== mShowAnimation) {
                mShowAnimation = showAnimation
            }
        }

    var showAnimator: Animator?
        get() = mShowAnimator
        set(showAnimator) {
            if (mShowAnimator != null) mShowAnimator!!.cancel()
            if (showAnimator != null && showAnimator !== mShowAnimator) {
                mShowAnimator = showAnimator
            }
        }

    var exitAnimation: Animation?
        get() = mExitAnimation
        set(exitAnimation) {
            if (mExitAnimation != null && mAnimaView != null) {
                mAnimaView.clearAnimation()
                mExitAnimation.cancel()
            }
            if (exitAnimation != null && exitAnimation !== mExitAnimation) {
                mExitAnimation = exitAnimation
            }
        }

    var exitAnimator: Animator?
        get() = mExitAnimator
        set(exitAnimator) {
            if (mExitAnimator != null) mExitAnimator!!.cancel()
            if (exitAnimator != null && exitAnimator !== mExitAnimator) {
                mExitAnimator = exitAnimator
            }
        }

    val context: Context
        get() = mContext

    /**
     * 获取popupwindow实例

     * @return
     */
    val popupWindow: PopupWindow
        get() = mPopupWindow

    //------------------------------------------状态控制-----------------------------------------------

    /**
     * 取消一个PopupWindow，如果有退出动画，PopupWindow的消失将会在动画结束后执行
     */
    fun dismiss() {
        try {
            mPopupWindow.dismiss()
        } catch (e: Exception) {
            Log.e(TAG, "dismiss error")
        }

    }

    override fun onBeforeDismiss(): Boolean {
        return checkPerformDismiss()
    }

    override fun callDismissAtOnce(): Boolean {
        var hasAnima = false
        if (mExitAnimation != null && mAnimaView != null) {
            if (!isExitAnimaPlaying) {
                mExitAnimation.setAnimationListener(mAnimationListener)
                mAnimaView.clearAnimation()
                mAnimaView.startAnimation(mExitAnimation)
                isExitAnimaPlaying = true
                hasAnima = true
            }
        } else if (mExitAnimator != null) {
            if (!isExitAnimaPlaying) {
                mExitAnimator.removeListener(mAnimatorListener)
                mExitAnimator.addListener(mAnimatorListener)
                mExitAnimator.start()
                isExitAnimaPlaying = true
                hasAnima = true
            }
        }
        //如果有动画，则不立刻执行dismiss
        return !hasAnima
    }

    /**
     * 直接消掉popup而不需要动画
     */
    fun dismissWithOutAnima() {
        if (!checkPerformDismiss()) return
        try {
            if (mExitAnimation != null && mAnimaView != null) mAnimaView.clearAnimation()
            if (mExitAnimator != null) mExitAnimator.removeAllListeners()
            mPopupWindow.callSuperDismiss()
        } catch (e: Exception) {
            Log.e(TAG, "dismiss error")
        }

    }


    private fun checkPerformDismiss(): Boolean {
        var callDismiss = true
        if (onDismissListener != null) {
            callDismiss = onDismissListener.onBeforeDismiss()
        }
        return callDismiss && !isExitAnimaPlaying
    }

    private fun checkPerformShow(v: View?): Boolean {
        var result = true
        if (onBeforeShowCallback != null) {
            result = onBeforeShowCallback.onBeforeShow(popupWindowView, v!!, this.mShowAnimation != null || this.mShowAnimator != null)
        }
        return result
    }

    //------------------------------------------Anima-----------------------------------------------

    private val mAnimatorListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {
            isExitAnimaPlaying = true
        }

        override fun onAnimationEnd(animation: Animator) {
            mPopupWindow.callSuperDismiss()
            isExitAnimaPlaying = false
        }

        override fun onAnimationCancel(animation: Animator) {
            isExitAnimaPlaying = false
        }

        override fun onAnimationRepeat(animation: Animator) {

        }
    }

    private val mAnimationListener = object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) {
            isExitAnimaPlaying = true
        }

        override fun onAnimationEnd(animation: Animation) {
            mPopupWindow.callSuperDismiss()
            isExitAnimaPlaying = false
        }

        override fun onAnimationRepeat(animation: Animation) {

        }
    }

    /**
     * 生成TranslateAnimation

     * @param durationMillis 动画显示时间
     * *
     * @param start          初始位置
     */
    protected fun getTranslateAnimation(start: Int, end: Int, durationMillis: Int): Animation {
        return SimpleAnimUtil.getTranslateAnimation(start, end, durationMillis)
    }

    /**
     * 生成ScaleAnimation
     *
     *
     * time=300
     */
    protected fun getScaleAnimation(fromX: Float,
                                    toX: Float,
                                    fromY: Float,
                                    toY: Float,
                                    pivotXType: Int,
                                    pivotXValue: Float,
                                    pivotYType: Int,
                                    pivotYValue: Float): Animation {
        return SimpleAnimUtil.getScaleAnimation(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType, pivotYValue)
    }

    /**
     * 生成自定义ScaleAnimation
     */
    protected val defaultScaleAnimation: Animation
        get() = SimpleAnimUtil.defaultScaleAnimation

    /**
     * 生成默认的AlphaAnimation
     */
    protected val defaultAlphaAnimation: Animation
        get() = SimpleAnimUtil.defaultAlphaAnimation

    /**
     * 从下方滑动上来
     */
    protected val defaultSlideFromBottomAnimationSet: AnimatorSet
        get() = SimpleAnimUtil.getDefaultSlideFromBottomAnimationSet(mAnimaView)

    /**
     * 获取屏幕高度(px)
     */
    val screenHeight: Int
        get() = context.resources.displayMetrics.heightPixels

    /**
     * 获取屏幕宽度(px)
     */
    val screenWidth: Int
        get() = context.resources.displayMetrics.widthPixels

    //------------------------------------------callback-----------------------------------------------
    protected fun showOnTop(mPopupView: View) {

    }

    protected fun showOnDown(mPopupView: View) {

    }

    override fun onDismiss() {
        if (onDismissListener != null) {
            onDismissListener.onDismiss()
        }
        isExitAnimaPlaying = false
    }


    //------------------------------------------Interface-----------------------------------------------
    interface OnBeforeShowCallback {
        /**
         * **return ture for perform show**

         * @param popupRootView The rootView of popup,it's usually be your layout
         * *
         * @param anchorView    The anchorView whitch popup show
         * *
         * @param hasShowAnima  Check if show your popup with anima?
         * *
         * @return
         */
        fun onBeforeShow(popupRootView: View, anchorView: View, hasShowAnima: Boolean): Boolean


    }

    abstract class OnDismissListener : PopupWindow.OnDismissListener {
        /**
         * **return ture for perform dismiss**

         * @return
         */
        fun onBeforeDismiss(): Boolean {
            return true
        }
    }

    companion object {
        private val TAG = "BasePopupWindow"
    }
}

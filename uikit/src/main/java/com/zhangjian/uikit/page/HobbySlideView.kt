package com.zhangjian.uikit.page

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.zhangjian.uikit.DisplayUtil
import kotlin.math.abs

/**
 *  Create by shang_dong on 2023/3/1
 */
class HobbySlideView : FrameLayout {
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    //默认偏移的角度
    var rotateOffset = 4f
    private val roofRotateOffset = 10f

    companion object {
        const val INDEX_ROOF = 2  //顶
        const val INDEX_CENTER = 1  //中间
        const val INDEX_BOTTOM = 0  //底
    }

    private var mAdapter: HobbySlideAdapter<*>? = null

    private var currentTopIndex = 0

    fun setData(adapter: HobbySlideAdapter<*>) {
        anim?.cancel()
        mAdapter = adapter
        removeAllViews()
        for (i in 0..2) {
            addView(
                adapter.onCreatePage(this),
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            )
        }
        initPivot()
        getRoofView().rotation = 0f
        getRoofView().translationX = 0f
        getCenterView().rotation = -rotateOffset
        getCenterView().translationX = 0f
        getBottomView().rotation = -rotateOffset
        getBottomView().translationX = 0f

        currentTopIndex = 0
        onBindData()
    }

    private fun onBindData() {
        (mAdapter as? HobbySlideAdapter<View>)?.let {
            it.onBindView(0, getRoofView())
            it.onBindView(1, getCenterView())
            it.onBindView(2, getBottomView())
        }
    }

    private fun HobbySlideAdapter<View>.onBindView(pos: Int, view: View) {
        val adapter = this
        val dataPos = pos + currentTopIndex
        view.visibility = if (dataPos < adapter.getCount()) {
            adapter.onBind(dataPos, view)
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun initPivot() {
        if (width > 0 && height > 0 && childCount == 3) {
            getRoofView().let {
                it.pivotX = width / 2f
                it.pivotY = bottom + DisplayUtil.dip2px(context, 200f).toFloat() //向下偏移一定距离
            }

            getCenterView().let {
                it.pivotX = width / 2f
                it.pivotY = height / 2f
            }

            getBottomView().let {
                it.pivotX = width / 2f
                it.pivotY = height / 2f
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initPivot()
    }

    private fun getRoofView() = getChildAt(INDEX_ROOF)
    private fun getCenterView() = getChildAt(INDEX_CENTER)
    private fun getBottomView() = getChildAt(INDEX_BOTTOM)

    /**
     * 调整index,发生在移除顶部的一个之后
     */
    private fun removeRoof(toRight: Boolean) {
        mAdapter?.onItemResult(currentTopIndex, toRight)
        val roof = getRoofView()
        removeView(roof)
        addView(roof, 0)
        initPivot()
        //最底下的由于是之前的顶，需要调整角度
        getBottomView().rotation = -rotateOffset
        getBottomView().translationX = 0f
        currentTopIndex++
        onBindData()
    }

    private var lastX: Float = 0f
    private var actionPointerId: Int = -1

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mAdapter == null || currentTopIndex == mAdapter!!.getCount()) return false

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                anim?.cancel()
                lastX = event.x
                actionPointerId = event.getPointerId(0)
            }
            MotionEvent.ACTION_MOVE -> {
                val actionPointIndex = event.findPointerIndex(actionPointerId)
                if (actionPointIndex == -1) {
                    return true
                }
                val curX = event.getX(actionPointIndex)
                val distanceX = curX - lastX
                if (distanceX == 0f) {
                    return true
                }
                //横向移动+旋转
                getRoofView().translationX += distanceX * 2 / 3f
                getRoofView().rotate(distanceX, roofRotateOffset, -roofRotateOffset)
                getCenterView().rotation =
                    -rotateOffset + abs(getRoofView().rotation * rotateOffset / roofRotateOffset)

                onSlideStateChange(
                    if (getRoofView().rotation > 0) {
                        HobbySlideState.RIGHT
                    } else if (getRoofView().rotation < 0) {
                        HobbySlideState.LEFT
                    } else {
                        HobbySlideState.IDLE
                    }
                )
                lastX = curX
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                doFinishAnim(
                    abs(getRoofView().rotation) < 6f,
                    getRoofView().rotation > 0
                )
            }
            //多点触控处理参考了ScrollView的处理方式,由新的手指延续滑动
            MotionEvent.ACTION_POINTER_DOWN -> {
                //新的手指按下之后替换成新的手指
                exchangeFinger(event)
            }
            MotionEvent.ACTION_POINTER_UP -> {
                //有手指离开之后
                val upPointIndex = event.actionIndex
                //离开的是当前标记的手指，替换成其他的，此处如果离开的是index0,则换成index1,如果不是index0,则换成index1
                if (event.getPointerId(upPointIndex) == actionPointerId) {
                    exchangeFinger(
                        event, pointIndex = if (upPointIndex == 0) {
                            1
                        } else {
                            0
                        }
                    )
                }
            }
        }
        return true
    }

    private fun View.rotate(distanceX: Float, maxRotate: Float, minRotate: Float) {
        val moveAllDistance = width / 2f
        if ((rotation < maxRotate && distanceX > 0) || (rotation > minRotate && distanceX < 0)) {
            //可选装区域
            rotation =
                maxRotate.coerceAtMost((minRotate).coerceAtLeast(rotation + roofRotateOffset * distanceX / moveAllDistance))
        }
    }

    private fun exchangeFinger(event: MotionEvent, pointIndex: Int = event.actionIndex) {
        lastX = event.getX(pointIndex)
        actionPointerId = event.getPointerId(pointIndex)
    }

    private fun onSlideStateChange(state: HobbySlideState) {
        (mAdapter as? HobbySlideAdapter<View>)?.onSlideStateChange(
            getRoofView(),
            state
        )
    }

    /**
     * 主动触发侧滑
     */
    fun autoSlide(toRight: Boolean) {
        if (currentTopIndex >= (mAdapter?.getCount() ?: 0)) return
        onSlideStateChange(
            if (toRight) {
                HobbySlideState.RIGHT
            } else {
                HobbySlideState.LEFT
            }
        )
        doFinishAnim(false, toRight)
    }

    private var anim: ValueAnimator? = null

    /**
     * 执行动画
     * @param reset true则是不移除，回到中间状态；false则是达到临界，需要执行移除
     */
    private fun doFinishAnim(reset: Boolean, toRight: Boolean) {
        val curRotation = getRoofView().rotation
        val animDuration = if (curRotation >= 6 || curRotation <= -6) {
            200L
        } else {
            350L
        }
        anim?.cancel()
        val roofViewRotationStart = getRoofView().rotation
        val roofViewTranslationXStart = getRoofView().translationX
        val centerViewRotationStart = getCenterView().rotation
        anim = ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                getRoofView().rotation = getNewValueInProgress(
                    roofViewRotationStart, if (reset) {
                        0f
                    } else {
                        if (toRight) {
                            roofRotateOffset
                        } else {
                            -roofRotateOffset
                        }
                    }, progress
                )
                getCenterView().rotation = getNewValueInProgress(
                    centerViewRotationStart, if (reset) {
                        -rotateOffset
                    } else {
                        0f
                    }, progress
                )
                getRoofView().translationX = getNewValueInProgress(
                    roofViewTranslationXStart, if (reset) {
                        0f
                    } else {
                        (width - if (toRight) {
                            paddingLeft
                        } else {
                            paddingRight
                        }) * if (toRight) {
                            1f
                        } else {
                            -1f
                        }
                    }, progress
                )

                if (progress == 1f) {
                    onSlideStateChange(HobbySlideState.IDLE)
                    if (!reset) {
                        removeRoof(toRight)
                    }
                }
            }
            duration = animDuration
        }
        anim!!.start()
    }

    private fun getNewValueInProgress(
        startRotation: Float,
        targetRotation: Float,
        progress: Float
    ): Float {
        return startRotation + (targetRotation - startRotation) * progress
    }

}

interface HobbySlideAdapter<T : View> {

    fun onCreatePage(parent: ViewGroup): T

    fun onBind(pos: Int, view: T)

    fun onItemResult(pos: Int, toRight: Boolean)

    fun onSlideStateChange(t: T, state: HobbySlideState)

    fun getCount(): Int
}

enum class HobbySlideState {
    IDLE, LEFT, RIGHT
}


package com.zhangjian.uikit.tab

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.zhangjian.uikit.DisplayUtil

/**
 * Created by zhangjian on 2021/2/20.
 */
const val TAG = "SlidingBlockTabView"
class SlidingBlockTabView : LinearLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    private val rectF = RectF()
    private val paint = Paint()

    private val slidingBlockMargin = DisplayUtil.dip2px(context, 3.5f)

    var textColor = Color.parseColor("#222222")
        set(value) {
            field = value
            for (textView in textViews) {
                textView.setTextColor(value)
            }
        }
    var textSize = DisplayUtil.dip2px(context, 14f).toFloat()
        set(value) {
            field = value
            for (textView in textViews) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
            }
        }

    private var bgColor = Color.parseColor("#F7F7F7")
    private var slidingBlockColor = Color.parseColor("#FFFFFF")
    private var slidingBlockColorShadow = Color.parseColor("#CCCCCC")

    private val radius = DisplayUtil.dip2px(context, 8f).toFloat()
    private val filter = BlurMaskFilter(radius / 2, BlurMaskFilter.Blur.OUTER)

    init {
        setHorizontalGravity(HORIZONTAL)
        setBackgroundColor(Color.TRANSPARENT)
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
    }

    internal val textViews = mutableListOf<TextView>()

    private var position = 0f  //当前进度
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 更新标题
     */
    fun updateTitle(index: Int, title: String) {
        if (index < 0 || index >= textViews.size) {
            Log.e(TAG,"index is out of textViews.size")
        } else {
            textViews[index].text = title
        }
    }

    fun bindContent(content: View, titles: MutableList<String>) {
        when (content) {
            is ViewPager -> {
                bindViewPager(content, titles)
            }
            is ViewPager2 -> {
                bindViewPager(content, titles)
            }
            else -> {
                throw Throwable("content only support ViewPager or ViewPager2 now!")
            }
        }
    }

    private fun bindViewPager(viewPager: ViewPager, titles: MutableList<String>) {
        val adapter = viewPager.adapter ?: throw Throwable("ViewPager should have PagerAdapter!")

        if (titles.size != adapter.count) {
            throw Throwable("title num should same as ViewPager`s page num!")
        }

        position = viewPager.currentItem.toFloat()  //初始选中位置为page当前位置

        bindTabTitles(titles) {
            tabChang?.invoke(it)
            viewPager.currentItem = it
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                this@SlidingBlockTabView.position = position + positionOffset  //便宜过程中移动的滑块进度
            }

            override fun onPageSelected(position: Int) {
                for (i in 0 until textViews.size) {
                    textViews[i].paint.isFakeBoldText = position == i  //选中的加粗
                    textViews[i].invalidate()
                }
            }
        })
    }

    private fun bindViewPager(viewPager: ViewPager2, titles: MutableList<String>) {
        val adapter = viewPager.adapter ?: throw Throwable("ViewPager should have PagerAdapter!")

        if (titles.size != adapter.itemCount) {
            throw Throwable("title num should same as ViewPager`s page num!")
        }

        position = viewPager.currentItem.toFloat()  //初始选中位置为page当前位置

        bindTabTitles(titles) {
            tabChang?.invoke(it)
            viewPager.currentItem = it
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                this@SlidingBlockTabView.position = position + positionOffset  //便宜过程中移动的滑块进度
            }

            override fun onPageSelected(position: Int) {
                for (i in 0 until textViews.size) {
                    textViews[i].paint.isFakeBoldText = position == i  //选中的加粗
                    textViews[i].invalidate()
                }
            }
        })
    }


    /**
     * 如果不是绑定ViewPager,可直接使用此方法设置标题以及点击时的事件
     */
    @SuppressLint("ClickableViewAccessibility")
    fun bindTabTitles(titles: MutableList<String>, click: (Int) -> Unit) {
        for (i in 0 until titles.size) {
            val textView = TextView(context)
            textView.text = titles[i]
            textView.setTextColor(textColor)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            textView.gravity = Gravity.CENTER
            textView.setOnTouchListener(TabTouchListener(context, {
                click.invoke(i)
            }) {
                doubleClick?.invoke(i)
            })
            textView.paint.isFakeBoldText = position.toInt() == i
            val layoutParams = LayoutParams(0, LayoutParams.MATCH_PARENT, 1f)
            addView(textView, layoutParams)
            textViews.add(textView)
        }
    }

    /**
     * 主动滚动到某个位置
     */
    fun activeScrollToIndex(index: Int) {
        val size = textViews.size
        if (index in 0 until size) {
            val ofFloat = ValueAnimator.ofFloat(position, index.toFloat())
            ofFloat.duration = 100
            ofFloat.addUpdateListener {
                position = it.animatedValue as Float
            }
            ofFloat.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    for (i in 0 until textViews.size) {
                        textViews[i].paint.isFakeBoldText =
                            (textViews[index] == textViews[i]) //选中的加粗
                        textViews[i].invalidate()
                    }
                    position = index.toFloat()
                    tabChang?.invoke(index)
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }

            })
            ofFloat.start()
        }
    }

    var tabChang: ((Int) -> Unit)? = null
    var doubleClick: ((Int) -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val perDistance = width / textViews.size.toFloat()  //每隔的长度
        val slideDistance = width - perDistance - slidingBlockMargin
        if (textViews.size <= 1) return
        val progress = position / (textViews.size - 1)

        //画主背景
        paint.color = bgColor
        rectF.left = 0f
        rectF.top = 0f
        rectF.bottom = height.toFloat()
        rectF.right = width.toFloat()
        paint.color = bgColor
        canvas.drawRoundRect(rectF, radius, radius, paint)

        //画滑块
        //阴影
        rectF.left = progress * slideDistance + slidingBlockMargin
        rectF.top = 0f + slidingBlockMargin
        rectF.bottom = height.toFloat() - slidingBlockMargin
        rectF.right = rectF.left + perDistance - slidingBlockMargin
        paint.color = slidingBlockColorShadow
        paint.maskFilter = filter
        canvas.drawRoundRect(rectF, radius, radius, paint)
        //主体
        paint.color = slidingBlockColor
        paint.maskFilter = null
        canvas.drawRoundRect(rectF, radius, radius, paint)
    }
}
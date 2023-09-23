package com.zhangjian.uikit.wheel

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.zhangjian.uikit.wheel.date.MonthLinkedWheelListAdapter
import com.hunliji.biz.uikit.widget.picker.wheel.WheelRecycle
import com.zhangjian.uikit.wheel.adapter.BaseWheelListAdapter
import com.zhangjian.uikit.wheel.adapter.DataChangeListener
import java.util.*
import kotlin.math.abs
import kotlin.math.asin

/**
 * Numeric wheel view.
 *
 * @author Yuri Kanivets
 * @modify by zhangjian
 */
open class WheelView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle), DataChangeListener {

    //当前的位置
    private var currentItem = 0

    //显示几个
    var visibleItems = 5

    //每个高度
    private var itemHeight = 106

    //支持自定义TextView的样式
    var itemCreator: IItemCreator = NormalItemItemCreate()

    private var adapter: BaseWheelListAdapter<*>? = null

    fun setAdapter(adapter: BaseWheelListAdapter<*>) {
        currentItem = 0
        firstItem = 0
        scrollingOffset = 0
        this.adapter?.dataChangeListener = null
        this.adapter = adapter
        adapter.dataChangeListener = this
        val defPos = adapter.refresh()
        invalidate()
        setCurrentItem(defPos, false)
    }

    open fun getDataCount() = adapter?.getSize() ?: 0

    override fun onDataChange() {
        val newIndex = ((adapter?.curList?.size ?: 1) - 1).coerceAtMost(currentItem)
        if (newIndex != currentItem) {
            setCurrentItem(newIndex, false)
        } else {
            //需要触发下一个
            adapter?.selectItem(newIndex)
            invalidate()
        }
    }

    //是否可以循环
    var isCyclic = false
        set(value) {
            field = value
        }

    //承载所有的条目,通过itemsLayout的绘制条目内容
    private val itemsLayout: LinearLayout by lazy {
        LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }
    }

    private var firstItem = 0

    // Recycle
    private val recycle = WheelRecycle()

    // Scrolling listener
    private var scrollingListener: WheelScroller.ScrollingListener =
        object : WheelScroller.ScrollingListener {
            override fun onStarted() {
                isScrollingPerformed = true
//            notifyScrollingListenersAboutStart()
            }

            override fun onScroll(distance: Int) {
                doScroll(distance)
                val height = height
                if (scrollingOffset > height) {
                    scrollingOffset = height
                    scroller.stopScrolling()
                } else if (scrollingOffset < -height) {
                    scrollingOffset = -height
                    scroller.stopScrolling()
                }
            }

            override fun onFinished() {
                if (isScrollingPerformed) {
//                notifyScrollingListenersAboutEnd()
                    isScrollingPerformed = false
                }
                scrollingOffset = 0
                invalidate()
            }

            override fun onJustify() {
                if (abs(scrollingOffset) > WheelScroller.MIN_DELTA_FOR_SCROLLING) {
                    scroller.scroll(scrollingOffset, 0)
                }
            }
        }

    // Scrolling
    private var scroller: WheelScroller = WheelScroller(getContext(), scrollingListener)
    private var isScrollingPerformed = false
    private var scrollingOffset = 0

    /**
     * Sets the current item. Does nothing when index is wrong.
     *
     * @param index the item index
     * @param animated the animation flag
     */
    open fun setCurrentItem(index: Int, animated: Boolean = false) {
        if (isEmpty()) {
            return
        }
        var indexT = index
        val itemCount = adapter!!.getSize()
        if (indexT < 0 || indexT >= itemCount) {
            if (isCyclic) {
                while (indexT < 0) {
                    indexT += itemCount
                }
                indexT %= itemCount
            } else {
                return
            }
        }
        if (indexT != currentItem) {
            if (animated) {
                var itemsToScroll = indexT - currentItem
                if (isCyclic) {
                    val scroll =
                        itemCount + indexT.coerceAtMost(currentItem) - indexT.coerceAtLeast(
                            currentItem
                        )
                    if (scroll < abs(itemsToScroll)) {
                        itemsToScroll = if (itemsToScroll < 0) scroll else -scroll
                    }
                }
                scroll(itemsToScroll)
            } else {
                scrollingOffset = 0
                currentItem = indexT
                adapter?.selectItem(currentItem)
                invalidate()
            }
        }
    }

    private fun getDesiredHeight(): Int {
        val itemHeight = getItemHeight()
        val desired = itemHeight * visibleItems
        return desired.coerceAtLeast(suggestedMinimumHeight)
    }

    /**
     * Returns height of wheel item
     * @return the item height
     */
    private fun getItemHeight(): Int {
        if (itemHeight != 0) {
            return itemHeight
        }
        if (itemsLayout.getChildAt(0) != null) {
            itemHeight = itemsLayout.getChildAt(0).height
            return itemHeight
        }
        return height / visibleItems
    }

    fun setItemHeight(itemHeight: Int) {
        this.itemHeight = itemHeight
    }

    private fun calculateLayoutWidth(widthSize: Int, mode: Int): Int {
        itemsLayout.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        itemsLayout.measure(
            MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        var width = itemsLayout.measuredWidth
        if (mode == MeasureSpec.EXACTLY) {
            width = widthSize
        } else {
            // Check against our minimum width
            width = width.coerceAtLeast(suggestedMinimumWidth)
            if (mode == MeasureSpec.AT_MOST && widthSize < width) {
                width = widthSize
            }
        }
        itemsLayout.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        return width
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        dealItem()
        val width = calculateLayoutWidth(widthSize, widthMode)
        var height: Int
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize
        } else {
            height = getDesiredHeight()
            if (heightMode == MeasureSpec.AT_MOST) {
                height = height.coerceAtMost(heightSize)
            }
        }
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        layout(r - l, b - t)
    }

    /**
     * Sets layouts width and height
     * @param width the layout width
     * @param height the layout height
     */
    private fun layout(width: Int, height: Int) {
        itemsLayout.layout(0, 0, width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!isEmpty()) {
            updateView()
            drawItems(canvas)
        }
    }

    private fun isEmpty() = (adapter?.getSize() ?: 0) == 0

    /**
     * Draws items
     * @param canvas the canvas for drawing
     */
    private fun drawItems(canvas: Canvas) {
        canvas.save()
        val top = (currentItem - firstItem) * getItemHeight() + (getItemHeight() - height) / 2
        canvas.translate(0f, (-top + scrollingOffset).toFloat())
        if (adapter is MonthLinkedWheelListAdapter) {
            log("firstItem:${firstItem};currentItem:${currentItem}")
        }
        itemsLayout.draw(canvas)
        canvas.restore()
    }

    private fun log(msg: String) {
        if (adapter is MonthLinkedWheelListAdapter) {
            Log.i("Wheel", msg)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled || isEmpty()) {
            return true
        }
        when (event.action) {
            MotionEvent.ACTION_MOVE -> if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_UP -> if (!isScrollingPerformed) {
                var distance = event.y.toInt() - height / 2
                if (distance > 0) {
                    distance += getItemHeight() / 2
                } else {
                    distance -= getItemHeight() / 2
                }
                val items = distance / getItemHeight()
                if (items != 0 && isValidItemIndex(currentItem + items)) {
                    setCurrentItem(currentItem + items, true)
                }
            }
        }
        return scroller.onTouchEvent(event)
    }

    /**
     * Scrolls the wheel
     * @param delta the scrolling value
     */
    private fun doScroll(delta: Int) {
        scrollingOffset += delta
        val itemHeight = getItemHeight()
        var count = scrollingOffset / itemHeight
        var pos = currentItem - count
        val itemCount = adapter?.getSize() ?: 0
        var fixPos = scrollingOffset % itemHeight
        if (abs(fixPos) <= itemHeight / 2) {
            fixPos = 0
        }
        if (isCyclic && itemCount > 0) {
            if (fixPos > 0) {
                pos--
                count++
            } else if (fixPos < 0) {
                pos++
                count--
            }
            // fix position by rotating
            while (pos < 0) {
                pos += itemCount
            }
            pos %= itemCount
        } else {
            //
            if (pos < 0) {
                count = currentItem
                pos = 0
            } else if (pos >= itemCount) {
                count = currentItem - itemCount + 1
                pos = itemCount - 1
            } else if (pos > 0 && fixPos > 0) {
                pos--
                count++
            } else if (pos < itemCount - 1 && fixPos < 0) {
                pos++
                count--
            }
        }
        val offset = scrollingOffset
        if (pos != currentItem) {
            setCurrentItem(pos, false)
        } else {
            invalidate()
        }

        // update offset
        scrollingOffset = offset - count * itemHeight
        if (scrollingOffset > height) {
            scrollingOffset = scrollingOffset % height + height
        }
    }

    /**
     * Scroll the wheel
     * @param itemsToScroll items to scroll
     * @param time scrolling duration
     */
    private fun scroll(itemsToScroll: Int) {
        val distance = itemsToScroll * getItemHeight() - scrollingOffset
        scroller.scroll(distance, 0)
    }

    /**
     * Calculates range for wheel items
     * @return the items range
     */
    private val itemsRange: ItemsRange?
        get() {
            if (getItemHeight() == 0) {
                return null
            }
            var first = currentItem
            var count = 1
            while (count * getItemHeight() < height) {
                first--
                count += 2 // top + bottom items
            }
            if (scrollingOffset != 0) {
                if (scrollingOffset > 0) {
                    first--
                }
                count++

                val emptyItems = scrollingOffset / getItemHeight()
                first -= emptyItems
                count += asin(emptyItems.toDouble()).toInt()
            }
            return ItemsRange(first, count)
        }

    /**
     * Rebuilds wheel items if necessary. Caches all unused items.
     *
     * @return true if items are rebuilt
     */
    private fun rebuildItems(): Boolean {
        val dealItemResult = dealItem()
        val range = dealItemResult.relRange
        val first = range.first
        for (i in 0 until itemsLayout.childCount) {
            val index = first + i
            (itemsLayout.getChildAt(i) as TextView).apply {
                itemCreator.updateState(index == currentItem, this)
                text = getItemString(index)
            }
        }
        firstItem = first
        return dealItemResult.change
    }

    private fun dealItem(): DealItemResult {
        val range = itemsRange ?: ItemsRange()
        val first = range.first
        var itemShowSize = 0
        var relFirst = -1
        for (i in 0 until range.count) {
            if ((first + i) >= 0 && (first + i) < (adapter?.curList?.size ?: 0)) {
                itemShowSize++
                if (relFirst == -1) {
                    relFirst = first + i
                }
            }
        }
        val change = itemsLayout.childCount != itemShowSize
        recycle.recycleItems(itemsLayout, itemShowSize)
        val needAddCount = itemShowSize - itemsLayout.childCount
        for (i in 0 until needAddCount) {
            val view = getItemView()
            itemsLayout.addView(view)
        }
        return DealItemResult(change, ItemsRange(relFirst, itemShowSize))
    }

    private class DealItemResult(val change: Boolean, val relRange: ItemsRange)

    /**
     * Updates view. Rebuilds items and label if necessary, recalculate items sizes.
     */
    private fun updateView() {
        if (rebuildItems()) {
            calculateLayoutWidth(width, MeasureSpec.EXACTLY)
            layout(width, height)
        }
    }

    /**
     * Checks whether intem index is valid
     * @param index the item index
     * @return true if item index is not out of bounds or the wheel is cyclic
     */
    private fun isValidItemIndex(index: Int): Boolean {
        return !isEmpty() && (isCyclic || index >= 0 && index < adapter!!.getSize())
    }

    /**
     * Returns view for specified item
     * @param index the item index
     * @return item view or empty view if index is out of bounds
     */
    private fun getItemView(): TextView {
        var item = recycle.item
        if (item == null) {
            item = itemCreator.create(context).apply {
                layoutParams =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getItemHeight())
            }
        }
        return item
    }

    private fun getItemString(index: Int): String {
        return adapter?.curList?.get(index)?.getText() ?: ""
    }
}

interface IItemCreator {
    fun create(context: Context): TextView

    fun updateState(select: Boolean, child: TextView)
}

class NormalItemItemCreate(
    private val textSizeNormal: Float = 16f,
    private val textSizeSelect: Float = 18f,
    private val textColorNormal: Int = Color.parseColor("#666666"),
    private val textColorSelect: Int = Color.parseColor("#333333")
) : IItemCreator {
    override fun create(context: Context): TextView {
        return TextView(context).apply {
            setTextColor(textColorNormal)
            gravity = Gravity.CENTER
            textSize = textSizeNormal
            setLines(1)
            ellipsize = TextUtils.TruncateAt.END
        }
    }

    override fun updateState(select: Boolean, child: TextView) {
        if (select) {
            child.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD)
            child.setTextColor(textColorSelect)
            child.textSize = textSizeSelect
        } else {
            child.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL)
            child.setTextColor(textColorNormal)
            child.textSize = textSizeNormal
        }
    }
}
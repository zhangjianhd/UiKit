package com.zhangjian.uikit.scope

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.zhangjian.uikit.R
import java.lang.Exception
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 *  Create by zhangjian on 2022/11/2
 */
class SlidingBlockScopeView : View {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SlidingBlockScopeView,
            defStyleAttr,
            0
        ).let {
            barHeight =
                it.getDimension(R.styleable.SlidingBlockScopeView_barHeight, barHeight.toFloat())
                    .toInt()
            textSize =
                it.getDimension(R.styleable.SlidingBlockScopeView_textSize, textSize.toFloat())
                    .toInt()
            spaceV =
                it.getDimension(R.styleable.SlidingBlockScopeView_spaceV, spaceV.toFloat()).toInt()
            spaceH =
                it.getDimension(R.styleable.SlidingBlockScopeView_spaceH, spaceH.toFloat()).toInt()
            blockSize =
                it.getDimension(R.styleable.SlidingBlockScopeView_blockSize, blockSize.toFloat())
                    .toInt()
            textColor = it.getColor(R.styleable.SlidingBlockScopeView_textColor, textColor)
            barColor = it.getColor(R.styleable.SlidingBlockScopeView_barColor, barColor)
            scopeColor = it.getColor(R.styleable.SlidingBlockScopeView_scopeColor, scopeColor)
            bitmapBlock = updateBlockDrawable(
                it.getResourceId(
                    R.styleable.SlidingBlockScopeView_drawableBlock,
                    R.drawable.ico_sliding_block_def
                )
            )
            it.recycle()
        }
    }

    private fun updateBlockDrawable(res: Int): Bitmap {
        val bitmap = (drawable(res) as BitmapDrawable).bitmap
        // 计算缩放比例
        val scaleWidth: Float = blockSize.toFloat() / bitmap.width
        val scaleHeight: Float = blockSize.toFloat() / bitmap.height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private var bitmapBlock: Bitmap

    private var barHeight: Int = 4.dp
    private var textSize: Int = 14.sp
    private var spaceV: Int = 8.dp //文字和滑块间距
    private var spaceH: Int = 8.dp //左右文字间距
    private var blockSize: Int = 28.dp
    private var textColor: Int = parseColor("#333333")
    private var barColor: Int = parseColor("#E2E2E2")
    private var scopeColor: Int = parseColor("#FF264A")

    private var adapter: SlidingBlockScopeAdapter? = null

    private var pointCount = 0

    private var leftPoint = 0  //左边选中的点，注意和[leftX]区分，leftX为绘制时的x轴坐标
    private var rightPoint = 0 //右边选中的点

    fun setAdapter(
        adapter: SlidingBlockScopeAdapter,
        leftIndex: Int? = null,
        rightIndex: Int? = null
    ) {
        this.adapter = adapter
        pointCount = adapter.getSize()
        if (pointCount < 2) {
            Log.e("SlidingBlockScopeView", "SlidingBlockScopeAdapter.getSize()必须有两个或以上的值才有意义")
            return
        }
        setIndex(leftIndex ?: 0, rightIndex ?: pointCount - 1)
    }

    /**
     * 设置状态
     */
    @Throws(Exception::class)
    fun setIndex(leftIndex: Int, rightIndex: Int) {
        if (leftIndex >= rightIndex) {
            throw Exception("范围错误，left应该小与right")
        }
        if (leftIndex > pointCount - 1 || leftIndex < 0) {
            throw IndexOutOfBoundsException("left越界")
        }
        if (rightIndex > pointCount - 1 || rightIndex < 0) {
            throw IndexOutOfBoundsException("right越界")
        }
        setBlockIndex(leftIndex, rightIndex)
    }

    /**
     * 通过序列更新滑块位置
     */
    private fun setBlockIndex(leftPoint: Int, rightPoint: Int) {
        Log.d(
            "SlidingBlockScopeView",
            "updateBlockIndex :leftPoint=${leftPoint};rightPoint=${rightPoint}"
        )
        this.leftPoint = leftPoint
        this.rightPoint = rightPoint
        if (width != 0) {
            updateBlock()
        }
        notifyScopeChange()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            updateBlock()
        }
    }

    private fun updateBlock() {
        val totalW = width - blockSize
        setBlockLocation(
            (leftPoint / (pointCount - 1).toFloat()) * totalW + blockSize / 2f,
            (rightPoint / (pointCount - 1).toFloat()) * totalW + blockSize / 2f
        )
    }

    /**
     * 更新左边和右边的滑块x轴的位置
     */
    private fun setBlockLocation(leftX: Float, rightX: Float) {
        Log.d("SlidingBlockScopeView", "updateBlockX :leftX=${leftX};rightX=${rightX}")
        this.leftX = leftX
        this.rightX = rightX
        invalidate()
    }

    private fun notifyScopeChange() {
        adapter?.onScopeChange(leftPoint, rightPoint)
    }

    private val paint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }

    private val textPaint: Paint by lazy {
        TextPaint(Paint.ANTI_ALIAS_FLAG)
    }

    private var leftX: Float = 0f //左边滑块x轴中心
    private var rightX: Float = 0f//右边滑块x轴中心

    private val rect: RectF by lazy {
        RectF()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //贴底绘制，即如果设置的高度比较高则滑块贴着底部绘制
        canvas ?: return
        adapter ?: return  //没有adapter什么都不绘制
        if (width <= 0 || height <= 0) return
        if (leftPoint >= rightPoint) return
        val blockCenterY = height - blockSize / 2f   //滑块y轴的中心
        drawBar(canvas, blockCenterY)
        drawBlock(canvas, blockCenterY)
        drawText(canvas)
    }

    /**
     * 绘制横条
     */
    private fun drawBar(canvas: Canvas, centerY: Float) {
        //绘制底层背景
        paint.color = barColor
        rect.left = blockSize / 2f
        rect.top = centerY - barHeight / 2f
        rect.right = width.toFloat() - blockSize / 2f
        rect.bottom = rect.top + barHeight
        canvas.drawRoundRect(rect, barHeight / 2f, barHeight / 2f, paint)

        paint.color = scopeColor
        //绘制选中后的颜色
        rect.left = leftX
        rect.right = rightX
        canvas.drawRoundRect(rect, barHeight / 2f, barHeight / 2f, paint)
    }

    /**
     * 绘制文本
     * 需要校准位置：规则 ： 左右的文字都不能超出控件的左右边界，另外，文字之间要保证空余[spaceH]的间隔空间
     */
    private fun drawText(canvas: Canvas) {

        textPaint.textSize = textSize.toFloat()
        textPaint.color = textColor

        val textBaseLine = (height - blockSize - spaceV).toFloat()  //文字BaseLine

        //左边文字
        val leftText = adapter!!.attachShow(getPointExact(leftX))
        val leftTextWidth = textPaint.measureText(leftText)
        //右边文字
        val rightText = adapter!!.attachShow(getPointExact(rightX))
        val rightTextWidth = textPaint.measureText(rightText)
        //校准位置
        var adjustingType = ADJUSTING_NONE

        var leftTextXCenter = leftX
        var rightTextXCenter = rightX

        //首先判断两边不要超出
        val leftTextLeft = leftTextXCenter - leftTextWidth / 2f //左边文字的左边
        val rightTextRight = rightTextXCenter + rightTextWidth / 2f //右边文字的右边

        /**
         * 判断是否两者太近
         */
        fun isTextToClose(): Boolean {
            return leftTextXCenter + leftTextWidth / 2f + spaceH > rightTextXCenter - rightTextWidth / 2f
        }

        if (leftTextLeft < 0) {
            adjustingType = adjustingType or ADJUSTING_LEFT_OUT
        }

        if (rightTextRight > width) {
            adjustingType = adjustingType or ADJUSTING_RIGHT_OUT
        }

        if (isTextToClose()) {
            adjustingType = adjustingType or ADJUSTING_INNER_TO_CLOSE
        }

        if (adjustingType > 0) {
            //需要校准的，重新调整位置
            if (adjustingType and ADJUSTING_LEFT_OUT == ADJUSTING_LEFT_OUT) {
                //重新调整，将左边文字放到左边界
                leftTextXCenter = leftTextWidth / 2f
                if (isTextToClose()) {
                    //太近则在左边文字后面接着放置右边文字
                    rightTextXCenter = leftTextWidth + spaceH + rightTextWidth / 2f
                }
            }
            if (adjustingType and ADJUSTING_RIGHT_OUT == ADJUSTING_RIGHT_OUT) {
                //重新调整，将右边文字放到右边界
                rightTextXCenter = width - rightTextWidth / 2f
                if (isTextToClose()) {
                    //太近则在右边文字左边接着放置左边文字
                    leftTextXCenter = width - rightTextWidth - spaceH - leftTextWidth / 2f
                }
            }
            if (adjustingType and ADJUSTING_INNER_TO_CLOSE == ADJUSTING_INNER_TO_CLOSE) {
                //太近，当左右都不超出但是太近时，这里相对复杂，因为只考虑将两者连续放置到中间则会发生校准后可能一边超出的情况,
                //如果先校准再重新递归一遍校准调试也不是很友好，所以此处更改一种计算方式，先两者平均需要平移的距离，
                // 然后再比较于各自能偏移的距离，阈值内重新调整两个的偏移量
                val xNeedSpace = leftTextWidth / 2f + rightTextWidth / 2f + spaceH
                val relSpaceX = rightTextXCenter - leftTextXCenter
                //需要重新调整出来的间距
                val adjustingDistance = xNeedSpace - relSpaceX
                //这两个计算的是左右各自能够移动的距离
                val leftFreeDistance = leftTextXCenter - leftTextWidth / 2f
                val rightFreeDistance = width - rightTextWidth / 2f - rightTextXCenter
                if (leftFreeDistance >= adjustingDistance && rightFreeDistance >= adjustingDistance) {
                    //都有富余，各自移动相同距离
                    leftTextXCenter -= adjustingDistance / 2f
                    rightTextXCenter += adjustingDistance / 2f
                } else {
                    //有一方不够了
                    if (leftFreeDistance < adjustingDistance) {
                        //左边不够，左边只移动可以移动的区域
                        leftTextXCenter -= leftFreeDistance
                        rightTextXCenter += adjustingDistance - leftFreeDistance
                    } else {
                        //右边不够了
                        rightTextXCenter += rightFreeDistance
                        leftTextXCenter -= adjustingDistance - rightFreeDistance
                    }
                }
            }
        }

        //绘制
        canvas.drawText(leftText, leftTextXCenter - leftTextWidth / 2f, textBaseLine, textPaint)
        canvas.drawText(rightText, rightTextXCenter - rightTextWidth / 2f, textBaseLine, textPaint)
    }


    companion object {
        private const val ADJUSTING_NONE = 0
        private const val ADJUSTING_LEFT_OUT = 1
        private const val ADJUSTING_RIGHT_OUT = 2
        private const val ADJUSTING_INNER_TO_CLOSE = 4
    }

    /**
     * 绘制滑块
     */
    private fun drawBlock(canvas: Canvas, centerY: Float) {
        canvas.drawBitmap(
            bitmapBlock,
            leftX - blockSize / 2f,
            centerY - blockSize / 2f,
            paint
        )
        canvas.drawBitmap(
            bitmapBlock,
            rightX - blockSize / 2f,
            centerY - blockSize / 2f,
            paint
        )
    }

    private enum class PressState {
        LEFT, RIGHT, NONE
    }

    /**
     * 左边的游标是否在动
     */
    private var pressState = PressState.NONE

    /**
     * 处理拖动事件
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false
        parent.requestDisallowInterceptTouchEvent(true)
        val eventX = event.x
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val distanceToLeftCenter = abs(eventX - leftX)
                val distanceToRightCenter = abs(eventX - rightX)
                val blockPressSize = blockSize + 3.dp //尽量扩大一点大小
                pressState =
                    if (distanceToLeftCenter > blockPressSize && distanceToRightCenter > blockPressSize) {
                        //距离都过大
                        PressState.NONE
                    } else {
                        if (distanceToLeftCenter < distanceToRightCenter) {
                            PressState.LEFT
                        } else {
                            PressState.RIGHT
                        }
                    }
            }
            MotionEvent.ACTION_MOVE -> {
                val safeSpaceBetweenBlock = getSafeSpaceBetweenBlock()
                when (pressState) {
                    PressState.LEFT -> {
                        //最大只能到right的左边
                        val x = eventX.coerceAtMost(rightX - safeSpaceBetweenBlock)
                        Log.d("SlidingBlockScopeView", "move left")
                        setBlockLocation(x.coerceAtLeast(blockSize / 2f), rightX)
                    }
                    PressState.RIGHT -> {
                        //最小只能到left的右边
                        val x = (leftX + safeSpaceBetweenBlock).coerceAtLeast(eventX)
                        Log.d("SlidingBlockScopeView", "move right")
                        setBlockLocation(leftX, (width - blockSize / 2f).coerceAtMost(x))
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                //需要回到最近的一个位置,同时考虑阈值
                if (pressState != PressState.NONE) {
                    when (pressState) {
                        PressState.LEFT -> {
                            val pointExact = getPointExactFloat(leftX)
                            if (pointExact + 1 >= rightPoint) {
                                //（四舍五入）入会到达rightPoint，所以不管任何情况都不能入1
                                setBlockIndex(rightPoint - 1, rightPoint)
                            } else {
                                setBlockIndex(pointExact.roundToInt(), rightPoint)
                            }
                        }
                        PressState.RIGHT -> {
                            val pointExact = getPointExactFloat(rightX)
                            if (pointExact - 1 <= leftPoint) {
                                //（四舍五入）舍会到达leftPoint，所以不管任何情况都不能舍1
                                setBlockIndex(leftPoint, leftPoint + 1)
                            } else {
                                setBlockIndex(leftPoint, pointExact.roundToInt())
                            }
                        }
                    }
                    pressState = PressState.NONE
                }
            }
        }
        return true
    }

    /**
     * 获取两个滑块之间的安全距离
     * 一个进度的间距
     */
    private fun getSafeSpaceBetweenBlock(): Int {
        val slidingWidth = width - blockSize
        return slidingWidth / (pointCount - 1)
    }

    /**
     * 根据当前滑块的x轴坐标换算在进度上的index的folat值
     */
    private fun getPointExactFloat(xLocation: Float): Float {
        return 0f.coerceAtLeast((xLocation - blockSize / 2f) / (width - blockSize) * (pointCount - 1))
            .coerceAtMost(pointCount - 1f)
    }

    /**
     * 根据当前滑块的x轴坐标换算在进度上的index的folat值
     */
    private fun getPointExact(xLocation: Float): Int {
        return getPointExactFloat(xLocation).roundToInt()
    }

    /**
     * 注意，不去考虑padding，无实际意义
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (heightMode == MeasureSpec.AT_MOST) {
            //高度定为文字高度加竖直方向的间距+滑块的高度
            val heightAtMost = textSize + spaceV + blockSize
            super.onMeasure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(heightAtMost, MeasureSpec.EXACTLY)
            )
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    private val Int.dp: Int
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            context.resources.displayMetrics
        ).toInt()

    private val Int.sp: Int
        get() = this.toFloat().sp.toInt()

    private val Float.sp: Float
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            this,
            context.resources.displayMetrics
        )

    private fun parseColor(colorStr: String): Int {
        return Color.parseColor(colorStr)
    }

    private fun drawable(id: Int): Drawable? = ContextCompat.getDrawable(context, id)

}

/**
 * 适配器
 */
interface SlidingBlockScopeAdapter {

    /**
     * 一共有多少个点
     */
    fun getSize(): Int

    /**
     * 某一点被选中时的显示文案
     */
    fun attachShow(position: Int): String

    /**
     * 滑块改变时的回调通知
     */
    fun onScopeChange(leftPoint: Int, rightPoint: Int)
}
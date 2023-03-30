package com.zhangjian.uikit.chart

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import com.zhangjian.uikit.DisplayUtil
import com.zhangjian.uikit.R

/**
 *  Create by zhangjian on 2023/3/3
 */
class PieChart : View {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val typedArray = context!!.obtainStyledAttributes(
            attrs,
            R.styleable.PieChart,
            defStyleAttr,
            0
        )
        divisionRadius = typedArray.getDimensionPixelSize(
            R.styleable.PieChart_divisionRadius,
            DisplayUtil.dip2px(context, 3f)
        )
        ringWidth = typedArray.getDimensionPixelSize(
            R.styleable.PieChart_ringWidth,
            DisplayUtil.dip2px(context, 17f)
        )
        coverRingWidth = typedArray.getDimensionPixelSize(
            R.styleable.PieChart_coverRingWidth,
            DisplayUtil.dip2px(context, 6f)
        )
        cornerCut =
            typedArray.getBoolean(R.styleable.PieChart_cornerCut, true)
        typedArray.recycle()

        setLayerType(LAYER_TYPE_SOFTWARE, null) //PorterDuff.Mode.CLEAR需要关闭硬件加速
    }

    private var divisionRadius: Int = 0
    private var ringWidth: Int = 0
    private var coverRingWidth: Int = 0

    //是否绘制小切角
    private var cornerCut: Boolean = true

    private var dataList: List<DataBean>? = null

    /**
     * @param dataBeans 数据集合
     * @param sort 是否启用从大到小排序
     */
    fun setData(dataBeans: List<DataBean>?, sort: Boolean = false) {
        dataBeans?.let {
            var totalCount = 0f
            it.forEach { data ->
                totalCount += data.values
            }

            it.forEach { data ->
                data.percent = data.values / totalCount
            }
            dataList = if (sort) {
                //可以设置从大到小排序
                it.sortedByDescending { bean -> bean.percent }
            } else {
                it
            }
            invalidate()
        }
    }

    private val paint by lazy {
        Paint()
    }

    private val path by lazy {
        Path()
    }

    private val outRectF by lazy {
        RectF()
    }

    private val innerRectF by lazy {
        RectF()
    }

    private fun drawArc(
        canvas: Canvas,
        path: Path,
        rect: RectF,
        arcWidth: Float,
        startAngle: Float,
        sweepAngle: Float,
        color: Int
    ) {
        path.reset()
        path.addArc(
            rect.left + arcWidth / 2f,
            rect.top + arcWidth / 2f,
            rect.right - arcWidth / 2f,
            rect.bottom - arcWidth / 2f,
            startAngle,
            sweepAngle
        )
        //外圈
        paint.color = color
        paint.strokeWidth = arcWidth
        canvas.drawPath(path, paint)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width == 0 || height == 0 || dataList.isNullOrEmpty()) return
        outRectF.set(0f, 0f, width.toFloat(), height.toFloat())
        val ringDistance = ringWidth - coverRingWidth
        innerRectF.set(
            outRectF.left + ringDistance,
            outRectF.top + ringDistance,
            outRectF.right - ringDistance,
            outRectF.bottom - ringDistance
        )
        val anglePointList = mutableListOf<Float>()
        paint.reset()
        paint.flags = Paint.ANTI_ALIAS_FLAG
        paint.style = Paint.Style.STROKE
        var angler = -90f
        dataList!!.forEach {
            val startAngle = angler
            val sweepAngle = it.percent * 360f
            angler += sweepAngle

            anglePointList.add(startAngle)

            drawArc(canvas, path, outRectF, ringWidth.toFloat(), startAngle, sweepAngle, it.color)
            it.colorInner?.let { colorInner ->
                drawArc(
                    canvas,
                    path,
                    innerRectF,
                    coverRingWidth.toFloat(),
                    startAngle,
                    sweepAngle,
                    colorInner
                )
            }

        }

        if (anglePointList.size <= 1 || !cornerCut) return
        //画小切角
        anglePointList.forEach {
            val saveCount = canvas.save()
            //旋转之后都在顶部中间处理
            canvas.rotate(it + 90, width / 2f, height / 2f)
            paint.style = Paint.Style.FILL
            paint.color = Color.TRANSPARENT
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            //外圈豁口
            path.reset()
            path.moveTo(width / 2f - divisionRadius, 0f)
            path.arcTo(
                width / 2f - 2f * divisionRadius,
                0f,
                width / 2f,
                2f * divisionRadius,
                -90f,
                90f,
                true
            )
            path.arcTo(
                width / 2f,
                0f,
                width / 2f + 2f * divisionRadius,
                2f * divisionRadius,
                180f,
                90f,
                true
            )
            path.lineTo(width / 2f - divisionRadius, 0f)
            path.close()
            canvas.drawPath(path, paint)
            //内圈豁口
            val pointContactBottom = ringWidth.toFloat() + 1  //向下便宜一点，因为左右会稍微低一点
            path.reset()
            path.moveTo(width / 2f - divisionRadius, pointContactBottom)
            path.arcTo(
                width / 2f - 2f * divisionRadius,
                pointContactBottom - 2f * divisionRadius,
                width / 2f,
                pointContactBottom,  //避免除以2时的精度影响
                90f,
                -90f,
                true
            )
            path.arcTo(
                width / 2f,
                pointContactBottom - 2f * divisionRadius,
                width / 2f + 2f * divisionRadius,
                pointContactBottom,
                180f,
                -90f,
                true
            )
            path.lineTo(width / 2f - divisionRadius, pointContactBottom)
            path.close()
            canvas.drawPath(path, paint)
            canvas.restoreToCount(saveCount)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(widthMeasureSpec)
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            super.onMeasure(
                MeasureSpec.makeMeasureSpec(
                    DisplayUtil.dip2px(context, 84f),
                    MeasureSpec.EXACTLY
                ),
                MeasureSpec.makeMeasureSpec(
                    DisplayUtil.dip2px(context, 84f),
                    MeasureSpec.EXACTLY
                )
            )
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

}

class DataBean(val values: Float, @ColorInt val color: Int, @ColorInt val colorInner: Int? = null) {
    internal var percent: Float = 0f
}


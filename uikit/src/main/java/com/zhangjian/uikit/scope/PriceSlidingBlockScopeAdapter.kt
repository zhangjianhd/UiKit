package com.zhangjian.uikit.scope

import kotlin.math.ceil
import kotlin.math.floor

/**
 *  Create by zhangjian on 2022/11/3
 */
class PriceSlidingBlockScopeAdapter(
    var minValue: Int,
    var step: Int,
    var count: Int,
    var uncapped: Boolean = true,  //上不封顶,会在count基础上主动增加一个
    var onScopeChange: (startPrice: Int, endPrice: Int?) -> Unit
) : SlidingBlockScopeAdapter {

    override fun getSize(): Int {
        return count + if (uncapped) {
            1
        } else {
            0
        }
    }

    override fun attachShow(position: Int): String {
        return if (uncapped && position == getSize() - 1) {
            val value = minValue + step * (position - 1)
            "¥${value}+"
        } else {
            val value = minValue + step * position
            "¥${value}"
        }
    }

    override fun onScopeChange(leftPoint: Int, rightPoint: Int) {
        val endPrice = if (uncapped && rightPoint == getSize() - 1) {
            null
        } else {
            minValue + step * rightPoint
        }
        onScopeChange.invoke(minValue + step * leftPoint, endPrice)
    }

    /**
     * 返回价格范围需要选中的位置
     */
    fun getIndex(startPrice: Int?, endPrice: Int?): PointIndex {
        val size = getSize()
        val startIndex = if (startPrice == null || startPrice <= minValue) {
            0
        } else {
            //往小了取
            0.coerceAtLeast(
                (floor((startPrice - minValue) / step.toDouble()).toInt()).coerceAtMost(
                    size - 1
                )
            )
        }
        val endIndex = if (endPrice == null) {
            size - 1
        } else {
            //往大了取
            1.coerceAtLeast(
                (ceil((endPrice - minValue) / step.toDouble()).toInt()).coerceAtMost(size - 1)
            )
        }
        return if (endIndex > startIndex) {
            PointIndex(startIndex, endIndex)
        } else {
            PointIndex(0, size - 1)
        }
    }
}

class PointIndex(val startIndex: Int, val endIndex: Int)
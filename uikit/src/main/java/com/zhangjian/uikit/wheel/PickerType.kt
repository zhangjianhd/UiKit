package com.zhangjian.uikit.wheel

/**
 *  Create by zhangjian on 2023/9/23
 */
object PickerType {
    const val YEAR = 1
    const val MONTH = 1 shl 1
    const val DAY = 1 shl 2
    const val HOUR = 1 shl 4
    const val MINUTE = 1 shl 5
    const val SECOND = 1 shl 6

    const val YEAR_MONTH_DAY = YEAR or MONTH or DAY
    const val YEAR_MONTH_DAY_HOUR_MINUTE = YEAR or MONTH or DAY or HOUR or MINUTE
    const val YEAR_MONTH = YEAR or MONTH
    const val HOUR_MINUTE_SECOND = HOUR or MINUTE or SECOND
    const val HOUR_MINUTE = HOUR or MINUTE
}
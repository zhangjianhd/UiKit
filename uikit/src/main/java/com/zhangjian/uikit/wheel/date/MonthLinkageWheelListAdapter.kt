package com.zhangjian.uikit.wheel.date

import com.zhangjian.uikit.wheel.IMultiPickerBean
import com.zhangjian.uikit.wheel.adapter.BaseLinkedWheelListAdapter
import java.util.*

/**
 *  Create by zhangjian on 2022/8/24
 */
class MonthLinkedWheelListAdapter(
    private val defCalendar: Calendar,
    private var maxCalendar: Calendar,
    private var minCalendar: Calendar
) :
    BaseLinkedWheelListAdapter<YearPickerBean, MonthPickerBean>() {

    private val calendar = Calendar.getInstance()

    private val curYear = calendar.get(Calendar.YEAR)

    override fun getList(upLevelSelect: YearPickerBean?): List<MonthPickerBean> {
        val selectYear = upLevelSelect?.year ?: curYear
        var maxMonth = 11
        var minMonth = 0

        minCalendar.let {
            if (selectYear == it.get(Calendar.YEAR)) {
                //最小年份则只能选择当前月份以及之后
                minMonth = it.get(Calendar.MONTH)
            } else if (selectYear < it.get(Calendar.YEAR)) {
                //历史年份则直接没有月份
                return mutableListOf()
            }
        }
        maxCalendar.let {
            if (selectYear == it.get(Calendar.YEAR)) {
                //今年则只能选择当前月份以及之前
                maxMonth = it.get(Calendar.MONTH)
            } else if (selectYear > it.get(Calendar.YEAR)) {
                //历史年份则直接没有月份
                return mutableListOf()
            }
        }
        return mutableListOf<MonthPickerBean>().apply {
            for (i in minMonth..maxMonth) {
                add(MonthPickerBean(i).apply {
                    year = selectYear
                })
            }
        }
    }

    override fun defUpLevel(): YearPickerBean {
        return YearPickerBean(defCalendar.get(Calendar.YEAR))
    }

    override fun defIndex(curList: List<MonthPickerBean>): Int {
        return curList.indexOfFirst { it.month == defCalendar.get(Calendar.MONTH) } ?: 0
    }
}

/**
 * month从0开始
 */
class MonthPickerBean(var month: Int) : IMultiPickerBean {
    var year: Int = 0
    override fun getText(): String {
        return "${month + 1}月"
    }
}


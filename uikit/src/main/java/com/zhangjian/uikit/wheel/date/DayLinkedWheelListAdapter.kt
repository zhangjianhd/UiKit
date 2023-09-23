package com.zhangjian.uikit.wheel.date

import com.zhangjian.uikit.wheel.IMultiPickerBean
import com.zhangjian.uikit.wheel.adapter.BaseLinkedWheelListAdapter
import java.util.*

/**
 *  Create by zhangjian on 2022/8/24
 */
class DayLinkedWheelListAdapter(
    private val defCalendar: Calendar,
    private var maxCalendar: Calendar,
    private var minCalendar: Calendar
) :
    BaseLinkedWheelListAdapter<MonthPickerBean, DayPickerBean>() {

    private val calendar = Calendar.getInstance()

    override fun getList(upLevelSelect: MonthPickerBean?): List<DayPickerBean> {
        if (upLevelSelect == null) return mutableListOf()
        val selectYear = upLevelSelect.year
        val selectMonth = upLevelSelect.month

        calendar[Calendar.YEAR] = selectYear
        calendar[Calendar.MONTH] = selectMonth
        calendar[Calendar.DAY_OF_MONTH] = 1
        var dayMax = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        var dayMin = 1

        maxCalendar.let {
            //历史的不能选择
            if (selectYear == it.get(Calendar.YEAR) && selectMonth == it.get(Calendar.MONTH)) {
                //当前这个月
                dayMax = it.get(Calendar.DAY_OF_MONTH)
            }
        }

        minCalendar.let {
            //历史的不能选择
            if (selectYear == it.get(Calendar.YEAR) && selectMonth == it.get(Calendar.MONTH)) {
                //当前这个月
                dayMin = it.get(Calendar.DAY_OF_MONTH)
            }
        }
        return mutableListOf<DayPickerBean>().apply {
            for (i in dayMin..dayMax) {
                add(DayPickerBean(i).apply {
                    year = selectYear
                    month = selectMonth
                })
            }
        }
    }

    override fun defUpLevel(): MonthPickerBean {
        return MonthPickerBean(defCalendar.get(Calendar.MONTH)).apply {
            year = defCalendar.get(Calendar.YEAR)
        }
    }

    override fun defIndex(curList: List<DayPickerBean>): Int {
        return curList.indexOfFirst { it.day == defCalendar.get(Calendar.DATE) } ?: 0
    }
}

/**
 * month从0开始
 */
class DayPickerBean(var day: Int) : IMultiPickerBean {

    var year: Int = 0
    var month: Int = 0

    override fun getText(): String {
        return "${day}日"
    }
}


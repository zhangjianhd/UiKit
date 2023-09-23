package com.zhangjian.uikit.wheel.date

import com.zhangjian.uikit.wheel.IMultiPickerBean
import com.zhangjian.uikit.wheel.adapter.TopLevelBaseLinkedWheelListAdapter
import java.util.*

/**
 *  Create by zhangjian on 2022/8/24
 */
class YearLinkedWheelListAdapter(
    private val defCalendar: Calendar,
    private var maxCalendar: Calendar,
    private var minCalendar: Calendar
) :
    TopLevelBaseLinkedWheelListAdapter<YearPickerBean>() {


    override fun getList(): List<YearPickerBean> {
        return mutableListOf<YearPickerBean>().apply {

            val minYear = minCalendar.get(Calendar.YEAR)

            val maxYear = maxCalendar.get(Calendar.YEAR)

            for (i in minYear until maxYear + 1) {
                add(YearPickerBean(i))
            }

        }
    }

    override fun defUpLevel(): IMultiPickerBean? {
        return null
    }

    override fun defIndex(curList: List<YearPickerBean>): Int {
        return curList.indexOfFirst { it.year == defCalendar.get(Calendar.YEAR) }
    }
}

class YearLinkedWheelListAdapterScope(
    private val defCalendar: Calendar,
    private var maxCalendar: Calendar,
    private var minCalendar: Calendar
) :
    TopLevelBaseLinkedWheelListAdapter<YearPickerBean>() {
    override fun getList(): List<YearPickerBean> {
        return mutableListOf<YearPickerBean>().apply {

            val minYear: Int = minCalendar.get(Calendar.YEAR)

            val maxYear: Int = maxCalendar.get(Calendar.YEAR)

            for (i in minYear..maxYear) {
                add(YearPickerBean(i))
            }
        }
    }

    override fun defUpLevel(): IMultiPickerBean? {
        return null
    }

    override fun defIndex(curList: List<YearPickerBean>): Int {
        return curList.indexOfFirst { it.year == defCalendar.get(Calendar.YEAR) }
    }
}

class YearPickerBean(var year: Int) : IMultiPickerBean {
    override fun getText(): String {
        return "${year}年"
    }
}


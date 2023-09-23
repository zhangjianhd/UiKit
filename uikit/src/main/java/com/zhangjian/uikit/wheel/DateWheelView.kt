package com.zhangjian.uikit.wheel

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.zhangjian.uikit.R
import com.zhangjian.uikit.wheel.adapter.BaseWheelListAdapter
import com.zhangjian.uikit.wheel.date.*
import java.util.Calendar

/**
 *  Create by zhangjian on 2023/8/10
 */
class DateWheelView : ConstraintLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_data_wheel, this, true)

    }

    private val llWheelView: LinearLayout = findViewById(R.id.llWheelView)

    private lateinit var yearAdapter: YearLinkedWheelListAdapterScope
    private lateinit var monthAdapter: MonthLinkedWheelListAdapter
    private lateinit var dayAdapter: DayLinkedWheelListAdapter


    fun init(
        defCalendar: Calendar = Calendar.getInstance(),
        maxCalendar: Calendar,
        minCalendar: Calendar
    ) {
        yearAdapter = YearLinkedWheelListAdapterScope(
            defCalendar,
            maxCalendar,
            minCalendar
        )
        monthAdapter = MonthLinkedWheelListAdapter(defCalendar, maxCalendar, minCalendar)
        yearAdapter.bindNextLevelAdapter(monthAdapter)
        dayAdapter = DayLinkedWheelListAdapter(defCalendar, maxCalendar, minCalendar)
        monthAdapter.bindNextLevelAdapter(dayAdapter)

        llWheelView.removeAllViews()
        val config = NormalItemItemCreate(16f, 16f)
        addWheelView(yearAdapter, config)
        monthWheelView = addWheelView(monthAdapter, config)
        dayWheelView = addWheelView(dayAdapter, config)
        switch(pickerType)
    }

    private lateinit var dayWheelView: WheelView
    private lateinit var monthWheelView: WheelView

    private fun addWheelView(
        adapter: BaseWheelListAdapter<*>,
        decoration: NormalItemItemCreate
    ): WheelView {
        val wheelView = WheelView(context)
        wheelView.layoutParams =
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                weight = 1f
            }
        wheelView.itemCreator = decoration
        wheelView.setAdapter(adapter)
        llWheelView.addView(wheelView)
        return wheelView
    }

    private var pickerType: Int = PickerType.YEAR_MONTH_DAY

    fun switch(pickerType: Int) {
        this.pickerType = pickerType
        dayWheelView.visibility = if (pickerType and PickerType.DAY == PickerType.DAY) {
            View.VISIBLE
        } else {
            View.GONE
        }
        monthWheelView.visibility = if (pickerType and PickerType.MONTH == PickerType.MONTH) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun getSelectResult(): DateSelectResult {
        val calendar = Calendar.getInstance()
        yearAdapter.getSelectItem()?.year?.let {
            calendar[Calendar.YEAR] = it
        }
        monthAdapter.getSelectItem()?.month?.let {
            calendar[Calendar.MONTH] = it
        }
        dayAdapter.getSelectItem()?.day?.let {
            calendar[Calendar.DATE] = it
        }
        return DateSelectResult(pickerType, calendar)
    }

}

class DateSelectResult(val pickerType: Int, val calendar: Calendar)
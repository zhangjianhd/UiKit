package com.zhangjian.samp.pick

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.zhangjian.samp.R
import com.zhangjian.uikit.wheel.PickerType
import com.zhangjian.uikit.wheel.impl.DateWheelView
import com.zhangjian.uikit.wheel.impl.MultiPickerView
import com.zhangjian.uikit.wheel.multilevel.MultilevelBean
import com.zhangjian.uikit.wheel.multilevel.looperSelectHeadNode
import java.util.Calendar

/**
 *  Create by zhangjian on 2023/3/30
 */
class PickActivity : FragmentActivity() {

    private val datePick by lazy {
        findViewById<DateWheelView>(R.id.datePick)
    }

    private val multiPick by lazy {
        findViewById<MultiPickerView>(R.id.multiPick)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick)

        val minCalendar = Calendar.getInstance()
        minCalendar.add(Calendar.YEAR, -10)

        val maxCalendar = Calendar.getInstance()
        maxCalendar.add(Calendar.YEAR, 10)
        datePick.init(
            defCalendar = Calendar.getInstance(),
            maxCalendar = maxCalendar,
            minCalendar = minCalendar
        )

        findViewById<Button>(R.id.btnMonth).setOnClickListener {
            datePick.switch(PickerType.YEAR_MONTH)
        }

        findViewById<Button>(R.id.btnDay).setOnClickListener {
            datePick.switch(PickerType.YEAR_MONTH_DAY)
        }

        findViewById<Button>(R.id.btnTimeGet).setOnClickListener {
            val result = datePick.getSelectResult()
            val calendar = result.calendar
            Toast.makeText(
                this, "当前选择：${
                    if (result.pickerType == PickerType.YEAR_MONTH) {
                        "${calendar.get(Calendar.YEAR)}年${calendar.get(Calendar.MONTH) + 1}月"
                    } else {
                        "${calendar.get(Calendar.YEAR)}年${calendar.get(Calendar.MONTH) + 1}月${
                            calendar.get(
                                Calendar.DATE
                            )
                        }日"
                    }
                }", Toast.LENGTH_SHORT
            ).show()
        }

        val getResult = multiPick.setMultilevelData(getAreaData(), 3, null)

        findViewById<View>(R.id.btnGetMulti).setOnClickListener {
            val result = getResult.invoke()
            val select = result?.looperSelectHeadNode()
            var readNode = select
            val areaList = mutableListOf<MultilevelBean>()
            readNode?.let {
                areaList.add(it)
            }
            while (readNode?.selectNext != null) {
                areaList.add(readNode.selectNext!!)
                readNode = readNode.selectNext
            }
            val selectText = areaList.map { it.name }.joinToString("/")

            Toast.makeText(
                this, "当前选择：${selectText}", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getAreaData(): MutableList<MultilevelBean> {
        return mutableListOf<MultilevelBean>().apply {
            for (i in 0..7) {
                val province = MultilevelBean()
                province.apply {
                    name = "省份${i}"
                    childList = mutableListOf<MultilevelBean>().apply {
                        for (j in 0..3) {
                            val city = MultilevelBean()
                            city.apply {
                                name = "城市${j}"
                                parent = province
                                childList = mutableListOf<MultilevelBean>().apply {
                                    add(MultilevelBean().apply {
                                        parent = city
                                        name = "区县一${i}-${j}"
                                    })
                                    add(MultilevelBean().apply {
                                        parent = city
                                        name = "区县二${i}-${j}"
                                    })
                                    add(MultilevelBean().apply {
                                        parent = city
                                        name = "区县三${i}-${j}"
                                    })
                                    add(MultilevelBean().apply {
                                        parent = city
                                        name = "区县四${i}-${j}"
                                    })
                                    add(MultilevelBean().apply {
                                        parent = city
                                        name = "区县五${i}-${j}"
                                    })
                                }
                            }
                            add(city)
                        }
                    }
                }
                add(province)
            }
        }
    }
}
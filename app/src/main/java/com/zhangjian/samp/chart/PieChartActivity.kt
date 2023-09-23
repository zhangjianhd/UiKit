package com.zhangjian.samp.chart

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.zhangjian.samp.R
import com.zhangjian.uikit.chart.DataBean
import com.zhangjian.uikit.chart.PieChart

/**
 *  Create by zhangjian on 2023/3/30
 */
class PieChartActivity : FragmentActivity() {

    private val pieChatCornerCutSort by lazy {
        findViewById<PieChart>(R.id.pieChatCornerCutSort)
    }

    private val pieChatCornerCut by lazy {
        findViewById<PieChart>(R.id.pieChatCornerCut)
    }

    private val pieChatNormal by lazy {
        findViewById<PieChart>(R.id.pieChatNormal)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pie_chart)

        val dataBeans = mutableListOf<DataBean>().apply {
            add(DataBean(70f, Color.parseColor("#FFC86B"), Color.parseColor("#FFBD56")))
            add(DataBean(100f, Color.parseColor("#71A7FF"), Color.parseColor("#5A95FF")))
            add(DataBean(35f, Color.parseColor("#FF6B83"), Color.parseColor("#FF566A")))
            add(DataBean(60f, Color.parseColor("#D765FF"), Color.parseColor("#CF51FF")))
            add(DataBean(45f, Color.parseColor("#5EE0E4"), Color.parseColor("#4BDADF")))
            add(DataBean(85f, Color.parseColor("#FF9141"), Color.parseColor("#FF7B34")))
        }

        pieChatCornerCutSort.setData(dataBeans, true)
        pieChatCornerCut.setData(dataBeans)
        pieChatNormal.setData(dataBeans)
    }
}
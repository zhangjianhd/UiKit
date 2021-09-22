package com.zhangjian.uikit.tab

import com.google.android.material.tabs.TabLayout

/**
 * Created zhangjian on 2021/7/29(10:34).
 */
class TabLayoutDoubleClick(private val doubleClick: (Int) -> Unit) : TabLayout.OnTabSelectedListener {

    private var lastClickTime: Long = 0
    private var lastClickIndex: Int = -1

    override fun onTabSelected(tab: TabLayout.Tab?) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        tab?.let {
            val curIndex = tab.position
            val curTime = System.currentTimeMillis()
            if (curIndex == lastClickIndex && (curTime - lastClickTime) < 1000) {
                doubleClick.invoke(curIndex)
                lastClickIndex = -1
                lastClickTime = 0
            } else {
                lastClickTime = curTime
                lastClickIndex = curIndex
            }
        }
    }
}
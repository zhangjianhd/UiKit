package com.zhangjian.uikit.wheel.multilevel

import com.zhangjian.uikit.wheel.IMultiPickerBean
import com.zhangjian.uikit.wheel.adapter.BaseLinkedWheelListAdapter
import com.zhangjian.uikit.wheel.adapter.TopLevelBaseLinkedWheelListAdapter

/**
 *  Create by zhangjian on 2022/10/12
 */
open class MultilevelLinkedWheelListAdapter(private val defUpLevel: MultilevelBean?) :
    BaseLinkedWheelListAdapter<MultilevelBean, MultilevelBean>() {
    override fun getList(upLevelSelect: MultilevelBean?): List<MultilevelBean> {
        upLevelSelect?.let {
            return it.childList ?: mutableListOf()
        }
        return mutableListOf()
    }

    override fun defUpLevel(): MultilevelBean? {
        return defUpLevel
    }

    override fun defIndex(curList: List<MultilevelBean>): Int {
        defUpLevel?.let {
            val select = it.selectNext
            return curList.indexOf(select)
        }
        return 0
    }
}

class MultilevelHeadLinkedWheelListAdapter(
    val data: List<MultilevelBean>,
    private val defLevel: MultilevelBean? = null
) :
    TopLevelBaseLinkedWheelListAdapter<MultilevelBean>() {

    override fun defUpLevel(): IMultiPickerBean? {
        return null
    }

    override fun getList(): List<MultilevelBean> {
        return data
    }

    override fun defIndex(curList: List<MultilevelBean>): Int {
        defLevel?.let {
            return curList.indexOf(it)
        }
        return 0
    }
}

open class MultilevelBean : IMultiPickerBean {

    var parent: MultilevelBean? = null  //前一级
    var childList: List<MultilevelBean>? = null  //后一级

    var selectNext: MultilevelBean? = null //外部不关心，用于反查选中项目

    var id: String? = null
    var name: String? = null

    override fun getText(): String {
        return name ?: ""
    }
}
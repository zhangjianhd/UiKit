package com.zhangjian.uikit.wheel.adapter

import com.zhangjian.uikit.wheel.IMultiPickerBean

/**
 *  Create by zhangjian on 2022/8/25
 */
abstract class BaseWheelListAdapter<T : IMultiPickerBean> {

    var dataChangeListener: DataChangeListener? = null

    var curList: List<T>? = null
        protected set

    private var curPos: Int = 0

    fun refresh(): Int {
        curList = initList()
        if (curList.isNullOrEmpty()){
            return 0
        }
        return defIndex(curList!!)
    }

    open fun selectItem(pos: Int) {
        curPos = pos
    }

    /**
     * 初始化数据
     */
    abstract fun initList(): List<T>

    /**
     * 初始化数据之后返回默认的位置
     */
    abstract fun defIndex(curList: List<T>): Int

    fun getSize() = curList?.size ?: 0

    fun getSelectPos() = curPos

    fun getSelectItem() = if (curList.isNullOrEmpty()) null else curList?.get(curPos)
}
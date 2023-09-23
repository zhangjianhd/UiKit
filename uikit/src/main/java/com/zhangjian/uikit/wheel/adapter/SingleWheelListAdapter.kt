package com.zhangjian.uikit.wheel.adapter

import com.zhangjian.uikit.wheel.IMultiPickerBean

/**
 *  Create by zhangjian on 2022/8/25
 *  普通的单个滚轮选择，传入数据即可
 */
class SingleWheelListAdapter<T : IMultiPickerBean>(val data : List<T>, private val defIndex : Int) : BaseWheelListAdapter<T>() {

    override fun initList(): List<T> {
        return data
    }

    override fun defIndex(curList: List<T>) = defIndex
}
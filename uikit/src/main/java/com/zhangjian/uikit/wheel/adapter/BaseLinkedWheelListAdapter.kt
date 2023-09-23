package com.zhangjian.uikit.wheel.adapter

import com.zhangjian.uikit.wheel.IMultiPickerBean


/**
 *  Create by zhangjian on 2022/8/24
 *  带有联动效果的BaseWheelListAdapter实现基类，多个之间构成链表，维护上一级[upLevelAdapter]和下一级[nextLevelAdapter]
 *  绑定下一级的adapter [bindNextLevelAdapter]，同时，给下一级赋值上一级为自己
 */
abstract class BaseLinkedWheelListAdapter<L : IMultiPickerBean, T : IMultiPickerBean> :
    BaseWheelListAdapter<T>() {

    override fun initList(): List<T> {
        return getList(defUpLevel())
    }

    var upLevelAdapter: BaseLinkedWheelListAdapter<*, L>? = null //上一级的滚轮
        private set

    var nextLevelAdapter: BaseLinkedWheelListAdapter<T, *>? = null //下一级的滚轮
        private set

    /**
     * 绑定下一级
     */
    fun bindNextLevelAdapter(adapter: BaseLinkedWheelListAdapter<T, *>?) {
        nextLevelAdapter = adapter
        adapter?.upLevelAdapter = this
    }

    private var upLevelCurSelect: L? = null

    var onWheelDataChange: (() -> Unit)? = null

    override fun selectItem(pos: Int) {
        super.selectItem(pos)
        /**
         * 通知下一个改变
         */
        onWheelDataChange?.invoke()
        nextLevelAdapter?.perChangeList(getSelectItem())?.let {
            if (it) {
                nextLevelAdapter?.dataChangeListener?.onDataChange()
            }
        }
    }

    /**
     * 上一级发生改变
     */
    fun perChangeList(upLevelSelect: L? = null): Boolean {
        if (upLevelCurSelect == upLevelSelect) {
            return false
        }
        upLevelCurSelect = upLevelSelect
        return if (shouldDataChange(upLevelSelect)) {
            val temp = getList(upLevelSelect)
            val change = temp != curList //返回数据相同则没发生改变
            curList = temp
            change
        } else {
            false
        }
    }

    /**
     * 根据上一个获取到当前级别的数据
     */
    protected abstract fun getList(upLevelSelect: L? = null): List<T>

    /**
     * 当上一级发生变化的时候，子类可以自己决定是否需要改变数据
     * @return 返回值表示是否需要更新数据，默认都需要，则会触发[getList]获取数据
     * 使用场景、存在多级但是上级选择跟当前级数据没有相关性
     */
    protected open fun shouldDataChange(upLevelSelect: L? = null): Boolean {
        return true
    }

    fun getDeep(): Int {
        return recursionDeep(nextLevelAdapter, 1)
    }

    /**
     * 递归遍历深度
     * @return 是否有下一个
     */
    private fun recursionDeep(adapter: BaseLinkedWheelListAdapter<*, *>?, deep: Int): Int {
        adapter?.nextLevelAdapter?.let {
            return recursionDeep(it, deep + 1)
        }
        return deep
    }

    fun getLastAdapter(): BaseLinkedWheelListAdapter<*, *> {
        return recursionLast(this)
    }

    private fun recursionLast(adapter: BaseLinkedWheelListAdapter<*, *>): BaseLinkedWheelListAdapter<*, *> {
        adapter.nextLevelAdapter?.let {
            return recursionLast(it)
        }
        return adapter
    }

    abstract fun defUpLevel(): L?

}

/**
 * 顶级的滚轮选项，没有前一项，会直接调用[perChangeList]获取数据
 */
abstract class TopLevelBaseLinkedWheelListAdapter<T : IMultiPickerBean> :
    BaseLinkedWheelListAdapter<IMultiPickerBean, T>() {
    final override fun getList(upLevelSelect: IMultiPickerBean?): List<T> {
        return getList()
    }

    abstract fun getList(): List<T>
}

interface DataChangeListener {
    fun onDataChange()
}
package com.zhangjian.uikit.wheel.impl

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.zhangjian.uikit.R
import com.zhangjian.uikit.wheel.NormalItemItemCreate
import com.zhangjian.uikit.wheel.WheelView
import com.zhangjian.uikit.wheel.adapter.BaseLinkedWheelListAdapter
import com.zhangjian.uikit.wheel.adapter.BaseWheelListAdapter
import com.zhangjian.uikit.wheel.multilevel.MultilevelBean
import com.zhangjian.uikit.wheel.multilevel.MultilevelHeadLinkedWheelListAdapter
import com.zhangjian.uikit.wheel.multilevel.MultilevelLinkedWheelListAdapter
import com.zhangjian.uikit.wheel.multilevel.looperSelectHeadNode

/**
 *  Create by zhangjian on 2022/8/24
 */
class MultiPickerView : ConstraintLayout {

    private lateinit var llWheelView: LinearLayout

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.layout_multi_picker, this, true)
        llWheelView = findViewById(R.id.llWheelView)
    }

    /**
     * 清除adapter
     */
    fun clearAdapter() {
        llWheelView.removeAllViews()
    }

    /**
     * 设置adapter
     * 可以单独设置普通的[BaseWheelListAdapter]，也可以是链表类型的[BaseLinkedWheelListAdapter]
     */
    fun setAdapter(adapter: BaseWheelListAdapter<*>) {

        clearAdapter()

        //此处设置的adapter是头，可通过其next获取到后面的节点依次注入，WheelListAdapter构成一个链表结构
        if (adapter is BaseLinkedWheelListAdapter<*, *>) {
            val many = adapter.getDeep() >= 5
            addWheelView(
                adapter, if (many) {
                    WheelViewManyStyleItemDecoration()
                } else {
                    WheelViewFewStyleItemDecoration()
                }
            )
        } else {
            addWheelView(adapter)
        }
    }

    /**
     * 同时设置多个非[BaseLinkedWheelListAdapter]类型的，多个滚轮但是没有相关性
     */
    fun setAdapters(adapterList: List<BaseWheelListAdapter<*>>) {

        clearAdapter()

        val many = adapterList.size >= 5
        val decoration = if (many) {
            WheelViewManyStyleItemDecoration()
        } else {
            WheelViewFewStyleItemDecoration()
        }
        adapterList.forEach {
            addWheelView(
                it, decoration
            )
        }
    }

    /**
     * 添加BaseWheelListAdapter，
     * 对于BaseLinkageWheelListAdapter类型会递归添加WheelView
     */
    fun addWheelView(
        adapter: BaseWheelListAdapter<*>,
        decoration: WheelViewItemDecoration = WheelViewFewStyleItemDecoration()
    ) {
        val wheelView = WheelView(context)
        wheelView.layoutParams =
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                weight = 1f
            }
        wheelView.itemCreator =
            NormalItemItemCreate(
                textSizeNormal = decoration.normalTextSize,
                textSizeSelect = decoration.selectTextSize
            )
        wheelView.setAdapter(adapter)
        llWheelView.addView(wheelView)

        if (adapter is BaseLinkedWheelListAdapter<*, *>) {
            adapter.nextLevelAdapter?.let {
                addWheelView(it, decoration)
            }
        }
    }

    fun setMultilevelData(
        data: List<MultilevelBean>,
        levelCount: Int,
        defLastLevel: MultilevelBean? = null
    ): () -> MultilevelBean? {
        //向上遍历
        val head = defLastLevel?.looperSelectHeadNode() ?: data.firstOrNull()

        val adapter = MultilevelHeadLinkedWheelListAdapter(data, head)

        var lastNode = head
        var lastAdapter: BaseLinkedWheelListAdapter<*, MultilevelBean> = adapter

        var i = levelCount - 1
        while (i > 0) {
            val nextNode = lastNode?.selectNext ?: lastNode?.childList?.firstOrNull()
            val nextAdapter = MultilevelLinkedWheelListAdapter(lastNode)
            lastAdapter.bindNextLevelAdapter(nextAdapter)
            lastNode = nextNode
            lastAdapter = nextAdapter
            i--
        }

        setAdapter(adapter)
        return {
            lastAdapter.getSelectItem()
        }
    }

}

open class WheelViewItemDecoration(val selectTextSize: Float, val normalTextSize: Float)
class WheelViewManyStyleItemDecoration : WheelViewItemDecoration(16f, 14f)
class WheelViewFewStyleItemDecoration : WheelViewItemDecoration(18f, 16f)

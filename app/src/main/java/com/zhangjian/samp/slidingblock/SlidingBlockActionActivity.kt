package com.zhangjian.samp.slidingblock

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zhangjian.samp.ButtonRouter
import com.zhangjian.samp.MenuListAdapter
import com.zhangjian.samp.R
import com.zhangjian.uikit.tab.SlidingBlockTabView

/**
 * Created zhangjian on 2021/9/22(10:31) in project UiKit.
 */
class SlidingBlockActionActivity : FragmentActivity() {

    var curRecycleViewIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sliding_block_action)

        val recycleView: RecyclerView = findViewById(R.id.rvContent)

        val tabView: SlidingBlockTabView = findViewById(R.id.slidingBlock)
        val titles = mutableListOf<String>()
        titles.apply {
            add("Line1")
            add("Line2")
            add("Line3")
            add("Line4")
            add("OTHER")
        }

        val linearLayoutManager = LinearLayoutManager(this@SlidingBlockActionActivity)
        recycleView.apply {
            val buttonRouters: MutableList<ButtonRouter> = mutableListOf()
            buttonRouters.apply {
                add(ButtonRouter("Line1"))
                add(ButtonRouter("Line2"))
                add(ButtonRouter("Line3"))
                add(ButtonRouter("Line4"))
                for (i in 0..20) {
                    add(ButtonRouter("Other"))
                }
            }
            layoutManager = linearLayoutManager
            adapter = MenuListAdapter(buttonRouters)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val firstVisibleIndex =
                        linearLayoutManager.findFirstVisibleItemPosition().coerceAtMost(titles.size - 1)
                    if (firstVisibleIndex != curRecycleViewIndex) {
                        tabView.activeScrollToIndex(firstVisibleIndex)
                        curRecycleViewIndex = firstVisibleIndex
                    }
                }
            })
        }

        tabView.bindTabTitles(titles) {
            linearLayoutManager.scrollToPosition(it)
        }
    }
}
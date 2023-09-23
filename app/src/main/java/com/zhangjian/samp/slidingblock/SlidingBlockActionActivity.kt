package com.zhangjian.samp.slidingblock

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zhangjian.samp.ButtonRouter
import com.zhangjian.samp.MenuListAdapter
import com.zhangjian.samp.R
import com.zhangjian.samp.getRandColorInt
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
            add("GROUP1")
            add("GROUP2")
            add("GROUP3")
            add("GROUP4")
            add("GROUP5")
        }

        val linearLayoutManager = LinearLayoutManager(this@SlidingBlockActionActivity)
        recycleView.apply {
            val buttonRouters: MutableList<ButtonRouter> = mutableListOf()
            buttonRouters.apply {
                for (title in titles) {
                    val textBuilder = StringBuilder(title)
                    for (i in 0 until 30) {
                        textBuilder.append("\n")
                    }
                    add(ButtonRouter(textBuilder.toString()){
                        it.setBackgroundColor(getRandColorInt())
                        it.gravity = Gravity.CENTER
                        it.setTextColor(Color.WHITE)
                    })
                }
            }
            layoutManager = linearLayoutManager
            adapter = MenuListAdapter(buttonRouters)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val firstVisibleIndex =
                        linearLayoutManager.findFirstVisibleItemPosition()
                    if (firstVisibleIndex != curRecycleViewIndex) {
                        tabView.activeScrollToIndex(firstVisibleIndex)
                        curRecycleViewIndex = firstVisibleIndex
                    }
                }
            })
        }

        tabView.bindTabTitles(titles) {
            linearLayoutManager.scrollToPositionWithOffset(it, 0)
        }
    }
}
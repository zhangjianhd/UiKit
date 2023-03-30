package com.zhangjian.samp.slidingblock

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zhangjian.samp.ButtonRouter
import com.zhangjian.samp.MenuListAdapter
import com.zhangjian.samp.R

/**
 * Created zhangjian on 2021/9/22(10:24) in project UiKit.
 */
class SlidingBlockTabActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonRouters: MutableList<ButtonRouter> = mutableListOf()
        buttonRouters.apply {
            add(ButtonRouter("绑定ViewPage", SlidingBlockViewPagerActivity::class.java))
            add(ButtonRouter("主动处理点击", SlidingBlockActionActivity::class.java))
        }
        val adapter = MenuListAdapter(buttonRouters)
        val recycleView: RecyclerView = findViewById(R.id.rvList)
        recycleView.layoutManager = GridLayoutManager(this, 2)
        recycleView.adapter = adapter
    }
}
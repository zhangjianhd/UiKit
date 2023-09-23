package com.zhangjian.samp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zhangjian.samp.chart.PieChartActivity
import com.zhangjian.samp.hobby.HobbySlideActivity
import com.zhangjian.samp.longimage.LongImagePreviewActivity
import com.zhangjian.samp.pick.PickActivity
import com.zhangjian.samp.scop.SlidingBlockScopeActivity
import com.zhangjian.samp.slidingblock.SlidingBlockTabActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonRouters: MutableList<ButtonRouter> = mutableListOf()
        buttonRouters.apply {
            add(ButtonRouter("TAB滑块", SlidingBlockTabActivity::class.java))
        }
        buttonRouters.apply {
            add(ButtonRouter("范围滑块", SlidingBlockScopeActivity::class.java))
        }
        buttonRouters.apply {
            add(ButtonRouter("饼图", PieChartActivity::class.java))
        }
        buttonRouters.apply {
            add(ButtonRouter("喜好侧滑", HobbySlideActivity::class.java))
        }
        buttonRouters.apply {
            add(ButtonRouter("长图预览", LongImagePreviewActivity::class.java))
        }
        buttonRouters.apply {
            add(ButtonRouter("滚轮联级选择", PickActivity::class.java))
        }
        val adapter = MenuListAdapter(buttonRouters)
        val recycleView: RecyclerView = findViewById(R.id.rvList)
        recycleView.layoutManager = GridLayoutManager(this, 3)
        recycleView.adapter = adapter
    }
}
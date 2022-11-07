package com.zhangjian.samp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zhangjian.samp.slidingblock.SlidingBlockScopeActivity
import com.zhangjian.samp.slidingblock.SlidingBlockTabActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonRouters: MutableList<ButtonRouter> = mutableListOf()
        buttonRouters.apply {
            add(ButtonRouter("SlidingBlockTabView", SlidingBlockTabActivity::class.java))
        }
        buttonRouters.apply {
            add(ButtonRouter("SlidingBlockScopeView", SlidingBlockScopeActivity::class.java))
        }
        val adapter = MenuListAdapter(buttonRouters)
        val recycleView: RecyclerView = findViewById(R.id.rvList)
        recycleView.layoutManager = LinearLayoutManager(this)
        recycleView.adapter = adapter
    }
}
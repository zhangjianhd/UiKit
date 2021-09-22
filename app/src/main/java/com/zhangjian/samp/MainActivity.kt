package com.zhangjian.samp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonRouters: MutableList<ButtonRouter> = mutableListOf()
        buttonRouters.apply {
            add(ButtonRouter("SlidingBlockTabView", MainActivity::class.java))
        }
        val adapter = MenuListAdapter(buttonRouters)
        val recycleView: RecyclerView = findViewById(R.id.rvList)
        recycleView.layoutManager = LinearLayoutManager(this)
        recycleView.adapter = adapter
    }
}
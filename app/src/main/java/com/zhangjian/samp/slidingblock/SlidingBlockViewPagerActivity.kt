package com.zhangjian.samp.slidingblock

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.zhangjian.samp.R
import com.zhangjian.samp.getRandColorInt
import com.zhangjian.uikit.tab.SlidingBlockTabView

/**
 * Created zhangjian on 2021/9/22(10:31) in project UiKit.
 */
class SlidingBlockViewPagerActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sliding_block_view_pager)

        val viewPager: ViewPager = findViewById(R.id.viewPager)
        val tabView: SlidingBlockTabView = findViewById(R.id.slidingBlock)

        val titles = mutableListOf<String>()
        titles.apply {
            add("page1")
            add("page2")
            add("page3")
            add("page4")
        }

        val pages = mutableListOf<View>()
        pages.apply {
            val buildView: (String) -> View = {
                val view = TextView(this@SlidingBlockViewPagerActivity)
                view.textSize = 16f
                view.setTextColor(resources.getColor(R.color.text))
                view.text = it
                view.gravity = Gravity.CENTER
                val layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                view.layoutParams = layoutParams
                view.setBackgroundColor(getRandColorInt())
                view
            }
            for (title in titles) {
                add(buildView(title))
            }
        }

        val adapter: PagerAdapter = SimpleViewPagerAdapter(pages)

        viewPager.adapter = adapter

        tabView.bindContent(viewPager, titles)
    }
}
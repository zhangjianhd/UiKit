package com.zhangjian.samp.hobby

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import com.zhangjian.samp.R
import com.zhangjian.uikit.DisplayUtil
import com.zhangjian.uikit.page.HobbySlideAdapter
import com.zhangjian.uikit.page.HobbySlideState
import com.zhangjian.uikit.page.HobbySlideView

/**
 *  Create by zhangjian on 2023/3/30
 */
class HobbySlideActivity : FragmentActivity() {

    private val hobbySlide by lazy {
        findViewById<HobbySlideView>(R.id.hobbySlide)
    }

    private val tvIndex by lazy {
        findViewById<TextView>(R.id.tvIndex)
    }

    private val ivDisLike by lazy {
        findViewById<ImageView>(R.id.ivDisLike)
    }

    private val ivLike by lazy {
        findViewById<ImageView>(R.id.ivLike)
    }

    private val tvReStart by lazy {
        findViewById<View>(R.id.tvReStart)
    }

    private val gSlide by lazy {
        findViewById<View>(R.id.gSlide)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hobby_slide)
        val color = mutableListOf<Int>().apply {
            add(Color.parseColor("#FFC86B"))
            add(Color.parseColor("#71A7FF"))
            add(Color.parseColor("#FF6B83"))
            add(Color.parseColor("#D765FF"))
            add(Color.parseColor("#5EE0E4"))
            add(Color.parseColor("#FF9141"))
            add(Color.parseColor("#FFBD56"))
            add(Color.parseColor("#5A95FF"))
            add(Color.parseColor("#FF566A"))
            add(Color.parseColor("#CF51FF"))
            add(Color.parseColor("#4BDADF"))
            add(Color.parseColor("#FF7B34"))
        }
        setDate(color)

        tvReStart.setOnClickListener {
            gSlide.visibility = View.VISIBLE
            tvReStart.visibility = View.GONE
            setDate(color)
        }

        ivLike.setOnClickListener {
            autoSlide(true)
        }
        ivDisLike.setOnClickListener {
            autoSlide(false)
        }
    }

    private fun autoSlide(like: Boolean) {
        hobbySlide.autoSlide(like)
    }

    private fun setDate(colors: List<Int>) {
        tvIndex.text = "1/${colors.size}"
        hobbySlide.setData(object : HobbySlideAdapter<CardView> {
            override fun onCreatePage(parent: ViewGroup): CardView {
                return CardView(parent.context).apply {
                    radius = DisplayUtil.dip2px(context, 14f).toFloat()
                }
            }

            override fun onBind(pos: Int, view: CardView) {
                view.setCardBackgroundColor(colors[pos])
            }

            override fun onItemResult(pos: Int, toRight: Boolean) {
                tvIndex.text = if (pos + 1 == colors.size) {
                    onFinish()
                    "结束了"
                } else {
                    "${pos + 2}/${colors.size}"
                }
            }

            override fun onSlideStateChange(t: CardView, state: HobbySlideState) {
                updateState(state)
            }

            override fun getCount(): Int {
                return colors.size
            }
        })
    }

    private fun onFinish() {
        gSlide.visibility = View.GONE
        tvReStart.visibility = View.VISIBLE
    }

    private fun updateState(state: HobbySlideState) {
        ivDisLike.isEnabled = state == HobbySlideState.IDLE
        ivLike.isEnabled = state == HobbySlideState.IDLE
        when (state) {
            HobbySlideState.RIGHT -> {
                ivLikeUpdate(true)
                ivDisLikeUpdate(false)
            }
            HobbySlideState.LEFT -> {
                ivLikeUpdate(false)
                ivDisLikeUpdate(true)
            }
            else -> {
                ivLikeUpdate(false)
                ivDisLikeUpdate(false)
            }
        }
    }

    private fun ivLikeUpdate(select: Boolean) {
        ivLike.background = resources.getDrawable(
            if (select) {
                R.drawable.sp_r100_gradient_ff617b_2_ff7070
            } else {
                R.drawable.sp_r100_stroke_line_solid_white
            }, null
        )
        ivLike.imageTintList = if (select) {
            ColorStateList.valueOf(Color.WHITE)
        } else {
            null
        }
    }

    private fun ivDisLikeUpdate(select: Boolean) {
        ivDisLike.background = resources.getDrawable(
            if (select) {
                R.drawable.sp_r100_fbb724
            } else {
                R.drawable.sp_r100_stroke_line_solid_white
            }, null
        )
        ivDisLike.imageTintList = if (select) {
            ColorStateList.valueOf(Color.WHITE)
        } else {
            null
        }
    }
}
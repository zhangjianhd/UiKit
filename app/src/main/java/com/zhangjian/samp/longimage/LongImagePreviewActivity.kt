package com.zhangjian.samp.longimage

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.zhangjian.samp.R
import com.zhangjian.uikit.chart.PieChart
import com.zhangjian.uikit.longimage.LongImageView
import java.io.File

/**
 *  Create by zhangjian on 2023/3/30
 */
class LongImagePreviewActivity : FragmentActivity() {

    private val longImage by lazy {
        findViewById<LongImageView>(R.id.longImage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_longimage_preview)

        Glide.with(this).download("https://qnm.hunliji.com/o_1haopjb6qk3r1l5ch8ems18a49.jpg")
            .addListener(object : RequestListener<File> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<File>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: File?,
                    model: Any?,
                    target: Target<File>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    resource?.let {
                        longImage.setImage(it)
                    }
                    return false
                }
            }).submit()

    }
}
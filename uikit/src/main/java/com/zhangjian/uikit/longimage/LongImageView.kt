package com.zhangjian.uikit.longimage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

/**
 *  Create by zhangjian on 2023/9/20
 *  借助BitmapRegionDecoder分块，然后使用RecyclerView以列表形式展示，在RecyclerView的回收时机回收bitmap,以达到
 *  分块加载图片的目的，避免加载整个大图导致内存溢出
 */
class LongImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    RecyclerView(context, attrs, defStyleAttr) {

    //不用太小，太小的话会导致切块过多，快速滚动时创建过多的临时bitmap有风险造成内存抖动
    private val blockHeight = 400

    private var lastBlockHeight: Int = 0
    private var blockCount: Int = 0

    private val mRect by lazy {
        Rect()
    }

    private var bitmapRegionDecoder: BitmapRegionDecoder? = null

    private val options: BitmapFactory.Options by lazy {
        BitmapFactory.Options()
    }

    fun setImage(file: File) {
        val opts = BitmapFactory.Options()
        opts.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.path, opts)
        try {
            setImage(FileInputStream(file.path), opts.outWidth, opts.outHeight)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setImage(inputStream: InputStream, imageWidth: Int, imageHeight: Int) {
        try {
            bitmapRegionDecoder = BitmapRegionDecoder.newInstance(inputStream, true)
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }
        blockCount = imageHeight / blockHeight
        lastBlockHeight = imageHeight % blockHeight
        if (lastBlockHeight > 0) {
            blockCount++
        }
        mRect.left = 0
        mRect.right = imageWidth

        layoutManager = LinearLayoutManager(context)
        adapter = ImageClipAdapter()
    }

    private fun getCurrentBitmap(i: Int): Bitmap? {
        mRect.top = i * blockHeight
        mRect.bottom = mRect.top + if (i == blockCount - 1) {
            lastBlockHeight
        } else {
            blockHeight
        }
        return bitmapRegionDecoder?.decodeRegion(mRect, options)
    }

    inner class ImageClipAdapter : Adapter<ImageViewHolder>() {

        override fun onViewRecycled(holder: ImageViewHolder) {
            super.onViewRecycled(holder)
            (holder.itemView as? ImageView)?.let {
                if (it.tag is Bitmap) {
                    (it.tag as Bitmap).recycle()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            return ImageViewHolder()
        }

        override fun getItemCount(): Int {
            return blockCount
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            (holder.itemView as? ImageView)?.let {
                val currentBitmap = getCurrentBitmap(position)
                it.setImageBitmap(currentBitmap)
                it.tag = currentBitmap
            }
        }
    }

    inner class ImageViewHolder : ViewHolder(ImageView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        adjustViewBounds = true
    })
}
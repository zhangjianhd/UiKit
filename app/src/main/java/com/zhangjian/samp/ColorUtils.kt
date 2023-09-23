package com.zhangjian.samp

import android.graphics.Color
import java.util.Locale
import java.util.Random

/**
 *  Create by shang_dong on 2023/9/23
 */
fun getRandColor(): String {
    var r: String
    var g: String
    var b: String
    val random = Random()
    r = Integer.toHexString(random.nextInt(256)).uppercase(Locale.getDefault())
    g = Integer.toHexString(random.nextInt(256)).uppercase(Locale.getDefault())
    b = Integer.toHexString(random.nextInt(256)).uppercase(Locale.getDefault())
    r = if (r.length == 1) "0$r" else r
    g = if (g.length == 1) "0$g" else g
    b = if (b.length == 1) "0$b" else b
    return "#$r$g$b"
}

fun getRandColorInt() : Int{
    return Color.parseColor(getRandColor())
}


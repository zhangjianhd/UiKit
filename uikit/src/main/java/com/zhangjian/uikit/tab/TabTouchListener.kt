package com.zhangjian.uikit.tab

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

class TabTouchListener(context: Context, var click: (() -> Unit)?, var doubleClick: () -> Unit) : OnTouchListener {

    private var doubleDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            doubleClick.invoke()
            return true
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }
    })

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            click?.invoke()
        }
        return doubleDetector.onTouchEvent(event)
    }
}
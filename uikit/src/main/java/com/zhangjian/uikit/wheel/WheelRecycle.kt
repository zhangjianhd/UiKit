/*
 *  Android Wheel Control.
 *  https://code.google.com/p/android-wheel/
 *  
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.hunliji.biz.uikit.widget.picker.wheel;

import android.widget.TextView
import android.widget.LinearLayout
import java.util.*

/**
 * Recycle stores wheel items to reuse.
 */
class WheelRecycle {
    // Cached items
    private var items: MutableList<TextView>? = null

    fun recycleItems(layout: LinearLayout, count: Int) {
        var needRecycleCount = layout.childCount - count
        while (needRecycleCount > 0) {
            recycleView(layout.getChildAt(0) as TextView)
            layout.removeViewAt(0)
            needRecycleCount--
        }
    }

    /**
     * Gets item view
     * @return the cached view
     */
    val item: TextView?
        get() = getCachedView()

    fun clearAll() {
        if (items != null) {
            items!!.clear()
        }
    }

    /**
     * Adds view to specified cache. Creates a cache list if it is null.
     * @param view the view to be cached
     * @param cache the cache list
     * @return the cache list
     */
    private fun addView(view: TextView): MutableList<TextView>? {
        if (items == null) {
            items = mutableListOf()
        }
        items!!.add(view)
        return items
    }

    /**
     * Adds view to cache. Determines view type (item view or empty one) by index.
     * @param view the view to be cached
     * @param index the index of view
     */
    private fun recycleView(view: TextView) {
        items = addView(view)
    }

    private fun getCachedView(): TextView? {
        if (items.isNullOrEmpty()) {
            return null
        }
        return items!!.removeAt(0)
    }
}
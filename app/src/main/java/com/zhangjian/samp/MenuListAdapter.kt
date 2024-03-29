package com.zhangjian.samp

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.zhangjian.uikit.DisplayUtil

/**
 * Created zhangjian on 2021/9/22(09:57) in project UiKit.
 */
class MenuListAdapter(private val buttonRouters: MutableList<ButtonRouter>) :
    RecyclerView.Adapter<ViewHolderImpl>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderImpl {
        val button = Button(parent.context)
        val layoutParams = MarginLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.topMargin = DisplayUtil.dip2px(parent.context, 15f)
        button.layoutParams = layoutParams
        button.isAllCaps = false
        return ViewHolderImpl(button)
    }

    override fun onBindViewHolder(holder: ViewHolderImpl, position: Int) {
        val buttonRouter = buttonRouters[position]
        holder.button.text = buttonRouter.text
        buttonRouter.decorateBtn?.invoke(holder.button)
        buttonRouter.targetActivityClass?.let { activityClass ->
            holder.button.setOnClickListener {
                val intent = Intent(it.context, activityClass)
                it.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount() = buttonRouters.size
}

class ViewHolderImpl(val button: Button) : RecyclerView.ViewHolder(button)

class ButtonRouter(
    val text: String,
    val targetActivityClass: Class<out Activity>? = null,
    val decorateBtn: ((Button) -> Unit)? = null
)
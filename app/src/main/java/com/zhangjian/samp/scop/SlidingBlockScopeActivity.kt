package com.zhangjian.samp.scop

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.zhangjian.samp.R
import com.zhangjian.uikit.scope.PriceSlidingBlockScopeAdapter
import com.zhangjian.uikit.scope.SlidingBlockScopeView

/**
 * Created zhangjian on 2022/11/07 in project UiKit.
 */
class SlidingBlockScopeActivity : FragmentActivity() {

    private var startPrice: Int? = null
    private var endPrice: Int? = null

    private val adapter = PriceSlidingBlockScopeAdapter(
        2000,
        1000,
        17,
        true
    ) { start, end ->
        startPrice = start
        endPrice = end
    }

    private lateinit var seekPrice: SlidingBlockScopeView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scope)


        seekPrice = findViewById(R.id.seekPrice)

        seekPrice.setAdapter(adapter)

        findViewById<View>(R.id.button).setOnClickListener {
            Toast.makeText(this, "最小值${startPrice}，最大值${endPrice}", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.change).setOnClickListener {
            changePrice(3000,6000)
        }
    }

    private fun changePrice(start : Int?,end : Int?) {
        adapter.getIndex(start, end).let {
            seekPrice.setIndex(it.startIndex, it.endIndex)
        }
    }
}
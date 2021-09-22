package com.zhangjian.samp.slidingblock;

import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 * Created by chenchengle on 2020/3/19.
 */
public class SimpleViewPagerAdapter<T extends View> extends PagerAdapter {

    private List<T> data;

    public SimpleViewPagerAdapter(List<T> data) {
        this.data = data;
    }

    public int getCount() {
        return this.data == null ? 0 : this.data.size();
    }

    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(data.get(position));
        return this.data.get(position);
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(data.get(position));
    }

}

package com.example.recycler.pager

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import org.jetbrains.anko.backgroundColor

/**
 * Pager Adapter for [View]s
 */
open class PagerAdapter4View(open var views: List<View> = emptyList()) : PagerAdapter() {
    override fun isViewFromObject(p0: View, p1: Any): Boolean = p0 === p1

    override fun getCount(): Int = views.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return views[position].apply {
            container.addView(this)
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}

open class BannerAdapter(var colors: List<Int>) : PagerAdapter() {
    override fun getCount(): Int = Int.MAX_VALUE

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return TextView(container.context).apply {
            backgroundColor = colors[position%colors.size]
            container.addView(this)
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any) = view == `object`
}
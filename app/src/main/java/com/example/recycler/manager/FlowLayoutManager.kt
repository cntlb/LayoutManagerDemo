package com.example.recycler.manager

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.wrapContent
import kotlin.math.max

/**
 * @author Linbing Tang
 * @since 19-8-1 10:03
 */
class FlowLayoutManager : RecyclerView.LayoutManager() {
    private var mFirst: Int = 0
    private var mLast: Int = 0
    private var mScrollY = 0

    override fun generateDefaultLayoutParams() = RecyclerView.LayoutParams(wrapContent, wrapContent)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        detachAndScrapAttachedViews(recycler)
        mFirst = 0
        mLast = itemCount - 1
        fill(recycler, state)
    }

    private fun fill(recycler: RecyclerView.Recycler, state: RecyclerView.State) {

        mFirst = 0
        mLast = itemCount - 1

        var left = 0
        var top = 0
        var lineHeight = 0
        for (i in mFirst..mLast) {
            val view = recycler.getViewForPosition(i)
            addView(view)
            measureChildWithMargins(view, 0, 0)

            val horizontal = measuredHorizontal(view)
            val vertical = measuredVertical(view)
            //一行放得下
            if (left + horizontal <= hSpace) {
                layoutDecoratedWithMargins(view, left, top, left + horizontal, top + vertical)
                left += horizontal
                lineHeight = max(lineHeight, vertical)
            } else {//换行
                left = 0
                top += lineHeight
                lineHeight = vertical

                if (top >= vSpace) {//超出屏幕回收
                    removeAndRecycleView(view, recycler)
                    mLast = i - 1
                    break
                } else {
                    layoutDecoratedWithMargins(view, left, top, left + horizontal, top + vertical)
                    left += horizontal
                }
            }
        }
    }

    override fun canScrollVertically(): Boolean {
        return true
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        if (childCount <= 0 || dy == 0) return 0

        var off = dy
        if (dy + mScrollY < 0) {
            off = -mScrollY
        } else if (dy > 0 && mLast == itemCount - 1) {
            val view = recycler.getViewForPosition(mLast)
            off = getDecoratedBottom(view) - (height - paddingBottom)
            if (off < 0) {
                off = -off
            }
        }
        mScrollY += off
        offsetChildrenVertical(-off)
        return off
    }


    private fun measuredHorizontal(view: View): Int {
        return (view.layoutParams as ViewGroup.MarginLayoutParams).run { getDecoratedMeasuredWidth(view) + leftMargin + rightMargin }
    }

    private fun measuredVertical(view: View): Int {
        return (view.layoutParams as ViewGroup.MarginLayoutParams).run { getDecoratedMeasuredHeight(view) + topMargin + bottomMargin }
    }

    private val hSpace: Int
        get() = width - paddingLeft - paddingRight
    private val vSpace: Int
        get() = height - paddingTop - paddingBottom
}
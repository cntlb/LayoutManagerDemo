package com.example.recycler.stack

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.wrapContent
import kotlin.math.abs

/**
 * @author Linbing Tang
 * @since 19-8-2 15:08
 */
class StackLayoutManager : RecyclerView.LayoutManager() {
    //最大堆叠数量
    private var mMaxStackCount = 4
    //水平滑动距离
    private var mHorizontalScroll = 0
    private var mFirstVisiblePosition = 0
    private var mLastVisiblePosition = 0
    //堆叠项错开的距离
    private var mStackMargin = 20
    //平铺项间距
    private var mSpreadMargin = 40
    //item完整的一次滑动距离
    private var mItemFullScrollDistance = 0

    override fun generateDefaultLayoutParams() = RecyclerView.LayoutParams(wrapContent, wrapContent)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (state.itemCount == 0 || state.isPreLayout) return
        fillChildren(recycler, state, 0)
    }

    override fun canScrollHorizontally() = true

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        if (dx == 0 || childCount == 0) return 0
        val ret = fillChildren(recycler, state, dx)
        recycleViews(recycler)
        Log.e(javaClass.simpleName, "child count: $childCount, scrap count:${recycler.scrapList.size}")
        return ret
    }

    private fun fillChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State, scrollX: Int): Int {
        //实际滑动距离
        var dx = scrollX

        //****************检测滑动边界****************
        //往右滑动, 已到达左边界
        if (dx <= 0 && mHorizontalScroll + dx <= 0) {
            mHorizontalScroll = 0
            dx = 0
        } else if (dx > 0 && mLastVisiblePosition - mFirstVisiblePosition <= mMaxStackCount - 1) {
            dx = 0
        } else {
            mHorizontalScroll += dx
        }


        //****************初始化参数****************
        detachAndScrapAttachedViews(recycler)
        if (mItemFullScrollDistance == 0) {
            val view = recycler.getViewForPosition(mFirstVisiblePosition)
            measureChildWithMargins(view, 0, 0)
            mItemFullScrollDistance = computeHorizontalSpacing(view)
            removeAndRecycleView(view, recycler)
        }
        //当前滑动在一次完整的item滑动距离中的完成进度
        val progress = (abs(mHorizontalScroll) % mItemFullScrollDistance) / (mItemFullScrollDistance * 1.0f)
        //堆叠区域item偏移
        val stackOffset = (mStackMargin * progress).toInt()
        //平铺区域item偏移
        val spreadOffset = (mItemFullScrollDistance * progress).toInt()
        //第一个可见项position=完整移动的item个数
        mFirstVisiblePosition = mHorizontalScroll / mItemFullScrollDistance
        mLastVisiblePosition = itemCount - 1

        var left = paddingLeft - mStackMargin

        //****************布局****************
        for (i in mFirstVisiblePosition until itemCount) {
            //堆叠区域
            if (i - mFirstVisiblePosition < mMaxStackCount) {
                val child = recycler.getViewForPosition(i)
                addView(child)
                measureChildWithMargins(child, 0, 0)
                left += mStackMargin
                if (i == mFirstVisiblePosition) {
                    left -= stackOffset
                }
                layoutDecoratedWithMargins(
                    child,
                    left,
                    paddingTop,
                    left + getDecoratedMeasuredWidth(child),
                    paddingTop + getDecoratedMeasuredHeight(child)
                )
            }
            //平铺区域
            else {
                if (left >= width - paddingRight - paddingLeft) {
                    mLastVisiblePosition = i - 1
                    break
                }
                val child = recycler.getViewForPosition(i)
                addView(child)
                measureChildWithMargins(child, 0, 0)
                left += mSpreadMargin + getDecoratedMeasuredWidth(child)
                if (i == mFirstVisiblePosition + mMaxStackCount) {
                    left -= spreadOffset
                }

                layoutDecoratedWithMargins(
                    child,
                    left,
                    paddingTop,
                    left + getDecoratedMeasuredWidth(child),
                    paddingTop + getDecoratedMeasuredHeight(child)
                )
            }
        }

        return dx
    }

    private fun recycleViews(recycler: RecyclerView.Recycler) {
        try {
            val scrapList = recycler.scrapList
            for (i in scrapList.indices) {
                removeAndRecycleView(scrapList[i].itemView, recycler)
            }
        } catch (e: Exception) {
        }
    }

    private fun computeHorizontalSpacing(child: View): Int {
        val lp = child.layoutParams as RecyclerView.LayoutParams
        return getDecoratedMeasuredWidth(child) + lp.leftMargin + lp.rightMargin
    }

    private fun computeVerticalSpacing(child: View): Int {
        val lp = child.layoutParams as RecyclerView.LayoutParams
        return getDecoratedMeasuredHeight(child) + lp.topMargin + lp.bottomMargin
    }
}
package com.example.recycler.stack

import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.wrapContent

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

    override fun generateDefaultLayoutParams() = RecyclerView.LayoutParams(wrapContent, wrapContent)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (state.itemCount == 0 || state.isPreLayout) return
        fillChildren(recycler, state, 0)
    }

    private fun fillChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State, dx: Int) {

        var left = 0
        mFirstVisiblePosition = 0
        mLastVisiblePosition = itemCount - 1

        detachAndScrapAttachedViews(recycler)

        for (i in mFirstVisiblePosition until itemCount) {
            //堆叠区域
            if (i - mFirstVisiblePosition < mMaxStackCount) {
                val child = recycler.getViewForPosition(i)
                addView(child)
                measureChildWithMargins(child, 0, 0)
                layoutDecoratedWithMargins(
                    child,
                    left,
                    paddingTop,
                    left + getDecoratedMeasuredWidth(child),
                    paddingTop + getDecoratedMeasuredHeight(child)
                )
                left += mStackMargin
            }
            //平铺区域
            else {
                if(left >= width-paddingRight-paddingLeft){
                    mLastVisiblePosition = i-1
                    break
                }
                val child = recycler.getViewForPosition(i)
                addView(child)
                measureChildWithMargins(child, 0, 0)
                layoutDecoratedWithMargins(
                    child,
                    left,
                    paddingTop,
                    left + getDecoratedMeasuredWidth(child),
                    paddingTop + getDecoratedMeasuredHeight(child)
                )
                left += mSpreadMargin+getDecoratedMeasuredWidth(child)
            }
        }
    }
}
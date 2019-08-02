package com.example.recycler.manager

import android.graphics.Rect
import android.util.Log
import android.util.SparseArray
import android.util.SparseBooleanArray
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.wrapContent

/**
 * @author Linbing Tang
 * @since 19-7-30 09:27
 */
class OverlapManager : RecyclerView.LayoutManager() {
    private var totalHeight = 0
    private var verticalScrollOffset = 0
    //保存所有的Item的上下左右的偏移量信息
    private val allItemFrames = SparseArray<Rect>()
    //记录Item是否出现过屏幕且还没有回收。true表示出现过屏幕上，并且还没被回收
    private val hasAttachedItems = SparseBooleanArray()


    override fun generateDefaultLayoutParams() = RecyclerView.LayoutParams(wrapContent, wrapContent)
    override fun isAutoMeasureEnabled() = true

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount <= 0 || state.isPreLayout) return

        detachAndScrapAttachedViews(recycler)
        var offsetY = 0
        for (i in 0 until itemCount) {
            val view = recycler.getViewForPosition(i)
            addView(view)
            measureChildWithMargins(view, 0, 0)
            val height = getDecoratedMeasuredHeight(view)
            val width = getDecoratedMeasuredWidth(view)

            val frame = allItemFrames.get(i) ?: Rect()
            frame.set(0, offsetY, width, offsetY + height)
            // 将当前的Item的Rect边界数据保存
            allItemFrames.put(i, frame)
            // 由于已经调用了detachAndScrapAttachedViews，因此需要将当前的Item设置为未出现过
            hasAttachedItems.put(i, false)

            //将竖直方向偏移量增大height
            offsetY += height
        }
        totalHeight = offsetY + 100

        recycleAndFillItems(recycler, state)
    }

    private fun recycleAndFillItems(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount <= 0 || state.isPreLayout) return

        // 当前scroll offset状态下的显示区域
        val displayFrame =
            Rect(0, verticalScrollOffset, horizontalSpace, verticalScrollOffset + verticalSpace)

        /**
         * 将滑出屏幕的Items回收到Recycle缓存中
         */
        val childFrame = Rect()
        for (i in 0 until childCount) {
            val child = getChildAt(i) ?: continue
            childFrame.left = getDecoratedLeft(child)
            childFrame.top = getDecoratedTop(child)
            childFrame.right = getDecoratedRight(child)
            childFrame.bottom = getDecoratedBottom(child)
            if (!Rect.intersects(displayFrame, childFrame)) {
                removeAndRecycleView(child, recycler)
            }
        }
        for (i in 0 until itemCount) {
            if (Rect.intersects(displayFrame, allItemFrames.get(i))) {
                val scrap = recycler.getViewForPosition(i)
                measureChildWithMargins(scrap, 0, 0)
                addView(scrap)

                val frame = allItemFrames.get(i)
                //将这个item布局出来
                layoutDecorated(
                    scrap,
                    frame.left,
                    frame.top - verticalScrollOffset,
                    frame.right,
                    frame.bottom - verticalScrollOffset
                )

            }
        }
    }

    override fun canScrollVertically() = true

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        detachAndScrapAttachedViews(recycler)
        var travel = dy
        if (verticalScrollOffset + dy < 0) {
            travel = -verticalScrollOffset
        } else if (verticalScrollOffset + dy > totalHeight - verticalSpace) {
            travel = totalHeight - verticalSpace - verticalScrollOffset
        }
        verticalScrollOffset += travel
        offsetChildrenVertical(-travel)
        recycleAndFillItems(recycler, state)
        Log.d("--->", " childView count:$childCount")
        return travel
    }

    private val verticalSpace: Int
        get() = height - paddingBottom - paddingTop
    private val horizontalSpace: Int
        get() = width - paddingLeft - paddingRight
}
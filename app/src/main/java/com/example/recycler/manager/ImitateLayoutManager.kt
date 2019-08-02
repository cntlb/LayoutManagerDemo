package com.example.recycler.manager

import android.graphics.Rect
import android.util.Log
import android.util.SparseArray
import android.util.SparseBooleanArray
import androidx.core.util.set
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

/**
 * 仿LineaLayoutManager
 * @author Linbing Tang
 * @since 19-7-30 17:06
 */
class ImitateLayoutManager : RecyclerView.LayoutManager() {
    private var totalHeight = 0
    private var totalScrollY = 0
    private val frames = SparseArray<Rect>()
    private val attached = SparseBooleanArray()


    override fun generateDefaultLayoutParams() = RecyclerView.LayoutParams(matchParent, wrapContent)
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

            val frame = frames[i] ?: Rect()
            frame.set(0, offsetY, width, offsetY + height)
            frames[i] = frame
            attached[i] = false
            offsetY += height
        }
        totalHeight = offsetY

        fillViews(recycler, state)
    }

    private fun fillViews(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount <= 0 || state.isPreLayout) return

        val displayFrame = Rect(0, totalScrollY, horizontalSpace, verticalSpace + totalScrollY)

        val rect = Rect()
        for (i in 0 until childCount) {
            val view = getChildAt(i) ?: continue
            rect.set(
                getDecoratedLeft(view),
                getDecoratedTop(view),
                getDecoratedRight(view),
                getDecoratedBottom(view)
            )
            if (!Rect.intersects(displayFrame, rect)) {
                removeAndRecycleView(view, recycler)
            }
        }
        for (i in 0 until itemCount) {
            if (Rect.intersects(displayFrame, frames[i])) {
                val view = recycler.getViewForPosition(i)
                measureChildWithMargins(view, 0, 0)
                addView(view)
                val frame = frames[i]
                layoutDecorated(
                    view,
                    frame.left,
                    frame.top - totalScrollY,
                    frame.right,
                    frame.bottom - totalScrollY
                )
            }
        }
    }

    override fun canScrollVertically() = true
    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        if (totalScrollY == 0 && dy < 0) return 0
        if (totalScrollY == totalHeight - verticalSpace && dy > 0) return 0

        detachAndScrapAttachedViews(recycler)
        var ret = dy
        if (totalScrollY + dy < 0) {
            ret = -totalScrollY
        } else if (totalScrollY + dy > totalHeight - verticalSpace) {
            ret = totalHeight - totalScrollY - verticalSpace
        }
        totalScrollY += ret
        offsetChildrenVertical(-ret)
        fillViews(recycler, state)
        Log.e(javaClass.simpleName, "dy = $dy   child count: $childCount")
        return ret
    }

    private val verticalSpace: Int
        get() = height - paddingTop - paddingBottom
    private val horizontalSpace: Int
        get() = width - paddingLeft - paddingRight


}
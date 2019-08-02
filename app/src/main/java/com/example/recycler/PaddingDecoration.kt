package com.example.recycler

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author Linbing Tang
 * @since 19-7-30 14:26
 */
class PaddingDecoration: RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.set(20, 20, 20, 20)
    }
}
package com.example.recycler

import android.graphics.Color
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import kotlin.random.Random

/**
 * @author Linbing Tang
 * @since 19-7-30 09:38
 */
class TestAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val words = """
        Adapter: A subclass of RecyclerView.Adapter responsible for providing views that represent items in a data set.
        Position: The position of a data item within an Adapter.
        Index: The index of an attached child view as used in a call to ViewGroup.getChildAt. Contrast with Position.
        Binding: The process of preparing a child view to display data corresponding to a position within the adapter.
        Recycle (view): A view previously used to display data for a specific adapter position may be placed in a cache for later reuse to display the same type of data again later. This can drastically improve performance by skipping initial layout inflation or construction.
        Scrap (view): A child view that has entered into a temporarily detached state during layout. Scrap views may be reused without becoming fully detached from the parent RecyclerView, either unmodified if no rebinding is required or modified by the adapter if the view was considered dirty.
        Dirty (view): A child view that must be rebound by the adapter before being displayed.
    """.trimIndent().split("\\W+".toRegex())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.e(javaClass.simpleName, "onCreateViewHolder($viewType)")
        return object : RecyclerView.ViewHolder(TextView(parent.context).apply {
            //            backgroundColor = Color.BLACK
//            textColor = Color.GREEN
            backgroundResource = attr(R.attr.selectableItemBackground).resourceId
        }) {}
    }

    override fun getItemCount(): Int {
        return 200
    }

//    override fun getItemViewType(position: Int): Int {
//        return position
//    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder.itemView as TextView).apply {
            text = "$position. ${words[position % words.size]}"
            onClick { context.toast(text) }
        }
    }
}
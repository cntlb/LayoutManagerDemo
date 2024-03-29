package com.example.recycler.stack

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.recycler.R
import kotlinx.android.synthetic.main.activity_stack_manager.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class StackManagerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stack_manager)

        list.apply {
            layoutManager = StackLayoutManager().apply {
                onScrollListener = StackScrollListener()
            }
            adapter = StackAdapter()
        }
    }


    class StackAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val colors =
            listOf(
                Color.RED,
                Color.parseColor("#FFA500"),
                Color.YELLOW,
                Color.GREEN,
                Color.CYAN,
                Color.BLUE,
                Color.MAGENTA
            )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = ImageView(parent.context).apply {
                layoutParams = RecyclerView.LayoutParams(200, 300)
            }
            return object : RecyclerView.ViewHolder(view) {}
        }

        override fun getItemCount() = 50

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder.itemView as ImageView).apply {
                setImageDrawable(ColorDrawable(colors[position % colors.size]))
                onClick {
                    Log.e(javaClass.simpleName, "$position")
                }
            }
        }
    }

    class StackScrollListener : StackLayoutManager.OnScrollListener {
        override fun onStacking(
            manager: StackLayoutManager,
            view: View,
            viewLayer: Int,
            maxLayerCount: Int,
            position: Int,
            fraction: Float,
            offset: Int
        ) {
            view.scaleX = 0.8f - (maxLayerCount-viewLayer )* 0.1f + fraction * 0.2f
            view.scaleY = 0.8f - (maxLayerCount-viewLayer )* 0.1f + fraction * 0.2f
            view.alpha  = 0.8f - (maxLayerCount-viewLayer )* 0.1f + fraction * 0.2f
        }

        override fun onFocusing(manager: StackLayoutManager, view: View, position: Int, fraction: Float, offset: Int) {
            view.scaleX = 0.8f + fraction * 0.2f
            view.scaleY = 0.8f + fraction * 0.2f
            view.alpha = 0.8f + fraction * 0.2f
        }

        override fun onSpreading(manager: StackLayoutManager, view: View, position: Int, fraction: Float, offset: Int) {
            view.scaleX = 0.8f
            view.scaleY = 0.8f
            view.alpha = 0.8f
        }
    }
}

package com.example.recycler.stack

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.recycler.R
import com.example.recycler.TestAdapter
import kotlinx.android.synthetic.main.activity_stack_manager.*
import org.jetbrains.anko.backgroundColor

class StackManagerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stack_manager)

        list.apply {
            layoutManager = StackLayoutManager()
            adapter = StackAdapter()
        }
    }


    class StackAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val colors = listOf(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = ImageView(parent.context).apply {
                layoutParams = RecyclerView.LayoutParams(200, 300)
            }
            return object : RecyclerView.ViewHolder(view) {}
        }

        override fun getItemCount() = 100

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder.itemView as ImageView).setImageDrawable(ColorDrawable(colors[position % colors.size]))
        }
    }
}

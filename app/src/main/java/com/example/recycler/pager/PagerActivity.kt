package com.example.recycler.pager

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.example.recycler.R
import kotlinx.android.synthetic.main.activity_pager.*
import org.jetbrains.anko.backgroundColor
import kotlin.math.abs

class PagerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pager)

        view_pager.apply {
            adapter = BannerAdapter(listOf(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA))
            offscreenPageLimit = 3
            currentItem = Int.MAX_VALUE / 2
            setPageTransformer(false, MyTransformer())
        }
    }

    class MyTransformer : ViewPager.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            val tv = page as TextView
            tv.text = position.toString()

            when {
                position > 0f && position < .5f -> {
                    page.scaleX = 1f - abs(position)*0.5f
                    page.scaleY = 1f - abs(position)*0.5f
                    page.translationX = -page.width * abs(position)
                }
            }
        }
    }
}

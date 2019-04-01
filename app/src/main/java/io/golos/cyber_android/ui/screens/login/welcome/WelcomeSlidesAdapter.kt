package io.golos.cyber_android.ui.screens.login.welcome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.item_welcome_slide.view.*

class WelcomeSlidesAdapter(val values: List<SlideItem>): RecyclerView.Adapter<WelcomeSlidesAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_welcome_slide, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = Integer.MAX_VALUE

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(values[position % 3])
    }

    class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(slideItem: SlideItem) {
            view.slideImg.setImageResource(slideItem.img)
            (view.slideImg.layoutParams as LinearLayout.LayoutParams).gravity = slideItem.imgGravity
            view.slideText.text = slideItem.text
        }
    }
}
package io.golos.cyber_android.ui.screens.wallet_shared.carousel

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.glide.GlideTarget
import io.golos.cyber_android.ui.shared.glide.clear
import io.golos.cyber_android.ui.shared.glide.load
import kotlinx.android.synthetic.main.view_wallet_carousel.view.*

class CarouselItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private var iconGlideTarget: GlideTarget? = null

    fun bind(item: CarouselListItem, position: Int) {

        iconGlideTarget = if(item.iconUrl == null) {
            itemView.listItemIcon.setImageResource(R.drawable.ic_commun)
            null
        } else {
            itemView.listItemIcon.load(item.iconUrl, R.drawable.ic_commun)
        }

        itemView.root.tag = CarouselItemTag(id = item.id.code, position = position)
    }

    fun recycle() {
        iconGlideTarget?.clear(itemView.context.applicationContext)
    }
}
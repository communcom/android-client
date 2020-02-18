package io.golos.cyber_android.ui.screens.wallet_shared.carousel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R


class CarouselAdapter(
    @LayoutRes
    private val layoutId: Int
) : RecyclerView.Adapter<CarouselItemViewHolder>() {

    private var items: List<CarouselListItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselItemViewHolder =
        CarouselItemViewHolder(LayoutInflater.from(parent.context).inflate(layoutId, parent, false))

    override fun onBindViewHolder(holder: CarouselItemViewHolder, position: Int) {
        val index = getItemIndexByPosition(position)

        holder.bind(items[index], position)
    }

    override fun onViewRecycled(holder: CarouselItemViewHolder) = holder.recycle()

    override fun getItemCount() = Int.MAX_VALUE

    fun setItems(newItems: List<CarouselListItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    /**
     * Calculate start scroll position by index of an item in the list
     */
    fun calculateStartPosition(index: Int): Int {
        val basePosition = Int.MAX_VALUE /2
        val baseIndex = getItemIndexByPosition(basePosition)

        return basePosition - (baseIndex - index)
    }

    private fun getItemIndexByPosition(position: Int) = position % items.size
}

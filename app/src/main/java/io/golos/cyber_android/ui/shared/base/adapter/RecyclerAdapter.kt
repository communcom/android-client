package io.golos.cyber_android.ui.shared.base.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

open class RecyclerAdapter(
    items: List<RecyclerItem> = mutableListOf(),
    var click: ((RecyclerItem) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    protected var items = items.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val renderFirstTime = holder.holderItem == null || holder.holderItem != item
        holder.holderItem = item
        val holderView = holder.itemView
        val context = holderView.context
        if (renderFirstTime) {
            holder.holderItem?.initView(context, holderView)
        }
        holderView.setOnClickListener {
            click?.invoke(item)
        }
        holder.holderItem?.renderView(context, holderView)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.holderItem?.onViewRecycled(holder.itemView)
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int = getItem(position).getLayoutId()

    fun updateAdapter(newItems: List<RecyclerItem>) {
        if (items.isEmpty()) {
            items = newItems.toMutableList()
            notifyItemRangeInserted(0, newItems.size)
        } else {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return items.size
                }

                override fun getNewListSize(): Int {
                    return newItems.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return items[oldItemPosition].areItemsTheSame() == newItems[newItemPosition].areItemsTheSame()
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    return items[oldItemPosition].areContentsSame(newItems[newItemPosition])
                }
            })
            items = newItems.toMutableList()
            try {
                result.dispatchUpdatesTo(this)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private fun getItem(position: Int) = items[position]

    class ViewHolder(holderView: View) : RecyclerView.ViewHolder(holderView) {
        var holderItem: RecyclerItem? = null
    }
}
package io.golos.cyber_android.ui.common.recycler_view

import androidx.recyclerview.widget.RecyclerView

abstract class StaticListAdapterBase<TListItemEventsProcessor, TItem: ListItem>(
    private val listItemEventsProcessor: TListItemEventsProcessor,
    private val items: List<TItem>
) : RecyclerView.Adapter<ViewHolderBase<TListItemEventsProcessor, TItem>>(),
    AdapterRawDataAccess<TItem> {

    override fun getItemId(position: Int): Long = items[getItemPosition(position)].id

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolderBase<TListItemEventsProcessor, TItem>, position: Int) =
        holder.init(items[getItemPosition(position)], listItemEventsProcessor)

    override fun onViewRecycled(holder: ViewHolderBase<TListItemEventsProcessor, TItem>) =  holder.release()

    override fun getItem(position: Int): TItem = items[position]

    protected open fun getItemPosition(sourcePosition: Int): Int = sourcePosition
}
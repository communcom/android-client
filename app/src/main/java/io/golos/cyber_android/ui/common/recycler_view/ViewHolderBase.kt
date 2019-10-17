package io.golos.cyber_android.ui.common.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

abstract class ViewHolderBase<TListItemEventsProcessor, TItem: ListItem>(
    parentView: ViewGroup,
    @LayoutRes layoutResId: Int
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parentView.context).inflate(layoutResId, parentView, false)
) {
    /**
     * UI elements must be initialized here
     */
    abstract fun init(listItem: TItem, listItemEventsProcessor: TListItemEventsProcessor)

    /**
     * Used resources of UI elements must be released here (called in ListAdapterBase::onViewRecycled)
     */
    open fun release() {}
}
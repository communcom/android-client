package io.golos.cyber_android.ui.common.recycler_view

interface AdapterRawDataAccess<TItem: ListItem> {
    fun getItem(position: Int): TItem
}
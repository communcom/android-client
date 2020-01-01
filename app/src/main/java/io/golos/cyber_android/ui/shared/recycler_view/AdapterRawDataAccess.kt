package io.golos.cyber_android.ui.shared.recycler_view

interface AdapterRawDataAccess<TItem: ListItem> {
    fun getItem(position: Int): TItem
}
package io.golos.cyber_android.ui.shared.recycler_view.versioned

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData

@BindingAdapter("listItems")
fun setListItemsBinding(view: View, dataSource: LiveData<List<VersionedListItem>>?) =
    dataSource?.value?.let { (view as DynamicListWidget).updateList(it) }
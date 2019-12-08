package io.golos.cyber_android.ui.common.recycler_view.versioned

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.ui.common.extensions.parentActivity

@BindingAdapter("listItems")
fun setListItemsBinding(view: View, dataSource: LiveData<List<VersionedListItem>>?) {
    dataSource?.let { source ->
        view.parentActivity?.let { activity ->
            source.observe(activity, Observer { (view as DynamicListWidget).updateList(it) })
        }
    }
}
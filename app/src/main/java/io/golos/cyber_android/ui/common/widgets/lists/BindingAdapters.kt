package io.golos.cyber_android.ui.common.widgets.lists

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.ui.common.extensions.parentActivity

@BindingAdapter("noDataTitle")
fun setNoDataTextBinding(view: NoDataStub, dataSource: LiveData<Int>?) {
    dataSource?.let { source ->
        view.parentActivity?.let { activity ->
            source.observe(activity, Observer { view.setTitle(it) })
        }
    }
}

@BindingAdapter("noDataExplanation")
fun setNoDataExplanationBinding(view: NoDataStub, dataSource: LiveData<Int>?) {
    dataSource?.let { source ->
        view.parentActivity?.let { activity ->
            source.observe(activity, Observer { view.setExplanation(it) })
        }
    }
}
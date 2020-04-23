package io.golos.cyber_android.ui.shared.widgets.lists

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData

@BindingAdapter("noDataTitle")
fun setNoDataTextBinding(view: NoDataStub, dataSource: LiveData<Int>?) = dataSource?.value?.let { view.setTitle(it) }

@BindingAdapter("noDataExplanation")
fun setNoDataExplanationBinding(view: NoDataStub, dataSource: LiveData<Int>?) = dataSource?.value?.let { view.setExplanation(it) }
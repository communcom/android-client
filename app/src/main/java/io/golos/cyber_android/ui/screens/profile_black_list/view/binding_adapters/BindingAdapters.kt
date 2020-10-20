package io.golos.cyber_android.ui.screens.profile_black_list.view.binding_adapters

import android.widget.ToggleButton
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.ui.dto.BlackListFilter
import io.golos.cyber_android.ui.shared.extensions.parentActivity

@BindingAdapter("filterBlackList")
fun setFilterBlackListBinding(view: ToggleButton, dataSource: MutableLiveData<BlackListFilter>?) {
    dataSource?.let { source ->
        val viewValue = BlackListFilter.create(view.tag as String)

        view.parentActivity?.let { activity ->
            source.observe(activity, Observer {
                val newState = viewValue == it
                if(view.isChecked != newState) {
                    view.isChecked = newState
                }
                view.isEnabled = !newState          // to simulate a radio-button behaviour
            })
        }

        view.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                source.value = viewValue
            }
        }
    }
}
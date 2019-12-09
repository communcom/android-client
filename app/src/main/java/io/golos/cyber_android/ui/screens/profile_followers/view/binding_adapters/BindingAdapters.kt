package io.golos.cyber_android.ui.screens.profile_followers.view.binding_adapters

import android.widget.ToggleButton
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import io.golos.cyber_android.ui.common.extensions.parentActivity
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.screens.profile_followers.view.widgets.NoDataStub

@BindingAdapter("filter")
fun setFilterBinding(view: ToggleButton, dataSource: MutableLiveData<FollowersFilter>?) {
    dataSource?.let { source ->
        val viewValue = FollowersFilter.create(view.tag as String)

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

@BindingAdapter("noDataTitle")
fun setNoDataTextBinding(view: NoDataStub, dataSource: LiveData<Int>?) {
    dataSource?.let { source ->
        view.parentActivity?.let { activity ->
            source.observe(activity, Observer { view.setTitle(it) })
        }
    }
}
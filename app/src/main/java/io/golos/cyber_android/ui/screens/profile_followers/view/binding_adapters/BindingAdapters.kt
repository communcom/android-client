package io.golos.cyber_android.ui.screens.profile_followers.view.binding_adapters

import android.widget.ToggleButton
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.ui.common.extensions.parentActivity
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.common.widgets.lists.NoDataStub

@BindingAdapter("filterFollowers")
fun setFollowersFilterBinding(view: ToggleButton, dataSource: MutableLiveData<FollowersFilter>?) {
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
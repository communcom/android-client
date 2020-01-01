package io.golos.cyber_android.ui.screens.post_edit.view.binding_adapters

import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.ui.shared.extensions.parentActivity
import io.golos.cyber_android.ui.screens.post_edit.view.widgets.CommunityContainer
import io.golos.domain.dto.CommunityDomain

/**
 * One-way binding adapter for community on the post editor screen
 */
@BindingAdapter("community")
fun setCommunityToCommunityContainer(view: CommunityContainer, valueToBind: MutableLiveData<CommunityDomain?>?) {
    valueToBind?.let { value ->
        view.parentActivity?.let { activity ->
            value.observe(activity, Observer { observedCommunity ->
                view.setCommunity(observedCommunity)
            })
        }
    }
}

@BindingAdapter("select_community_enabled")
fun setSelectCommunityEnabled(view: CommunityContainer, valueToBind: MutableLiveData<Boolean>?) {
    valueToBind?.let { value ->
        view.parentActivity?.let { activity ->
            value.observe(activity, Observer { isEnabled ->
                view.setSelectCommunityEnabled(isEnabled)
            })
        }
    }
}
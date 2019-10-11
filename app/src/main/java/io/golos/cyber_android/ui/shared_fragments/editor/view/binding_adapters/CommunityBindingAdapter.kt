package io.golos.cyber_android.ui.shared_fragments.editor.view.binding_adapters

import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.ui.common.extensions.parentActivity
import io.golos.cyber_android.ui.shared_fragments.editor.view.widgets.CommunityContainer
import io.golos.domain.commun_entities.Community

/**
 * One-way binding adapter for community on the post editor screen
 */
@BindingAdapter("community")
fun setCommunityToCommunityContainer(view: CommunityContainer, valueToBind: MutableLiveData<Community?>?) {
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
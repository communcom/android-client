package io.golos.cyber_android.ui.screens.post_edit.fragment.view.binding_adapters

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.screens.post_edit.fragment.view.widgets.CommunityContainer
import io.golos.domain.dto.CommunityDomain

/**
 * One-way binding adapter for community on the post editor screen
 */
@BindingAdapter("community")
fun setCommunityToCommunityContainer(view: CommunityContainer, valueToBind: LiveData<CommunityDomain?>?) {
    valueToBind?.value?.let { view.setCommunity(it) }
}

@BindingAdapter("select_community_enabled")
fun setSelectCommunityEnabled(view: CommunityContainer, valueToBind: LiveData<Boolean>?) {
    valueToBind?.value?.let { view.setSelectCommunityEnabled(it) }
}
package io.golos.cyber_android.ui.screens.profile_communities.view.binding_adapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.profile_communities.dto.CommunitiesCount
import io.golos.utils.format.KiloCounterFormatter
import io.golos.utils.getFormattedString

@BindingAdapter("communitiesCount")
fun setKiloBinding(view: TextView, valueToBind: LiveData<CommunitiesCount>?) =
    valueToBind?.value?.let {
        view.text = view.context.resources.getFormattedString(
            R.string.communities_count,
            KiloCounterFormatter.format(it.communitiesSubscribedCount),
            KiloCounterFormatter.format(it.highlightCommunitiesCount))
        }
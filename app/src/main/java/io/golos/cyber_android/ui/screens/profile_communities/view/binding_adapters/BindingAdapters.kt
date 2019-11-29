package io.golos.cyber_android.ui.screens.profile_communities.view.binding_adapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.extensions.getFormattedString
import io.golos.cyber_android.ui.common.extensions.parentActivity
import io.golos.cyber_android.ui.common.formatters.counts.KiloCounterFormatter
import io.golos.cyber_android.ui.screens.profile_communities.dto.CommunitiesCount

@BindingAdapter("communitiesCount")
fun setKiloBinding(view: TextView, valueToBind: LiveData<CommunitiesCount>?) {
    valueToBind?.let { value ->
        view.parentActivity?.let { activity ->
            value.observe(activity, Observer {
                view.text = activity.resources.getFormattedString(
                    R.string.communities_count,
                    KiloCounterFormatter.format(it.communitiesSubscribedCount),
                    KiloCounterFormatter.format(it.highlightCommunitiesCount))
            })
        }
    }
}
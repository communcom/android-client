package io.golos.cyber_android.ui.shared_fragments.post.view.binding_adapters

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.ui.common.extensions.parentActivity
import io.golos.cyber_android.ui.shared_fragments.post.dto.PostHeader
import io.golos.cyber_android.ui.shared_fragments.post.view.widgets.PostPageHeader

/**
 * One-way binding adapter for post header
 */
@BindingAdapter("post")
fun setCommunityToCommunityContainer(view: PostPageHeader, valueToBind: LiveData<PostHeader>?) {
    valueToBind?.let { value ->
        view.parentActivity?.let { activity ->
            value.observe(activity, Observer { observedHeader ->
                view.setHeader(observedHeader)
            })
        }
    }
}

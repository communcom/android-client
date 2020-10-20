package io.golos.cyber_android.ui.screens.post_view.view.binding_adapters

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import io.golos.cyber_android.ui.screens.post_view.dto.PostHeader
import io.golos.cyber_android.ui.shared.widgets.post_comments.PostPageHeaderWidget

/**
 * One-way binding adapter for post header
 */
@BindingAdapter("post")
fun setCommunityToCommunityContainer(view: PostPageHeaderWidget, valueToBind: LiveData<PostHeader>?) {
    valueToBind?.value?.let { view.setHeader(it) }
}

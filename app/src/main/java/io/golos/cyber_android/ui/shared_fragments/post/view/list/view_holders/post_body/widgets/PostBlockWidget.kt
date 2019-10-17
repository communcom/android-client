package io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets

import io.golos.cyber_android.ui.shared_fragments.post.view_model.PostPageViewModelItemsClickProcessor
import io.golos.domain.post.post_dto.Block

interface PostBlockWidget<T: Block> {
    fun render(block: T)

    fun setOnClickProcessor(processor: PostPageViewModelItemsClickProcessor?) {}

    fun cancel()
}
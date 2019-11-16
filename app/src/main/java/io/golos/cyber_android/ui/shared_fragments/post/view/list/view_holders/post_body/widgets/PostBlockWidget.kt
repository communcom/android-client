package io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets

import io.golos.domain.use_cases.post.post_dto.Block

interface PostBlockWidget<T : Block, L: BasePostBlockWidgetListener> {
    fun render(block: T)

    fun setOnClickProcessor(processor: L?) {}

    fun release()
}
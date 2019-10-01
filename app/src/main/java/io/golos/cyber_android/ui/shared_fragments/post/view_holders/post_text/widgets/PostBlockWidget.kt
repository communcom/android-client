package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.widgets

import io.golos.domain.post.post_dto.Block

interface PostBlockWidget<T: Block> {
    fun render(block: T)

    fun cancel()
}
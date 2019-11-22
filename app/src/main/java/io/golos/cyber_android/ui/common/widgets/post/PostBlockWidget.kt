package io.golos.cyber_android.ui.common.widgets.post

import io.golos.domain.use_cases.post.post_dto.Block

interface PostBlockWidget<T : Block, L: BasePostBlockWidgetListener> {
    fun render(block: T)

    fun setOnClickProcessor(processor: L?) {}

    fun release()
}
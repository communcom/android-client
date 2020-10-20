package io.golos.cyber_android.ui.shared.widgets.post_comments

import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.Block

interface BlockWidget<T : Block, L: BasePostBlockWidgetListener> {
    fun render(block: T)

    fun setOnClickProcessor(processor: L?) {}

    fun release()
}
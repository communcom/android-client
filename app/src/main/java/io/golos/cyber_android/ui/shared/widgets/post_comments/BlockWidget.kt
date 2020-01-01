package io.golos.cyber_android.ui.shared.widgets.post_comments

import io.golos.domain.use_cases.post.post_dto.Block

interface BlockWidget<T : Block, L: BasePostBlockWidgetListener> {
    fun render(block: T)

    fun setOnClickProcessor(processor: L?) {}

    fun release()
}
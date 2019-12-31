package io.golos.cyber_android.ui.screens.post_view.helpers

import io.golos.domain.use_cases.post.post_dto.Block

interface CommentTextRenderer {
    fun render(post: List<Block>): List<CharSequence>
}
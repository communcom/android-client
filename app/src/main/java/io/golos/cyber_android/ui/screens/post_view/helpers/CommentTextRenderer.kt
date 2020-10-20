package io.golos.cyber_android.ui.screens.post_view.helpers

import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.Block

interface CommentTextRenderer {
    fun render(post: List<Block>): List<CharSequence>
}
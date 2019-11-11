package io.golos.cyber_android.ui.shared_fragments.post.helpers

import io.golos.domain.use_cases.post.post_dto.Block

interface CommentTextRenderer {
    fun render(post: List<Block>): List<CharSequence>
}
package io.golos.cyber_android.ui.shared.widgets.comment

import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.posts_parsing_rendering.post_metadata.editor_output.ControlMetadata

data class CommentContent(
    val contentId: ContentIdDomain?,
    val metadata: List<ControlMetadata>,
    val state: ContentState
)

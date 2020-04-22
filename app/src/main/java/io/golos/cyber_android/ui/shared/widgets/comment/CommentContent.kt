package io.golos.cyber_android.ui.shared.widgets.comment

import io.golos.cyber_android.ui.dto.ContentId
import io.golos.domain.posts_parsing_rendering.post_metadata.editor_output.ControlMetadata

data class CommentContent(
    val contentId: ContentId?,
    val metadata: List<ControlMetadata>,
    val state: ContentState
)

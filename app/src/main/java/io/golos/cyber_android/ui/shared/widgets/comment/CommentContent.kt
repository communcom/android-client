package io.golos.cyber_android.ui.shared.widgets.comment

import android.net.Uri
import io.golos.cyber_android.ui.dto.ContentId

data class CommentContent(
    val contentId: ContentId?,
    val message: String?,
    val imageUri: Uri?,
    val state: ContentState
)

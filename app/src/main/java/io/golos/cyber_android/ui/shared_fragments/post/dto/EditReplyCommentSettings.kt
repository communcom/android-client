package io.golos.cyber_android.ui.shared_fragments.post.dto

data class EditReplyCommentSettings(
    val oldText: List<CharSequence>,
    val newText: List<CharSequence>,
    val isInEditMode: Boolean
)

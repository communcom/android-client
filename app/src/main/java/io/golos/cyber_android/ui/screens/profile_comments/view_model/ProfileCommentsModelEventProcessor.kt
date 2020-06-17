package io.golos.cyber_android.ui.screens.profile_comments.view_model

import io.golos.cyber_android.ui.shared.widgets.post_comments.*
import io.golos.cyber_android.ui.dto.Comment
import io.golos.domain.dto.ContentIdDomain

interface ProfileCommentsModelEventProcessor :
    ProfileCommentsProgressEventProcessor,
    ProfileCommentsVoteListener,
    RichWidgetListener,
    EmbedWidgetListener,
    AttachmentWidgetListener,
    EmbedImageWidgetListener,
    EmbedWebsiteWidgetListener,
    EmbedVideoWidgetListener,
    ParagraphWidgetListener,
    CommentLongClickListener

interface ProfileCommentsProgressEventProcessor {
    fun onRetryLoadComments()
}

interface ProfileCommentsVoteListener {

    fun onCommentUpVoteClick(commentId: ContentIdDomain)

    fun onCommentDownVoteClick(commentId: ContentIdDomain)

}

interface CommentLongClickListener {

    fun onCommentLongClick(comment: Comment)

}
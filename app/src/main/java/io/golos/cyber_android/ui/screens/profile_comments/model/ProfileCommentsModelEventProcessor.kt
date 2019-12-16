package io.golos.cyber_android.ui.screens.profile_comments.model

import io.golos.cyber_android.ui.common.widgets.post_comments.*

interface ProfileCommentsModelEventProcessor :
    ProfileCommentsProgressEventProcessor,
    ProfileCommentsVoteListener,
    RichWidgetListener,
    EmbedWidgetListener,
    AttachmentWidgetListener,
    EmbedImageWidgetListener,
    EmbedWebsiteWidgetListener,
    EmbedVideoWidgetListener,
    ParagraphWidgetListener

interface ProfileCommentsProgressEventProcessor {
    fun onRetryLoadComments()
}

interface ProfileCommentsVoteListener {

    fun onCommentUpVoteClick(commentId: String)

    fun onCommentDownVoteClick(commentId: String)

}
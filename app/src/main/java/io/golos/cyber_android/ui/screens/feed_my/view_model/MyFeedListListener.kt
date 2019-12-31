package io.golos.cyber_android.ui.screens.feed_my.view_model

import io.golos.cyber_android.ui.common.widgets.post_comments.*
import io.golos.cyber_android.ui.dto.ContentId

interface MyFeedListListener :
    RichWidgetListener,
    EmbedWidgetListener,
    AttachmentWidgetListener,
    EmbedImageWidgetListener,
    EmbedWebsiteWidgetListener,
    EmbedVideoWidgetListener,
    ParagraphWidgetListener,
    PostCommentsListener,
    PostVotesListener,
    PostShareListener,
    MenuListener

interface PostCommentsListener {

    fun onCommentsClicked(postContentId: ContentId)
}

interface PostShareListener {

    fun onShareClicked(shareUrl: String)
}

interface PostVotesListener {

    fun onUpVoteClicked(contentId: ContentId)

    fun onDownVoteClicked(contentId: ContentId)
}
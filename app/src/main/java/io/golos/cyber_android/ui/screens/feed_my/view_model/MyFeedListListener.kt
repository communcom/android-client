package io.golos.cyber_android.ui.screens.feed_my.view_model

import io.golos.cyber_android.ui.shared.widgets.post_comments.*
import io.golos.domain.dto.ContentIdDomain

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
    MenuListener,
    BlockBodyListener,
    RewardListener

interface PostCommentsListener {

    fun onCommentsClicked(postContentId: ContentIdDomain)
}

interface PostShareListener {

    fun onShareClicked(shareUrl: String)
}

interface PostVotesListener {

    fun onUpVoteClicked(contentId: ContentIdDomain)

    fun onDownVoteClicked(contentId: ContentIdDomain)
}
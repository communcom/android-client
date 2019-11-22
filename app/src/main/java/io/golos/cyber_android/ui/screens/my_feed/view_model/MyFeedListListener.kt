package io.golos.cyber_android.ui.screens.my_feed.view_model

import io.golos.cyber_android.ui.common.widgets.post.*
import io.golos.cyber_android.ui.dto.Post

interface MyFeedListListener :
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

    fun onCommentsClicked(postContentId: Post.ContentId)
}

interface PostShareListener {

    fun onShareClicked(shareUrl: String)
}

interface PostVotesListener {

    fun onUpVoteClicked()

    fun onDownVoteClicked()
}
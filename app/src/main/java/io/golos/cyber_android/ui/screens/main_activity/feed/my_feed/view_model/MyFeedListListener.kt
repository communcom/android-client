package io.golos.cyber_android.ui.screens.main_activity.feed.my_feed.view_model

import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.shared_fragments.post.view.list.view_holders.post_body.widgets.*

interface MyFeedListListener :
    AttachmentWidgetListener,
    EmbedImageWidgetListener,
    EmbedWebsiteWidgetListener,
    EmbedVideoWidgetListener,
    ParagraphWidgetListener,
    PostCommentsListener,
    PostVotesListener

interface PostCommentsListener {

    fun onCommentsClicked(postContentId: Post.ContentId)
}

interface PostVotesListener {

    fun onUpVoteClicked()

    fun onDownVoteClicked()
}
package io.golos.cyber_android.ui.screens.post_view.view_model

import io.golos.cyber_android.ui.shared.widgets.post_comments.*
import io.golos.domain.use_cases.model.DiscussionIdModel

/**
 * Processing post items clicks
 */
interface PostPageViewModelListEventsProcessor :
    AttachmentWidgetListener,
    EmbedImageWidgetListener,
    EmbedWebsiteWidgetListener,
    EmbedVideoWidgetListener,
    ParagraphWidgetListener,
    PostCommentVoteListener,
    PostVoteListener,
    CommentsListener,
    RichWidgetListener,
    EmbedWidgetListener

interface PostCommentVoteListener : BasePostBlockWidgetListener {

    fun onCommentUpVoteClick(commentId: DiscussionIdModel)

    fun onCommentDownVoteClick(commentId: DiscussionIdModel)
}

interface PostVoteListener : BasePostBlockWidgetListener {

    fun onUpVoteClick()

    fun onDownVoteClick()
}

interface CommentsListener : BasePostBlockWidgetListener {

    fun onCommentsTitleMenuClick()

    fun onNextCommentsPageReached()

    fun onRetryLoadingFirstLevelCommentButtonClick()

    fun onCollapsedCommentsClick(parentCommentId: DiscussionIdModel)

    fun onRetryLoadingSecondLevelCommentButtonClick(parentCommentId: DiscussionIdModel)

    fun onCommentLongClick(commentId: DiscussionIdModel)

    fun startReplyToComment(commentToReplyId: DiscussionIdModel)
}
package io.golos.cyber_android.ui.screens.post_view.view_model

import io.golos.cyber_android.ui.dto.DonateType
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.screens.feed_my.view_model.PostShareListener
import io.golos.cyber_android.ui.shared.widgets.post_comments.*
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.UserBriefDomain

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
    EmbedWidgetListener,
    PostShareListener

interface PostCommentVoteListener : BasePostBlockWidgetListener {

    fun onCommentUpVoteClick(commentId: ContentIdDomain)

    fun onCommentDownVoteClick(commentId: ContentIdDomain)

    fun onCommentUnVoteClick(commentId: ContentIdDomain)

    fun onForbiddenClick()
}

interface PostVoteListener : BasePostBlockWidgetListener {
    fun onUpVoteClick()

    fun onDownVoteClick()
    fun onUnVoteClick()

    fun onDonateClick(donate: DonateType, contentId: ContentIdDomain, communityId: CommunityIdDomain, contentAuthor: UserBriefDomain)

    fun onDonatePopupClick(post: Post)
}

interface CommentsListener : BasePostBlockWidgetListener {
    fun onCommentsTitleMenuClick()

    fun onNextCommentsPageReached()

    fun onRetryLoadingFirstLevelCommentButtonClick()

    fun onCollapsedCommentsClick(parentCommentId: ContentIdDomain)

    fun onRetryLoadingSecondLevelCommentButtonClick(parentCommentId: ContentIdDomain)

    fun onCommentLongClick(commentId: ContentIdDomain)

    fun startReplyToComment(commentToReplyId: ContentIdDomain)

    fun onUserClicked(userId: String)
}
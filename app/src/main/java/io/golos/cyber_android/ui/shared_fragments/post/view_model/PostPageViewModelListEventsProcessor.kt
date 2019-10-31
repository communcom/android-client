package io.golos.cyber_android.ui.shared_fragments.post.view_model

import android.net.Uri
import io.golos.domain.interactors.model.DiscussionIdModel

/**
 * Processing post items clicks
 */
interface PostPageViewModelListEventsProcessor {
    fun onImageInPostClick(imageUri: Uri)

    fun onLinkInPostClick(link: Uri)

    fun onUserInPostClick(userName: String)

    fun onUpVoteClick()

    fun onDownVoteClick()

    fun onCommentUpVoteClick(commentId: DiscussionIdModel)

    fun onCommentDownVoteClick(commentId: DiscussionIdModel)

    fun onCommentsTitleMenuClick()

    fun onNextCommentsPageReached()

    fun onRetryLoadingFirstLevelCommentButtonClick()

    fun onCollapsedCommentsClick(parentCommentId: DiscussionIdModel)

    fun onRetryLoadingSecondLevelCommentButtonClick(parentCommentId: DiscussionIdModel)

    fun onCommentLongClick(commentId: DiscussionIdModel)

    fun startReplyToComment(commentToReplyId: DiscussionIdModel)
}
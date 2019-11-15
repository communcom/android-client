package io.golos.cyber_android.ui.shared_fragments.post.view_model

import android.net.Uri
import io.golos.domain.use_cases.model.DiscussionIdModel

/**
 * Processing post items clicks
 */
interface PostPageViewModelListEventsProcessor : PostLinkListener,
    PostImageListener,
    PostUserListener,
    PostCommentVoteListener,
    PostVoteListener,
    CommentsListener

interface PostLinkListener {

    fun onLinkInPostClick(link: Uri)
}

interface PostImageListener {

    fun onImageInPostClick(imageUri: Uri)
}

interface PostUserListener {

    fun onUserInPostClick(userName: String)
}

interface PostCommentVoteListener {

    fun onCommentUpVoteClick(commentId: DiscussionIdModel)

    fun onCommentDownVoteClick(commentId: DiscussionIdModel)
}

interface PostVoteListener {

    fun onUpVoteClick()

    fun onDownVoteClick()
}

interface CommentsListener {

    fun onCommentsTitleMenuClick()

    fun onNextCommentsPageReached()

    fun onRetryLoadingFirstLevelCommentButtonClick()

    fun onCollapsedCommentsClick(parentCommentId: DiscussionIdModel)

    fun onRetryLoadingSecondLevelCommentButtonClick(parentCommentId: DiscussionIdModel)

    fun onCommentLongClick(commentId: DiscussionIdModel)

    fun startReplyToComment(commentToReplyId: DiscussionIdModel)
}
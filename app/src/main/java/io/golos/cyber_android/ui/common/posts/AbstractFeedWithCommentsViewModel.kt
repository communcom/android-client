package io.golos.cyber_android.ui.common.posts

import io.golos.cyber_android.ui.common.AbstractFeedViewModel
import io.golos.cyber_android.utils.ValidationConstants
import io.golos.domain.entities.DiscussionEntity
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.feed.AbstractFeedUseCase
import io.golos.domain.interactors.model.CommentCreationRequestModel
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.DiscussionModel
import io.golos.domain.interactors.publish.DiscussionPosterUseCase
import io.golos.domain.interactors.sign.SignInUseCase
import io.golos.domain.requestmodel.FeedUpdateRequest

/**
 * Extends [AbstractFeedViewModel] with ability to create comments via [sendComment] method. Result of creation
 * can be listened by [discussionCreationLiveData]
 */
abstract class AbstractFeedWithCommentsViewModel<out R : FeedUpdateRequest, E: DiscussionEntity, M: DiscussionModel>(
    feedUseCase: AbstractFeedUseCase<out R, E, M>,
    voteUseCase: VoteUseCase,
    protected val posterUseCase: DiscussionPosterUseCase,
    signInUseCase: SignInUseCase
) :
    AbstractFeedViewModel<R, E, M>(feedUseCase, voteUseCase, signInUseCase, posterUseCase) {

    /**
     * Sends comment to discussion
     * @param discussion [DiscussionModel] from where [DiscussionIdModel] will be extracted
     * @param comment comment content
     */
    fun sendComment(discussion: DiscussionModel, comment: CharSequence) {
        sendComment(discussion.contentId, comment)
    }

    /**
     * Sends comment to discussion
     * @param id id of a parent discussion
     * @param comment comment content
     */
    fun sendComment(id: DiscussionIdModel, comment: CharSequence) {
        if (validateComment(comment)) {
            val postRequest = CommentCreationRequestModel(comment, id, emptyList())
            posterUseCase.createPostOrComment(postRequest)
        }
    }

    protected open fun validateComment(comment: CharSequence) =
        comment.isNotBlank() && comment.length <= ValidationConstants.MAX_POST_CONTENT_LENGTH
}
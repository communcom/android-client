package io.golos.cyber_android.ui.common.posts

import io.golos.cyber_android.ui.common.AbstractFeedViewModel
import io.golos.utils.PostConstants
import io.golos.domain.dto.DiscussionEntity
import io.golos.domain.use_cases.action.VoteUseCase
import io.golos.domain.use_cases.feed.AbstractFeedUseCase
import io.golos.domain.use_cases.model.CommentCreationRequestModel
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.use_cases.model.DiscussionModel
import io.golos.domain.use_cases.publish.DiscussionPosterUseCase
import io.golos.domain.use_cases.sign.SignInUseCase
import io.golos.domain.requestmodel.FeedUpdateRequest
import io.golos.domain.posts_parsing_rendering.mappers.comment_to_json.CommentToJsonMapper

/**
 * Extends [AbstractFeedViewModel] with ability to create comments via [sendComment] method. Result of creation
 * can be listened by [discussionCreationLiveData]
 */
abstract class AbstractFeedWithCommentsViewModel<out R : FeedUpdateRequest, E: DiscussionEntity, M: DiscussionModel>(
    feedUseCase: AbstractFeedUseCase<out R, E, M>,
    voteUseCase: VoteUseCase,
    protected val posterUseCase: DiscussionPosterUseCase,
    signInUseCase: SignInUseCase
) : AbstractFeedViewModel<R, E, M>(feedUseCase, voteUseCase, signInUseCase, posterUseCase) {

    /**
     * Sends comment to discussion
     * @param discussion [DiscussionModel] from where [DiscussionIdModel] will be extracted
     * @param comment comment contentBodyEntityList
     */
    fun sendComment(discussion: DiscussionModel, comment: CharSequence) {
        sendComment(discussion.contentId, comment)
    }

    /**
     * Sends comment to discussion
     * @param id id of a parent discussion
     * @param comment comment contentBodyEntityList
     */
    fun sendComment(id: DiscussionIdModel, comment: CharSequence) {
        if (validateComment(comment)) {
            val jsonComment = CommentToJsonMapper.mapTextToJson(comment.toString())

            val postRequest = CommentCreationRequestModel(jsonComment, id, emptyList())
            posterUseCase.createPostOrComment(postRequest)
        }
    }

    protected open fun validateComment(comment: CharSequence) =
        comment.isNotBlank() && comment.length <= io.golos.utils.PostConstants.MAX_POST_CONTENT_LENGTH
}
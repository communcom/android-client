package io.golos.cyber_android.ui.screens.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.posts.AbstractFeedWithCommentsViewModel
import io.golos.domain.entities.CommentEntity
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.feed.PostWithCommentUseCase
import io.golos.domain.interactors.model.CommentModel
import io.golos.domain.interactors.model.DiscussionIdModel
import io.golos.domain.interactors.model.PostModel
import io.golos.domain.interactors.publish.DiscussionPosterUseCase
import io.golos.domain.model.CommentFeedUpdateRequest
import io.golos.domain.model.VoteRequestModel

class PostPageViewModel(
    postWithCommentUseCase: PostWithCommentUseCase,
    voteUseCase: VoteUseCase,
    posterUseCase: DiscussionPosterUseCase
) : AbstractFeedWithCommentsViewModel<CommentFeedUpdateRequest, CommentEntity, CommentModel>(
    postWithCommentUseCase,
    voteUseCase,
    posterUseCase
) {

    enum class Visibility {
        VISIBLE, GONE
    }

    /**
     * [LiveData] for post content
     */
    val postLiveData = postWithCommentUseCase.getPostAsLiveData

    private val discussionToReplyLiveData = MutableLiveData<DiscussionIdModel?>(null)

    /**
     * [LiveData] currently selected discussion to reply
     */
    val getDiscussionToReplyLiveData = discussionToReplyLiveData as LiveData<DiscussionIdModel?>


    private val commentValidnessLiveData = MutableLiveData(false)

    /**
     * [LiveData] that indicates validness of the user comment
     */
    val getCommentValidnessLiveData = commentValidnessLiveData as LiveData<Boolean>


    private val commentInputVisibilityLiveData = MutableLiveData(Visibility.GONE)

    /**
     * [LiveData] that indicates visibility of the comment input widget
     */
    val getCommentInputVisibilityLiveData = commentInputVisibilityLiveData as LiveData<Visibility>

    /**
     * Sets [DiscussionIdModel] which should be used as a parent of a new comment (created via [sendComment])
     */
    fun setDiscussionToReply(id: DiscussionIdModel) {
        discussionToReplyLiveData.postValue(id)
    }

    /**
     * Clears currently selected [DiscussionIdModel] to reply
     */
    fun clearDiscussionToReply() {
        discussionToReplyLiveData.postValue(null)
    }

    /**
     * Sends comment to post associated with this view model. Method checks if there is selected [DiscussionIdModel] to reply
     * and send it there or send it as a top level comment
     */
    fun sendComment(text: CharSequence) {
        if (discussionToReplyLiveData.value == null) {
            postLiveData.value?.let {
                sendComment(it, text)
            }
        } else {
            discussionToReplyLiveData.value?.let {
                sendComment(it, text)
            }
        }
    }

    fun onPostDownvote() {
        postLiveData.value?.let {
            val power = if (!it.votes.hasDownVote) (-10_000).toShort() else 0.toShort()
            voteForPost(power, it)
        }
    }

    fun onPostUpvote() {
        postLiveData.value?.let {
            val power = if (!it.votes.hasUpVote) 10_000.toShort() else 0.toShort()
            voteForPost(power, it)
        }
    }

    override fun vote(power: Short, discussionModel: CommentModel) {
        if (!discussionModel.votes.hasUpVoteProgress
            && !discussionModel.votes.hasDownVotingProgress
            && !discussionModel.votes.hasVoteCancelProgress
        ) {
            val request = VoteRequestModel.VoteForComentRequest(power, discussionModel.contentId)
            vote(request, discussionModel.contentId)
        }
    }


    private fun voteForPost(power: Short, discussionModel: PostModel) {
        if (!discussionModel.votes.hasUpVoteProgress
            && !discussionModel.votes.hasDownVotingProgress
            && !discussionModel.votes.hasVoteCancelProgress
        ) {
            val request = VoteRequestModel.VoteForPostRequest(power, discussionModel.contentId)
            vote(request, discussionModel.contentId)
        }
    }

    private var currentCommentText: CharSequence = ""

    fun onCommentChanged(text: CharSequence) {
        currentCommentText = text
        commentValidnessLiveData.postValue(validateComment(text))
    }

    override fun validateComment(comment: CharSequence): Boolean {
        return (super.validateComment(comment)
                && (discussionToReplyLiveData.value == null
                || comment.length > discussionToReplyLiveData.value!!.userId.length + 4))
    }

    fun setCommentInputVisibility(visibility: Visibility) {
        commentInputVisibilityLiveData.postValue(visibility)
    }
}
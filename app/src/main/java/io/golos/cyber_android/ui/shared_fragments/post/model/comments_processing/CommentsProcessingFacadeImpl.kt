package io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing

import dagger.Lazy
import io.golos.cyber_android.ui.shared_fragments.post.dto.post_list_items.CommentListItemState
import io.golos.cyber_android.ui.shared_fragments.post.helpers.CommentTextRenderer
import io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.loaders.first_level.FirstLevelLoader
import io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.loaders.first_level.FirstLevelLoaderImpl
import io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.loaders.second_level.SecondLevelLoader
import io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.loaders.second_level.SecondLevelLoaderImpl
import io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.comments_storage.CommentsStorage
import io.golos.cyber_android.ui.shared_fragments.post.model.post_list_data_source.PostListDataSourceComments
import io.golos.cyber_android.ui.shared_fragments.post.model.voting.CommentVotingMachineImpl
import io.golos.cyber_android.ui.shared_fragments.post.model.voting.VotingEvent
import io.golos.cyber_android.ui.shared_fragments.post.model.voting.VotingMachine
import io.golos.data.api.discussions.DiscussionsApi
import io.golos.data.repositories.current_user_repository.CurrentUserRepository
import io.golos.data.repositories.discussion.DiscussionRepository
import io.golos.data.repositories.vote.VoteRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.mappers.new_mappers.CommentToModelMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class CommentsProcessingFacadeImpl
@Inject
constructor(
    private val postToProcess: DiscussionIdModel,
    private val postListDataSource: PostListDataSourceComments,
    private val discussionsApi: DiscussionsApi,
    private val discussionRepository: DiscussionRepository,
    private val dispatchersProvider: DispatchersProvider,
    private val commentToModelMapper: CommentToModelMapper,
    private val commentsStorage: Lazy<CommentsStorage>,
    private val currentUserRepository: CurrentUserRepository,
    private val commentTextRenderer: CommentTextRenderer,
    private val voteRepository: VoteRepository
): CommentsProcessingFacade {

    override val pageSize: Int
        get() = 20

    private val secondLevelLoaders = mutableMapOf<DiscussionIdModel, SecondLevelLoader>()

    private val firstLevelCommentsLoader: FirstLevelLoader by lazy {
        FirstLevelLoaderImpl(
            postToProcess,
            postListDataSource,
            discussionsApi,
            dispatchersProvider,
            commentToModelMapper,
            pageSize,
            commentsStorage.get(),
            currentUserRepository
        )
    }

    private val voteMachines = mutableMapOf<DiscussionIdModel, VotingMachine>()

    override suspend fun loadStartFirstLevelPage() = firstLevelCommentsLoader.loadStartPage()

    override suspend fun loadNextFirstLevelPageByScroll() = firstLevelCommentsLoader.loadNextPageByScroll()

    override suspend fun retryLoadFirstLevelPage() = firstLevelCommentsLoader.retryLoadPage()

    override suspend fun loadNextSecondLevelPage(parentCommentId: DiscussionIdModel) {
        getSecondLevelLoader(parentCommentId).loadNextPage()
    }

    override suspend fun retryLoadSecondLevelPage(parentCommentId: DiscussionIdModel) =
        getSecondLevelLoader(parentCommentId).retryLoadPage()

    override suspend fun sendComment(commentText: String, postHasComments: Boolean) {
        if(!postHasComments) {
            postListDataSource.addCommentsHeader()
        }
        postListDataSource.addLoadingForNewComment()

        try {
            val commentModel = withContext(dispatchersProvider.ioDispatcher) {
                delay(1000)
                discussionRepository.createCommentForPost(postToProcess, commentText)
            }
            postListDataSource.addNewComment(commentModel)
            commentsStorage.get().addPostedComment(commentModel)
        } catch(ex: Exception) {
            Timber.e(ex)
            postListDataSource.removeLoadingForNewComment()
            throw ex
        }
    }

    override suspend fun deleteComment(commentId: DiscussionIdModel, isSingleComment: Boolean) {
        postListDataSource.updateCommentState(commentId, CommentListItemState.PROCESSING)

        try {
            withContext(dispatchersProvider.ioDispatcher) {
                delay(1000)

                discussionRepository.deleteComment(commentId)
            }

            postListDataSource.deleteComment(commentId)

            if(isSingleComment) {
                postListDataSource.deleteCommentsHeader()
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            postListDataSource.updateCommentState(commentId, CommentListItemState.ERROR)
            throw ex
        }
    }

    override fun getCommentText(commentId: DiscussionIdModel): List<CharSequence> =
        commentTextRenderer.render(commentsStorage.get().getComment(commentId)!!.content.body.postBlock.content)

    override suspend fun updateCommentText(commentId: DiscussionIdModel, newCommentText: String) {
        postListDataSource.updateCommentState(commentId, CommentListItemState.PROCESSING)

        val oldComment = commentsStorage.get().getComment(commentId)!!

        try {
            val newComment = withContext(dispatchersProvider.ioDispatcher) {
                delay(1000)

                discussionRepository.updateCommentText(oldComment, newCommentText)
            }
            postListDataSource.updateCommentText(newComment)
            commentsStorage.get().updateComment(newComment)
        } catch(ex: Exception) {
            Timber.e(ex)
            postListDataSource.updateCommentState(commentId, CommentListItemState.ERROR)
            throw ex
        }
    }

    override suspend fun replyToComment(repliedCommentId: DiscussionIdModel, newCommentText: String) {
        postListDataSource.addLoadingForRepliedComment(repliedCommentId)

        try {
            val commentModel = withContext(dispatchersProvider.ioDispatcher) {
                delay(1000)
                discussionRepository.createReplyComment(repliedCommentId, newCommentText)
            }

            val repliedComment = commentsStorage.get().getComment(repliedCommentId)!!

            postListDataSource.addReplyComment(
                repliedCommentId,
                repliedComment.author,
                repliedComment.content.commentLevel,
                commentModel)

            commentsStorage.get().addPostedComment(commentModel)
        } catch (ex: Exception) {
            Timber.e(ex)
            postListDataSource.removeLoadingForRepliedComment(repliedCommentId)
            throw ex
        }
    }

    override suspend fun vote(commentId: DiscussionIdModel, isUpVote: Boolean) {
        val comment = commentsStorage.get().getComment(commentId)!!
        val oldVotes = comment.votes
        val newVotes = getVoteMachine(commentId).processEvent(if(isUpVote) VotingEvent.UP_VOTE else VotingEvent.DOWN_VOTE, oldVotes)
        commentsStorage.get().updateComment(comment.copy(votes = newVotes))
    }

    private fun getSecondLevelLoader(parentCommentId: DiscussionIdModel): SecondLevelLoader {
        return secondLevelLoaders[parentCommentId]
            ?: SecondLevelLoaderImpl(
                parentCommentId,
                firstLevelCommentsLoader.getLoadedComment(parentCommentId).childTotal.toInt(),
                postListDataSource,
                discussionsApi,
                dispatchersProvider, commentToModelMapper,
                pageSize,
                commentsStorage.get(),
                currentUserRepository
            ).also {
                secondLevelLoaders[parentCommentId] = it
            }
    }

    private fun getVoteMachine(commentId: DiscussionIdModel) : VotingMachine {
        return voteMachines[commentId]
            ?: CommentVotingMachineImpl(
                dispatchersProvider,
                voteRepository,
                postListDataSource,
                commentId)
    }
}
package io.golos.cyber_android.ui.screens.post_view.model.comments_processing

import dagger.Lazy
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.mappers.mapToContentIdDomain
import io.golos.cyber_android.ui.screens.post_view.dto.post_list_items.CommentListItemState
import io.golos.cyber_android.ui.screens.post_view.helpers.CommentTextRenderer
import io.golos.cyber_android.ui.screens.post_view.model.comments_processing.comments_storage.CommentsStorage
import io.golos.cyber_android.ui.screens.post_view.model.comments_processing.loaders.first_level.FirstLevelLoader
import io.golos.cyber_android.ui.screens.post_view.model.comments_processing.loaders.first_level.FirstLevelLoaderImpl
import io.golos.cyber_android.ui.screens.post_view.model.comments_processing.loaders.second_level.SecondLevelLoader
import io.golos.cyber_android.ui.screens.post_view.model.comments_processing.loaders.second_level.SecondLevelLoaderImpl
import io.golos.cyber_android.ui.screens.post_view.model.post_list_data_source.PostListDataSourceComments
import io.golos.cyber_android.ui.screens.post_view.model.voting.CommentVotingMachineImpl
import io.golos.cyber_android.ui.screens.post_view.model.voting.VotingEvent
import io.golos.cyber_android.ui.screens.post_view.model.voting.VotingMachine
import io.golos.data.repositories.vote.VoteRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.mappers.new_mappers.CommentToModelMapper
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.repositories.DiscussionRepository
import io.golos.domain.use_cases.model.CommentModel
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.use_cases.post.post_dto.AttachmentsBlock
import io.golos.domain.use_cases.post.post_dto.Block
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class CommentsProcessingFacadeImpl
@Inject
constructor(
    private val postContentId: ContentId,
    private val postListDataSource: PostListDataSourceComments,
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
            postContentId.mapToContentIdDomain(),
            postListDataSource,
            discussionRepository,
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

    override suspend fun sendComment(content: List<Block>, attachments: AttachmentsBlock?) {
        try {
            postListDataSource.addLoadingForNewComment()
            val commentDomain = withContext(dispatchersProvider.ioDispatcher) {
                discussionRepository.sendComment(postContentId.mapToContentIdDomain(), content, attachments)
            }
            val commentModel = commentToModelMapper.map(commentDomain)
            postListDataSource.addNewComment(commentModel)
            postListDataSource.removeEmptyCommentsStub()
            postListDataSource.removeLoadingForNewComment()
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
        commentTextRenderer.render(commentsStorage.get().getComment(commentId)!!.body!!.content)

    override fun getComment(commentId: ContentId): CommentModel? {
        val discussion = DiscussionIdModel(commentId.userId, Permlink(commentId.permlink))
        val comment = commentsStorage.get().getComment(discussion)
        return comment
    }

    override fun getComment(discussionIdModel: DiscussionIdModel): CommentModel? = commentsStorage.get().getComment(discussionIdModel)

    override suspend fun updateCommentText(commentId: DiscussionIdModel, content: List<Block>, attachments: AttachmentsBlock?) {
        postListDataSource.updateCommentState(commentId, CommentListItemState.PROCESSING)

        val oldComment = commentsStorage.get().getComment(commentId)!!

        try {
            /*val newComment = withContext(dispatchersProvider.ioDispatcher) {
                discussionRepository.updateCommentText(oldComment, newCommentText)
            }
            postListDataSource.updateCommentText(newComment)
            commentsStorage.get().updateComment(newComment)*/
        } catch(ex: Exception) {
            Timber.e(ex)
            postListDataSource.updateCommentState(commentId, CommentListItemState.ERROR)
            throw ex
        }
    }

    override suspend fun replyToComment(repliedCommentId: DiscussionIdModel, content: List<Block>, attachments: AttachmentsBlock?) {
        postListDataSource.addLoadingForRepliedComment(repliedCommentId)

        try {
            /*val commentModel = withContext(dispatchersProvider.ioDispatcher) {
                discussionRepository.createReplyComment(repliedCommentId, postContentId.mapToContentIdDomain(), newCommentText)
            }

            val repliedComment = commentsStorage.get().getComment(repliedCommentId)!!

            postListDataSource.addReplyComment(
                repliedCommentId,
                repliedComment.author,
                repliedComment.content.commentLevel,
                commentModel)

            commentsStorage.get().addPostedComment(commentModel)*/
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
                postContentId.mapToContentIdDomain(),
                parentCommentId,
                firstLevelCommentsLoader.getLoadedComment(parentCommentId).childTotal.toInt(),
                postListDataSource,
                discussionRepository,
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
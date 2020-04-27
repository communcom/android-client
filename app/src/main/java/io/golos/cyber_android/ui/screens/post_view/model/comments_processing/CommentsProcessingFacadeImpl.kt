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
import io.golos.cyber_android.ui.screens.post_view.model.voting.comment.CommentVotingUseCase
import io.golos.cyber_android.ui.screens.post_view.model.voting.comment.CommentVotingUseCaseImpl
import io.golos.domain.DispatchersProvider
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.*
import io.golos.domain.mappers.new_mappers.CommentToModelMapper
import io.golos.domain.posts_parsing_rendering.PostGlobalConstants
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.JsonToDtoMapper
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.*
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.repositories.DiscussionRepository
import io.golos.domain.use_cases.model.CommentModel
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.utils.id.IdUtil
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
    private val commentTextRenderer: CommentTextRenderer
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

    private val voteMachines = mutableMapOf<DiscussionIdModel, CommentVotingUseCase>()

    override suspend fun loadStartFirstLevelPage() = firstLevelCommentsLoader.loadStartPage()

    override suspend fun loadNextFirstLevelPageByScroll() = firstLevelCommentsLoader.loadNextPageByScroll()

    override suspend fun retryLoadFirstLevelPage() = firstLevelCommentsLoader.retryLoadPage()

    override suspend fun loadNextSecondLevelPage(parentCommentId: DiscussionIdModel) {
        getSecondLevelLoader(parentCommentId).loadNextPage()
    }

    override suspend fun retryLoadSecondLevelPage(parentCommentId: DiscussionIdModel) =
        getSecondLevelLoader(parentCommentId).retryLoadPage()

    override suspend fun sendComment(jsonBody: String) {
        try {
            postListDataSource.addLoadingForNewComment()

            val commentDomain = withContext(dispatchersProvider.ioDispatcher) {
                discussionRepository.sendComment(postContentId.mapToContentIdDomain(), jsonBody)
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
                discussionRepository.deleteComment(commentId.permlink.value, postContentId.communityId)
            }
            postListDataSource.deleteComment(commentId)
            if(postListDataSource.isNotComments()){
                postListDataSource.addEmptyCommentsStub()
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

    override suspend fun updateComment(commentId: DiscussionIdModel, jsonBody: String) {
        postListDataSource.updateCommentState(commentId, CommentListItemState.PROCESSING)

        val oldComment = commentsStorage.get().getComment(commentId)!!
        val contentId = ContentIdDomain(postContentId.communityId, commentId.permlink.value, commentId.userId)
        val authorDomain = AuthorDomain(currentUserRepository.userAvatarUrl, currentUserRepository.userId.userId, currentUserRepository.userName)
        val votesModel = oldComment.votes
        val votesDomain = VotesDomain(votesModel.downCount, votesModel.upCount, votesModel.hasUpVote, votesModel.hasDownVote)

        val parentCommentDomain = if (oldComment.commentLevel == 0) {
            ParentCommentDomain(null, postContentId.mapToContentIdDomain())
        } else {
            val parentContentId =
                ContentIdDomain(postContentId.communityId, oldComment.parentId!!.permlink.value, oldComment.parentId!!.userId)
            ParentCommentDomain(parentContentId, null)
        }
        val commentDomain = CommentDomain(
            contentId = contentId,
            author = authorDomain,
            votes = votesDomain,
            body = null,
            jsonBody = jsonBody,
            childCommentsCount = oldComment.childTotal.toInt(),
            community = CommunityDomain(postContentId.communityId, null, "", null, null, 0, 0, false),
            meta = MetaDomain(oldComment.meta.time),
            parent = parentCommentDomain,
            type = "comment",
            isDeleted = false,
            isMyComment = true,
            commentLevel = oldComment.commentLevel
        )
        try {
            withContext(dispatchersProvider.ioDispatcher) {
                discussionRepository.updateComment(commentDomain)
            }
            val newComment = oldComment.copy(
                body = JsonToDtoMapper().map(jsonBody)
            )
            postListDataSource.updateComment(newComment)
            commentsStorage.get().updateComment(newComment)
        } catch(ex: Exception) {
            Timber.e(ex)
            postListDataSource.updateCommentState(commentId, CommentListItemState.ERROR)
            throw ex
        }
    }

    override suspend fun replyToComment(repliedCommentId: DiscussionIdModel, jsonBody: String) {
        postListDataSource.addLoadingForRepliedComment(repliedCommentId)

        try {
            val commentDomain = withContext(dispatchersProvider.ioDispatcher) {
                val parentContentId =
                    ContentIdDomain(postContentId.communityId, repliedCommentId.permlink.value, repliedCommentId.userId)
                discussionRepository.replyOnComment(parentContentId, jsonBody)
            }

            val commentModel = commentToModelMapper.map(commentDomain)
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

    override suspend fun vote(communityId: CommunityIdDomain, commentId: DiscussionIdModel, isUpVote: Boolean) {
        val oldComment = commentsStorage.get().getComment(commentId)!!

        val votingUseCase = getVoteUseCase(commentId)

        val newComment = if(isUpVote) {
            votingUseCase.upVote(oldComment, communityId, commentId.userId, commentId.permlink.value)
        } else {
            votingUseCase.downVote(oldComment, communityId, commentId.userId, commentId.permlink.value)
        }

        commentsStorage.get().updateComment(oldComment.copy(votes = newComment.votes))
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

    private fun getVoteUseCase(commentId: DiscussionIdModel) : CommentVotingUseCase {
        return voteMachines[commentId]
            ?: CommentVotingUseCaseImpl(
                dispatchersProvider,
                discussionRepository,
                postListDataSource)
    }
}
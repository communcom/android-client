package io.golos.cyber_android.ui.screens.post_view.model.comments_processing

import dagger.Lazy
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
import io.golos.domain.dto.*
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.JsonToDtoMapper
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.repositories.DiscussionRepository
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class CommentsProcessingFacadeImpl
@Inject
constructor(
    private val postContentId: ContentIdDomain,
    private val postListDataSource: PostListDataSourceComments,
    private val discussionRepository: DiscussionRepository,
    private val dispatchersProvider: DispatchersProvider,
    private val commentsStorage: Lazy<CommentsStorage>,
    private val currentUserRepository: CurrentUserRepository,
    private val commentTextRenderer: CommentTextRenderer
): CommentsProcessingFacade {

    override val pageSize: Int
        get() = 20

    private val secondLevelLoaders = mutableMapOf<ContentIdDomain, SecondLevelLoader>()

    private val firstLevelCommentsLoader: FirstLevelLoader by lazy {
        FirstLevelLoaderImpl(
            postContentId,
            postListDataSource,
            discussionRepository,
            dispatchersProvider,
            pageSize,
            commentsStorage.get()
        )
    }

    private val voteMachines = mutableMapOf<ContentIdDomain, CommentVotingUseCase>()

    override suspend fun loadStartFirstLevelPage() = firstLevelCommentsLoader.loadStartPage()

    override suspend fun loadNextFirstLevelPageByScroll() = firstLevelCommentsLoader.loadNextPageByScroll()

    override suspend fun retryLoadFirstLevelPage() = firstLevelCommentsLoader.retryLoadPage()

    override suspend fun loadNextSecondLevelPage(parentCommentId: ContentIdDomain) {
        getSecondLevelLoader(parentCommentId).loadNextPage()
    }

    override suspend fun retryLoadSecondLevelPage(parentCommentId: ContentIdDomain) =
        getSecondLevelLoader(parentCommentId).retryLoadPage()

    override suspend fun sendComment(jsonBody: String) {
        try {
            postListDataSource.addLoadingForNewComment()

            val commentDomain = withContext(dispatchersProvider.ioDispatcher) {
                discussionRepository.sendComment(postContentId, jsonBody)
            }

            postListDataSource.addNewComment(commentDomain.copy())
            postListDataSource.removeEmptyCommentsStub()
            postListDataSource.removeLoadingForNewComment()
            commentsStorage.get().addPostedComment(commentDomain.copy())
        } catch(ex: Exception) {
            Timber.e(ex)
            postListDataSource.removeLoadingForNewComment()
            throw ex
        }
    }

    override suspend fun deleteComment(commentId: ContentIdDomain, isSingleComment: Boolean) {
        postListDataSource.updateCommentState(commentId, CommentListItemState.PROCESSING)

        try {
            withContext(dispatchersProvider.ioDispatcher) {
                discussionRepository.deleteComment(commentId.permlink, postContentId.communityId)
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

    override fun getCommentText(commentId: ContentIdDomain): List<CharSequence> =
        commentTextRenderer.render(commentsStorage.get().getComment(commentId)!!.body!!.content)

    override fun getComment(commentId: ContentIdDomain): CommentDomain? = commentsStorage.get().getComment(commentId)

    override suspend fun updateComment(commentId: ContentIdDomain, jsonBody: String) {
        postListDataSource.updateCommentState(commentId, CommentListItemState.PROCESSING)

        val oldComment = commentsStorage.get().getComment(commentId)!!
        val contentId = ContentIdDomain(postContentId.communityId, commentId.permlink, commentId.userId)
        val authorDomain = UserBriefDomain(currentUserRepository.userAvatarUrl, currentUserRepository.userId, currentUserRepository.userName)
        val votesModel = oldComment.votes
        val votesDomain = VotesDomain(votesModel.downCount, votesModel.upCount, votesModel.hasUpVote, votesModel.hasDownVote)

        val parentCommentDomain = if (oldComment.commentLevel == 0) {
            ParentCommentDomain(null, postContentId)
        } else {
            val parentContentId =
                ContentIdDomain(
                    postContentId.communityId,
                    oldComment.parent.comment!!.permlink,
                    UserIdDomain(oldComment.parent.comment!!.userId.userId)
                )
            ParentCommentDomain(parentContentId, null)
        }
        val commentDomain = CommentDomain(
            contentId = contentId,
            author = authorDomain,
            votes = votesDomain,
            body = null,
            jsonBody = jsonBody,
            childCommentsCount = oldComment.childCommentsCount,
            community = CommunityDomain(postContentId.communityId, null, "", null, null, 0, 0, false),
            meta = oldComment.meta,
            parent = parentCommentDomain,
            type = "comment",
            isDeleted = false,
            isMyComment = true,
            commentLevel = oldComment.commentLevel,
            donations = oldComment.donations
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

    override suspend fun replyToComment(repliedCommentId: ContentIdDomain, jsonBody: String) {
        postListDataSource.addLoadingForRepliedComment(repliedCommentId)

        try {
            val commentDomain = withContext(dispatchersProvider.ioDispatcher) {
                val parentContentId =
                    ContentIdDomain(postContentId.communityId, repliedCommentId.permlink, repliedCommentId.userId)
                discussionRepository.replyOnComment(parentContentId, jsonBody)
            }

            val repliedComment = commentsStorage.get().getComment(repliedCommentId)!!

            postListDataSource.addReplyComment(
                repliedCommentId,
                repliedComment.author,
                repliedComment.commentLevel,
                commentDomain)

            commentsStorage.get().addPostedComment(commentDomain.copy())
        } catch (ex: Exception) {
            Timber.e(ex)
            postListDataSource.removeLoadingForRepliedComment(repliedCommentId)
            throw ex
        }
    }

    override suspend fun vote(communityId: CommunityIdDomain, commentId: ContentIdDomain, isUpVote: Boolean) {
        val oldComment = commentsStorage.get().getComment(commentId)!!

        val votingUseCase = getVoteUseCase(commentId)

        val newComment = if(isUpVote) {
            votingUseCase.upVote(oldComment, communityId, commentId.userId, commentId.permlink)
        } else {
            votingUseCase.downVote(oldComment, communityId, commentId.userId, commentId.permlink)
        }

        commentsStorage.get().updateComment(oldComment.copy(votes = newComment.votes))
    }

    private fun getSecondLevelLoader(parentCommentId: ContentIdDomain): SecondLevelLoader {
        return secondLevelLoaders[parentCommentId]
            ?: SecondLevelLoaderImpl(
                postContentId,
                parentCommentId,
                firstLevelCommentsLoader.getLoadedComment(parentCommentId).childCommentsCount,
                postListDataSource,
                discussionRepository,
                dispatchersProvider,
                pageSize,
                commentsStorage.get()
            ).also {
                secondLevelLoaders[parentCommentId] = it
            }
    }

    private fun getVoteUseCase(commentId: ContentIdDomain) : CommentVotingUseCase {
        return voteMachines[commentId]
            ?: CommentVotingUseCaseImpl(
                dispatchersProvider,
                discussionRepository,
                postListDataSource)
    }
}
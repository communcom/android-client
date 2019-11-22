package io.golos.cyber_android.ui.shared_fragments.post.model

import androidx.lifecycle.LiveData
import dagger.Lazy
import io.golos.commun4j.utils.toCyberName
import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.cyber_android.ui.shared_fragments.post.dto.PostHeader
import io.golos.cyber_android.ui.shared_fragments.post.dto.SortingType
import io.golos.cyber_android.ui.shared_fragments.post.model.comments_processing.CommentsProcessingFacade
import io.golos.cyber_android.ui.shared_fragments.post.model.post_list_data_source.PostListDataSource
import io.golos.cyber_android.ui.shared_fragments.post.model.voting.VotingEvent
import io.golos.cyber_android.ui.shared_fragments.post.model.voting.VotingMachine
import io.golos.domain.DispatchersProvider
import io.golos.domain.api.AuthApi
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.repositories.DiscussionRepository
import io.golos.domain.use_cases.community.SubscribeToCommunityUseCase
import io.golos.domain.use_cases.community.UnsubscribeToCommunityUseCase
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.use_cases.model.PostModel
import io.golos.domain.use_cases.post.post_dto.PostMetadata
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PostPageModelImpl
@Inject
constructor(
    private val postToProcess: DiscussionIdModel,
    private val dispatchersProvider: DispatchersProvider,
    private val discussionRepository: DiscussionRepository,
    private val currentUserRepository: CurrentUserRepositoryRead,
    private val authApi: Lazy<AuthApi>,
    private val postListDataSource: PostListDataSource,
    private val postVoting: Lazy<VotingMachine>,
    private val commentsProcessing: CommentsProcessingFacade,
    private val subscribeToCommunityUseCase: SubscribeToCommunityUseCase,
    private val unsubscribeToCommunityUseCase: UnsubscribeToCommunityUseCase
) : ModelBaseImpl(),
    PostPageModel,
    SubscribeToCommunityUseCase by subscribeToCommunityUseCase,
    UnsubscribeToCommunityUseCase by unsubscribeToCommunityUseCase{

    private lateinit var postModel: PostModel

    override val post: LiveData<List<VersionedListItem>> = postListDataSource.post

    override val postId: DiscussionIdModel
        get() = postModel.contentId

    override val postMetadata: PostMetadata
        get() = postModel.content.body.postBlock.metadata

    override val commentsPageSize: Int
        get() = commentsProcessing.pageSize

    override suspend fun loadPost() =
        withContext(dispatchersProvider.ioDispatcher) {
            postModel = discussionRepository.getPost(postToProcess.userId.toCyberName(), postToProcess.permlink)

            postListDataSource.createOrUpdatePostData(postModel)
        }

    override fun getPostMenu(): PostMenu {
        return PostMenu(
            communityId = postModel.community.id.id,
            communityName = postModel.community.name,
            communityAvatarUrl = postModel.community.avatarUrl,
            contentId = Post.ContentId(
                communityId = postModel.community.id.id,
                permlink = postId.permlink.value,
                userId = currentUserRepository.userId
            ),
            creationTime = postModel.meta.time,
            authorUsername = postModel.author.username,
            authorUserId = postModel.author.userId.userId,
            shareUrl = postModel.shareUrl,
            isMyPost = currentUserRepository.userId == postToProcess.userId,
            isSubscribed = postModel.community.isSubscribed,
            permlink = postId.permlink.value
        )
    }

    override fun getPostHeader(): PostHeader =
        PostHeader(
            postModel.community.name,
            postModel.community.avatarUrl,
            postModel.meta.time,

            postModel.author.username,
            postModel.author.userId.userId,

            false,
            postModel.author.userId.userId == currentUserRepository.userId,
            postModel.community.isSubscribed
        )

    override suspend fun addToFavorite(permlink: String) {
        delay(100)
    }

    override suspend fun removeFromFavorite(permlink: String) {
        delay(100)
    }

    override suspend fun getUserId(userName: String): String =
        withContext(dispatchersProvider.ioDispatcher) {
            authApi.get().getUserProfile(userName).userId.name
        }

    override suspend fun deletePost() =
        withContext(dispatchersProvider.ioDispatcher) {
            delay(500)
            discussionRepository.deletePost(postId)
        }

    override suspend fun voteForPost(isUpVote: Boolean) {
        val newVotesModel =
            postVoting.get().processEvent(if (isUpVote) VotingEvent.UP_VOTE else VotingEvent.DOWN_VOTE, postModel.votes)
        postModel = postModel.copy(votes = newVotesModel)
    }

    override suspend fun voteForComment(commentId: DiscussionIdModel, isUpVote: Boolean) =
        commentsProcessing.vote(commentId, isUpVote)

    override suspend fun updateCommentsSorting(sortingType: SortingType) = postListDataSource.updateCommentsSorting(sortingType)

    override suspend fun loadStartFirstLevelCommentsPage() = commentsProcessing.loadStartFirstLevelPage()

    override suspend fun loadNextFirstLevelCommentsPage() = commentsProcessing.loadNextFirstLevelPageByScroll()

    override suspend fun retryLoadingFirstLevelCommentsPage() = commentsProcessing.retryLoadFirstLevelPage()

    override suspend fun loadNextSecondLevelCommentsPage(parentCommentId: DiscussionIdModel) =
        commentsProcessing.loadNextSecondLevelPage(parentCommentId)

    override suspend fun retryLoadingSecondLevelCommentsPage(parentCommentId: DiscussionIdModel) =
        commentsProcessing.retryLoadSecondLevelPage(parentCommentId)

    override suspend fun sendComment(commentText: String) {
        val totalComments = postModel.comments.count

        commentsProcessing.sendComment(commentText, totalComments > 0)
        postModel = postModel.copy(comments = postModel.comments.copy(count = totalComments + 1))
    }

    override suspend fun deleteComment(commentId: DiscussionIdModel) {
        val totalComments = postModel.comments.count

        commentsProcessing.deleteComment(commentId, totalComments == 1L)
        postModel = postModel.copy(comments = postModel.comments.copy(count = totalComments - 1))
    }

    override fun getCommentText(commentId: DiscussionIdModel): List<CharSequence> =
        commentsProcessing.getCommentText(commentId)

    override suspend fun updateCommentText(commentId: DiscussionIdModel, newCommentText: String) =
        commentsProcessing.updateCommentText(commentId, newCommentText)

    override suspend fun replyToComment(repliedCommentId: DiscussionIdModel, newCommentText: String) =
        commentsProcessing.replyToComment(repliedCommentId, newCommentText)
}
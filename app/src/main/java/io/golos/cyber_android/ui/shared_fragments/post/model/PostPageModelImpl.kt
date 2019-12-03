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
import io.golos.cyber_android.ui.shared_fragments.post.model.voting.VotingMachine
import io.golos.domain.DispatchersProvider
import io.golos.domain.api.AuthApi
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.PostDomain
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.repositories.DiscussionRepository
import io.golos.domain.use_cases.community.SubscribeToCommunityUseCase
import io.golos.domain.use_cases.community.UnsubscribeToCommunityUseCase
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.use_cases.post.post_dto.PostMetadata
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
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
    private val unsubscribeToCommunityUseCase: UnsubscribeToCommunityUseCase,
    private val contentId: Post.ContentId?) : ModelBaseImpl(),
    PostPageModel,
    SubscribeToCommunityUseCase by subscribeToCommunityUseCase,
    UnsubscribeToCommunityUseCase by unsubscribeToCommunityUseCase {

    private lateinit var postDomain: PostDomain

    override val post: LiveData<List<VersionedListItem>> = postListDataSource.post

    override val postId: DiscussionIdModel
        get() = DiscussionIdModel(postDomain.contentId.userId, Permlink(postDomain.contentId.permlink))

    override val postMetadata: PostMetadata
        get() = postDomain.body!!.metadata

    override val commentsPageSize: Int
        get() = commentsProcessing.pageSize

    override suspend fun loadPost() {
        withContext(dispatchersProvider.ioDispatcher) {
            postDomain = discussionRepository.getPost(
                contentId?.userId.orEmpty().toCyberName(),
                contentId?.communityId.orEmpty(),
                contentId?.permlink.orEmpty()
            )
            postListDataSource.createOrUpdatePostData(postDomain)
        }
    }

    override fun getPostMenu(): PostMenu {
        return PostMenu(
            communityId = postDomain.community.communityId,
            communityName = postDomain.community.name,
            communityAvatarUrl = postDomain.community.avatarUrl,
            contentId = Post.ContentId(
                communityId = postDomain.community.communityId,
                permlink = postId.permlink.value,
                userId = currentUserRepository.userId
            ),
            creationTime = postDomain.meta.creationTime,
            authorUsername = postDomain.author.username,
            authorUserId = postDomain.author.userId,
            shareUrl = postDomain.shareUrl,
            isMyPost = currentUserRepository.userId == postToProcess.userId,
            isSubscribed = postDomain.community.isSubscribed,
            permlink = postId.permlink.value
        )
    }

    override fun getPostHeader(): PostHeader =
        PostHeader(
            postDomain.community.name,
            postDomain.community.avatarUrl,
            postDomain.meta.creationTime,

            postDomain.author.username,
            postDomain.author.userId,

            false,
            postDomain.author.userId == currentUserRepository.userId,
            postDomain.community.isSubscribed
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

    override suspend fun upVote(communityId: String, userId: String, permlink: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.upVote(communityId, userId, permlink)
            updateUpVote()
        }
    }

    override suspend fun downVote(communityId: String, userId: String, permlink: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.downVote(communityId, userId, permlink)
            updateDownVote()
        }
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
        val totalComments = postDomain.stats?.commentsCount ?: 0

        commentsProcessing.sendComment(commentText, totalComments > 0)
        postDomain = postDomain.copy(
            stats = postDomain.stats?.copy(
                commentsCount = totalComments + 1
            )
        )
    }

    override suspend fun deleteComment(commentId: DiscussionIdModel) {
        val totalComments = postDomain.stats?.commentsCount ?: 0

        commentsProcessing.deleteComment(commentId, totalComments == 1)
        postDomain = postDomain.copy(
            stats = postDomain.stats?.copy(
                commentsCount = totalComments - 1
            )
        )
    }

    override suspend fun reportPost(communityId: String, userId: String, permlink: String, reason: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.reportPost(communityId, userId, permlink, reason)
        }
    }

    override fun getCommentText(commentId: DiscussionIdModel): List<CharSequence> =
        commentsProcessing.getCommentText(commentId)

    override suspend fun updateCommentText(commentId: DiscussionIdModel, newCommentText: String) =
        commentsProcessing.updateCommentText(commentId, newCommentText)

    override suspend fun replyToComment(repliedCommentId: DiscussionIdModel, newCommentText: String) =
        commentsProcessing.replyToComment(repliedCommentId, newCommentText)

    private suspend fun updateUpVote() {
        val votes = postDomain.votes
        if (!votes.hasUpVote) {
            postDomain = postDomain.copy(
                votes = PostDomain.VotesDomain(
                    downCount = votes.downCount,
                    upCount = votes.upCount + 1,
                    hasDownVote = false,
                    hasUpVote = true
                )
            )
        }
        postListDataSource.createOrUpdatePostData(postDomain)
    }

    private suspend fun updateDownVote() {
        val votes = postDomain.votes
        if (!votes.hasDownVote) {
            postDomain = postDomain.copy(
                votes = PostDomain.VotesDomain(
                    downCount = votes.downCount + 1,
                    upCount = votes.upCount,
                    hasDownVote = true,
                    hasUpVote = false
                )
            )
        }
        postListDataSource.createOrUpdatePostData(postDomain)
    }
}
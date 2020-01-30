package io.golos.cyber_android.ui.screens.post_view.model

import androidx.lifecycle.LiveData
import dagger.Lazy
import io.golos.commun4j.utils.toCyberName
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.cyber_android.ui.screens.post_view.dto.PostHeader
import io.golos.cyber_android.ui.screens.post_view.dto.SortingType
import io.golos.cyber_android.ui.screens.post_view.model.comments_processing.CommentsProcessingFacade
import io.golos.cyber_android.ui.screens.post_view.model.post_list_data_source.PostListDataSource
import io.golos.cyber_android.ui.screens.post_view.model.voting.VotingMachine
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.DispatchersProvider
import io.golos.domain.api.AuthApi
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.PostDomain
import io.golos.domain.dto.VotesDomain
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.repositories.DiscussionRepository
import io.golos.domain.use_cases.community.SubscribeToCommunityUseCase
import io.golos.domain.use_cases.community.UnsubscribeToCommunityUseCase
import io.golos.domain.use_cases.model.CommentModel
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.domain.use_cases.post.post_dto.AttachmentsBlock
import io.golos.domain.use_cases.post.post_dto.Block
import io.golos.domain.use_cases.post.post_dto.PostMetadata
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import io.golos.use_cases.reward.*

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
    private val contentId: ContentId?
) : ModelBaseImpl(),
    PostPageModel,
    SubscribeToCommunityUseCase by subscribeToCommunityUseCase,
    UnsubscribeToCommunityUseCase by unsubscribeToCommunityUseCase {

    override suspend fun uploadAttachmentContent(file: File): String = discussionRepository.uploadContentAttachment(file)

    private lateinit var postDomain: PostDomain

    override val post: LiveData<List<VersionedListItem>> = postListDataSource.post

    override val postMetadata: PostMetadata
        get() = postDomain.body!!.metadata

    override val commentsPageSize: Int
        get() = commentsProcessing.pageSize

    override suspend fun loadPost() {
        try {
            withContext(dispatchersProvider.ioDispatcher) {
                postDomain = discussionRepository.getPost(
                    contentId?.userId.orEmpty().toCyberName(),
                    contentId?.communityId.orEmpty(),
                    contentId?.permlink.orEmpty()
                )
                postListDataSource.createOrUpdatePostData(postDomain)
            }
        } catch (e: Exception){
            Timber.e(e)
        }
    }

    override fun getPostMenu(): PostMenu {
        return PostMenu(
            communityId = postDomain.community.communityId,
            communityName = postDomain.community.name,
            communityAvatarUrl = postDomain.community.avatarUrl,
            contentId = ContentId(
                communityId = postDomain.community.communityId,
                permlink = postDomain.contentId.permlink,
                userId = currentUserRepository.userId.userId
            ),
            creationTime = postDomain.meta.creationTime,
            authorUsername = postDomain.author.username,
            authorUserId = postDomain.author.userId,
            shareUrl = postDomain.shareUrl,
            isMyPost = currentUserRepository.userId.userId == postToProcess.userId,
            isSubscribed = postDomain.community.isSubscribed,
            permlink = postDomain.contentId.permlink
        )
    }

    override fun getPostHeader(): PostHeader {
        return PostHeader(
            postDomain.community.name,
            postDomain.community.avatarUrl,
            postDomain.community.communityId,
            postDomain.meta.creationTime,

            postDomain.author.username,
            postDomain.author.userId,

            canJoinToCommunity = false,
            isJoinedToCommunity = postDomain.community.isSubscribed,
            isBackFeatureEnabled = true,
            isRewarded = postDomain.reward.isRewarded(),
            rewardValue = postDomain.reward.getRewardValue()
        )
    }

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

    override suspend fun deletePost(): String {
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.deletePost(
                postDomain.contentId.permlink,
                postDomain.contentId.communityId
            )
        }
        return postDomain.contentId.permlink
    }

    override suspend fun upVote(communityId: String, userId: String, permlink: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.upVote(ContentIdDomain(communityId = communityId, permlink = permlink, userId = userId))
            updateUpVote()
        }
    }

    override suspend fun downVote(communityId: String, userId: String, permlink: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.downVote(ContentIdDomain(communityId = communityId, permlink = permlink, userId = userId))
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

    override suspend fun sendComment(content: List<Block>, attachments: AttachmentsBlock?) {
        val totalComments = postDomain.stats?.commentsCount ?: 0

        commentsProcessing.sendComment(content, attachments)
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

    override suspend fun reportPost(
        authorPostId: String,
        communityId: String,
        permlink: String,
        reason: String
    ) {
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.reportPost(communityId, authorPostId, permlink, reason)
        }
    }

    override fun getCommentText(commentId: DiscussionIdModel): List<CharSequence> =
        commentsProcessing.getCommentText(commentId)

    override fun getComment(commentId: ContentId): CommentModel? {
        return commentsProcessing.getComment(commentId)
    }

    override fun getComment(discussionIdModel: DiscussionIdModel): CommentModel? {
        return commentsProcessing.getComment(discussionIdModel)
    }

    override suspend fun updateComment(commentId: DiscussionIdModel, content: List<Block>, attachments: AttachmentsBlock?) =
        commentsProcessing.updateComment(commentId, content, attachments)

    override suspend fun replyToComment(repliedCommentId: DiscussionIdModel, content: List<Block>, attachments: AttachmentsBlock?) =
        commentsProcessing.replyToComment(repliedCommentId, content, attachments)

    private suspend fun updateUpVote() {
        val votes = postDomain.votes
        if (!votes.hasUpVote) {
            postDomain = postDomain.copy(
                votes = VotesDomain(
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
                votes = VotesDomain(
                    downCount = votes.downCount + 1,
                    upCount = votes.upCount,
                    hasDownVote = true,
                    hasUpVote = false
                )
            )
        }
        postListDataSource.createOrUpdatePostData(postDomain)
    }

    override fun isTopReward(): Boolean? = postDomain.reward.isTopReward()
}
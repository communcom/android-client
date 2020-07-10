package io.golos.cyber_android.ui.screens.post_view.model

import androidx.lifecycle.LiveData
import dagger.Lazy
import io.golos.cyber_android.ui.dto.PostDonation
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.cyber_android.ui.screens.post_view.dto.PostHeader
import io.golos.cyber_android.ui.screens.post_view.dto.RewardInfo
import io.golos.cyber_android.ui.screens.post_view.dto.SortingType
import io.golos.cyber_android.ui.screens.post_view.model.comments_processing.CommentsProcessingFacade
import io.golos.cyber_android.ui.screens.post_view.model.post_list_data_source.PostListDataSource
import io.golos.cyber_android.ui.screens.post_view.model.voting.post.PostPageVotingUseCase
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.data.mappers.mapToCyberName
import io.golos.data.repositories.wallet.WalletRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.*
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.PostMetadata
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.repositories.DiscussionRepository
import io.golos.domain.repositories.GlobalSettingsRepository
import io.golos.domain.repositories.UsersRepository
import io.golos.domain.use_cases.community.SubscribeToCommunityUseCase
import io.golos.domain.use_cases.community.UnsubscribeToCommunityUseCase
import io.golos.domain.use_cases.model.DiscussionIdModel
import io.golos.use_cases.reward.getRewardValue
import io.golos.use_cases.reward.isRewarded
import io.golos.use_cases.reward.isTopReward
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class PostPageModelImpl
@Inject
constructor(
    private val postToProcess: DiscussionIdModel,
    private val dispatchersProvider: DispatchersProvider,
    private val discussionRepository: DiscussionRepository,
    private val currentUserRepository: CurrentUserRepositoryRead,
    private val postListDataSource: PostListDataSource,
    private val postPageVotingUseCase: Lazy<PostPageVotingUseCase>,
    private val commentsProcessing: CommentsProcessingFacade,
    private val subscribeToCommunityUseCase: SubscribeToCommunityUseCase,
    private val unsubscribeToCommunityUseCase: UnsubscribeToCommunityUseCase,
    private val contentId: ContentIdDomain?,
    private val usersRepository: Lazy<UsersRepository>,
    private val walletRepository: WalletRepository,
    private val globalSettingsRepository: GlobalSettingsRepository
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

    override val rewardCurrency: RewardCurrency
        get() = globalSettingsRepository.rewardCurrency

    override val rewardCurrencyUpdates: Flow<RewardCurrency?>
        get() = globalSettingsRepository.rewardCurrencyUpdates

    override suspend fun loadPost() {
        try {
            withContext(dispatchersProvider.ioDispatcher) {
                postDomain = discussionRepository.getPost(
                    contentId!!.userId.mapToCyberName(), contentId.communityId, contentId.permlink)
                postListDataSource.createOrUpdatePostData(postDomain)
            }
        } catch (e: Exception){
            Timber.e(e)
            throw e
        }
    }

    override fun getPostMenu(): PostMenu {
        return PostMenu(
            communityId = postDomain.community.communityId,
            communityName = postDomain.community.name,
            communityAvatarUrl = postDomain.community.avatarUrl,
            contentId = ContentIdDomain(
                communityId = postDomain.community.communityId,
                permlink = postDomain.contentId.permlink,
                userId = postDomain.author.userId
            ),
            creationTime = postDomain.meta.creationTime,
            authorUsername = postDomain.author.username,
            authorUserId = postDomain.author.userId.userId,
            authorAvatarUrl = postDomain.author.avatarUrl,
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
            postDomain.author.userId.userId,
            postDomain.author.avatarUrl,

            canJoinToCommunity = false,
            isJoinedToCommunity = postDomain.community.isSubscribed,
            isBackFeatureEnabled = true,

            reward = takeIf { postDomain.reward.isRewarded() }?.let {
                RewardInfo(
                    rewardValueInPoints = postDomain.reward.getRewardValue(),
                    rewardValueInCommun = postDomain.reward?.rewardValueCommun,
                    rewardValueInUSD = postDomain.reward?.rewardValueUSD,
                    rewardCurrency = rewardCurrency
                )
            }
        )
    }

    override suspend fun addToFavorite(permlink: String) {
        delay(100)
    }

    override suspend fun removeFromFavorite(permlink: String) {
        delay(100)
    }

    override suspend fun getUserId(userNameOrId: String): UserIdDomain =
        usersRepository.get().getUserId(userNameOrId)

    override suspend fun deletePost(): String {
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.deletePost(
                postDomain.contentId.permlink,
                postDomain.contentId.communityId
            )
        }
        return postDomain.contentId.permlink
    }

    override suspend fun upVote(communityId: CommunityIdDomain, userId: UserIdDomain, permlink: String) {
        postDomain = postPageVotingUseCase.get().upVote(postDomain, communityId, userId, permlink)
    }

    override suspend fun downVote(communityId: CommunityIdDomain, userId: UserIdDomain, permlink: String) {
        postDomain = postPageVotingUseCase.get().downVote(postDomain, communityId, userId, permlink)
    }

    override suspend fun voteForComment(communityId: CommunityIdDomain, commentId: ContentIdDomain, isUpVote: Boolean) =
        commentsProcessing.vote(communityId, commentId, isUpVote)

    override suspend fun updateCommentsSorting(sortingType: SortingType) = postListDataSource.updateCommentsSorting(sortingType)

    override suspend fun loadStartFirstLevelCommentsPage() = commentsProcessing.loadStartFirstLevelPage()

    override suspend fun loadNextFirstLevelCommentsPage() = commentsProcessing.loadNextFirstLevelPageByScroll()

    override suspend fun retryLoadingFirstLevelCommentsPage() = commentsProcessing.retryLoadFirstLevelPage()

    override suspend fun loadNextSecondLevelCommentsPage(parentCommentId: ContentIdDomain) =
        commentsProcessing.loadNextSecondLevelPage(parentCommentId)

    override suspend fun retryLoadingSecondLevelCommentsPage(parentCommentId: ContentIdDomain) =
        commentsProcessing.retryLoadSecondLevelPage(parentCommentId)

    override suspend fun sendComment(jsonBody: String) {
        val totalComments = postDomain.stats?.commentsCount ?: 0

        commentsProcessing.sendComment(jsonBody)
        postDomain = postDomain.copy(
            stats = postDomain.stats?.copy(
                commentsCount = totalComments + 1
            )
        )
    }

    override suspend fun deleteComment(commentId: ContentIdDomain) {
        val totalComments = postDomain.stats?.commentsCount ?: 0

        commentsProcessing.deleteComment(commentId, totalComments == 1)
        postDomain = postDomain.copy(
            stats = postDomain.stats?.copy(
                commentsCount = totalComments - 1
            )
        )
    }

    override suspend fun reportPost(
        authorPostId: UserIdDomain,
        communityId: CommunityIdDomain,
        permlink: String,
        reason: String
    ) {
        withContext(dispatchersProvider.ioDispatcher) {
            discussionRepository.reportPost(communityId, authorPostId, permlink, reason)
        }
    }

    override fun getCommentText(commentId: ContentIdDomain): List<CharSequence> =
        commentsProcessing.getCommentText(commentId)

    override fun getComment(commentId: ContentIdDomain): CommentDomain? {
        return commentsProcessing.getComment(commentId)
    }

    override suspend fun updateComment(commentId: ContentIdDomain, jsonBody: String) =
        commentsProcessing.updateComment(commentId, jsonBody)

    override suspend fun replyToComment(repliedCommentId: ContentIdDomain, jsonBody: String) =
        commentsProcessing.replyToComment(repliedCommentId, jsonBody)

    override fun isTopReward(): Boolean? = postDomain.reward.isTopReward()

    override suspend fun getWalletBalance(): List<WalletCommunityBalanceRecordDomain> = walletRepository.getBalance()

    override suspend fun updateDonation(donation: PostDonation) {
        if(donation.postId == postDomain.contentId) {
            postListDataSource.updateDonation(donation.donation)
        }
    }

    override suspend fun updateRewardCurrency(currency: RewardCurrency) = globalSettingsRepository.updateRewardCurrency(currency)
}